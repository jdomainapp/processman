package vn.com.processman.modules.teaching.subjectbysem.model;

import java.util.ArrayList;
import java.util.Collection;

import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.model.TeachingBySemester;
import vn.com.processman.util.model.Semester;
import domainapp.basics.core.dodm.DODMToolkit;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.NotPossibleException;
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
import domainapp.basics.util.Toolkit;
import domainapp.basics.util.Tuple;
import domainapp.basics.util.cache.StateHistory;

/**
 * @overview
 *  Represents a course module (subject) that is implemented in a semester of a given year.
 *  
 * @author dmle
 *
 */
public class SubjectBySemester {

  public static final String A_id = "id";
  public static final String A_approvedForExam = "approvedForExam";
  public static final String A_subject = "subject";
  public static final String A_semester = "semester";
  public static final String A_year = "year";
  public static final String A_code = "code";
  private static final String A_teachers = "teachers";
  
  private static final String A_teachings = "teachings";
  private static final String A_subjectTasks = "subjectTasks";

  public static final String A_displayInfo = "displayInfo";
  
  public static final String Assoc_SubjectBySemesterAndTeaching = "SubjectBySemesterAndTeaching";
  public static final String Assoc_SubjectBySemesterAndSubjectTask = "SubjectBySemesterAndSubjectTask";
  public static final String Assoc_SubjectBySemesterAndSubjectAction = "SubjectBySemesterAndSubjectAction";

  /**attributes of this that make up the candidate key*/
  public static final String[] A_candidateKey = 
    {A_subject, A_semester, A_year};

  public static final int CodeLength = 10;
  public static final int SemLength = 10;
  public static final int YearLength = 4;
  private static final String DisplayInfoFormat = "%-"+CodeLength+"s | %"+SemLength+"s | %"+YearLength+"d";
  
//  public static final String A_info = "info";
//  private static final int SubjectCodeLength = 30;
//  private static final int SemesterLength = 10;
//  private static final String InfoFormat = "%-"+SubjectCodeLength+"s | %-"+SemesterLength+"s | %s";
  
  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
  /**the {@link Subject} from which this is derived */
  @DAssoc(ascName = Subject.Assoc_SubjectAndSubjectBySems, role = "semesterSubj", 
    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
    associate = @Associate(type=Subject.class, cardMin = 1, cardMax = 1),
    dependsOn=true)
  @DAttr(name=A_subject, type=Type.Domain, optional=false)
  private Subject subject;

  /** derived from {@link #subject} */
  @DAttr(name=A_code, type=Type.String, //auto=true, 
      mutable=false, 
      derivedFrom={A_subject}, // v3.3
      length=30, 
      serialisable=false)
  private String code;
  
//  /**description of each {@link SubjectBySemester} that is displayed on a user interface for 
//   * user to select */
//  @DomainConstraint(name=A_info, type=Type.String, auto=true, mutable=false, 
//      derivedFrom={A_subject, A_semester, A_year}, 
//      length=30, 
//      serialisable=false)
//  private String info;
  
  @DAttr(name=A_semester, type=Type.Domain,length=50,defaultValueFunction=true)
  private Semester semester;
  
  @DAttr(name=A_year, type=Type.Integer, defaultValueFunction=true)
  private Integer year;

  /**derived from {@link #code}, {@link #semester}, {@link #year}*/
  @DAttr(name=A_displayInfo,type=Type.String,auto=true,mutable=false,optional=false,serialisable=false,
      derivedFrom={A_code, A_semester, A_year})
  private String displayInfo;
  
  private StateHistory<String, Object> stateHist;

  @DAttr(name=A_approvedForExam, type=Type.Boolean, length=10)
  private boolean approvedForExam;
  
  @DAssoc(ascName = Teacher.Assoc_TeacherAndSubjectBySemester, role = "subjectBySemester", 
      ascType = AssocType.Many2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Teacher.class, cardMin = 0, cardMax = 10),
      normAttrib=A_teachings)
  @DAttr(name=A_teachers, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=Teacher.class))
  private Collection<Teacher> teachers;
  
  /** normaliser association for {@link #teachers}*/
  @DAssoc(ascName = Assoc_SubjectBySemesterAndTeaching, role = "subjectBySemester", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = TeachingBySemester.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
  @DAttr(name=A_teachings, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=TeachingBySemester.class))
  private Collection<TeachingBySemester> teachings;
  private int teachingsCount;

  // TODO: complete this association when needed
  @DAssoc(ascName = Assoc_SubjectBySemesterAndSubjectTask, role = "subjectBySemester", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = SubjectTask.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
  @DAttr(name=A_subjectTasks, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=SubjectTask.class))
  private Collection<SubjectTask> subjectTasks;
  private int subjectTasksCount;
  
  // TODO: complete this association when needed
  @DAssoc(ascName = Assoc_SubjectBySemesterAndSubjectAction, role = "subjectBySemester", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = SubjectAction.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
  @DAttr(name=A_subjectTasks, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=SubjectAction.class))
  private Collection<SubjectAction> subjectActions;
  private int subjectActionsCount;
  
  // constructor: base constructor used by others
  private SubjectBySemester(Integer id, Subject subject, Semester semester, Integer year, Boolean approvedForExam, Collection<Teacher> teachers) {
    this.id = nextID(id);
    
    this.subject = subject;
    this.code = subject.getCode();
    
    this.semester = semester;
    this.year = year;
    if (approvedForExam != null)
      this.approvedForExam = approvedForExam;
    else
      this.approvedForExam = false;
    
    teachings = new ArrayList();
    
    stateHist = new StateHistory<>();
    
    this.teachers = teachers;
    
    updateDisplayInfo();
  }
  
  // constructor: from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public SubjectBySemester(Integer id, Subject subject, Semester semester, Integer year, Boolean approvedForExam) {
//    this.id = nextID(id);
//    
//    this.subject = subject;
//    this.code = subject.getCode();
//    
//    this.semester = semester;
//    this.year = year;
//    if (approvedForExam != null)
//      this.approvedForExam = approvedForExam;
//    else
//      this.approvedForExam = false;
//    
//    teachings = new ArrayList();
//    
//    stateHist = new StateHistory<>();
    this(id, subject, semester, year, approvedForExam, null);
  }
  
  // constructor: from object form
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public SubjectBySemester(Subject subject, Semester semester, Integer year, Boolean approvedForExam) {
    this(null, subject, semester, year, approvedForExam, null);
  }

  // constructor: from object form (with approvedForExam=false)
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public SubjectBySemester(Subject subject, Semester semester, Integer year) {
    this(null, subject, semester, year, Boolean.FALSE, null);
  }
  
  // constructor: from object form (with teachers & approvedForExam=false)
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public SubjectBySemester(Subject subject, Semester semester, Integer year, Collection<Teacher> teachers) {
    this(null, subject, semester, year, Boolean.FALSE, teachers);
  }
  
  
  public int getId() {
    return id;
  }


  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    setSubject(subject, false);
    //this.subject = subject;
  }

  public void setSubject(Subject subject, boolean updateDerivedAttributes) {
    this.subject = subject;
    
    this.code = subject.getCode();
    
    if (updateDerivedAttributes) {
      updateDisplayInfo();
    }
  }

//  /**
//   * @requires 
//   *  subject != null
//   *  
//   * @effects 
//   *  return {@link #subject}.getCode
//   */
//  private String getSubjectCode() {
//    return subject.getCode();
//  }
  
  public String getCode() {
    return code;
  }

  @DOpt(type=DOpt.Type.DerivedAttributeGetter)
  @AttrRef(value=A_code)
  public static Object genSubjectCode(Object[] args) {
    // no special format is required, simply return the value;
    return args[0];
  }
  
  @DOpt(type=DOpt.Type.DerivedAttributeParser)
  @AttrRef(value=A_code)
  public static Object[] parseSubjectCode(DAttr displayInfoAttrib, Object displayInfo, 
      DAttr[] derivingAttribs) throws NotPossibleException {
    // simply return the value;
    // TODO: to properly pass this value requires retrieving the actual Subject (which requires access to  
    // DOMBasic)
    return new Object[] {displayInfo};
  }
  
//  @Metadata(type=Metadata.Type.MethodGetDerivedAttributeValue)
//  @MemberRef(name=A_code)
//  public static String genCode(Object[] args) {
//    Subject subj = (Subject) args[0];
//    return subj.getCode();
//  }
  
//  @Metadata(type=Metadata.Type.MethodParseDerivedAttributeValue)
//  @MemberRef(name=A_code)
//  public static Object[] parseFamilyInfo(DomainConstraint codeAttrib, Object codeObj, 
//      DomainConstraint[] derivingAttribs) throws NotPossibleException {
//    // this is the reverse of genFamilyInfo
//    
//    if (codeObj == null || !(codeObj instanceof String) || derivingAttribs == null)
//      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
//          new Object[] {""});  // error
//    
//    String code = (String) codeObj;
//    
//    if (derivingAttribs.length != 1) {
//      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
//          new Object[] {""});  // error
//    }
//    
//    Object[] values = new Object[1];
//    values[0] = ...; // todo: look up subject (?)
//    
//    
//    return values;
//  }
  
  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    setSemester(semester, false);
    //this.semester = semester;
  }

  public void setSemester(Semester semester, boolean updateDerivedAttributes) {
    this.semester = semester;
    
    if (updateDerivedAttributes) {
      updateDisplayInfo();
    }
  }
  
  /**
   * @effects 
   *  return {@link Semester} suitable for the current time of year
   */
  @DOpt(type=DOpt.Type.DefaultValueFunction)
  @AttrRef(value=A_semester)
  public static Semester getDefaultSemester() {
    return Semester.getSemesterFor(Toolkit.getCurrentSeason());
  }
    
  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    setYear(year, false);
    //this.year = year;
  }

  public void setYear(Integer year, boolean updateDerivedAttributes) {
    this.year = year;
    
    if (updateDerivedAttributes) {
      updateDisplayInfo();
    }
  }
  
  @DOpt(type=DOpt.Type.DefaultValueFunction)
  @AttrRef(value=A_year)
  public static int getDefaultYear() {
    return Toolkit.getCurrentYear();
  }

  public boolean getApprovedForExam() {
    return approvedForExam;
  }

  public void setApprovedForExam(boolean approvedForExam) {
    this.approvedForExam = approvedForExam;
  }

  /** METHODS: {@link #displayInfo} */

  public String getDisplayInfo() {
    return getDisplayInfo(false);
  }

  public String getDisplayInfo(boolean cached) throws IllegalStateException {
    if (cached) {
      Object val = stateHist.get(A_displayInfo);
      if (val == null)
        throw new IllegalStateException("SubjectBySemester.getDisplayInfo: cached value is null");
      return (String) val;
    } else {
      return displayInfo;
    }
  }
  
  /**
   * @effects 
   *  cache old value of {@link #displayInfo}; 
   *  update {@link #displayInfo} based on current state of this; 
   *  return old value
   */
  @DOpt(type=DOpt.Type.DerivedAttributeUpdater)
  @AttrRef(value=A_displayInfo)
  public Object updateDisplayInfo() {
    // cache old info
    String oldVal = displayInfo;
    stateHist.put(A_displayInfo, oldVal);

    displayInfo = String.format(DisplayInfoFormat, getCode(), getSemester(), getYear()); 
    
    return oldVal;
  }
  
  @DOpt(type=DOpt.Type.DerivedAttributeGetter)
  @AttrRef(value=A_displayInfo)
  public static String genDisplayInfo(Object[] args) {
    String code = (String) args[0];
    String sem = (String) args[1];
    Integer year = (Integer) args[2];
    String displayInfo = String.format(DisplayInfoFormat, code, sem, year); 
    return displayInfo;
  }
  
  @DOpt(type=DOpt.Type.DerivedAttributeParser)
  @AttrRef(value=A_displayInfo)
  public static Object[] parseDisplayInfo(DAttr displayInfoAttrib, Object displayInfo, 
      DAttr[] derivingAttribs) throws NotPossibleException {
    // this is the reverse of genDisplayInfo
    
    if (displayInfo == null || !(displayInfo instanceof String) || derivingAttribs == null)
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {""});  // error
    
    String displayInfoStr = (String) displayInfo;
    String[] args = displayInfoStr.split("\\|");
    
    if (args.length != 3 || derivingAttribs.length != 3) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {""});  // error
    }
    
    String code = args[0].trim();
    String semStr = args[1].trim();
    String yearStr = args[2].trim();
    
    Object[] values = new Object[3];
    values[0] = code;
    
    // parse semester
    DAttr semAttrib = derivingAttribs[1]; 
    try {
      values[1] = DODMToolkit.parseDomainValue(semAttrib, Semester.class, semStr);
    } catch (ConstraintViolationException e) {
      // error
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PARSE_ATTRIBUTE_VALUE, e, 
          new Object[] {SubjectBySemester.class.getSimpleName(), semAttrib.name(), semStr});  // error
    }

    // parse year
    DAttr yearAttrib = derivingAttribs[2]; 
    try {
      values[2] = DODMToolkit.parseDomainValue(yearAttrib, null, yearStr);
    } catch (ConstraintViolationException e) {
      // error
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PARSE_ATTRIBUTE_VALUE, e, 
          new Object[] {SubjectBySemester.class.getSimpleName(), yearAttrib.name(), yearStr});  // error
    }
    
    return values;
  }
  
  /** end METHODS: {@link #displayInfo} */
  
  /** ASSOCIATION methods: {@link #teachings} */
  public Collection<TeachingBySemester> getTeachingBySemesters() {
    return teachings;
  }

  public void setTeachingBySemesters(Collection<TeachingBySemester> teachings) {
    this.teachings = teachings;
    teachingsCount = teachings.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getTeachingBySemestersCount() {
    return teachingsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setTeachingBySemestersCount(int teachingCount) {
    this.teachingsCount = teachingCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_teachings)
  public boolean addTeachingBySemester(Collection<TeachingBySemester> teachings) {
    boolean updated = false;
    
    for (TeachingBySemester teaching : teachings) {
      if (!this.teachings.contains(teaching)) {
        this.teachings.add(teaching);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_teachings)
  public boolean addTeachingBySemester(TeachingBySemester teaching) {
    boolean updated = false;
    
    if (!this.teachings.contains(teaching)) {
      this.teachings.add(teaching);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_teachings)
  public boolean addNewTeachingBySemester(Collection<TeachingBySemester> teachings) {
    boolean updated = false;
    
    this.teachings.addAll(teachings);
    teachingsCount += teachings.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_teachings)
  public boolean addNewTeachingBySemester(TeachingBySemester teaching) {
    boolean updated = false;
    
    this.teachings.add(teaching);
    teachingsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_teachings)  
  public boolean removeTeachingBySemester(TeachingBySemester teaching) {
    boolean removed = teachings.remove(teaching);
    if (removed) {
      teachingsCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #teachings}*/

  /** ASSOCIATION methods: {@link #teachings} */
  public Collection<TeachingBySemester> getTeachings() {
    return teachings;
  }

  public void setTeachings(Collection<TeachingBySemester> teachings) {
    this.teachings = teachings;
    teachingsCount = teachings.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getTeachingsCount() {
    return teachingsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setTeachingsCount(int teachingCount) {
    this.teachingsCount = teachingCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_teachings)
  public boolean addTeaching(Collection<TeachingBySemester> teachings) {
    boolean updated = false;
    
    for (TeachingBySemester teaching : teachings) {
      if (!this.teachings.contains(teaching)) {
        this.teachings.add(teaching);
        
        addTeacher(teaching.getTeacher());
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_teachings)
  public boolean addTeaching(TeachingBySemester teaching) {
    boolean updated = false;
    
    if (!this.teachings.contains(teaching)) {
      this.teachings.add(teaching);
      
      addTeacher(teaching.getTeacher());
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_teachings)
  public boolean addNewTeaching(Collection<TeachingBySemester> teachings) {
    boolean updated = false;
    
    this.teachings.addAll(teachings);
    
    for (TeachingBySemester teaching : teachings) {
      addTeacher(teaching.getTeacher());
    }
    
    teachingsCount += teachings.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_teachings)
  public boolean addNewTeaching(TeachingBySemester teaching) {
    boolean updated = false;
    
    this.teachings.add(teaching);
    addTeacher(teaching.getTeacher());

    teachingsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_teachings)  
  public boolean removeTeaching(TeachingBySemester teaching) {
    boolean removed = teachings.remove(teaching);
    if (removed) {
      removeTeacher(teaching.getTeacher());

      teachingsCount--;
    }
    
    return false;
  }

  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #teachings}*/
  
  /** ASSOCIATION methods: {@link #teachers} */
  
  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value=A_teachers)  
  public Collection<Teacher> getTeachers() {
    return teachers;
  }

  @DOpt(type = DOpt.Type.Setter)
  @AttrRef(value=A_teachers)  
  public void setTeachers(Collection<Teacher> teachers) {
    this.teachers = teachers;
  }  
  
  private void addTeacher(Teacher teacher) {
    if (teachers == null) teachers = new ArrayList<>();
    
    teachers.add(teacher);
  }

  private void removeTeacher(Teacher teacher) {
    if (teachers != null) {    
      teachers.remove(teacher);
    }
  }

  /** end ASSOCIATION: {@link #teachingSubjs}*/
  
//  public String getInfo() {
//    return getInfo(false);
//  }
//
//  public String getInfo(boolean cached) throws IllegalStateException {
//    if (cached) {
//      Object val = stateHist.get(A_info);
//      if (val == null)
//        throw new IllegalStateException("SubjectBySemester.getInfo: cached value is null");
//      return (String) val;
//    } else {
//      return info;
//    }
//  }
//  
//  /**
//   * @effects 
//   *  cache old value of {@link #info}; 
//   *  update {@link #info} based on current state of this; 
//   *  return old value
//   */
//  @Metadata(type=Metadata.Type.MethodUpdateDerivingValue)
//  @MemberRef(name=A_info)
//  public Object updateInfo() {
//    // cache old info
//    String oldVal = info;
//    stateHist.put(A_info, oldVal);
//
//    info = String.format(InfoFormat, getSubjectCode(), getSemester(), getYear()); 
//    
//    return oldVal;
//  }
//  
//  @Metadata(type=Metadata.Type.MethodGetDerivedAttributeValue)
//  @MemberRef(name=A_info)
//  public static String genInfo(Object[] args) {
//    Subject subject = (Subject) args[0];
//    Semester sem = (Semester) args[1];
//    Integer year = (Integer) args[2];
//        
//    String info = String.format(InfoFormat, subject.getCode(), sem, year); 
//    return info;
//  }
//  
//  @Metadata(type=Metadata.Type.MethodParseDerivedAttributeValue)
//  @MemberRef(name=A_info)
//  public static Object[] parseInfo(DomainConstraint infoAttrib, Object info, 
//      DomainConstraint[] derivingAttribs) throws NotPossibleException {
//    // this is the reverse of genFamilyInfo
//    
//    if (info == null || !(info instanceof String) || derivingAttribs == null)
//      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
//          new Object[] {""});  // error
//    
//    String infoStr = (String) info;
//    String[] args = infoStr.split("\\|");
//    
//    if (args.length != 3 || derivingAttribs.length != 3) {
//      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
//          new Object[] {""});  // error
//    }
//    
//    return args;
//  }
  
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
      if (attrib.name().equals(A_id)) {
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
    SubjectBySemester other = (SubjectBySemester) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SubjectBySemester (" + id + ", " + subject.getCode() + ", " + semester + ", " + year + ")";
  }
}
