package vn.com.processman.test.processapplication.orgunit;

import org.junit.Test;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;

public class BrowseOrgUnitsValidationTest extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    browseValidation(OrgUnit.class);
  }
}
