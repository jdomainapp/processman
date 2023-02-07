package vn.com.processman.test.processstructure.task;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;

public class GetTaskIdIfBeforeLastTest extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    Tuple2<Oid,Oid> idRange = getOidRange(Task.class);
    Oid last = idRange.getSecond();
    
    Oid oid = getIdFirstBefore(Task.class, last);
    
    printf("Last oid: %s%n  --> second last: %s%n", last, oid);
  }
}
