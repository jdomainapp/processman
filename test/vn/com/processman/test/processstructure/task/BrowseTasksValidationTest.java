package vn.com.processman.test.processstructure.task;

import org.junit.Test;

import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;

public class BrowseTasksValidationTest extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    browseValidation(Task.class);
  }
}
