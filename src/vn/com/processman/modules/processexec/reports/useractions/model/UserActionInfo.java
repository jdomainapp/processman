package vn.com.processman.modules.processexec.reports.useractions.model;

import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.controller.helper.indexer.AbstractIndexable;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.security.DomainUser;

/**
 * @overview
 *  An information class that provides aggregated data about a {@link DomainUser} and an {@link Action} that
 *  she performs.  
 *  
 * @author dmle
 *
 * @version 1.2
 */
@DClass(serialisable=false)
public class UserActionInfo extends AbstractIndexable {
  
  public static final String A_actionInfo = "actionInfo";
  public static final String A_userName = "userName";
  public static final String A_taskInfo = "taskInfo";
  
  @DAttr(name = "id", type = Type.Integer, id = true, auto = true, mutable = false, optional = false,length=5)
  private int id;
  private static int idCounter = 0;
  
  // optional
  private DomainUser user;

  // required
  @DAttr(name="action", type=Type.Domain)
  private Action action;
  
  ///// DERIVED
  @DAttr(name=A_taskInfo, type=DAttr.Type.String, length=30, mutable=false)
  private String taskInfo; 
  
  @DAttr(name=A_actionInfo, type=DAttr.Type.String, length=30, mutable=false)
  private String actionInfo;

  @DAttr(name=A_userName, type=DAttr.Type.String, length=DomainUser.LENGTH_NAME, mutable=false)
  private String userName;
  
  @DAttr(name="actionStatus", type=Type.Domain, length=5, mutable=false)
  private StatusCode actionStatus;
  
  /** the number of rows that value of attribute {@link #taskInfo} will span */
  @DAttr(name="taskViewRowSpan", type=DAttr.Type.Integer, mutable=false)
  private int taskViewRowSpan;
  
  @DAttr(name="taskStatus", type=Type.Domain, length=5, mutable=false)
  private StatusCode taskStatus;

  // link attribute
  @DAttr(name="reportUserActions",type=Type.Domain,serialisable=false)
  private ReportUserActions reportUserActions;
  
  public UserActionInfo(DomainUser user, Action action, Integer taskViewRowSpan) {
    id = ++idCounter;
    this.user = user;
    this.action = action;

    this.taskViewRowSpan = taskViewRowSpan;
    
    generateOutput();
  }

  public UserActionInfo(String userName, Action action, Integer taskViewRowSpan) {
    id = ++idCounter;
    this.user = null;
    this.userName = userName;
    this.action = action;
    
    this.taskViewRowSpan = taskViewRowSpan;
    
    generateOutput();
  }
  
  /**
   * @effects 
   *  generate values of the derived attributes of this from {@link #action} and {@link #user} (if it is specified) 
   */
  private void generateOutput() {
    //id = action.getId();

    String actCode = action.getCodeDef();
    String actName = action.getNameDef();
    
    actionInfo = actCode + ". " + actName;
    actionStatus = action.getStatus();
    
    Task task = action.getTask();
    String taskCode = task.getCodeDef();
    String taskName = task.getNameDef();
    Process proc = task.getProcess();
    String procCode = proc.getCodeDef();
    
    taskInfo = procCode + "." + taskCode + ". " + taskName;
    
    taskStatus = task.getStatus();
    
    if (user != null) {
      userName = user.getName();//user.getLogin();
    }
  }
  
  // IMPORTANT: needed to refresh the derived attributes of this when the referenced objects have been changed
  @DOpt(type=DOpt.Type.ObjectStateRefresher)
  public void refresh() {
    generateOutput();
  }

  public int getId() {
    return id;
  }

//  /**
//   * This method is only used by sub-type to override the id (if needed).
//   */
//  protected void setId(int id) {
//    this.id = id;
//  }
  
  public String getUserName() {
    return userName;
  }

  
  public String getActionInfo() {
    return actionInfo;
  }

  /**
   * This method is only used by sub-type to override the id (if needed).
   */
  protected void setActionInfo(String actionInfo) {
    this.actionInfo = actionInfo;
  }

  public Action getAction() {
    return action;
  }
  
  public String getTaskInfo() {
    return taskInfo;
  }

  public StatusCode getActionStatus() {
    return actionStatus;
  }

  public StatusCode getTaskStatus() {
    return taskStatus;
  }

  public ReportUserActions getReportUserActions() {
    return reportUserActions;
  }

  public void setReportUserActions(ReportUserActions rptUserActions) {
    this.reportUserActions = rptUserActions;
  }

  public int getTaskViewRowSpan() {
    return taskViewRowSpan;
  }

  /**
   * This method is used by embedded scripts of the export document 
   */
  public boolean isTaskGroupStart() {
    return taskViewRowSpan > 0;
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
    UserActionInfo other = (UserActionInfo) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()+" (" + getId() + ", " + getActionInfo() + ")";
  }
}
