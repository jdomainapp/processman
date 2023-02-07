/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.processmanager.processapplication.forsubject.controller.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processmanager.processapplication.controller.command.CreateNewProcessApplicationCommand;
import vn.com.processman.modules.processmanager.processapplication.forsubject.model.Process4SubjectApplicationManager;
import vn.com.processman.modules.processmanager.processapplication.model.ProcessApplicationManager;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  A {@link DataControllerCommand} that is used to create a new {@link ProcessApplication} from 
 *  details provided in a {@link ProcessApplicationManager} <tt>m</tt> as follows:
 *  <pre>
 *    retrieve the original {@link Process} p that fits the description (i.e. process code)
 *    let subj := m.subj
 *    create derived {@link Process} p' from p 
 *    for each {@link Task4Subject} t of p
 *      create in p' a derived {@link Task4Subject} t' of t st: t'.def=t /\ t'.prev=derived(t.prev) /\ t'.process = p'
 *      create in t' new {@link SubjectTask} k st: k.task=t' /\ k.subject=subj
 *      for each {@link Action4Subject} a of t
 *        create in t' a derived {@link Action4Subject} a' of a st: a'.def=a /\ a'.prev=derived(a.prev) /\ a'.task = t'
 *        create in a' new {@link SubjectAction} c st: c.action=a' /\ c.subject=subj
 *      
 *    create {@link ProcessApplication} n for p' from m.orgUnit, semester, year
 *  </pre> 
 *   
 *  Open derived process <tt>p'</tt> for viewing. 
 *  
 * @author dmle
 */
public class CreateNewProcess4SubjectCommand<C> extends CreateNewProcessApplicationCommand {

  // these attributes are to reduce performance overhead
  
  private DataController<Process> dctl;
  private DataController<Task> tdctl;
  private DataController<Action> adctl;
  private DataController<SubjectTask> sdctl;
  private DataController<SubjectAction> sadctl;
  private DataController<SubjectBySemester> jdctl;
  
  private Boolean oldSubjTaskPopUp;
  private Boolean oldSubjActionPopUp;
  private Boolean oldSubjBySemPopUp;
  private List<SubjectBySemester> subjectBySems;
  
  /**
   * @effects 
   * 
   */
  public CreateNewProcess4SubjectCommand(DataController dctl) {
    super(dctl);
  }

  @Override
  protected void validateInput(ProcessApplicationManager m)
      throws ConstraintViolationException {
    super.validateInput(m); // v1.2: added this
    
    Process4SubjectApplicationManager mj = (Process4SubjectApplicationManager) m;

    // check that subjects are selected on the Subject sub-form
    Collection<Subject> subjects = mj.getSubjects();
    
    if (subjects == null) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED, 
          new Object[] {Process4SubjectApplicationManager.A_subjects, null});
    }
  }

  @Override
  protected void beforeCreateNewDerivedProcess(ProcessApplicationManager m) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    if (dctl == null) dctl = getDataController();
    if (tdctl == null) tdctl = dctl.getChildController(Process.A_tasks);
    if (adctl == null) adctl = tdctl.getChildController(Task.A_actions);
    if (sdctl == null) sdctl = tdctl.getChildController(Task4Subject.A_subjectTasks);
    if (sadctl == null) sadctl = adctl.getChildController(Action4Subject.A_subjectActions); 
    if (jdctl == null) jdctl = lookUpRootDataController(SubjectBySemester.class);
    
    // temporarily shut pop-up message for creating objects
    oldSubjTaskPopUp = setPropertyShowPopUpMessage(sdctl, Boolean.FALSE);
    oldSubjActionPopUp = setPropertyShowPopUpMessage(sadctl, Boolean.FALSE);
    oldSubjBySemPopUp = setPropertyShowPopUpMessage(jdctl, Boolean.FALSE);
    
    // retrieve (create if needed) SubjectBySemester(s) of the specified Subjects
    Process4SubjectApplicationManager mj = (Process4SubjectApplicationManager) m;
    Collection<Subject> subjects = mj.getSubjects();
    Semester semester = mj.getSemester();
    Integer year = mj.getYear();
    
    DODMBasic dodm = dctl.getDodm();
    
    if (subjectBySems == null)
      subjectBySems = new ArrayList();
    
    SubjectBySemester subjBySem;
    for (Subject subj : subjects) {
      // if already exist SubjectBySemester matching the specified input then use it; otherwise create new
      subjBySem = DomainToolKit.retrieveSubjectBySemester(dodm, subj, semester, year); 
      if (subjBySem == null) 
        subjBySem = createNewObject(jdctl, new Object[] {subj, semester, year}, false);
      
      subjectBySems.add(subjBySem);
    } 
  }

  @Override
  protected void afterCreateNewDerivedProcess(ProcessApplicationManager m) {
    // reset pop-up message
    if (sdctl != null) setPropertyShowPopUpMessage(sdctl, oldSubjTaskPopUp);
    if (sadctl != null) setPropertyShowPopUpMessage(sadctl, oldSubjActionPopUp);
    if (jdctl != null) setPropertyShowPopUpMessage(jdctl, oldSubjBySemPopUp);
    
    subjectBySems = null;
  }

  @Override
  protected void postCreateTask(ProcessApplicationManager m,
      DataController tdctl, Task td) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // apply td to each selected Subject in m

    Object[] subjTaskValues = new Object[2];
    subjTaskValues[1] = td;
    for (SubjectBySemester subjBySem: subjectBySems) {
      // apply to td
      subjTaskValues[0] = subjBySem;
      createNewObject(sdctl, subjTaskValues);
    } // end for: Subject
  }

  @Override
  protected void postCreateAction(ProcessApplicationManager m,
      DataController adctl, Action ad) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // apply ad to each selected Subject in m
    
    
    Object[] subjActionValues = new Object[2];
    subjActionValues[1] = ad;
    for (SubjectBySemester subjBySem: subjectBySems) {
      subjActionValues[0] = subjBySem;
      createNewObject(sadctl, subjActionValues);
    } // end for: Subject
  }
}
