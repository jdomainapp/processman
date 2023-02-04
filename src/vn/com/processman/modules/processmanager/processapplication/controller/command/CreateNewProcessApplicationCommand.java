/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.processmanager.processapplication.controller.command;

import java.util.Collection;
import java.util.Map;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processmanager.processapplication.model.ProcessApplicationManager;
import vn.com.processman.modules.processmanager.processapplication.model.ValidateNewProcessApplication;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.model.Semester;
import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.controller.util.MessageCode;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.ControllerBasic.DataController;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.model.util.ObjectComparator;
import domainapp.basics.model.util.ObjectComparator.SortBy;

/**
 * @overview
 *  A {@link DataControllerCommand} that is used to create a new {@link ProcessApplication} from 
 *  details provided in a {@link ProcessApplicationManager} <tt>m</tt> as follows:
 *  <pre>
 *    retrieve the original {@link Process} p that fits the description (i.e. process code)
 *    create derived {@link Process} p' from p
 *    for each {@link Task} t of p
 *      create in p' a derived {@link Task} t' of t st: t'.def=t /\ t'.prev=derived(t.prev) /\ t'.process = p'
 *      for each {@link Action} a of t
 *        create in t' a derived {@link Action} a' of a st: a'.def=a /\ a'.prev=derived(a.prev) /\ a'.task = t'
 *        
 *    create {@link ProcessApplication} n for p' from m.orgUnit, semester, year
 *  </pre> 
 *   
 *  Open derived process <tt>p'</tt> for viewing. 
 *  
 * @author dmle
 */
public class CreateNewProcessApplicationCommand<C> extends DataControllerCommand {

  /**IMPORTANT: must sort the Task and Actions retrieved to preserve the precedence order!*/
  private static ObjectComparator sorterTaskByCodeAsc;
  private static ObjectComparator sorterActionByCodeAsc;
  
  private static ValidateNewProcessApplication validator; // v1.2

  public CreateNewProcessApplicationCommand(DataController<C> dctl) {
    super(dctl);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {

    DataController<Process> dctl = getDataController();
    ControllerBasic<Process> ctl = dctl.getCreator();
    
    // confirm with user
    boolean confirmed = ctl.displayConfirmFromCode(MessageCode.CONFIRM_CREATE_OBJECT, 
        dctl, ctl.getDomainClassLabel());
    
    if (!confirmed) {
      // stop here
      return;
    }
    
    // data controllers of descendant modules
    ControllerBasic<Task> tctl = ctl.getMainController().lookUp(Task.class);
    ControllerBasic<Action> actl = ctl.getMainController().lookUp(Action.class);
    
    ProcessApplicationManager m = (ProcessApplicationManager) dctl.getParent().getCurrentObject();
    
    // used for checking special rules
    validateInput(m);
    
    final OrgUnit orgUnit = m.getOrgUnit();
    final String processCode = m.getProcessCode();
    final Semester sem = m.getSemester();
    final int year = m.getYear();

    DODMBasic dodm = dctl.getDodm();
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();

    // find original Process p and populate it with Tasks and Actions
    Process p = dom.retrieveObject(Process.class, Process.A_code, Op.EQ, processCode);
    
    if (p == null) {
      // process code incorrect
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {ctl.getDomainClassLabel(), ctl.getAttribNameLabel(Process.A_code) + "=" + processCode});
    }
    
    // populate p with Tasks and Actions
    Collection<Task> taskObjs;
    if (sorterTaskByCodeAsc == null) {
      DAttr taskCodeAttrib = dsm.getDomainConstraint(Task.class, Task.A_code);
      sorterTaskByCodeAsc = new ObjectComparator(dsm, taskCodeAttrib, SortBy.ASC);
    }
    
    Map<Oid,Task> tasks = dom.retrieveAssociatedObjects(p, Process.class, 
        Task.class, Process.Assoc_ProcessAndTask, sorterTaskByCodeAsc);
    if (tasks == null) {
      // no tasks found
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {tctl.getDomainClassLabel(), tctl.getAttribNameLabel(Task.A_process) + "=" + p});
    }
    
    taskObjs = tasks.values();
    
    // debug
    //System.out.printf("%nProcess tasks: %s%n", p.getTasks());
      
    Map<Oid,Action> actions;
    if (sorterActionByCodeAsc == null) {
      DAttr actionCodeAttrib = dsm.getDomainConstraint(Action.class, Action.A_code);
      sorterActionByCodeAsc = new ObjectComparator(dsm, actionCodeAttrib, SortBy.ASC);
    }
    
    for (Task t : taskObjs) {
      if (!t.hasActions()) {
        actions = dom.retrieveAssociatedObjects(t, Task.class, 
            Action.class, Task.Assoc_TaskAndAction, sorterActionByCodeAsc);
  
        if (actions == null) {
          // no actions found
          throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
              new Object[] {actl.getDomainClassLabel(), actl.getAttribNameLabel(Action.A_task) + "=" + t});
        }
      }
    }
      
    // create derived Process, Tasks, Actions
    Process pd = createAndOpenDerivedProcess(dodm, m, p);
    
    // create ProcessApplication
    DataController<ProcessApplication> ndctl = lookUpRootDataController(ProcessApplication.class);
    
    //prepareForCreateObject(ndctl);
    createNewObject(ndctl, new Object[] {orgUnit, pd, sem, year}, false);
  }

  /**
   * <b>IMPORTANT</b>: sub-types must invoke this method first, before implementing their own validation
   * 
   * @effects 
   *  if <tt>m</tt> is configured with special input validation rules
   *    check those rules 
   *    throws ConstraintViolationException if failed
   */
  protected void validateInput(ProcessApplicationManager m) throws ConstraintViolationException {
    // ensure that no other ProcessApplications have been created for the same process and in the same period
    String processCode = m.getProcessCode();
    Semester semester = m.getSemester();
    Integer year = m.getYear();
    
    if (validator == null) {
      validator = new ValidateNewProcessApplication(getDodm(), ProcessApplication.class);
    }
    
    validator.eval(processCode, semester, year);
  }

  /**
   * @requires
   *  p has been populated with associated {@link Task}s /\
   *  each of these {@link Task}s has been populated with associated {@link Action}s /\ 
   *  this has descendant data controllers for {@link Task} and {@link Action} 
   * 
   * @modifies dctl = {@link #getDataController()}, tdctl = dctl.childController[Task], tdctl.childController[Action]
   *  
   * @effects 
   *  <pre>
   *    let dctl = {@link #getDataController()}
   *    let tdctl = dctl.childController[Task] 
   *    let adctl = tdctl.childController[Action]
   *    
   *    create in dctl a derived {@link Process} p' from p st: p'.def = p
   *    
   *    for each {@link Task} t of p
   *      create in tdctl a derived {@link Task} t' of t st: t'.def=t /\ t'.prev=derived(t.prev) /\ t'.process = p'
   *      for each {@link Action} a of t
   *        create in adctl derived {@link Action} a' of a st: a'.def=a /\ a'.prev=derived(a.prev) /\ a'.task = t'
   *  </pre> 
   *  
   *  return <tt>p'</tt>
   * @version 1.1
   * @param m 
   */
  protected Process createAndOpenDerivedProcess(DODMBasic dodm, ProcessApplicationManager m, final Process p) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    DataController<Process> dctl = getDataController();
    DataController<Task> tdctl = dctl.getChildController(Process.A_tasks);
    DataController<Action> adctl = tdctl.getChildController(Task.A_actions);
    
    // temporarily shut pop-up message for creating objects
    Boolean oldPopUp = setPropertyShowPopUpMessage(dctl, Boolean.FALSE);
    Boolean oldTaskPopUp = setPropertyShowPopUpMessage(tdctl, Boolean.FALSE);
    Boolean oldActionPopUp = setPropertyShowPopUpMessage(adctl, Boolean.FALSE);

    // A 3-step procedure for creating a new derived process
    Process pd = null;
    try {
      // step 1:
      beforeCreateNewDerivedProcess(m);
      
      // step 2:
      pd = createNewDerivedProcess(m, dctl, tdctl, adctl, p);
    
    } catch (Exception e) {
      throw e;
    } finally {
      // step 3:
      afterCreateNewDerivedProcess(m);
    }
    
    // reset the child data controllers to display the first object
    //TODO: tdctl.first() could be used here but sometimes shows incorrect object 
    dctl.refresh();

    // reset shut pop-up message
    setPropertyShowPopUpMessage(dctl, oldPopUp);
    setPropertyShowPopUpMessage(tdctl, oldTaskPopUp);
    setPropertyShowPopUpMessage(adctl, oldActionPopUp);

    return pd;
  }


  /**
   * @effects 
   *   perform tasks before {@link #createNewDerivedProcess(ProcessApplicationManager, DataController, DataController, DataController, Process)} is completed
   */
  protected void beforeCreateNewDerivedProcess(ProcessApplicationManager m) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException{
    // for sub-types to implement if needed    
  }
  
  /**
   * @effects 
   *   perform tasks after {@link #createNewDerivedProcess(ProcessApplicationManager, DataController, DataController, DataController, Process)} is completed
   */
  protected void afterCreateNewDerivedProcess(ProcessApplicationManager m) {
    // for sub-types to implement if needed
  }


  /**
   * @param m 
   * @effects 
   *  create and return a derived {@link Process} of <tt>p</tt>
   */
  protected Process createNewDerivedProcess(ProcessApplicationManager m, DataController<Process> dctl,
      DataController<Task> tdctl, DataController<Action> adctl, Process p) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // create in dctl a derived {@link Process} p' from p st: p'.def = p
    Process pd = createNewObject(dctl, new Object[] {p});
    
    // create derived tasks and actions of p1
    /* for each {@link Task} t of p
     *  create in tdctl a derived {@link Task} t' of t st: t'.def=t /\ t'.prev=derived(t.prev) /\ t'.process = p'
     *    for each {@link Action} a of t
     *      create in adctl derived {@link Action} a' of a st: a'.def=a /\ a'.prev=derived(a.prev) /\ a'.task = t'
     */ 
    Collection<Task> tasks = p.getTasks();
    Task tdprev, tprev, td;
    Object[] values = null, avalues = null;
    Collection<Action> actions;
    Action adprev, aprev, ad;
    
    for (Task t : tasks) {
      tprev = t.getPrev();
      tdprev = (tprev != null) ? pd.lookUpTask(tprev.getCodeDef()) : null; // prev task of the derived task to be created
      if (values == null) {
        values = new Object[] {pd, t, tdprev};
      } else {
        // update
        values[0] = pd;
        values[1] = t;
        values[2] = tdprev;
      }

      td = createNewObject(tdctl,values);
      
      // post-processing of td (to support extension)
      postCreateTask(m, tdctl, td);
      
      // create derived actions for td
      actions = t.getActions();          // the original actions
      for (Action a : actions) {
        aprev = a.getPrev();
        adprev = ((aprev != null) ? td.lookUpAction(aprev.getCodeDef()) : null); // prev action of the derived action to be created
        if (avalues == null) {
          avalues = new Object[] {a, adprev, td};
        } else {
          avalues[0] = a; avalues[1] = adprev; avalues[2] = td;
        }

        ad = createNewObject(adctl,avalues);
        
        // post-processing of ad (to support extension)
        postCreateAction(m, adctl, ad);
      }
    }    
    
//  // create derived actions (do this after creating all tasks (above) in case dependencies exist)
//  tasks = pd.getTasks();  // tasks of the derived process
//  values = null;
//  Action adprev, aprev;
//  Task t;
//  Collection<Action> actions;
//  for (Task td : tasks) {
//    t = p.lookUpTask(td.getCodeDef()); // the original task
//    actions = t.getActions();          // the original actions
//    for (Action a : actions) {
//      aprev = a.getPrev();
//      adprev = ((aprev != null) ? td.lookUpAction(aprev.getCodeDef()) : null); // prev action of the derived action to be created
//      if (values == null) {
//        values = new Object[] {a, adprev, td};
//      } else {
//        values[0] = a; values[1] = adprev; values[2] = td;
//      }
//
//      createNewObject(adctl,values);
//    }
//  }
  
    return pd;
  }

  /**
   * @requires 
   *  <tt>td = tdctl.getCurrentObject()</tt>
   *  
   * @effects
   *  performs post-process of <tt>td</tt> that is being current in <tt>tdctl</tt> 
   *
   */
  protected void postCreateTask(ProcessApplicationManager m,
      DataController<Task> tdctl, Task td) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // for extension by sub-types
  }

  /**
   * @requires 
   *  <tt>ad = adctl.getCurrentObject()</tt>
   *  
   * @effects
   *  performs post-process of <tt>ad</tt> that is being current in <tt>adctl</tt> 
   *
   */
  protected void postCreateAction(ProcessApplicationManager m,
      DataController<Action> adctl, Action ad) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // for extension by sub-types
  }
  
//  /**
//   * @requires
//   *  p has been populated with associated {@link Task}s /\
//   *  each of these {@link Task}s has been populated with associated {@link Action}s /\ 
//   *  this has descendant data controllers for {@link Task} and {@link Action} 
//   * 
//   * @modifies dctl = {@link #getDataController()}, tdctl = dctl.childController[Task], tdctl.childController[Action]
//   *  
//   * @effects 
//   *  <pre>
//   *    let dctl = {@link #getDataController()}
//   *    let tdctl = dctl.childController[Task] 
//   *    let adctl = tdctl.childController[Action]
//   *    
//   *    create in dctl a derived {@link Process} p' from p st: p'.def = p
//   *    
//   *    for each {@link Task} t of p
//   *      create in tdctl a derived {@link Task} t' of t st: t'.def=t /\ t'.prev=derived(t.prev) /\ t'.process = p'
//   *      for each {@link Action} a of t
//   *        create in adctl derived {@link Action} a' of a st: a'.def=a /\ a'.prev=derived(a.prev) /\ a'.task = t'
//   *  </pre> 
//   *  
//   *  return <tt>p'</tt>
//   * @version 1.1
//   * @param m 
//   */
//  protected Process createAndOpenDerivedProcess(DODMBasic dodm, ProcessApplicationManager m, final Process p) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
//    DOMBasic dom = dodm.getDom();
//    DSMBasic dsm = dodm.getDsm();
//    
//    DataController<Process> dctl = getDataController();
//    DataController<Task> tdctl = dctl.getChildController(Process.A_tasks);
//    DataController<Action> adctl = tdctl.getChildController(Task.A_actions);
//    
//    // temporarily shut pop-up message for creating objects
//    Boolean oldPopUp = setPropertyShowPopUpMessage(dctl, Boolean.FALSE);
//    Boolean oldTaskPopUp = setPropertyShowPopUpMessage(tdctl, Boolean.FALSE);
//    Boolean oldActionPopUp = setPropertyShowPopUpMessage(adctl, Boolean.FALSE);
//    
//    // create in dctl a derived {@link Process} p' from p st: p'.def = p
//    Process pd = createNewObject(dctl, new Object[] {p});
//    
//    // create derived tasks and actions of p1
//    /* for each {@link Task} t of p
//     *  create in tdctl a derived {@link Task} t' of t st: t'.def=t /\ t'.prev=derived(t.prev) /\ t'.process = p'
//     *    for each {@link Action} a of t
//     *      create in adctl derived {@link Action} a' of a st: a'.def=a /\ a'.prev=derived(a.prev) /\ a'.task = t'
//     */ 
//    Collection<Task> tasks = p.getTasks();
//    Task tdprev, tprev;
//    Object[] values = null;
//    for (Task t : tasks) {
//      tprev = t.getPrev();
//      tdprev = (tprev != null) ? pd.lookUpTask(tprev.getCodeDef()) : null; // prev task of the derived task to be created
//      if (values == null) {
//        values = new Object[] {pd, t, tdprev};
//      } else {
//        // update
//        values[0] = pd;
//        values[1] = t;
//        values[2] = tdprev;
//      }
//
//      createNewObject(tdctl,values);
//    }
//    
//    
//    // create derived actions (do this after creating all tasks (above) in case dependencies exist)
//    //prepareForCreateObject(adctl);
//
//    tasks = pd.getTasks();  // tasks of the derived process
//    values = null;
//    Action adprev, aprev;
//    Task t;
//    Collection<Action> actions;
//    for (Task td : tasks) {
//      t = p.lookUpTask(td.getCodeDef()); // the original task
//      actions = t.getActions();          // the original actions
//      for (Action a : actions) {
//        aprev = a.getPrev();
//        adprev = ((aprev != null) ? td.lookUpAction(aprev.getCodeDef()) : null); // prev action of the derived action to be created
//        if (values == null) {
//          values = new Object[] {a, adprev, td};
//        } else {
//          values[0] = a; values[1] = adprev; values[2] = td;
//        }
//
//        createNewObject(adctl,values);
//      }
//    }
//    
//    // reset the child data controllers to display the first object
//    tdctl.first();
//
//    // reset shut pop-up message
//    setPropertyShowPopUpMessage(dctl, oldPopUp);
//    setPropertyShowPopUpMessage(tdctl, oldTaskPopUp);
//    setPropertyShowPopUpMessage(adctl, oldActionPopUp);
//
//    return pd;
//  }
}
