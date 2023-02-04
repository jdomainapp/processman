package vn.com.processman.test.common;

import org.junit.Test;

import vn.com.processman.test.TestProcessMan;
import domainapp.basics.exceptions.DataSourceException;


public class PrintDataDB extends TestProcessMan {
  
  @Test
  public void doTest() throws DataSourceException { instance.printDataDB(); }
  
}
