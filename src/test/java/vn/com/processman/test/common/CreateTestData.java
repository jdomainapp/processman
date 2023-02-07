package vn.com.processman.test.common;

import org.junit.Test;

import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import vn.com.processman.test.TestProcessMan;

public class CreateTestData extends TestProcessMan {
  
  @Test
  public void doTest() throws Exception {
    SetUpBasic su = getDefaultSetUp();
    
    su.run(Cmd.PostSetUpDb, null);
  }
  
}
