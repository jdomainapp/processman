package vn.com.processman.modules.processmanager.processapplication.controller.command;

import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.core.ControllerBasic.DataController;

/**
 * @overview
 *  A {@link DataControllerCommand} used to customise how the new-object operation that is performed on the 
 *  {@link Process}-subform of module {@link ProcessApplication} initialise just the object-metadata 
 *  instead of performing a normal open operation.
 *  
 *  The later is not suitable for this context because the association {@link ProcessApplication}-{@link Process} 
 *  has <tt>updateLink=false</tt>.
 *  
 * @author dmle
 *
 * @version 
 */
public class OpenOnNewAppliedProcessCommand<T> extends DataControllerCommand {

  /**
   * @effects 
   * 
   */
  public OpenOnNewAppliedProcessCommand(DataController dctl) {
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
    DataController dctl = getDataController();
    
    // only open the metadata instead of performing the normal open operation.
    if (!dctl.isOpenMetadata()) {
      dctl.openMetadata();
    }
  }

}
