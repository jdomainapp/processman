package vn.com.processman.modules.processexec.forsubject.model;

import jda.modules.dcsl.syntax.DClass;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.mosa.view.assets.panels.DefaultPanel;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processexec.base.model.ProcessExecution;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  A sub-type of {@link ProcessExecution} that defines a few scope definition extensions 
 *  for module used for performing a {@link Process}. 
 *  
 * @author dmle
 */
@DClass(serialisable=false)
public class ProcessExecution4Subject extends ProcessExecution {
  
  public ProcessExecution4Subject(Integer id, OrgUnit orgUnit,
      String processName, Semester semester, Integer year) {
    super(id, orgUnit, processName, semester, year);
  }


  public ProcessExecution4Subject(OrgUnit orgUnit, String processName,
      Semester semester, Integer year) {
    super(orgUnit, processName, semester, year);
  }

  /**extended scope definitions */
  public static final ScopeDef ScopeDefProcessExec = new ScopeDef(
      Process.class,
      new String[] {Process.A_codeDef, Process.A_nameDef, Process.A_tasks},Boolean.FALSE); // end ScopeDefProcess 

  public static final ScopeDef ScopeDefTask4SubjectExec = new ScopeDef(
      Task4Subject.class,
      new String[] {Task.A_id, Task.A_codeDef, Task.A_nameDef, Task.A_process, Task.A_status, 
          Task.A_actions 
          ,Task4Subject.A_subjectTasks
        },Boolean.FALSE); // end ScopeDefTask
  

  public static final ScopeDef ScopeDefAction4SubjectExec = new ScopeDef(
    Action4Subject.class,
    new String[] {
      Action.A_codeDef, Action.A_nameDef, //Action.A_task, 
      Action.A_preConds,
      //Action.A_output, 
      Action.A_postConds,
      Action.A_status,
      Action4Subject.A_subjectActions
      },Boolean.FALSE); // end ScopeDefAction4Subject

  public static final ScopeDef ScopeDefSubjectTaskExec = new ScopeDef(
      SubjectTask.class,
      new String[] {SubjectTask.A_subject, SubjectTask.A_subjStatus},
      Boolean.FALSE, DefaultPanel.class);
  
  public static final ScopeDef ScopeDefSubjectActionExec = Action4Subject.ScopeDefSubjectActionExec;
      /*v1.2: use shared definition
      new ScopeDef(
      SubjectAction.class,
      new String[] {"*"},Boolean.FALSE) {
    @Override // enable SubjectAction.A_subjStatus
    public Boolean isEditable(String attribName) {
      if (attribName.equals(SubjectAction.A_subjStatus)) {
        return Boolean.TRUE;
      } else {
        return null;
      }
    }
  };
  */
  
  public static final ScopeDef ScopeDefFileExec = new ScopeDef(
      FileWrapper.class,
      FileWrapper.A_EssentialCreateNews,Boolean.TRUE); // end ScopeDefFileExec
  
  // no other design spec beyound this point
}
