package vn.com.processman.test.processapplication.orgunit;

import org.junit.Test;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;

public class BrowseOrgUnitsValidation2Test extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    browseValidation2(OrgUnit.class);
  }
}
