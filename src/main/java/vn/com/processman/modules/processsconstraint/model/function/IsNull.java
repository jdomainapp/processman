package vn.com.processman.modules.processsconstraint.model.function;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  Represents a {@link Function} that checks if an object is null. 
 *  
 * @author dmle
 *
 */
public class IsNull extends Function {

  public IsNull(Object... args) {
    super(args);
  }

  @Override
  public Object eval() throws NotPossibleException {
    Object[] args = getArgs();
    
    // 0: the object
    Object obj = args[0];
    
    // evaluate obj (and recursively so) if it is a function
    if (obj instanceof Function) {
      obj = ((Function) obj).eval();
    }
    
    // check null
    return (obj == null);
  }

}
