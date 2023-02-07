package vn.com.processman.modules.processapplication.model;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DOpt;
import jda.mosa.model.DDataValidator;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview
 *  Represent an association between {@link SubjectBySemester} and {@link Action4Subject}.
 *  
 * @author dmle
 */
@DDataValidator(type=ValidateSubjectAction.class)
public class SubjectAction {

  public static final String A_id = "id";
  public static final String A_subject = "subject";
  public static final String A_action = "action";
  public static final String A_subjStatus = "subjStatus";
  public static final String A_output = "output";
  public static final String Assoc_SubjectActionAndFile = "subjectAction-hasoutput-File";
  
  /**
   * non-id, essential attributes neede for the non-id constructor to create objects
   */
  public static final String[] A_EssentialCreateNews = {
    A_subject, A_action
  };

  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;

  /**the {@link SubjectBySemester} object */
  @DAssoc(ascName = SubjectBySemester.Assoc_SubjectBySemesterAndSubjectAction, role = "subjectAction", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = SubjectBySemester.class, cardMin = 1, cardMax = 1))
  @DAttr(name=A_subject,type=Type.Domain, optional=false)
  private SubjectBySemester subject;
  
  /**association {@link Action4Subject#Assoc_Action4SubjectAndSubjectAction}*/
  @DAssoc(ascName = Action4Subject.Assoc_Action4SubjectAndSubjectAction, role = "subjectAction", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Action4Subject.class, cardMin = 1, cardMax = 1),
      dependsOn=true)
  @DAttr(name=A_action,type=Type.Domain, optional=false)
  private Action4Subject action;
  
  /** the {@link FileWrapper} which contains the output of this*/
  @DAssoc(ascName = Assoc_SubjectActionAndFile, role = "subjectAction", 
      ascType = AssocType.One2One, endType = AssocEndType.One, 
      associate = @Associate(type = FileWrapper.class, cardMin = 0, cardMax = 1))  
  @DAttr(name=A_output, type=Type.Domain)
  private FileWrapper output;
  
  @DAttr(name=A_subjStatus,type=Type.Domain)
  private StatusCode subjStatus;
  
  // constructor: from data source
  public SubjectAction(Integer id, SubjectBySemester subject, Action4Subject action, FileWrapper output, StatusCode subjStatus) {
    this.id = nextID(id);
    this.subject = subject;
    this.action = action;
    this.output= output;
    this.subjStatus = subjStatus;
  }

  // constructor: from object form
  public SubjectAction(SubjectBySemester subject, Action4Subject action, FileWrapper output, StatusCode subjStatus) {
    this(null, subject, action, output, subjStatus);
  }

  // constructor: from object form (no output, subjStatus)
  public SubjectAction(SubjectBySemester subject, Action4Subject action
      //, File output, StatusCode subjStatus
      ) {
    this(null, subject, action
        //, output
        ,null
        //, subjStatus
        ,null
        );
  }
  
  public SubjectBySemester getSubject() {
    return subject;
  }

  public void setSubject(SubjectBySemester subject) {
    this.subject = subject;
  }

  public Action4Subject getAction() {
    return action;
  }

  public void setAction(Action4Subject action) {
    this.action = action;
  }

  public FileWrapper getOutput() {
    return output;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_output)
  public void setNewOutput(FileWrapper output) {
    setOutput(output);
  }
  
  public void setOutput(FileWrapper output) {
    this.output = output;
    
    
  }

  /**
   * @effects 
   *  if {@link #output} is not null
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean hasOutput() {
    return output != null;
  }
  
  /**
   * @effects 
   *  if <tt>subjStatus != null</tt> and is equal <tt>status</tt>
   *    return true
   *  else
   *    return false
   * @version 1.3
   */
  public boolean isSubjStatusEqual(StatusCode status) {
    return (subjStatus != null && status != null && subjStatus.equals(status));
  }
  
  /**
   * @effects 
   *  if this.{@link #subjStatus} is {@link StatusCode#Done}
   *    return true
   *  else
   *    return false
   * @version 1.3
   */
  public boolean isSubjStatusDone() {
    return isSubjStatusEqual(StatusCode.Done);
  }

  public StatusCode getSubjStatus() {
    return subjStatus;
  }

  public void setSubjStatus(StatusCode subjStatus) {
    this.subjStatus = subjStatus;
  }

  /**
   * This method is invoked automatically by the system to refresh the state of this. 
   * We use this mechanism to update subjStatus.
   * 
   * @requires 
   *  this is an active object in {@link #action}
   * 
   *  @effects 
   *    ask {@link #action} to evaluate status of the active {@link SubjectAction} (which is this) and 
   *    update {@link #subjStatus} accordingly
   */
  @DOpt(type=DOpt.Type.ObjectStateRefresher)
  public boolean refresh() {
    /*v1.2: moved to method 
    if (action.getActiveSubjectAction() != this) {
      // pre-condition not satisfied
      return false;
    }
    
    StatusCode s = action.evaluateSubjStatus();
    
    if (!s.equals(getSubjStatus())) {
      setSubjStatus(s);
      return true;
    } else {
      return false;
    }
    */
    //return evaluateSubjStatus();
    boolean canBeDone = evaluateSubjStatusSimulator();
    return false;
  }
  
  /**
   * <b>Note</b>: use the static method {@link #evaluateSubjStatus(SubjectAction)} instead of this method to initiate the actual evaluation procedure.
   * This method is used to perform the main part of that procedure.
   *    
   * @requires 
   *  this is an active object in {@link #action}
   * 
   * @modifies this.{@link #subjStatus}, {@link #action}
   * 
   *  @effects 
   *    ask {@link #action} to evaluate status of the active {@link SubjectAction} (which is this) and 
   *    then update {@link #subjStatus} accordingly
   *
   *    <p>if {@link #subjStatus} is changed return <tt>true</tt>, else return <tt>false</tt>
   *    
   * @version 1.2
   */
  public boolean evaluateSubjStatus() {
    if (action.getActiveSubjectAction() != this) {
      // pre-condition not satisfied
      return false;
    }
    
    StatusCode s = action.evaluateSubjStatus();
    
    StatusCode oldStatus = getSubjStatus();
    if (oldStatus == null || !s.equals(oldStatus)) {
      setSubjStatus(s);
      return true;
    } else {
      return false;
    }    
  }

  /**
   * This method is similar to {@link #evaluateSubjStatus()} except that it does not modify {@link #subjStatus}. 
   * 
   * <br><b>Note</b>: use the static method {@link #evaluateSubjStatusSimulator(SubjectAction)} instead of this method to initiate the actual evaluation procedure.
   * This method is used to perform the main part of that procedure.
   *    
   * @requires 
   *  this is an active object in {@link #action}
   * 
   * @modifies this.{@link #action}
   * 
   *  @effects 
   *    ask {@link #action} to evaluate status of the active {@link SubjectAction} (which is this)
   *
   *    <p>if result is {@link StatusCode#Done} return <tt>true</tt>, else return <tt>false</tt>
   *    
   * @version 1.2
   */
  public boolean evaluateSubjStatusSimulator() {
    if (action.getActiveSubjectAction() != this) {
      // pre-condition not satisfied
      return false;
    }
    
    StatusCode s = action.evaluateSubjStatus();
    
    if (s != null && s.equals(StatusCode.Done)) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * This method is similar to {@link #evaluateSubjStatus(SubjectAction)} except that it does not modify <tt>subjAct, subjAct.action</tt>  
   * 
   * @modifies subjAct.{@link #action}
   * 
   * @effects 
   *  check (without actually changing the value) if <tt>subjAct.subjStatus</tt> can be set to {@link StatusCode#Done},
   *  if so then return <tt>true</tt>, else return <tt>false</tt> 
   *  
   *  <p>set active-subject-action in subjAct.{@link #action} to <tt>subjAct</tt>
   *   
   *  <p>the result of each pre- and post-condition in subjAct.{@link #action} is updated accordingly.
   * @version 1.2
   */
  public static boolean evaluateSubjStatusSimulator(SubjectAction subjAct) {
    Action4Subject action = subjAct.getAction();
    
    // (1) set active subject action on the Action object
    SubjectAction formerActive = action.setActiveSubjectAction(subjAct);
    
    // (2) carry out the evaluation using subjAct (but without changing subjAct.subjStatus)
    boolean canBeDone = subjAct.evaluateSubjStatusSimulator();
    
    // reset active subject action
    //action.setActiveSubjectAction(formerActive);
    
    return canBeDone;
  }
  
  /**
   * This is a short-cut for {@link #evaluateSubjStatus(SubjectAction, boolean)}.
   * 
   * @modifies subjAct, subjAct.action.activeSubjectAction
   * 
   * @effects 
   *  return {@link #evaluateSubjStatus(SubjectAction, boolean)}<tt>(subjAct, true)</tt>
   * @version 1.2
   */
  public static boolean evaluateSubjStatus(SubjectAction subjAct) {
    boolean updateActiveSubjAction = true;
    return evaluateSubjStatus(subjAct, updateActiveSubjAction);
  }
  
  /**
   * This method performs the actual evaluation procedure for {@link SubjectAction#subjStatus}.
   * 
   * <p>The evaluation of {@link #subjStatus} is a bit more complex that other evaluations, in that it 
   * relies on evaluating the pre- and post-conditions of the concerned {@link #action}.
   * 
   * @requires 
   *  subjAct != null
   *  
   * @modifies subjAct, subjAct.action.activeSubjectAction
   * 
   * @effects 
   *  carry out the evaluation procedure for <tt>subjAct.subjStatus</tt>.
   *  
   *  <p>the result of each pre- and post-condition in subjAct.action is updated accordingly.
   *  
   *  <p>if <tt>updateActiveSubjAction = true</tt> then set <tt>subjAct.action.activeSubjectAction = subjAct</tt>
   *  
   *  <p>if <tt>subjAct.subjStatus</tt> is changed
   *      return true
   *    else 
   *      return false
   *
   * @version 1.2
   */
  private static boolean evaluateSubjStatus(SubjectAction subjAct, boolean updateActiveSubjAction) {
    Action4Subject action = subjAct.getAction();
    
    // (1) set active subject action on the Action object
    SubjectAction formerActive = action.setActiveSubjectAction(subjAct);
    
    // (2) carry out the evaluation using subjAct
    boolean updated = subjAct.evaluateSubjStatus();
    
    // reset active subject action (if needed)
    if (!updateActiveSubjAction) {
      action.setActiveSubjectAction(formerActive);
    }
    
    return updated;
  }

  /**
   * @modifies {@link #subjStatus}, {@link #action}
   * 
   * @effects 
   *  if {@link #subjStatus} = null
   *    evaluate it (thereby also making <tt>this</tt> the active-subject-action of {@link #action})
   *    
   *  <p>return {@link #subjStatus}
   *  
   * @version 1.2
   */
  public StatusCode ensureStatusIsSet() {
    if (subjStatus == null) {
      SubjectAction.evaluateSubjStatus(this, true);
    }
    
    return subjStatus;
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
    SubjectAction other = (SubjectAction) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SubjectAction (" + id + ", " + subject + ", " + action + ", "
        + subjStatus + ")";
  }
}
