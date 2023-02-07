/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.dsecurity.controller.command;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.Role;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.UpdateObjectAndManyAssociatesDataControllerCommand;
import vn.com.processman.modules.dsecurity.model.Teacher;

/**
 * @overview
 *  Transform {@link DomainUser} to {@link Teacher} or vice versa depending on the input roles.
 *  
 * @author dmle
 */
public class UpdateDomainUserHierarchyCommand<C> extends UpdateObjectAndManyAssociatesDataControllerCommand<C> {

  public UpdateDomainUserHierarchyCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   * further effects on <tt>updatedObj</tt> are specified as follow:
   * <pre>
   *  if user updates roles to remove teacher-role
   *    transform user to become {@link DomainUser}  
   *  else if user updates roles to include teacher-role
   *    transform user to become {@link Teacher}
   *  else
   *    no further effects 
   * </pre>
   */
  @Override
  protected void postUpdateManyAssocState(C updatedObj, Map<DAttr, Object[]> oldManyValMap) throws NotPossibleException {
    DAttr manyAttrib;
    String manyAttribName;
    Object[] oldVal = null;
    Collection newVal = null;
    DSMBasic dsm = getDodm().getDsm();

    for (Entry<DAttr,Object[]> e : oldManyValMap.entrySet()) {
      manyAttrib = e.getKey();
      manyAttribName = manyAttrib.name();
      if (manyAttribName.equals(DomainUser.Attribute_theRoles)) {
        // roles were changed, lets see...
        oldVal = e.getValue();
        newVal = (Collection) dsm.getAttributeValue(updatedObj, manyAttribName);

        break; // only interested in role
      }
    }

    boolean oldValHasTeacherRole = false, newValHasTeacherRole = false;
    Role r;
    if (oldVal != null) {
      for (Object o : oldVal) {
        r = (Role) o;
        if (r.getName().equals(Teacher.RoleName)) {
          oldValHasTeacherRole = true; 
          break;
        }
      }
    }
    
    if (newVal != null) {
      for (Object o : newVal) {
        r = (Role) o;
        if (r.getName().equals(Teacher.RoleName)) {
          newValHasTeacherRole = true; 
          break;
        }
      }
    }
    
    try {
      if (oldValHasTeacherRole && !newValHasTeacherRole) {
        // transform teacher to domain user
        // get the actual Teacher first. This is needed because we are working from the DomainUser module
        
        Teacher teacher = retrieveTeacherSubtypeOf((DomainUser)updatedObj); 
        transformTeacherToDomainUser(teacher);
        
      } else if (!oldValHasTeacherRole && newValHasTeacherRole) {
        // transform domain user to teacher
        transformDomainUserToTeacher((DomainUser) updatedObj);
      }
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, new Object[] {""});
    }
  }

  /**
   * @effects 
   *  retrieve and return the actual {@link Teacher} sub-type object of user
   */
  private Teacher retrieveTeacherSubtypeOf(DomainUser user) throws NotFoundException, DataSourceException {
    return getDodm().getDom().retrieveObject(Teacher.class, Teacher.Attribute_login, Op.EQ, user.getLogin());
  }

  /**
   * @effects 
   *  transform user to become {@link Teacher}
   *  <br>Throws DataSourceException if failed to update data source.
   *    
   *  <br><i>Note</i>: No actual {@link Teacher} object is created by this method and so if application needs to access this object
   *  then it is required to reload objects from the data source.    
   */
  private void transformDomainUserToTeacher(DomainUser user) throws DataSourceException {
    /*
      insert a new record in the Teacher table that references user (without creating a new DomainUser record)
      DOM.transformObjectToASubtype(user, Teacher.class):
        - use osm.transformObjectToASubtype(Object, Class subType)
     */
    DOM dom = (DOM) getDodm().getDom();
    
    Class subType = Teacher.class;
    
    dom.transformObjectToASubType(DomainUser.class, user, subType);
  }

  /**
   * @effects 
   *  transform teacher to become {@link DomainUser}  
   *  <br>Throws DataSourceException if failed to update data source.
   *  
   *  <br><i>Note</i>: No actual {@link DomainUser} object is created by this method and so if application needs to access this object
   *  then it is required to reload objects from the data source.    
   */
  private void transformTeacherToDomainUser(Teacher teacher) throws NotFoundException, SecurityException, DataSourceException {
    /* 
     1/ remove all the association links between teacher and other associated objects (if any)
     2/ remove the corresponding record in the Teacher table (but without touching the DomainUser record)
     
     Specifics:
     1/ use Teacher's data controller to remove association links for teacher
     
     2/ DOM.transformObjectToSuperType(teacher, DomainUser)
       - use osm.transformObjectToSuperType(Object, Class superType)
           remove the corresponding record in the Teacher table (but without touching the DomainUser record)
           
     */
    DOM dom = (DOM) getDodm().getDom();
    
    // 1
    //DataController<Teacher> tDctl = lookUpRootDataController(Teacher.class);
    dom.removeAssociationLinks(Teacher.class, teacher);
    
    // 2
    Class supType = DomainUser.class;
    
    dom.transformObjectToSuperType(Teacher.class, teacher, supType);
  }

}
