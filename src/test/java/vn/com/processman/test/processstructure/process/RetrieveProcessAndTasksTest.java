package vn.com.processman.test.processstructure.process;

import java.util.Map;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.model.Oid;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.test.TestProcessMan;

/**
 * @overview 
 *  Retrieve a {@link Process} and its {@link Task}s, {@link Action}s
 *  
 * @author dmle
 *
 */
public class RetrieveProcessAndTasksTest extends TestProcessMan {
  
  /**
   * @effects 
   *  set up the expressions
   */
  @Test
  public void setUp() throws Exception {
    // register schema
    registerDomainSchema();
    
    DOMBasic dom = instance.getDODM().getDom();

    // retrieve a process
    printf("%nProcess: ");
    Process p = dom.retrieveObject(Process.class, Process.A_code, Op.EQ, "dt-05");

    printf("%n%s%n%n", p);
    
    // retrieve tasks 
    Map<Oid,Task> tasks = dom.retrieveAssociatedObjects(p, Process.class, Task.class, Process.Assoc_ProcessAndTask);
    
    if (tasks == null) {
      // no tasks found
      printf("Process has no tasks%n");
    } else {
      //printObjects(Task.class, tasks.values());
      
      printObjects(Task.class, p.getTasks());
    }
    
    printf("...done%n");
  }
}
