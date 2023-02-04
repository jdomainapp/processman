package vn.com.processman.modules.processsconstraint.model.function;

import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.query.Expression.Op;


/**
 * @overview 
 *  Represents a {@link Function} that retrieves from the system a domain object based on user's specified input.
 *  
 *  <p>Note: this function does not actually create a new domain object. It assumes that the object exists and 
 *  merely tries to retrieve it from the system.
 *  
 * @author dmle
 *
 */
public class InstanceOf extends Function {

  public InstanceOf(Object...args) {
    super(args);
  }

  @Override
  public Object eval() throws NotPossibleException {
    
    DOMBasic dom = getDodm().getDom();
    
    Object[] args = getArgs();
    
    // 0: the object class
    Class c = (Class) args[0];
    
    // 1,2,...: attribute, value pairs
    int numPairs = (args.length-1) / 2;
    Object attribObj, attribVal;
    String[] attribNames = new String[numPairs];
    Object[] attribVals = new Object[numPairs];
    Op[] ops = new Op[numPairs];
    for (int i = 0; i < numPairs; i++) {
      attribObj = args[1+i*2];
      attribVal = args[1+i*2+1];
      
      // evaluate attribVal (and recursively so) if it is a function
      if (attribVal instanceof Function) {
        attribVal = ((Function) attribVal).eval();
      }

      attribNames[i] = attribObj.toString();
      attribVals[i] = attribVal;
      //TODO: support Op specification?
      ops[i] = Op.EQ;
    }
    
    // retrieve from dodm the object matching the specified attribute, value pairs
    Object object;
    try {
      object = dom.retrieveObject(c, attribNames, ops, attribVals);
      
      return object;
    } catch (NotFoundException | DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, e, 
          new Object[] {AttribValue.class.getSimpleName()+".eval", "", ""} );
    }
  }

}
