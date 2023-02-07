package vn.com.processman.test.processapplication.subjectAction;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Query;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.test.TestProcessMan;
import vn.com.processman.util.DomainToolKit;

/**
 * @overview 
 *  Load a {@link SubjectAction}s of a given {@link Action4Subject} twice to test the object buffer's function
 *  
 * @author dmle
 */
public class RetrieveSubjectActionsTwiceTest extends TestProcessMan {
  
  @Test
  public void doTest() throws Exception {
    registerClasses(new Class[] {
        Action.class,
        Action4Subject.class, 
        SubjectAction.class
    });
    
    DODMBasic dodm = instance.getDODM();
    DOM dom = (DOM) dodm.getDom();
    DSMBasic dsm = dom.getDsm();
    
    printf("%n");

    // (1) Load an Action4Subject
    // the def action
    String actCode = "5.5.4";
    Action4Subject defAct = dom.retrieveObject(Action4Subject.class,
        new Query(QueryToolKit.createObjectExpression(dsm, Action4Subject.class, Action4Subject.A_code, Op.EQ, actCode)));

    if (defAct == null) {
      printf("Action4Subject not found: code = %s%n", actCode);
    } else {
      printf("Definitive action-for-subject: %s%n", defAct);
      
      //dom.setDebugOn(true);
      Action4Subject act = dom.retrieveObject(Action4Subject.class, 
          Action.A_def,
          Op.EQ, 
          defAct);      
      //dom.setDebugOn(false);

      printf("SubjectActions recorded in %s:%n", act);
      printObjects(Action4Subject.class, act.getSubjectActions());
      
      if (act == null) {
        printf("Action4Subject not found: def = %s%n", defAct);
      } else {
        printf("Derived action-for-subject: %s%n", act);
        /* (2) load SubjectActions: divided into 2 steps
         * (2.1) load one SubjectAction
         * (2.2) load all SubjectAction (to test if the previously loaded SubjectAction is loaded again)
         */
        
        printf("%nLoading first SubjectAction...%n");
        SubjectAction first = dom.retrieveAssociatedObject(act, Action4Subject.class, 
            SubjectAction.class, Action4Subject.Assoc_Action4SubjectAndSubjectAction);
        if (first == null) {
          printf("Could not find first object of SubjectAction that is linked to %s%n", act);
        } else {
          printf("%s%n", first);
          
          printf("SubjectActions recorded in %s:%n", act);
          printObjects(Action4Subject.class, act.getSubjectActions());
        }
        
        printf("%nLoading ALL SubjectActions...%n");

        Collection<SubjectAction> subjActs = DomainToolKit.retrieveSubjectActionsOf(dodm, act);
        if (subjActs != null) {
          printf("Results:%n");
          printObjects(Action4Subject.class, subjActs);
          
          printf("SubjectActions recorded in %s:%n", act);
          printObjects(Action4Subject.class, act.getSubjectActions());
        } else {
          printf("not found!%n");
        }
      }      
    }

  }
}
