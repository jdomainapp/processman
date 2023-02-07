/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.dsecurity.controller.command;

import java.util.Collection;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.Role;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.CreateObjectAndManyAssociatesDataControllerCommand;
import vn.com.processman.modules.dsecurity.model.Teacher;

/**
 * @overview
 *  Create {@link DomainUser} or {@link Teacher} depending on the input roles.
 *  
 * @author dmle
 */
public class CreateDomainUserHierarchyCommand<C> extends CreateObjectAndManyAssociatesDataControllerCommand<C> {

  public CreateDomainUserHierarchyCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   *  check the user input data; 
   *  if exist teacher role 
   *    create user as the sub-type {@link Teacher}
   *  else
   *    create user normally using type {@link DomainUser}
   */
  /* (non-Javadoc)
   * @see domainapp.controller.datacontroller.command.CreateObjectCommand#createObject()
   */
  @Override
  protected C createObject() throws ConstraintViolationException,
      NotPossibleException, NotFoundException, DataSourceException {
    DataController<C> dctl = getDataController();
    
    // get roles from user input, check if teacher role is among them
    Collection<Role> roles = (Collection<Role>) dctl.getDataFieldValue(DomainUser.Attribute_theRoles);
    
    // create teacher or domain user (depending on the presence of teacher role
    boolean isTeacher = false;
    for (Role role : roles) {
      if (role.getName().equals("GIAV")) {
        // teacher
        isTeacher = true; break;
      }
    }
    
    C object;
    if (isTeacher) {
      // create teacher object
      object = (C) createSubTypeObject(Teacher.class);
    } else {
      // create domain user object
      object = super.createObject();
    }
    
    return object;
  }
}
