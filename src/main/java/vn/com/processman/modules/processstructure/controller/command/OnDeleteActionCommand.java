package vn.com.processman.modules.processstructure.controller.command;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.security.def.Role;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.util.DomainToolKit;

/**
 * @overview
 *  Perfom post-delete task on {@link Action}, which includes removing association links between {@link Role} and 
 *  {@link Process} that contains the updated {@link Action} object.
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class OnDeleteActionCommand<C> extends DataControllerCommand {

  /**
   * @effects 
   * 
   */
  public OnDeleteActionCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   *  Perfom post-update task on {@link Action}, which includes updating association links between {@link Role} and 
   *    {@link Process} that contains the updated {@link Action} object.
   */
  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    Action obj = (Action) args[0];
    
    /* 
      let r = obj.role, and p be the (def) Process that contains the obj
      if r does not perform any other actions of p
        remove association link (r1, p) from RolePerfProcess
     */   
    DODMBasic dodm = getDodm();
    DSMBasic dsm = dodm.getDsm();
    
    DAttr attrRole = dsm.getDomainConstraint(Action.class, Action.A_role);
    Role actRole = (Role) dsm.getAttributeValue(Action.class, obj, attrRole);
      
    // (def) Process of action
    Process p = obj.getTask().getProcess();  
    
    if (actRole != null && !DomainToolKit.existRolePerformsAnyActionOf(dodm, actRole, p)) {
      // rold does not perform any other actions of p
      // remove association link (r1, p) from RolePerfProcess
      DomainToolKit.deleteRolePerformsProcess(dodm, actRole, p);
    }      
  }
}
