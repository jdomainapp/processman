package vn.com.processman.test.dsecurity.browser;

import org.junit.Test;

import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.test.processstructure.browser.BrowserMasterTest;
import domainapp.basics.model.security.DomainUser;

public class BrowseTeachersAllTest extends BrowserMasterTest {
  
  @Test
  public void testName() throws Exception {
    
    registerClasses(new Class[] {
        DomainUser.class, 
        Teacher.class
    });
    
    browseFirstToLast(Teacher.class);
  }
}
