package vn.com.processman.test.teaching;

import java.util.Collection;

import org.junit.Test;

import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.test.TestProcessMan;
import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.model.meta.DAttr;
import domainapp.core.dodm.dom.DOM;

/**
 * @overview 
 *  Load values of the multi-source derived attribute {@link SubjectBySemester.displayInfo}, one of whose  
 *  attribute (<tt>code</tt>) is in turn derived from another domain-typed attribute (<tt>subject</tt>)
 *  
 * @author dmle
 */
public class LoadSubjectBySemesterDisplayInfoTest extends TestProcessMan {
  
  @Test
  public void doTest() throws Exception {
    registerClasses(new Class[] {
        Subject.class,
        SubjectBySemester.class
    });
    
    DOM dom = (DOM) instance.getDom();
    DSMBasic dsm = instance.getDsm();
    
    String attribName = SubjectBySemester.A_displayInfo;
    printf("Attribute: %s.%s%n", SubjectBySemester.class.getSimpleName(), attribName);
    
    DAttr attrib = dsm.getDomainConstraint(SubjectBySemester.class, attribName);
    Collection vals = dom.loadAttributeValues(SubjectBySemester.class, attrib);

    if (vals == null) {
      printf("  No values found%n");
    } else {
      // values found
      printObjects(String.class, vals);
    }
  }
}
