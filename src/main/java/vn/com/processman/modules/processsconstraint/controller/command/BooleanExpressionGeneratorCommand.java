package vn.com.processman.modules.processsconstraint.controller.command;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.command.ControllerCommand;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.modules.processstructure.model.Action;

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
