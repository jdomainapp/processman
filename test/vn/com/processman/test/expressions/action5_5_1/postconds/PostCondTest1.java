package vn.com.processman.test.expressions.action5_5_1.postconds;

import org.junit.Test;

import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.test.expressions.action5_5_1.ExpressionTest5_5_1;

/**
 * @overview 
 *  Test a post-condition 
 *  
 * @author dmle
 */
public class PostCondTest1 extends ExpressionTest5_5_1 {
  
  @Test
  public void doTest() throws Exception {
    setUp();

    // choose one subject action to use
    int index = 0;
    setSubjAct(index);

    // the post-condition to test
    int condIndex = 0;
    BooleanExpression exp = getPostCondition(getAction(), condIndex);
    
    evalExpression(exp);
  }
}
