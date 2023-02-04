package vn.com.processman.modules.processapplication.model;

import java.util.Collection;

import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Tuple;

/**
 * @overview
 *  Represent an association between {@link SubjectBySemester} and {@link Task4Subject}.
 *  
 * @author dmle
 */
public class SubjectTask {

  public static final String A_subject = "subject";
  public static final String A_task = "task";
  public static final String A_subjStatus = "subjStatus";
  public static final String A_baseSubject = "baseSubject";
  
  public static final String[] A_EssentialCreateNews = {
    A_subject, A_task
  };

  @DAttr(name="id", type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;

  /**
   *  derived from {@link #subject} 
   */
  @DAttr(name=A_baseSubject,type=Type.Domain, auto=true, optional=false, serialisable=false)
  private Subject baseSubject;
  
  /**association with {@link SubjectBySemester} */
  @DAssoc(ascName = SubjectBySemester.Assoc_SubjectBySemesterAndSubjectTask, role = "subjectTask", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = SubjectBySemester.class, cardMin = 1, cardMax = 1),
      dependsOn=true)
  @DAttr(name=A_subject,type=Type.Domain, optional=false)
  private SubjectBySemester subject;
  
  /**association {@link Task4Subject#Assoc_Task4SubjectAndSubjectTask}*/
  @DAssoc(ascName = Task4Subject.Assoc_Task4SubjectAndSubjectTask, role = "subjectTask", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Task4Subject.class, cardMin = 1, cardMax = 1),
      dependsOn=true)
  @DAttr(name=A_task,type=Type.Domain, optional=false)
  private Task4Subject task;
  
  @DAttr(name=A_subjStatus,type=Type.Domain)
  private StatusCode subjStatus;
  
  // constructor: from data source
  public SubjectTask(Integer id, SubjectBySemester subject, Task4Subject task, StatusCode subjStatus) {
    this.id = nextID(id);
    this.subject = subject;
    this.task = task;
    this.subjStatus = subjStatus;
    
    // derived attribute
    baseSubject = subject.getSubject();
  }

  // constructor: from object form
  public SubjectTask(SubjectBySemester subject, Task4Subject task, StatusCode subjStatus) {
    this(null, subject, task, subjStatus);
  }

  // constructor: from nested object form (no status)
  public SubjectTask(SubjectBySemester subject, Task4Subject task
      //, StatusCode subjStatus
      ) {
    this(null, subject, task,
        //subjStatus
        null
        );
  }
  
  public SubjectBySemester getSubject() {
    return subject;
  }

  public void setSubject(SubjectBySemester subject) {
    this.subject = subject;
    
    // update derived attribute
    baseSubject = subject.getSubject();
  }

  public Subject getBaseSubject() {
    return baseSubject;
  }

//  public void setBaseSubject(Subject baseSubject) {
//    this.baseSubject = baseSubject;
//    
//    // set subject.subject = baseSubject
//    if (subject != null) { // just to be sure
//      subject.setSubject(baseSubject);
//    }
//  }

  public Task4Subject getTask() {
    return task;
  }

  public void setTask(Task4Subject task) {
    this.task = task;
  }

  public StatusCode getSubjStatus() {
    return subjStatus;
  }

  public void setSubjStatus(StatusCode subjStatus) {
    this.subjStatus = subjStatus;
  }

  /**
   * This method is invoked automatically by the system to refresh the state of this. 
   * We use this mechanism to perform {@link #evaluateSubjStatus()} and update {@link #status}
   */
  @DOpt(type=DOpt.Type.ObjectStateRefresher)
  public boolean refresh() {
    boolean statusChanged = evaluateSubjStatus();
    return statusChanged;
  }
  
  /**
   * This method evaluates {@link #subjStatus} for {@link #subject} based on the 
   * <tt>subjStatus</tt> of all {@link SubjectAction}s concerning {@link #subject} 
   * in every {@link Action4Subject} of {@link #task}.  
   * 
   * @requires 
   *  {@link #task} is populated with all {@link Action4Subject} AND 
   *      each of such {@link Action4Subject} is populated with all {@link SubjectAction} AND 
   *        each of such {@link SubjectAction}<tt>.subjStatus</tt> has been evaluated 
   *  
   * @modifies subjStatus
   * @effects <pre>
   *  let StatusCode s
   *  for each {@link Action4Subject} a in {@link #task}  
   *      for each {@link SubjectAction} j in a. j.subject = {@link #subject}  
   *        if j.subjStatus is {@link StatusCode#NotDone}
   *          s = NotDone
   *          stop
   *   
   *   {@link #subjStatus} = s
   *   
   *   if {@link #subjStatus} is changed
   *    return true
   *   else
   *    return false
   *   </pre>
   */
  public boolean evaluateSubjStatus() {
    Collection<Action> actions = task.getActions();
    
    if (actions.isEmpty()) {
      // pre-condition violated
      return false;
    }

    // evaluate (or re-evaluate) the status each time this method this invoked because the statuses of the relevant 
    // SubjectActions may have been updated since the last invocation
    
    StatusCode s = null; //StatusCode.Done;
    
    Action4Subject a;
    Collection<SubjectAction> subjActions;
    
    OUTER: for (Action ac : actions) {
      a = (Action4Subject) ac;
      subjActions = a.getSubjectActions();
      
      /* v1.3: removed this restriction
      if (subjActions.isEmpty()) {
        // precondition violated
        return false;
      }
      */
      
      for (SubjectAction j : subjActions) {
        if (j.getSubject().equals(subject)) {
          // found matching
          if (//j.getSubjStatus().equals(StatusCode.NotDone)
              !j.isSubjStatusEqual(StatusCode.Done)
              ) {
            s = StatusCode.NotDone; 
            break OUTER;
          } else {
            s = StatusCode.Done;
            break;
          }
        }
      }
    }
    
    if ((s != null && (subjStatus == null || !subjStatus.equals(s))) 
        || (s == null && subjStatus != null)  // this case rarely happens but included for completeness
        ){
      subjStatus = s;
      return true;
    } else {
      return false;
    }
  }
  
  public int getId() {
    return id;
  }

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
    SubjectTask other = (SubjectTask) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SubjectTask (" + id + ", " + subject + ", " + task + ")";
  }
}
