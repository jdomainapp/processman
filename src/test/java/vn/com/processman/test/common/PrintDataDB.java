package vn.com.processman.test.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import vn.com.processman.test.TestProcessMan;


public class PrintDataDB extends TestProcessMan {
  
  @Test
  public void doTest() throws DataSourceException { instance.printDataDB(); }
  
}
