package vn.com.processman.test.dsecurity;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dodm.dom.DOM;
import jda.modules.security.def.DomainUser;
import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.test.TestProcessMan;

/**
 * @overview 
 *  Transform {@link DomainUser} to {@link Teacher}
 *  
 * @author dmle
 *
 */
public class TransformDomainUserToTeacherTest extends TestProcessMan {
  
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
    
    printf("DomainUser: ");
    String login="quantv";
    DomainUser user = dom.retrieveObject(DomainUser.class, DomainUser.Attribute_login, Op.EQ, login);

    if (user == null) {
      printf("User not found: %s%n", login);
    } else {
      // transform
      printf("%s%n", user);
      printf("Transforming to teacher%n");

      dom.transformObjectToASubType(DomainUser.class, user, Teacher.class);
      
      printf("...done%n");
    }
  }
}
