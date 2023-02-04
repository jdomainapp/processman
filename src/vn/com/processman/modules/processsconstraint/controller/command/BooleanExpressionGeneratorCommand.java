package vn.com.processman.modules.processsconstraint.controller.command;

import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.modules.processstructure.model.Action;
import domainapp.basics.controller.command.ControllerCommand;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.exceptions.ApplicationRuntimeException;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotPossibleException;

/**
 * @overview
 *  A {@link ControllerCommand} that generates {@link BooleanExpression}s that are used as pre- and post-conditions 
 *  of various {@link Action}s of the application.
 *  
 * @author dmle
 */
public class BooleanExpressionGeneratorCommand extends ControllerCommand {

  // a shared instance
  private static final BooleanExpressionGenerator expGen = new BooleanExpressionGenerator();
  
  public BooleanExpressionGeneratorCommand(ControllerBasic controller) {
    super(controller);
  }

  @Override
  public void doTask() throws ApplicationRuntimeException {
    DODMBasic dodm = getController().getDodm();
    
    try {
      expGen.init(dodm);
      expGen.genObjects(dodm);
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_COMMAND, new Object[] {this.getClass().getSimpleName()});
    }
  }

}
