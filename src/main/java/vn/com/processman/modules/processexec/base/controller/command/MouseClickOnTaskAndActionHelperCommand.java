package vn.com.processman.modules.processexec.base.controller.command;

import jda.modules.common.exceptions.NotFoundException;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.MouseClickOnRefObjectHelperCommand;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;

/**
 * @overview
 *  A custom helper command to handle mouse-click actions on referenced objects of this module and of the descendant modules.
 *  
 * @author dmle
 */
public class MouseClickOnTaskAndActionHelperCommand<C> extends MouseClickOnRefObjectHelperCommand {

  public MouseClickOnTaskAndActionHelperCommand(DataController dctl) {
    super(dctl);
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.controller.datacontroller.command.MouseClickOnRefObjectHelperCommand#lookUpTargetModule(java.lang.Class)
   */
  @Override
  protected ControllerBasic lookUpTargetModule(Class refClass) throws NotFoundException, SecurityException {
    final DataController dctl = getDataController(); 

    final ControllerBasic mainCtl = dctl.getCreator().getMainController();
    
    ControllerBasic targetCtl = null;
    if (Action.class == refClass) {
      // an Action object: use ModuleActionExec
      targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleActionExec");
    } else if (Action4Subject.class == refClass) {
      // an Action object: use ModuleActionExec
      targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleAction4SubjectExec");
    } else if (Task.class == refClass) {
      // a Task object
      targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleTaskExecBase");
    } else if (Task4Subject.class == refClass) {
      // a Task4Subject object
      targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleTask4SubjectExecBase");
    } 
    
    
    return targetCtl;
  }
}
