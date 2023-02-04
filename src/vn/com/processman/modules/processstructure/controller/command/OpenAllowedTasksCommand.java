package vn.com.processman.modules.processstructure.controller.command;

import java.util.Map;

import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.DomainToolKit;
import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.controller.util.MessageCode;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.ControllerBasic.DataController;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.model.Oid;
import domainapp.basics.model.security.DomainUser;

/**
 * @overview
 *  If security is enabled and user is a regular user then only open {@link Task}s that user is allowed to perform, otherwise open all {@link Task} 
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class OpenAllowedTasksCommand<C> extends DataControllerCommand {

//  protected static final Class<Action> ActionCls = Action.class;
//  protected static final Class<Task> TaskCls = Task.class;
//  
//  // the query used to retrieve objects under security restriction
//  private static FlexiQuery query;
  
  /**
   * @effects 
   * 
   */
  public OpenAllowedTasksCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    /*
     if security is enabled and user is a process-user
       retrieve all Tasks that user is allowed to perform
       i.e. Tasks that contain at least one Action that user is allowed to perform
       select Task.id
       where Task.process = parentObj and 
             Action (a1) join Task and
             a1 join Action (a2) on a1.def = a2.id and  
               a2.role in currentUser.roles 
     else
       retrieve all Tasks
     */
    DataController dctl = getDataController();
    DODMBasic dodm = dctl.getDodm();
//    DSMBasic dsm = dodm.getDsm();
//    DOMBasic dom = dodm.getDom();

    DomainUser user;
    if (isLoggedIn()) {
      user = getCurrentUser();
      Process parentObj = (Process) dctl.getParentObject();

      if (DomainToolKit.isProcessUserStrictly(user)) {
        // user is a process-user
        
        // Map<Oid,Task> result = null;
        // ensure that object metadata are fully loaded
        // NOTE: this must be done before openning result objects (below)
        if (!dctl.isOpenMetadata()) {
          dctl.openMetadata();
        }
        
        Map<Oid, Task> result = DomainToolKit.retrieveAllowedTasks(dodm, user, parentObj);
        
//        
//        // retrieve all Tasks that user is allowed to perform
//        if (query == null) {
//          query = new FlexiQuery(dsm, TaskCls);
//          
//          // Action (a1) join Task
//          query.addJoinExpression(new Class[] {ActionCls, TaskCls}, new String[] {Task.Assoc_TaskAndAction});
//        }
//
//        // Task.process = parentObj
//        Expression exp1 = query.addConstraintExpression(Task.A_process, Op.EQ, parentObj); 
//        
//        // Action (a1) join Task (moved up to add once)  
//        // Expression exp3 = query.addJoinExpression(new Class[] {ActionCls, TaskCls}, new String[] {Task.Assoc_TaskAndAction});
//        
//        // a1 join Action (a2) on a1.def = a2.id and 
//        //    a2.role in currentUser.roles
//        Role[] userRoles = user.getTheRoles().toArray(new Role[0]);
//        Expression exp2 = query.addJoinOnAttributesWithValueConstraint(
//            new Class[] {ActionCls, ActionCls}, new String[] {Action.A_def, Action.A_id},
//            Action.A_role, Op.IN, userRoles
//            );
//        
//        // debug on
//        //dom.setDebugOn(true);
//        
//        try {
//          result = dom.retrieveObjectsWoutSubTypes(TaskCls, query);
//        } catch (Exception e) {
//          throw e;
//        } finally { 
//          query.remove(exp1, exp2);
//        
//          // debug off
//          //dom.setDebugOn(false);
//        }
        
        if (result != null) {
          dctl.openObjects(result.values(), true);
        } else {
          // clear existing result (if any)
          ControllerBasic parentCtl = dctl.getParent().getCreator();
          String parentObjName = parentCtl.getDomainClassLabel() + " (" + parentObj.getCode() + ")";
          String functionTitle = parentCtl.getModuleTitle();
          parentCtl.displayMessageFromCode(MessageCode.NO_OBJECTS_FOUND_FOR_FUNCTION, dctl,
              new Object[] {parentObjName, functionTitle}
              );
        }
      } else {
        // not a process-user: open normally
        dctl.open();
      }
    } else {
      // no user: open normally
      dctl.open();
    }
  }
}
