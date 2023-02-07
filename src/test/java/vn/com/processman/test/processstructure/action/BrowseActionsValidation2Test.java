package vn.com.processman.test.processstructure.action;

import org.junit.Test;

import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;

public class BrowseActionsValidation2Test extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    browseValidation2(Action.class);
  }
}
