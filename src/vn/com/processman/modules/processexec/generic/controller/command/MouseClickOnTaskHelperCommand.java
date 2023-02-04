package vn.com.processman.modules.processexec.generic.controller.command;

import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.ControllerBasic.DataController;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.SecurityException;
import domainapp.controller.datacontroller.command.MouseClickOnRefObjectHelperCommand;

/**
 * @overview
 *  A custom helper command to handle mouse-click actions on referenced objects of this module and of the descendant modules.
 *  
 * @author dmle
 */
public class MouseClickOnTaskHelperCommand<C> extends MouseClickOnRefObjectHelperCommand {

  public MouseClickOnTaskHelperCommand(DataController dctl) {
    super(dctl);
  }

  /* (non-Javadoc)
   * @see domainapp.controller.datacontroller.command.MouseClickOnRefObjectHelperCommand#lookUpTargetModule(java.lang.Class)
   */
  @Override
  protected ControllerBasic lookUpTargetModule(Class domainCls) throws NotFoundException, SecurityException {
    ControllerBasic mainCtl = getDataController().getCreator().getMainController();
    
    if (domainCls == Task.class) {
      return mainCtl.lookUpByModuleWithPermission("ModuleTaskExec"); 
    } else if (domainCls == Task4Subject.class) {
      return mainCtl.lookUpByModuleWithPermission("ModuleTask4SubjectExec");
    }
    
    // others: default
    return null;
  }
}
