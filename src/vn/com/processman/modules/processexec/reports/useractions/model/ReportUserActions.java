package vn.com.processman.modules.processexec.reports.useractions.model;

import java.util.ArrayList;
import java.util.Collection;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.setup.data.processstructure.Actions;
import vn.com.processman.util.model.Semester;
import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.security.DomainUser;
import domainapp.basics.modules.report.model.meta.Output;
import domainapp.basics.util.Toolkit;
import domainapp.basics.util.Tuple;
import domainapp.modules.report.model.Report;

/**
 * @overview
 *  Report on {@link DomainUser} and their {@link Actions} 
 *  
 * @author dmle
 *
 * @version 1.2
 */
@DClass(serialisable=false)
public class ReportUserActions implements Report {

  public static final String A_orgUnit = "orgUnit";
  public static final String A_semester = "semester";
  public static final String A_year = "year";
  public static final String A_userLogin = "userLogin";
  public static final String A_userActionInfos = "userActionInfos";

  @DAttr(name="id", type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
//  @DAssoc(ascName = OrgUnit.Assoc_OrgUnitAndProcessApplication, role = "processApplication", 
//      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
//      associate = @Associate(type = OrgUnit.class, cardMin = 1, cardMax = 1))
  //@Input()
  @DAttr(name=A_orgUnit,type=Type.Domain, optional=false, mutable=false, defaultValueFunction=true)
  private OrgUnit orgUnit;  
  
  //@Input()
  @DAttr(name=A_userLogin,type=Type.String, length=30, optional=true)
  private String userLogin;

  //@Input()
  @DAttr(name=A_semester, type=Type.Domain,length=50, defaultValueFunction=true, optional=false)
  private Semester semester;
  
  //@Input()
  @DAttr(name=A_year, type=Type.Integer, defaultValueFunction=true, optional=false)
  private Integer year;
  
  // retrieve the relevant UserActionInfo objects and use them as output
  @Output(filter=ReportUserActionsFilter.class, outputClass=UserActionInfo.class)
  @DAttr(name=A_userActionInfos,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=UserActionInfo.class),auto=true)
  @DAssoc(ascName="reportUserActions-and-userActionInfo",ascType=AssocType.One2Many,
    endType=AssocEndType.One,role="reportUserActions",
    associate=@Associate(type=UserActionInfo.class,cardMin=0,cardMax=MetaConstants.CARD_MORE))
  private Collection<UserActionInfo> userActionInfos;
  private int userActionInfosCount;
  
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public ReportUserActions(
      // Integer id, : auto-attribute
      OrgUnit orgUnit, String userLogin, Semester semester, Integer year) {
    this.id = nextID(null);
    this.orgUnit = orgUnit;
    this.userLogin = userLogin;
    this.semester = semester;
    this.year = year;
  }

  // util methods
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter) {
        idCounter = num;
      }
      return currID;
    }
  }

  /**
   * @requires minVal != null /\ maxVal != null
   * @effects update the auto-generated value of attribute <tt>attrib</tt>,
   *          specified for <tt>derivingValue</tt>, using
   *          <tt>minVal, maxVal</tt>
   */
  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(DAttr attrib,
      Tuple derivingValue, Object minVal, Object maxVal)
      throws ConstraintViolationException {
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)
          idCounter = maxIdVal;
      }
      // TODO add support for other attributes here
    }
  }

  public int getId() {
    return id;
  }
 
  public String getUserLogin() {
    return userLogin;
  }

  public void setUserLogin(String userName) {
    this.userLogin = userName;
  }

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

  /**
   * @effects 
   *  return {@link Semester} suitable for the current time of year
   */
  @DOpt(type=DOpt.Type.DefaultValueFunction) @AttrRef(value=A_semester)
  public static Semester getDefaultSemester() {
    return Semester.getSemesterFor(Toolkit.getCurrentSeason());
  }
  
  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }
  
  @DOpt(type=DOpt.Type.DefaultValueFunction) @AttrRef(value=A_year)
  public static int getDefaultYear() {
    return Toolkit.getCurrentYear();
  }
  
  public OrgUnit getOrgUnit() {
    return orgUnit;
  }

  @DOpt(type=DOpt.Type.DefaultValueFunction) @AttrRef(value=A_orgUnit)
  public static OrgUnit getDefaultOrgUnit(DOMBasic dom) throws NotFoundException, DataSourceException {
    return OrgUnit.getDefaultOrgUnit(dom);
  }

  /** association methods: {@link #userActionInfos} */
  
  @DOpt(type = DOpt.Type.Getter) @AttrRef(value=A_userActionInfos)
  public Collection<UserActionInfo> getUserActionInfos() {
    return userActionInfos;
  }
  
//  @Metadata(type = Metadata.Type.MethodValueAdder)
//  public boolean addUserActionInfo(UserActionInfo userActionInfo) {
//    if (!userActionInfos.contains(userActionInfo)) {
//      userActionInfos.add(userActionInfo);
//    }
//
//    return false;
//  }

//  @Metadata(type = Metadata.Type.MethodValueAdderNew)
//  public boolean addNewUserActionInfo(
//      UserActionInfo userActionInfo) {
//    userActionInfos.add(userActionInfo);
//    userActionInfosCount++;
//
//    return false;
//  }

  @DOpt(type = DOpt.Type.LinkAdder) @AttrRef(value=A_userActionInfos)
  public boolean addUserActionInfo(
      Collection<? extends UserActionInfo> userActionInfos) {
    //boolean added = false;
    if (this.userActionInfos == null) this.userActionInfos = new ArrayList<>();
    
    for (UserActionInfo userActionInfo : userActionInfos) {
      if (!this.userActionInfos.contains(userActionInfo)) {
        this.userActionInfos.add(userActionInfo);
        //if (!added) added = true;
      }
    }

//    if (added) {
//      return true;
//    } else {
//      return false;
//    }
    return false;
  }

//  @Metadata(type = Metadata.Type.MethodValueAdderNew)
//  public boolean addNewUserActionInfo(
//      Collection<UserActionInfo> userActionInfos) {
//    this.userActionInfos.addAll(userActionInfos);
//    userActionInfosCount += userActionInfos.size();
//
//    return false;
//  }

//  @Metadata(type = Metadata.Type.MethodValueRemover)
//  public boolean removeUserActionInfo(
//      UserActionInfo userActionInfo) {
//    boolean removed = userActionInfos.remove(userActionInfo);
//    if (removed) {
//      userActionInfosCount--;
//    }
//    return false;
//  }

  // TODO: MethodValueUpdater?

  @DOpt(type = DOpt.Type.LinkCountGetter)
  public Integer getUserActionInfosCount() {
    return userActionInfosCount;
  }

  @DOpt(type = DOpt.Type.LinkCountSetter)
  public void setUserActionInfosCount(int userActionInfosCount) {
    this.userActionInfosCount = userActionInfosCount;
  }
  /** END association methods: {@link #userActionInfos} */
  
  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.modules.report.model.Report#clearOutput()
   */
  @Override /**{@link Report} */
  public void clearOutput() {
    if (userActionInfos != null) {
      userActionInfos.clear();
    } else {
      userActionInfos = new ArrayList();
    }    
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ReportUserActions other = (ReportUserActions) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()+" (" + id + ")";
  }

}