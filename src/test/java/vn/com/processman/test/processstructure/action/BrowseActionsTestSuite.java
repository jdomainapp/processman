package vn.com.processman.test.processstructure.action;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import vn.com.processman.test.common.RegisterClass;

@RunWith(Suite.class)
@SuiteClasses({
  RegisterClass.class,
  // BrowseActionsValidationTest.class,
  BrowseActionsValidation2Test.class,
})
public class BrowseActionsTestSuite {

}
