package vn.com.processman.test.dsecurity;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dodm.dom.DOM;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.Role;
import jda.modules.security.def.UserRole;
import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.test.TestProcessMan;

/**
 * @overview 
 *  Load a {@link DomainUser} together with its associated roles.
 *  
 * @author dmle
 */
public class LoadDomainUserTest extends TestProcessMan {
  
  @Test
  public void doTest() throws Exception {
    registerClasses(new Class[] {
        DomainUser.class, 
        Teacher.class
    });
    
    DOM dom = (DOM) instance.getDODM().getDom();
    
    printf("%n");
    
    String[] logins={"duclm", "hoahk", "thangnx"};
    for (String login : logins) {
      DomainUser user = dom.retrieveObject(DomainUser.class, DomainUser.Attribute_login, Op.EQ, login);
  
      if (user == null) {
        printf("User not found: %s%n", login);
      } else {
        // load roles
        printf("%s%n", user);
        printf("Loading the roles%n");
  
        //Oid oid = dom.lookUpObjectId(DomainUser.class, user);
        
        //dom.loadManyToManyAssociatesOf(DomainUser.class, user, oid);
        
        printObjects(UserRole.class, user.getRoles());
        
        printObjects(Role.class, user.getTheRoles());
        
        printf("...done%n");
      }
    }
  }
}
