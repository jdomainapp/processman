package vn.com.processman.test.processstructure.task;

import org.junit.Test;

import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;

public class BrowseTasksAllTest extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    browseFirstToLast(Task.class);
  }
}
