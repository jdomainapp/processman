package vn.com.processman.test.dsecurity.browser;

import org.junit.Test;

import vn.com.processman.test.processstructure.browser.BrowserMasterTest;
import domainapp.basics.model.security.DomainUser;

public class BrowseDomainUsersAllTest extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    
    registerClasses(new Class[] {
        DomainUser.class 
    });
    
    browseFirstToLast(DomainUser.class);
  }
}
