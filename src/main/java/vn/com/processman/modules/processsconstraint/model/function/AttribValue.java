package vn.com.processman.modules.processsconstraint.model.function;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dsm.DSMBasic;


/**
 * @overview 
 *  Represents a {@link Function} that obtains a value of an object's attribute.
 *  
 * @author dmle
 *
 */
public class AttribValue extends Function {

  public AttribValue(Object... args) {
    super(args);
  }

  @Override
  public Object eval() throws NotPossibleException {
    Object[] args = getArgs();
    
    DSMBasic dsm = getDodm().getDsm();
    
    // arg[0]: the object
    Object obj = args[0];
    
    // evaluate (recursively) if obj is a function
    if (obj instanceof Function) {
      obj = ((Function)obj).eval();
    }
    
    if (obj == null) {
      // null object
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, 
          new Object[] {this+".eval", "", "Object is null"});
    }
    
    // args[1]: the attribute name
    Object attribObj = args[1];
    String attribName = (String) attribObj;
    
    // get attribute value
    try {
      Object attribVal = dsm.getAttributeValue(obj, attribName);
      
      return attribVal;
    } catch (NotFoundException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, e, 
          new Object[] {AttribValue.class.getSimpleName()+".eval", "", ""} );
    }
  }
  
}
