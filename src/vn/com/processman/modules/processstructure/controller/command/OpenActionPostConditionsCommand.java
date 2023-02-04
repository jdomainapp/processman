package vn.com.processman.modules.processstructure.controller.command;

import java.util.Collection;

import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.modules.processstructure.model.Action;
import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.core.ControllerBasic.DataController;

/**
 * @overview
 *  A {@link DataControllerCommand} used to customise the Open action that retrieves post-conditions 
 *  for a given {@link Action}. This is required because of:
 *  <br> (1) the way post-conditions are 
 *  created in memory (not in data source) and are added to each {@link Action}
 *  directly and 
 *  <br> (2) the reflexive association in {@link Action}
 *  (pre-conditions of derived Action are obtained from those of the base Action)
 *  
 * @author dmle
 */
public class OpenActionPostConditionsCommand<C> extends DataControllerCommand {

  public OpenActionPostConditionsCommand(DataController dctl) {
    super(dctl);
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController<BooleanExpression> dctl = getDataController();
    DataController<Action> parentDctl = dctl.getParent();
    
    final Action parentObj = parentDctl.getCurrentObject(); 
        
    // get the post-conditions directly from the definition Action, without actually retrieving the conditions 
    // from the data source
    Collection<BooleanExpression> conds = parentObj.getPostConds();
    
    if (conds != null && !conds.isEmpty())
      dctl.openObjects(conds, false);
  }

}
