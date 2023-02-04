package vn.com.processman.test.common;

import org.junit.Test;

import vn.com.processman.test.TestProcessMan;
import domainapp.basics.setup.Cmd;
import domainapp.basics.setup.SetUpBasic;

public class CreateTestData extends TestProcessMan {
  
  @Test
  public void doTest() throws Exception {
    SetUpBasic su = getDefaultSetUp();
    
    su.run(Cmd.PostSetUpDb, null);
  }
  
}
