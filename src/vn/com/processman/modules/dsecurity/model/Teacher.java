/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.dsecurity.model;

import java.util.ArrayList;
import java.util.Collection;

import vn.com.processman.modules.teaching.model.TeachingBySemester;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
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
import domainapp.basics.model.security.DomainUser;
import domainapp.basics.model.security.Role;

/**
 * @overview
 *  A sub-type of {@link DomainUser} that is also a teacher who teaches certain {@link SubjectBySemester}. 
 *  
 * @author dmle
 */
public class Teacher extends DomainUser {

  
  /***
   * The {@link Role}.name that is specifically assigned to {@link Teacher}s
   */
  public static final String RoleName = "GIAV";
  
  public static final String Assoc_TeacherAndSubjectBySemester = "TeacherAndSubjectBySemester";
  public static final String Assoc_TeacherAndTeachingBySemester = "TeacherAndTeachingBySemester";
  public static final String A_teachings = "teachings";
  public static final String A_teachingSubjs = "teachingSubjs";

  @DAssoc(ascName = Assoc_TeacherAndSubjectBySemester, role = "teacher", 
      ascType = AssocType.Many2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = SubjectBySemester.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE),
      normAttrib=A_teachings)
  @DAttr(name=A_teachingSubjs, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=SubjectBySemester.class))
  private Collection<SubjectBySemester> teachingSubjs;
  
  /**normaliser for {@link #A_teachingSubjs*/
  @DAssoc(ascName = Assoc_TeacherAndTeachingBySemester, role = "teacher", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = TeachingBySemester.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
  @DAttr(name=A_teachings, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=TeachingBySemester.class))
  private Collection<TeachingBySemester> teachings;
  private int teachingsCount;

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Teacher(Integer id, String name, String login, String pwd) {
    super(id, name, login, pwd);
    
    teachings = new ArrayList();
  }

  @DOpt(type=DOpt.Type.ObjectFormConstructor) // with both roles and teachingSubjs
  public Teacher(String name, String login, String pwd, Collection<Role> roles, Collection teachingSubjs) {
    super(null, name, login, pwd, roles);
    
    teachings = new ArrayList();
    this.teachingSubjs = teachingSubjs;
  }

  @DOpt(type=DOpt.Type.ObjectFormConstructor) // with only teachingSubjs
  public Teacher(String name, String login, String pwd, Collection teachingSubjs) {
    this(name, login, pwd, null, teachingSubjs);
  }

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
        
        addTeachingSubj(teaching.getSubject());
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
      
      addTeachingSubj(teaching.getSubject());
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_teachings)
  public boolean addNewTeaching(Collection<TeachingBySemester> teachings) {
    boolean updated = false;
    
    this.teachings.addAll(teachings);
    
    for (TeachingBySemester teaching : teachings) {
      addTeachingSubj(teaching.getSubject());
    }
    
    teachingsCount += teachings.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_teachings)
  public boolean addNewTeaching(TeachingBySemester teaching) {
    boolean updated = false;
    
    this.teachings.add(teaching);
    addTeachingSubj(teaching.getSubject());

    teachingsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_teachings)  
  public boolean removeTeaching(TeachingBySemester teaching) {
    boolean removed = teachings.remove(teaching);
    if (removed) {
      removeTeachingSubj(teaching.getSubject());

      teachingsCount--;
    }
    
    return false;
  }

  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #teachings}*/
  
  /** ASSOCIATION methods: {@link #teachingSubjs} */
  
  
  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value=A_teachingSubjs)  
  public Collection<SubjectBySemester> getTeachingSubjs() {
    return teachingSubjs;
  }

  @DOpt(type = DOpt.Type.Setter)
  @AttrRef(value=A_teachingSubjs)  
  public void setTeachingSubjs(Collection<SubjectBySemester> teachingSubjs) {
    this.teachingSubjs = teachingSubjs;
  }  
  
  private void addTeachingSubj(SubjectBySemester subj) {
    if (teachingSubjs == null) teachingSubjs = new ArrayList<>();
    
    teachingSubjs.add(subj);
  }

  private void removeTeachingSubj(SubjectBySemester subj) {
    if (teachingSubjs != null) {    
      teachingSubjs.remove(subj);
    }
  }

  /** end ASSOCIATION: {@link #teachingSubjs}*/
}
