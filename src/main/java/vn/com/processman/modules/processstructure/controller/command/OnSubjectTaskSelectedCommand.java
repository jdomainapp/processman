package vn.com.processman.modules.processstructure.controller.command;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processstructure.model.Task4Subject;

/**
 * @overview 
 *  An extension command that performs post-processing after a {@link SubjectTask} has been selected by the user. 
 *  
 * @author dmle
 *
 */
public class OnSubjectTaskSelectedCommand<C> extends DataControllerCommand {

  public OnSubjectTaskSelectedCommand(DataController dctl) {
    super(dctl);
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController<SubjectTask> dctl = getDataController();
    DataController<Task4Subject> parentDctl = dctl.getParent();
    
    if (parentDctl == null)
      throw new NotPossibleException(NotPossibleException.Code.DATA_CONTROLLER_NOT_A_CHILD, new Object[] {dctl});

    Task4Subject parentObj = parentDctl.getCurrentObject();
    
    if (parentObj == null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_PARENT_OBJECT, new Object[] {
          parentDctl.getCreator().getDomainClassLabel()});
    }
    
    // the selected object
    SubjectTask obj = (SubjectTask) args[0]; 
    
    // evaluate obj to obtain the status for the subject
    boolean statusChanged= obj.evaluateSubjStatus();
    
    // if status is changed, update the status attribute value of obj
    if (statusChanged) {
      DOMBasic dom = getDodm().getDom();
      dom.updateObject(obj, null);
      dctl.getDataContainer().updateDataComponent(SubjectTask.A_subjStatus);
    }
  }
}
