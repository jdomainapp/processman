package vn.com.processman.modules.processstructure.controller.command;

import vn.com.processman.modules.dsecurity.model.RolePerfProcess;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.util.DomainToolKit;
import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.core.ControllerBasic.DataController;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.security.Role;

/**
 * @overview
 *  Perfom post-create task on {@link Action}, which includes adding association link between {@link Role} and 
 *  {@link Process} that contains the updated {@link Action} object.
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class OnCreateActionCommand<C> extends DataControllerCommand {

  /**
   * @effects 
   * 
   */
  public OnCreateActionCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   *  Perfom post-update task on {@link Action}, which includes adding association link between {@link Role} and 
   *  {@link Process} that contains the updated {@link Action} object.
   */
  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    Action obj = (Action) args[0];
    
    /* 
      let r = obj.role, and p be the (def) Process that contains the obj
      if there is no association link for (r, p)
        add association link (r1, p) to RolePerfProcess
     */   
    DODMBasic dodm = getDodm();
    DSMBasic dsm = dodm.getDsm();
    
    DAttr attrRole = dsm.getDomainConstraint(Action.class, Action.A_role);
    Role actRole = (Role) dsm.getAttributeValue(Action.class, obj, attrRole);
      
    // (def) Process of action
    Process p = obj.getTask().getProcess();  
    
    if (actRole != null && !DomainToolKit.existRolePerformsProcess(dodm, actRole, p)) {
      boolean initForm = false, showPopUpMesg = false;
      Object[] values = {actRole, p};
      createNewObject(RolePerfProcess.class, values, initForm, showPopUpMesg);
    }      
  }
}
