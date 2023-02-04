package vn.com.processman.modules.dsecurity.model;

import vn.com.processman.util.model.Semester;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.security.DomainUser;
import domainapp.basics.model.security.Role;
import domainapp.basics.model.security.UserRole;

/**
 * @overview
 *  Similar to {@link UserRole} but supports attributes for historical data.
 *  
 * @author dmle
 *
 * @version 3.3 
 * @todo not yet finished! (not yet used in modules)
 */
public class UserRoleHistory {

  public static final String A_semester = "semester";
  public static final String A_year = "year";
  
  public static final String A_role = "role";
  public static final String A_user = "user";
  
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,mutable=false,length=6)
  private int id; 
  private static int idCounter;
  
  @DAttr(name=A_user,type=Type.Domain,optional=false,length=6)
  @DAssoc(ascName=DomainUser.Association_WithUserRole,role="userRole",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=DomainUser.class,cardMin=1,cardMax=1)
    ,dependsOn=true // v3.2
    )
  private DomainUser user; 
  
  @DAttr(name=A_role,type=Type.Domain,optional=false,length=20)
  @DAssoc(ascName="role-for-users",role="userRole",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Role.class,cardMin=1,cardMax=MetaConstants.CARD_MORE)) 
  private Role role;
  
  @DAttr(name=A_semester, type=Type.Domain,length=50, optional=false)
  private Semester semester;
  
  @DAttr(name=A_year, type=Type.Integer, defaultValueFunction=true, optional=false)
  private Integer year;
  
  @DOpt(type = DOpt.Type.DataSourceConstructor)
  public UserRoleHistory(Integer id, DomainUser user, Role role, Semester semester, Integer year) {
    this.id = nextID(id);
    this.user=user;
    this.role=role;
    
    this.semester = semester;
    this.year = year;
  }
  
  @DOpt(type = DOpt.Type.ObjectFormConstructor)
  @DOpt(type = DOpt.Type.RequiredConstructor)
  public UserRoleHistory(DomainUser user, Role role, Semester semester, Integer year) {
    this(null, user, role,semester, year);
  }

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }
  
  @DOpt(type = DOpt.Type.AutoAttributeValueGen) @AttrRef(value="id")
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) 
        idCounter=num;
      
      return currID;
    }
  }
  
}
