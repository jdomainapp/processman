package vn.com.processman.test.processstructure;

import org.junit.Test;

import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.test.TestProcessMan;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.model.query.Expression.Op;

/**
 * @overview 
 *  Create a {@link Task4Subject}
 *  
 * @author dmle
 *
 */
public class CreateTask4SubjectTest extends TestProcessMan {
  
  /**
   * @effects 
   *  set up the expressions
   */
  @Test
  public void setUp() throws Exception {
    // register schema
    registerDomainSchema();
    
    DOMBasic dom = instance.getDODM().getDom();
    
    printf("Process: ");
    Process proc = dom.retrieveObject(Process.class, Process.A_code, Op.EQ, "dt-05");

    if (!dom.isIdRangeInitialised(Task.class)) {
      dom.retrieveMetadata(Task.class);
    }
    
    printf("%s%n", proc);

    Task4Subject t = new Task4Subject("5.5", "", "", 1, 15, proc, null, null, StatusCode.NotDone);
    
    printf("Creating object in data source: %s%n", t);

    dom.addObject(t);
    
    printf("...done%n");
  }
}
