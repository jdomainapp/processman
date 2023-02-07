package vn.com.processman.modules.processstructure.controller.command;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processstructure.model.Action4Subject;

/**
 * @overview 
 *  An extension command that performs post-processing after a {@link SubjectAction} has been selected by the user. 
 *  
 * @author dmle
 *
 */
public class OnSubjectActionSelectedCommand<C> extends DataControllerCommand {

  public OnSubjectActionSelectedCommand(DataController dctl) {
    super(dctl);
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController<SubjectAction> dctl = getDataController();
    DataController<Action4Subject> parentDctl = dctl.getParent();
    
    if (parentDctl == null)
      throw new NotPossibleException(NotPossibleException.Code.DATA_CONTROLLER_NOT_A_CHILD, new Object[] {dctl});

    Action4Subject parentObj = parentDctl.getCurrentObject();

    // the selected object
    SubjectAction obj = (SubjectAction) args[0];

    if (parentObj == null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_PARENT_OBJECT, new Object[] {
          parentDctl.getCreator().getDomainClassLabel()});
    }

    if (!obj.getAction().equals(parentObj)) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_PARENT_OBJECT, new Object[] {
          obj, parentDctl.getCreator().getDomainClassLabel()});
    }

    /* v1.3: simulate evaluation
    boolean updated = SubjectAction.evaluateSubjStatus(obj);
    
    if (updated) {
      // serialise and update view
      DOMBasic dom = getDodm().getDom();
      dom.updateObject(obj, null);
      dctl.getDataContainer().updateDataComponent(SubjectAction.A_subjStatus);
    }
    */
    boolean canBeDone = SubjectAction.evaluateSubjStatusSimulator(obj);
    
    // update pre- & post-conditions listing on the parent Action
    parentDctl.getDataContainer().updateDataComponent(Action4Subject.A_preConds);
    parentDctl.getDataContainer().updateDataComponent(Action4Subject.A_postConds);
    
    if (!canBeDone && obj.isSubjStatusDone()) {
      // set it to null so user has to update again
      obj.setSubjStatus(null);
      // serialise and update view
      DOMBasic dom = getDodm().getDom();
      dom.updateObject(obj, null);
      dctl.getDataContainer().updateDataComponent(SubjectAction.A_subjStatus);      
    }
  }
}
