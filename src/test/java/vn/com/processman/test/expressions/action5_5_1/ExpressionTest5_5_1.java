package vn.com.processman.test.expressions.action5_5_1;

import java.util.Map;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.model.Oid;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.test.expressions.ExpressionTest;

public class ExpressionTest5_5_1 extends ExpressionTest {

  private Action4Subject defAction;
  private Action4Subject action;
  private SubjectAction subjAct; 
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    printf("%s.setUp: %n", this.getClass().getSimpleName());
    
    DOMBasic dom = getDom();
    
    // retrieve the source Action 5.5.1
    defAction = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.5.1");

    // retrieve a derived Action 5.5.1
    action = dom.retrieveObject(Action4Subject.class, Action4Subject.A_def, Op.EQ, defAction);
    
    printf("  %s%n    Derived action: %s%n", defAction, action);

    // (if not already) retrieve subject-action for derivedAction  
    Map<Oid, SubjectAction> subjActs = dom.retrieveAssociatedObjects(
        action, Action4Subject.class, SubjectAction.class, Action4Subject.Assoc_Action4SubjectAndSubjectAction);
    
    if (subjActs == null) {
      // error
      throw new NotFoundException(NotFoundException.Code.OBJECT_ASSOCIATE_NOT_FOUND, 
          new Object[] {SubjectAction.class.getSimpleName(), "", ""}); 
    } else {
      printObjects(SubjectAction.class, subjActs.values());
    }
    
//    // choose one to use for testing
//    subjAct = subjActs.values().iterator().next();
//    
//    // set the above as action's current 
//    // this is needed to test the evaluation of the pre-conditions
//    derivedAction.setSubjectAction(subjAct);
  }

  public Action4Subject getDefAction() {
    return defAction;
  }

  public Action4Subject getAction() {
    return action;
  }

  public SubjectAction getSubjAct() {
    return subjAct;
  }
  
  /**
   * @requires 
   *  {@link #setUp()} has been performed
   * @effects 
   *  sets {@link #subjAct} to the element at <tt>index</tt> in {@link #action}
   */
  public void setSubjAct(int index) throws IllegalArgumentException {
    if (action != null && action.hasSubjectActionAt(index)) {
      subjAct = action.getSubjectAction(index);
      
      action.setActiveSubjectAction(subjAct);
      
      printf("Active test object: %s%n", subjAct);
    } else {
      throw new IllegalArgumentException(
          String.format("%s.setSubjAct: invalid subject action index (expected: [0,%d]): %d", 
              this.getClass().getSimpleName(), action.getSubjectActionsCount()-1, index));
    }
  }
}
