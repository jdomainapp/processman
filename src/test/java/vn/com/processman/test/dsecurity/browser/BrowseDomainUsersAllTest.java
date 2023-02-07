package vn.com.processman.test.dsecurity.browser;

import org.junit.Test;

import jda.modules.security.def.DomainUser;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;

public class BrowseDomainUsersAllTest extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    
    registerClasses(new Class[] {
        DomainUser.class 
    });
    
    browseFirstToLast(DomainUser.class);
  }
}
