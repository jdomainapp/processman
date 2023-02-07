package vn.com.processman.test.processapplication.orgunit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import vn.com.processman.test.common.RegisterClass;

@RunWith(Suite.class)
@SuiteClasses({
  RegisterClass.class,
  //BrowseOrgUnitsValidationTest.class,
  BrowseOrgUnitsValidation2Test.class,
})
public class BrowseOrgUnitsTestSuite {

}
