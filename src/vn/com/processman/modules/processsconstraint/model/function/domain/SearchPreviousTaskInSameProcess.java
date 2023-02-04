package vn.com.processman.modules.processsconstraint.model.function.domain;

import vn.com.processman.modules.processsconstraint.model.function.Function;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.exceptions.NotPossibleException;


/**
 * @overview 
 *  Represents a {@link Function} that looks up a previous {@link Task} of a given {@link Task}
 *  that matches certain criteria. The input {@link Task} and criteria values are passed in as input.
 *  
 * @author dmle
 * 
 * @version 1.3
 *
 */
public class SearchPreviousTaskInSameProcess extends Function {

  public SearchPreviousTaskInSameProcess(Object... args) {
    super(args);
  }

  @Override
  public Object eval() throws NotPossibleException {
    Object[] args = getArgs();
    
    DSMBasic dsm = getDodm().getDsm();
    
    // arg[0]: the input Task object
    Object obj = args[0];
    
    // evaluate (recursively) if obj is a function
    if (obj instanceof Function) {
      obj = ((Function)obj).eval();
    } 
    
    if (obj == null) {
      // null object
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, 
          new Object[] {this+".eval", "", "Task object is null"});
    }
    
    if (!(obj instanceof Task)) {
      // not a Task object
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, 
          new Object[] {this+".eval", "", "args[0] is not a Task object"});
    }
    
    Task task = (Task) obj;
    
    // args[1]: the previous task's code
    Object codeObj = args[1];
    //TODO: validate codeObj
    String taskCode = (String) codeObj;
    
    // get the task
    Process proc = task.getProcess();
    
    Task prevTask = proc.lookUpTask(taskCode);
    
    if (prevTask == null) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, 
          new Object[] {SearchPreviousTaskInSameProcess.class.getSimpleName()+".eval", "", "No previous task with codeDef=" + taskCode + " is found in " + proc} );        
    } else {
      return prevTask;
    }
  }
  
}
