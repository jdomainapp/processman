package vn.com.processman.test.dsecurity;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dodm.dom.DOM;
import jda.modules.security.def.DomainUser;
import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.test.TestProcessMan;

/**
 * @overview 
 *  Transform {@link Teacher} to {@link DomainUser}
 *  
 * @author dmle
 *
 */
public class TransformTeacherToDomainUserTest extends TestProcessMan {
  
  @Test
  public void doTest() throws Exception {
//    ProcessManSetUp su = (ProcessManSetUp) getDefaultSetUp();
//    
//    // register schema
//    registerDomainSchema();
    registerClasses(new Class[] {
        DomainUser.class, 
        Teacher.class
    });
    
    DOM dom = (DOM) instance.getDODM().getDom();
    
    printf("Teacher: ");
    String login="quantv";
    Teacher teacher = dom.retrieveObject(Teacher.class, Teacher.Attribute_login, Op.EQ, login);

    if (teacher == null) {
      printf("Teacher not found: %s%n", login);
    } else {
      // transform
      printf("%s%n", teacher);
      printf("Transforming back to domain user%n");

      dom.transformObjectToSuperType(Teacher.class, teacher, DomainUser.class);
      
      printf("...done%n");
    }
  }
}
