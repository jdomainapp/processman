package vn.com.processman.test.expressions;

import vn.com.processman.modules.processsconstraint.controller.command.BooleanExpressionGenerator;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.test.TestProcessMan;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.query.Expression.Op;

/**
 * @overview 
 *  Retrieve a {@link BooleanExpression} by id and evaluates it.
 *  
 * @author dmle
 *
 */
public abstract class ExpressionTest extends TestProcessMan {
  
  /**
   * @effects 
   *  set up the expressions
   */
  protected void setUp() throws Exception {
    
    // register schema
    registerDomainSchema();
    
    // generate test expression objects and register them to dodm
    BooleanExpressionGenerator expGen = new BooleanExpressionGenerator();
    DODMBasic dodm = getDODM();
    expGen.init(dodm);
    expGen.genObjects(dodm);
  }
  
  /**
   * @effects 
   *  retrieve expression whose id is <tt>expId</tt> and return it
   */
  protected BooleanExpression getExpression(int expId) throws Exception {
    DOMBasic dom = instance.getDom();
    
    BooleanExpression exp = dom.getObject(BooleanExpression.class, BooleanExpression.A_id, Op.EQ, expId);
    
    return exp;
  }
  
  /**
   * @effects 
   *  evaluate {@link BooleanExpression} whose id is <tt>expId</tt> and print the result
   */
  protected void evalExpression(int expId) throws Exception {
    // retrieve expression from the system
    BooleanExpression exp = getExpression(expId);
    
    printf("%n%s.evalExpression(expId=%d)%n", this.getClass().getSimpleName(), expId);
    
    evalExpression(exp);
  }
  
  /**
   * @effects 
   *  evaluate <tt>exp</tt> and print the result
   */
  public void evalExpression(BooleanExpression exp) {
    printf("%n%s.evalExpression(exp: \"%s\"):%n  %s", this.getClass().getSimpleName(), exp.getDescription(), exp);
    
    if (exp != null) {
      // evaluate the expression
      Boolean result;
      try {
        result = exp.eval();
        
        printf("%n  result: %b [%b]%n", result, exp.getResult());
      } catch (NotPossibleException e) {
        // failed
        result = exp.getResult();
        printf("%n  result: %b [%b]%n", result, exp.getResult());
        
        printf("%n  ...With Error:%n");
        e.printStackTrace();
      }
    } else {
      // expression not found
      printf("Error: expression is null%n");
    }
  }


  /**
   * @requires 
   *  action != null
   * @effects 
   *  if exists in <tt>action.preConditions</tt> an {@link BooleanExpression} at index = <tt>condIndex</tt>
   *    return it
   *  else
   *    throws NotFoundException
   */
  public BooleanExpression getPreCondition(Action action,
      int condIndex) throws NotFoundException {
    if (action == null)
      return null;
    
    if (action.hasPreCondition(condIndex)) {
      return action.getPreCondition(condIndex);
    } else {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {BooleanExpression.class.getSimpleName(), "index="+condIndex});
    }
  }
  
  /**
   * @requires 
   *  action != null
   * @effects 
   *  if exists in <tt>action.postConditions</tt> an {@link BooleanExpression} at index = <tt>condIndex</tt>
   *    return it
   *  else
   *    throws NotFoundException
   */
  public BooleanExpression getPostCondition(Action action,
      int condIndex) throws NotFoundException {
    if (action == null)
      return null;
    
    if (action.hasPostCondition(condIndex)) {
      return action.getPostCondition(condIndex);
    } else {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {BooleanExpression.class.getSimpleName(), "index="+condIndex});
    }
  }
}
