/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.processmanager.processapplication.forsubject.controller.command;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processmanager.processapplication.forsubject.model.Process4SubjectApplicationManager;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.model.Semester;
import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.ControllerBasic.DataController;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.util.collection.CollectionToolkit;

/**
 * @overview
 *  Performs operation Update which checks for changes to user input and either create new or update 
 *  the existing {@link ProcessApplication}.
 *  
 *  <p>If user selected new {@link Subject}(s) while keeping other attributes unchanged then
 *  update the existing {@link Process} to apply to the selected subjects.
 *  
 *  <p>If user changed other attributes (e.g. semester, year, process code) then re-open the sub-form({@link Process})
 *  to display new result (if any).
 *  
 * @author dmle
 */
public class UpdateProcess4SubjectApplicationCommand<C> extends DataControllerCommand {

  /**
   * @effects 
   * 
   */
  public UpdateProcess4SubjectApplicationCommand(DataController dctl) {
    super(dctl);
  }

  /**
   * @effects
   * If user selected new {@link Subject}(s) while keeping other attributes unchanged then
   *  update the existing {@link Process} to apply to the selected subjects.
   *  
   *  <p>If user changed other attributes (e.g. semester, year, process code) then re-open the sub-form({@link Process})
   *  to display new result (if any).
   *  
   */
  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DODMBasic dodm = getDodm();
    
    DataController<Process4SubjectApplicationManager> dctl = getDataController();
    DataController<Process> pdctl = dctl.getChildController(Process4SubjectApplicationManager.A_processes);

    Process4SubjectApplicationManager m = dctl.getCurrentObject();
    
    // old subjects value (to check later): must get this before update (below)
    Collection<Subject> oldSubjs = m.getSubjects();
    
    // perform an update as usual
    Map<DAttr,Object> affectedValMap = dctl.updateObject();

    // there is an active process
    if (affectedValMap != null) { // should not need this check but just in case...
      Collection<Subject> newInputSubjs = null;
      boolean otherAttributes = false;

      DAttr attrib;
      Object val;
      for (Entry<DAttr,Object> valEntry : affectedValMap.entrySet()) {
        attrib = valEntry.getKey();
        if (attrib.name().equals(Process4SubjectApplicationManager.A_subjects)) {
          // subjects were changed: 
          newInputSubjs = m.getSubjects(); // the updated subjects
        } else {
          // other attributes
          if (!otherAttributes) otherAttributes = true;
        }
      }
      
      // now determine what to do with the result
      if (newInputSubjs != null && !otherAttributes) {
        // subjects were changed while other attributes are unchanged
        // if there is a process is being edited then determine if we can apply the process to new subjects
        if (!pdctl.isCurrentObjectNull()) {
          // confirm with user first
          Process p = pdctl.getCurrentObject();
  
          ControllerBasic ctl = dctl.getCreator();
          boolean confirmed = ctl.displayConfirmFromCode(vn.com.processman.util.message.DomainMessageCode.
              CONFIRM_APPLY_SELECTED_SUBJECTS_TO_PROCESS, dctl, new Object[] {p.getCodeDef()});
          
          if (!confirmed) {
            // stop here
            return;
          }
  
          // apply the same process to any new Subjects that were specified (old Subjects are skipped)
          Collection<Subject> newSubjs = CollectionToolkit.createCollectionFromDisjoint(newInputSubjs, oldSubjs);
          if (newSubjs != null) {
            Semester sem = m.getSemester();
            Integer year = m.getYear();
            applyExistingProcessToSubjects(pdctl, newSubjs, sem, year);
          }
        }
      } else if (otherAttributes) {
        // other attributes were changed: reopen
        pdctl.reopen();
      }
    }
  }

  /**
   * @requires 
   *  subjects != null /\ this.dataController displays a {@link Process}
   *   
   * @effects 
   *  apply {@link Task} and {@link Action} of {@link Process} <tt>pd</tt> to each {@link Subject} in <tt>subjects</tt> and to <tt>semester, year</tt>
   *    
   */
  public void applyExistingProcessToSubjects(
      DataController<Process> dctl, 
      final Collection<Subject> subjects, 
      final Semester semester, final int year) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    //DataController<Process> dctl = getDataController();
    DODMBasic dodm = dctl.getDodm(); 
    DataController<Task> tdctl = dctl.getChildController(Process.A_tasks);
    DataController<Action> adctl = tdctl.getChildController(Task.A_actions);

    DataController<SubjectTask> sdctl = tdctl.getChildController(Task4Subject.A_subjectTasks);
    DataController<SubjectAction> sadctl = adctl.getChildController(Action4Subject.A_subjectActions);
    DataController<SubjectBySemester> jdctl = lookUpRootDataController(SubjectBySemester.class);
    
    // temporarily shut pop-up message for creating objects
    Boolean oldSubjTaskPopUp = setPropertyShowPopUpMessage(sdctl, Boolean.FALSE);
    Boolean oldSubjActionPopUp = setPropertyShowPopUpMessage(sadctl, Boolean.FALSE);
    Boolean oldSubjBySemPopUp = setPropertyShowPopUpMessage(jdctl, Boolean.FALSE);

    //Collection<Task> tasks = pd.getTasks();
    //Collection<Action> actions;
    Task td;
    Object[] subjTaskValues = new Object[2];
    Object[] subjActionValues = new Object[2];
    for (Subject subj : subjects) {
      // if already exist SubjectBySemester matching the specified input then use it; otherwise create new
      SubjectBySemester subjBySem = DomainToolKit.retrieveSubjectBySemester(dodm, subj, semester, year); 
      if (subjBySem == null) 
        subjBySem = createNewObject(jdctl, new Object[] {subj, semester, year}, false);
      
      // browse through each Task of pd and each Action of these Tasks
      tdctl.firstAndWait();
      td = tdctl.getCurrentObject();
      subjTaskValues[0] = subjBySem;
      subjTaskValues[1] = td;
      
      subjActionValues[0] = subjBySem;
      applyTaskToSubject(tdctl, adctl, sdctl, sadctl, td, subjBySem, subjTaskValues, subjActionValues);
      
    } // end for: Subject

    // reset the child data controllers to display the first object
    tdctl.first();
    
    // reset pop-up message
    setPropertyShowPopUpMessage(sdctl, oldSubjTaskPopUp);
    setPropertyShowPopUpMessage(sadctl, oldSubjActionPopUp);
    setPropertyShowPopUpMessage(jdctl, oldSubjBySemPopUp);
  }

  /**
   * @effects 
   *
   * @version
   */
  private void applyTaskToSubject(DataController<Task> tdctl, DataController<Action> adctl, 
      DataController<SubjectTask> sdctl, DataController<SubjectAction> sadctl, 
      Task td, SubjectBySemester subjBySem, 
      final Object[] subjTaskValues,
      final Object[] subjActionValues) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // create a SubjectTask for the specified subject
    createNewObject(sdctl, subjTaskValues);
    
    // create a SubjectAction for each Action of td for the specified subject
    adctl.firstAndWait();
    Action ad = adctl.getCurrentObject();
    // should not need this check but just in case
    if (ad != null) {
      subjActionValues[1] = ad;
      applyActionToSubject(adctl, sadctl, ad, subjBySem, subjActionValues);
    }
    // recursive: move to next Task and apply
    if (tdctl.getBrowser().hasNext()) {
      tdctl.nextAndWait();
      td = tdctl.getCurrentObject();
      subjTaskValues[1] = td;
      applyTaskToSubject(tdctl, adctl, sdctl, sadctl, td, subjBySem, subjTaskValues, subjActionValues);
    }
  }

  /**
   * @effects 
   *
   * @version
   */
  private void applyActionToSubject(DataController<Action> adctl,
      DataController<SubjectAction> sadctl, Action ad, SubjectBySemester subjBySem, 
      final Object[] subjActionValues) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    createNewObject(sadctl, subjActionValues);    
    
    // recursive: move to next Action and apply
    if (adctl.getBrowser().hasNext()) {
      adctl.nextAndWait();
      ad = adctl.getCurrentObject();
      subjActionValues[1] = ad;
      applyActionToSubject(adctl, sadctl, ad, subjBySem, subjActionValues);
    }
  }

}
