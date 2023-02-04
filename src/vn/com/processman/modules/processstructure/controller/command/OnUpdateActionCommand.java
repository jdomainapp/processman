package vn.com.processman.modules.processstructure.controller.command;

import java.util.LinkedHashMap;

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
 *  Perfom post-update task on {@link Action}, which includes updating association links between {@link Role} and 
 *  {@link Process} that contains the updated {@link Action} object.
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class OnUpdateActionCommand<C> extends DataControllerCommand {

  /**
   * @effects 
   * 
   */
  public OnUpdateActionCommand(DataController dctl) {
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
    LinkedHashMap<DAttr, Object> oldChangedVals = (LinkedHashMap<DAttr, Object>) args[1];
    LinkedHashMap<DAttr, Object> newChangedVals = (LinkedHashMap<DAttr, Object>) args[2];
    
    /* if Action.role was among the attribute that was changed then:
      let r1 be the old role, r2 be the new role, and p be the (def) Process that contains the Action
      if r1 does not perform any other actions of p
        remove association link (r1, p) from RolePerfProcess
      if does not exist (r2,p) in RolePerfProcess
        add this link to RolePerfProcess
     */   
    DODMBasic dodm = getDodm();
    DSMBasic dsm = dodm.getDsm();
    
    DAttr attrRole = dsm.getDomainConstraint(Action.class, Action.A_role);
    Role rOld, rNew;
    if (oldChangedVals.containsKey(attrRole)) {
      // Action.role was changed
      rOld = (Role) oldChangedVals.get(attrRole);
      rNew = (Role) newChangedVals.get(attrRole);
      
      // (def) Process of action
      Process p = obj.getTask().getProcess();  
      
      if (rOld != null && !DomainToolKit.existRolePerformsAnyActionOf(dodm, rOld, p)) {
        // rold does not perform any other actions of p
        // remove association link (r1, p) from RolePerfProcess
        //if (ProcessManToolKit.existRolePerformsProcess(dodm, rOld, p))
        DomainToolKit.deleteRolePerformsProcess(dodm, rOld, p);
      }
      
      if (rNew != null && !DomainToolKit.existRolePerformsProcess(dodm, rNew, p)) {
        // does not exist (r2,p) in RolePerfProcess
        // add this link to RolePerfProcess
        boolean initForm = false, showPopUpMesg = false;
        Object[] values = {rNew, p};
        createNewObject(RolePerfProcess.class, values, initForm, showPopUpMesg);
      }
    }
  }
}
