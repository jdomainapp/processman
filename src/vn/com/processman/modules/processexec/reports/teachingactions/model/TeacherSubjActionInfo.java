package vn.com.processman.modules.processexec.reports.teachingactions.model;

import java.awt.Color;

import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processexec.reports.useractions.model.UserActionInfo;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.security.DomainUser;
import domainapp.basics.view.tables.model.TableDataRow;

/**
 * @overview
 *  A sub-type of {@link UserActionInfo} that provides extra information relating to the {@link Subject}s that are
 *  involved in the action. 
 *  
 * @author dmle
 *
 * @version  1.2
 */
@DClass(serialisable=false)
public class TeacherSubjActionInfo extends UserActionInfo implements TableDataRow {
  
  private static final Color Aggregated_Bg_Color = new Color(255, 255, 153);

  public static final String A_subjectCode = "subjectCode";
  
  @DAttr(name="subjectAction", type=DAttr.Type.Domain)
  private SubjectAction subjectAction;
  
  // DERIVED
  @DAttr(name=A_subjectCode, type=DAttr.Type.String, length=Subject.LENGTH_CODE, mutable=false)
  private String subjectCode;

  /**either the status of {@link #subjectAction} or that of super.{@link #action} */
  @DAttr(name="status", type=DAttr.Type.Domain, length=5, mutable=false)
  private StatusCode status;

  /** the number of rows that value of attribute {@link #actionInfo} will span */
  @DAttr(name="actionViewRowSpan", type=DAttr.Type.Integer, mutable=false)
  private int actionViewRowSpan;
  
  /** the number of rows that value of attribute {@link #userName} will span */
  @DAttr(name="userViewRowSpan", type=DAttr.Type.Integer, mutable=false)
  private int userViewRowSpan;

  /**
   * @effects 
   * 
   */
  public TeacherSubjActionInfo(DomainUser user, Action4Subject action, SubjectAction subjectAction, 
      Integer actViewRowSpan, Integer userViewRowSpan) {
    super(user, action, 
        -1 // taskViewRowSpan
        );
    
    this.subjectAction = subjectAction;
    
    this.actionViewRowSpan = actViewRowSpan;
    this.userViewRowSpan = userViewRowSpan;
    
    generateOutput();
  }
  
  private void generateOutput() {
    // override id attribute value: set it to subjectAction.id
    //setId(subjectAction.getId());
    
    // override actionInfo: to include both task and action
    Action4Subject action = (Action4Subject) getAction();
    String actCode = action.getCodeDef();
    String actName = action.getNameDef();
    
    Task task = action.getTask();
    //String taskCode = task.getCodeDef();
    //String taskName = task.getNameDef();
    Process proc = task.getProcess();
    String procCode = proc.getCodeDef();
    
    String actionInfo = procCode + "." + actCode + ". " + actName;
    setActionInfo(actionInfo);
    
    if (subjectAction != null) {
      subjectCode = subjectAction.getSubject().getCode();
      
      // use subject status
      status = subjectAction.getSubjStatus();
    } else if (action != null) {
      // use action status
      status = getActionStatus();
    }
  }

  public SubjectAction getSubjectAction() {
    return subjectAction;
  }

  public String getSubjectCode() {
    return subjectCode;
  }

  public StatusCode getStatus() {
    return status;
  }
  
  /**
   * This method is used by embedded scripts of the export document 
   */
  public boolean isAggregated() {
    return subjectAction == null;
  }
  
  public int getActionViewRowSpan() {
    return actionViewRowSpan;
  }

  /**
   * This method is used by embedded scripts of the export document 
   */
  public boolean isActionGroupStart() {
    return actionViewRowSpan > 0;
  }
  
  public int getUserViewRowSpan() {
    return userViewRowSpan;
  }

  /**
   * This method is used by embedded scripts of the export document 
   */
  public boolean isUserGroupStart() {
    return userViewRowSpan > 0;
  }
  
  // IMPORTANT: needed to refresh the derived attributes of this when the referenced objects have been changed
  @DOpt(type=DOpt.Type.ObjectStateRefresher)
  public void refresh() {
    super.refresh();
    
    generateOutput();
  }

  /**
   * @effects
   *  return a colour to differentiate b/w aggregated and non-aggregated objects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.tables.model.TableDataRow#getBgColor()
   */
  @Override
  public Color getBgColor() {
    if (isAggregated()) {
      return Aggregated_Bg_Color;
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see domainapp.basics.view.tables.model.TableDataRow#getFgColor()
   */
  @Override
  public Color getFgColor() {
    return null; // use default
  }
}
