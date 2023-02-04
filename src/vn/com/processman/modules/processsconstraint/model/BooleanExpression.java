package vn.com.processman.modules.processsconstraint.model;

import vn.com.processman.modules.processsconstraint.model.function.Function;
import vn.com.processman.modules.processstructure.model.Action;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Tuple;

/**
 * @overview
 * 	Represents a boolean expression
 * 
 * @author dmle
 */
@DClass(serialisable=false)
public class BooleanExpression {
  public static final String A_id = "id";
  
  // attributes
  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
  /** the LHS of expression: must be a <b>boolean function</b>, i.e. when evaluated results in boolean*/
  @DAttr(name="rootFunc", type=Type.Domain, optional=false)
  private Function rootFunc;
  
  /** expression's operator */
  @DAttr(name="op", type=Type.Domain, optional=false)
  private Op op;
  
  /**the RHS of expression */
  //@DomainConstraint(name="value", type=Type.Other, optional=false)
  private Object value;
  
  @DAttr(name="description", type=Type.String, length=255)
  private String description;
  
  /**the actual value of expression as determined by {@link #eval()}*/
  @DAttr(name="result", type=Type.Boolean, auto=true, mutable=false)
  private boolean result;
  
  /** the data object management component used by the functions of this to retrieve objects */
  private DODMBasic dodm;

  /***
   * data links
   */
  /**the {@link Action} for which this is used as a condition */
  @DAttr(name="action", type=Type.Domain)
  private Action action;
  
  /**
   * @modifies rootFunc
   */
  public BooleanExpression(DODMBasic dodm, Action act, Function rootFunc, Op op, Object value, String description) {
    this.id = nextID(null);
    this.dodm = dodm;
    this.action = act;
    this.rootFunc = rootFunc;
    this.rootFunc.setExp(this);
    
    this.op = op;
    this.value = value;
    
    this.description = description;
  }

  public BooleanExpression(DODMBasic dodm, Action act, Function rootFunc, Object value, String description) {
    this(dodm, act, rootFunc, Op.EQ, value, description);
  }

  /**
   * used for testing ONLY
   * @effects 
   *
   */
  public BooleanExpression(Function rootFunc, Op op, Object value, String description) {
    this(
        // dodm
        null, 
        // act
        null, 
        rootFunc, op, value, description);
  }

  /**
   * used for testing ONLY
   * @effects   
   *  call {@link #BooleanExpression(Function, Op, Boolean)}<tt> --> (rootFunc, {@link Op#EQ}, value)</tt>
   */
  public BooleanExpression(Function rootFunc, Object value, String description) {
    this(
        // dodm
        null, 
        // act
        null,
        rootFunc, Op.EQ, value, description);
  }
  
  /**
   * @modifies this.{@link #result}
   * 
   * @effects <pre>
   *  evaluate this by evaluating the {@link Function}s in the function tree whose
   *  root is {@link #rootFunc}; 
   *  if succeeded
   *    if <tt>evaluated_result != null /\ evaluated_result {@link #op) value</tt>
   *      set result = true
   *    else
   *      set result = false
   *    
   *    return result
   *  else
   *    set result = false
   *    throws NotPossibleException 
   *  
   *  return result
   *  </pre>
   */
  public boolean eval() throws NotPossibleException {
    // evaluate function tree, catching exceptions (if any)
    // if exceptions were thrown then set result to false
    
    try {
      Object evalResult = rootFunc.eval();
      
      // compare result with value
      //TODO: support different Op types
      result = (evalResult != null && evalResult.equals(value));
    } catch (NotPossibleException e) {
      // error evaluation function tree
      result = false;
      throw e;
    }
    
    return result;
  }
  
  
  public Object getValue() {
    return value;
  }


  public void setValue(Object value) {
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean getResult() {
    return result;
  }

  public Function getRootFunc() {
    return rootFunc;
  }

  public DODMBasic getDodm() {
    return dodm;
  }

  public void setDodm(DODMBasic dodm) {
    this.dodm = dodm;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public int getId() {
    return id;
  }

  // util methods
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter) {
        idCounter = num;
      }
      return currID;
    }
  }

  /**
   * @requires minVal != null /\ maxVal != null
   * @effects update the auto-generated value of attribute <tt>attrib</tt>,
   *          specified for <tt>derivingValue</tt>, using
   *          <tt>minVal, maxVal</tt>
   */
  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(DAttr attrib,
      Tuple derivingValue, Object minVal, Object maxVal)
      throws ConstraintViolationException {
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)
          idCounter = maxIdVal;
      }
      // TODO add support for other attributes here
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BooleanExpression other = (BooleanExpression) obj;
    if (id != other.id)
      return false;
    return true;
  }


  @Override
  public String toString() {
    return String.format("BooleanExpression: %s (%s %s %s)", 
        (description!=null && description.length()>0) ? "\""+description+"\"" : "" , rootFunc, op, value);
  }
}
