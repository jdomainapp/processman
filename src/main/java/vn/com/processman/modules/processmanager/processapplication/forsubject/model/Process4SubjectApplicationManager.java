/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.processmanager.processapplication.forsubject.model;

import java.util.Collection;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.mosa.model.DDataValidator;
import jda.mosa.view.assets.tables.JObjectTable;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processmanager.processapplication.model.ProcessApplicationManager;
import vn.com.processman.modules.processmanager.processapplication.model.ValidateProcessApplication;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  A sub-type of {@link ProcessApplicationManager} to encapsulate an additional attribute concerning 
 *  the {@link Subject}s to which the {@link Process}'s {@link Task} and {@link Action} will be applied. 
 *  
 * @author dmle
 */
@DClass(serialisable=false)
@DDataValidator(type=ValidateProcessApplication.class)
public class Process4SubjectApplicationManager extends ProcessApplicationManager {

  /**extended scope definitions */
  public static final ScopeDef ScopeDefTask4SubjectApplication = new ScopeDef(
      Task4Subject.class,
      Task4Subject.A_application,
      Boolean.FALSE); // end ScopeDefTask4SubjectApplication
  
  public static final ScopeDef ScopeDefAction4SubjectApplication = new ScopeDef(
      Action4Subject.class,
      Action4Subject.A_application,
      Boolean.FALSE); // end ScopeDefAction4SubjectApplication
  
  public static final ScopeDef ScopeDefSubjectTask = new ScopeDef(
      SubjectTask.class,
      SubjectTask.A_EssentialCreateNews,
      Boolean.TRUE
   ); // end ScopeDefSubjectTask
  
  public static final ScopeDef ScopeDefSubjectAction = new ScopeDef(
      SubjectAction.class,
      SubjectAction.A_EssentialCreateNews,Boolean.TRUE, 
      JObjectTable.class); // end ScopeDefSubjectAction
  
  public static final String A_subjects = "subjects";

//  /**
//   * The subject to which a {@link Process} will be applied. 
//   * It <b>needs not be specified when</b> user only wishes to Open the {@link ProcessApplication}s for viewing. 
//   * It <b>must be specified when</b> user creates new {@link ProcessApplication}   
//   */
//  @DomainConstraint(name=A_subject, type=Type.Domain, optional=true)
//  private Subject subject;

  /** Specify the objects to which the user is interested in applying a process*/
  @DAttr(name=A_subjects, type=Type.Collection, serialisable=false, 
    filter=@Select(clazz=Subject.class))
  private Collection<Subject> subjects;

  /**
   * @effects 
   *  create new object from object form
   */
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Process4SubjectApplicationManager(OrgUnit orgUnit, String processCode,
      Semester semester, Integer year
      , Collection<Subject> subjects
      ) {
    super(orgUnit, processCode, semester, year);
    
    this.subjects = subjects;
  }

  // processCode is a shadowed attribute in the module configuration (placed after semester, year) 
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Process4SubjectApplicationManager(OrgUnit orgUnit,
      Semester semester, Integer year
      , String processCode
      , Collection<Subject> subjects
      ) {
    super(orgUnit, processCode, semester, year);
    
    this.subjects = subjects;
  }

//  public Subject getSubject() {
//    return subject;
//  }
//
//  public void setSubject(Subject subj) {
//    this.subject = subj;
//  }

  /** ASSOCIATION methods: {@link #subjects} */
  public Collection<Subject> getSubjects() {
    return subjects;
  }
//
  public void setSubjects(Collection<Subject> subjects) {
    this.subjects = subjects;
    //subjectsCount = subjects.size();
  }
//
//  @Metadata(type=Metadata.Type.MethodGetAssociationLinkCount)
//  public Integer getSubjectsCount() {
//    return subjectsCount;
//  }
//
//  @Metadata(type=Metadata.Type.MethodSetAssociationLinkCount)
//  public void setSubjectsCount(int subjectCount) {
//    this.subjectsCount = subjectCount;
//  }
//  
//  @Metadata(type=Metadata.Type.MethodValueAdder)
//  @MemberRef(name=A_subjects)
//  public boolean addSubject(Collection<Subject> subjects) {
//    if (this.subjects == null)
//      this.subjects = new ArrayList();
//    
//    boolean updated = false;
//    
//    for (Subject subject : subjects) {
//      if (!this.subjects.contains(subject)) {
//        this.subjects.add(subject);
//      }
//    }
//    
//    return updated;
//  }
//  
//  @Metadata(type=Metadata.Type.MethodValueAdder)
//  @MemberRef(name=A_subjects)
//  public boolean addSubject(Subject subject) {
//    if (this.subjects == null)
//      this.subjects = new ArrayList();
//    boolean updated = false;
//    
//    if (!this.subjects.contains(subject)) {
//      this.subjects.add(subject);
//    }
//    
//    return updated;
//  }
//
//  @Metadata(type=Metadata.Type.MethodValueAdderNew)
//  @MemberRef(name=A_subjects)
//  public boolean addNewSubject(Collection<Subject> subjects) {
//    if (this.subjects == null)
//      this.subjects = new ArrayList();
//    
//    boolean updated = false;
//    
//    this.subjects.addAll(subjects);
//    subjectsCount += subjects.size();
//    
//    return updated;
//  }
//
//  @Metadata(type=Metadata.Type.MethodValueAdderNew)
//  @MemberRef(name=A_subjects)
//  public boolean addNewSubject(Subject subject) {
//    if (this.subjects == null)
//      this.subjects = new ArrayList();
//    
//    boolean updated = false;
//    
//    this.subjects.add(subject);
//    subjectsCount++;
//    
//    return updated;
//  }
//  
//  @Metadata(type = Metadata.Type.MethodValueRemover)
//  @MemberRef(name=A_subjects)  
//  public boolean removeSubject(Subject subject) {
//    boolean removed = subjects.remove(subject);
//    if (removed) {
//      subjectsCount--;
//    }
//    
//    return false;
//  }
//  
//  //TODO: method updater (?)
//  
//  /** end ASSOCIATION: {@link #subjects}*/    
}
