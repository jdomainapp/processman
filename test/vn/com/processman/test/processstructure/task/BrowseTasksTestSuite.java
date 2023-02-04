package vn.com.processman.test.processstructure.task;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import vn.com.processman.test.common.RegisterClass;

@RunWith(Suite.class)
@SuiteClasses({
  RegisterClass.class,
  //GetTaskIdIfBeforeLastTest.class,
  //BrowseTasksValidationTest.class,
  //BrowseTasksRandomTest.class,
  //BrowseTasksAllTest.class
  BrowseTasksValidation2Test.class
})
public class BrowseTasksTestSuite {

}
