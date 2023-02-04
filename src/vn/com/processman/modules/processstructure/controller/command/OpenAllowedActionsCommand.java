package vn.com.processman.modules.processstructure.controller.command;

import java.util.Map;

import vn.com.processman.modules.processstructure.model.Action;
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
 *  If security is enabled and user is a regular user then only open {@link Action}s that user is allowed to perform, otherwise open all {@link Action} 
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class OpenAllowedActionsCommand<C> extends DataControllerCommand {
//  protected static final Class<Action> ActionCls = Action.class;
//
//  // the query used to retrieve objects under security restriction
//  private static FlexiQuery query;
  
  /**
   * @effects 
   * 
   */
  public OpenAllowedActionsCommand(DataController dctl) {
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
    if security is enabled and user is a process user
      retrieve Actions that user is allowed to perform, as follows:
      select Action.id
      where Action (a1) join Task ... (parent query) and 
            a1 join Action (a2) on a1.def = a2.id and  
            a2.role in currentUser.roles 
    else
      retrieve all Tasks
    */
    DataController dctl = getDataController();
    DODMBasic dodm = dctl.getDodm();
//    DSMBasic dsm = dodm.getDsm();
//    DOMBasic dom = dodm.getDom();

    if (isLoggedIn()) {
      DomainUser user = getCurrentUser();
      
      if (DomainToolKit.isProcessUserStrictly(user)) {
        // user is a process-user
        
        // open Actions that user is allowed to perform
        
        // ensure that object metadata are fully loaded
        // NOTE: this must be done before openning result objects (below)
        if (!dctl.isOpenMetadata()) {
          dctl.openMetadata();
        }
        
        Task parentObj = (Task) dctl.getParentObject();
        
        Map<Oid,Action> result = DomainToolKit.retrieveAllowedActions(dodm, user, parentObj);
//        if (query == null) {
//          query = new FlexiQuery(dsm, ActionCls);
//        }
//
//        // Action (a1) join Task (parent query)
//        Task parentObj = (Task) dctl.getParentObject();
//        Expression exp1 = query.addConstraintExpression(Action.A_task, Op.EQ, parentObj); 
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
//          result = dom.retrieveObjectsWoutSubTypes(ActionCls, query);
//        } catch (Exception e) {
//          throw e;
//        } finally { 
//          query.remove(exp1,exp2); 
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
      // not logged-in: open normally
      dctl.open();
    }
  }
}
