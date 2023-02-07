package vn.com.processman.test.processstructure.process;

import java.util.Map;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.model.Oid;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.test.TestProcessMan;

/**
 * @overview 
 *  Retrieve a {@link OrgUnit} and the {@link Process}es that it has created.
 *  
 * @author dmle
 *
 */
public class RetrieveOrgUnitAndProcessesTest extends TestProcessMan {
  
  /**
   * @effects 
   *  set up the expressions
   */
  @Test
  public void setUp() throws Exception {
    // register schema
    registerDomainSchema();
    
    DOMBasic dom = instance.getDODM().getDom();

    // retrieve an org unit
    printf("OrgUnit: ");
    OrgUnit u = dom.retrieveObject(OrgUnit.class, OrgUnit.A_name, Op.MATCH, "%CNTT");

    printf("%s%n", u);
    
    // retrieve tasks 
    Map<Oid,Process> processes = dom.retrieveAssociatedObjects(u, OrgUnit.class, Process.class, OrgUnit.Assoc_OrgUnitCreateProcess);
    
    if (processes == null) {
      // no objects found
      printf("Org unit has created no processes%n");
    } else {
      printObjects(Process.class, processes.values());
    }
    
    printf("...done%n");
  }
}
