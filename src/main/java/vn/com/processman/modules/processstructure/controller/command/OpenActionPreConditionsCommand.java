package vn.com.processman.modules.processstructure.controller.command;

import java.util.Collection;

import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.modules.processstructure.model.Action;

/**
 * @overview
 *  A {@link DataControllerCommand} used to customise the Open action that retrieves pre-conditions 
 *  for a given {@link Action}. This is required because of:
 *  <br> (1) the way pre-conditions are 
 *  created in memory (not in data source) and are added to each {@link Action}
 *  directly and 
 *  <br> (2) the reflexive association in {@link Action} 
 *  (pre-conditions of derived Action are obtained from those of the base Action)
 *  
 * @author dmle
 */
public class OpenActionPreConditionsCommand<C> extends DataControllerCommand {

  public OpenActionPreConditionsCommand(DataController dctl) {
    super(dctl);
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController<BooleanExpression> dctl = getDataController();
    DataController<Action> parentDctl = dctl.getParent();
    
    final Action parentObj = parentDctl.getCurrentObject(); 
    
    // get the pre-conditions directly from the definition Action, without actually retrieving the conditions 
    // from the data source
    Collection<BooleanExpression> conds = parentObj.getPreConds();
    
    if (conds != null && !conds.isEmpty())
      dctl.openObjects(conds, false);
    
  }

}
