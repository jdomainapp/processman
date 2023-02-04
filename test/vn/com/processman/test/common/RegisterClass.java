package vn.com.processman.test.common;

import org.junit.Test;

import vn.com.processman.test.TestProcessMan;


public class RegisterClass extends TestProcessMan {
  
  @Test
  public void doTest() throws Exception {
    // alternative: 
    //    registerDomainSchema();
    instance.registerClasses();
  }
  
}
