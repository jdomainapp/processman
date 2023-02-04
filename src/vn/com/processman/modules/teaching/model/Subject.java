package vn.com.processman.modules.teaching.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.util.Tuple;

/**
 * @overview
 *  Represents a course module (subject).
 *  
 * @author dmle
 *
 */
public class Subject {

  public static final String A_id = "id";
  public static final String A_code = "code";
  public static final String A_name = "name";
  public static final String A_semesterSubjs = "semesterSubjs";
  public static final String Assoc_SubjectAndSubjectBySems = "SubjectAndSubjectBySemster";
  
  public static final int LENGTH_CODE = 30;
  
  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
  @DAttr(name=A_code, type=Type.String, optional=false, unique=true, length=LENGTH_CODE)
  private String code;
  
  @DAttr(name=A_name, type=Type.String, optional=false, length=255)
  private String name;
  
  @DAssoc(ascName = Assoc_SubjectAndSubjectBySems, role = "subject", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = SubjectBySemester.class, cardMin=0, cardMax = MetaConstants.CARD_MORE))
  @DAttr(name=A_semesterSubjs, type=Type.Collection, optional=false, serialisable=false, 
      filter=@Select(clazz=SubjectBySemester.class))
  private Collection<SubjectBySemester> semesterSubjs;
  private int semesterSubjsCount;
  
  // constructor: from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Subject(Integer id, String code, String name) {
    this.id = nextID(id);
    this.code = code;
    this.name = name;
    
    semesterSubjs = new ArrayList<>();
  }
  
  // constructor: from object form
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Subject(String code, String name) {
    this(null, code, name);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  /** ASSOCIATION methods: {@link #semesterSubjs} */
  public Collection<SubjectBySemester> getSemesterSubjs() {
    return semesterSubjs;
  }

  public void setSemesterSubjs(List<SubjectBySemester> semesterSubjs) {
    this.semesterSubjs = semesterSubjs;
    semesterSubjsCount = semesterSubjs.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getSemesterSubjsCount() {
    return semesterSubjsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setSemesterSubjsCount(int semesterSubjCount) {
    this.semesterSubjsCount = semesterSubjCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_semesterSubjs)
  public boolean addSubjectBySemester(Collection<SubjectBySemester> semesterSubjs) {
    boolean updated = false;
    
    for (SubjectBySemester semesterSubj : semesterSubjs) {
      if (!this.semesterSubjs.contains(semesterSubj)) {
        this.semesterSubjs.add(semesterSubj);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_semesterSubjs)
  public boolean addSubjectBySemester(SubjectBySemester semesterSubj) {
    boolean updated = false;
    
    if (!this.semesterSubjs.contains(semesterSubj)) {
      this.semesterSubjs.add(semesterSubj);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_semesterSubjs)
  public boolean addNewSubjectBySemester(Collection<SubjectBySemester> semesterSubjs) {
    boolean updated = false;
    
    this.semesterSubjs.addAll(semesterSubjs);
    semesterSubjsCount += semesterSubjs.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_semesterSubjs)
  public boolean addNewSubjectBySemester(SubjectBySemester semesterSubj) {
    boolean updated = false;
    
    this.semesterSubjs.add(semesterSubj);
    semesterSubjsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_semesterSubjs)  
  public boolean removeSubjectBySemester(SubjectBySemester semesterSubj) {
    boolean removed = semesterSubjs.remove(semesterSubj);
    if (removed) {
      semesterSubjsCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #semesterSubjs}*/
  
  // util methods
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter) {
        idCounter = num;
      }
      return currID;
    }
  }

  /**
   * @requires minVal != null /\ maxVal != null
   * @effects update the auto-generated value of attribute <tt>attrib</tt>,
   *          specified for <tt>derivingValue</tt>, using
   *          <tt>minVal, maxVal</tt>
   */
  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(DAttr attrib,
      Tuple derivingValue, Object minVal, Object maxVal)
      throws ConstraintViolationException {
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)
          idCounter = maxIdVal;
      }
      // TODO add support for other attributes here
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Subject other = (Subject) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Subject (" + id + ", " + code + ")";
  }
}
