package vn.com.processman.test.expressions.action5_5_1.preconds;

import org.junit.Test;

import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.test.expressions.action5_5_1.ExpressionTest5_5_1;

/**
 * @overview 
 *  Test a precondition 
 *  
 * @author dmle
 *
 */
public class PreCondTest3 extends ExpressionTest5_5_1 {
  
  @Test
  public void doTest() throws Exception {
    setUp();

    // choose one subject action to use
    int index = 0;
    setSubjAct(index);

    // the pre-condition to test
    int condIndex = 2;
    BooleanExpression exp = getPreCondition(getAction(), condIndex);
    
    evalExpression(exp);
    
  }
}
