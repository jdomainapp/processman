package vn.com.processman.modules.processapplication.model;

import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.util.model.Semester;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Toolkit;
import domainapp.basics.util.Tuple;

/**
 * @overview
 *  Represents an application of a {@link Process} to an {@link OrgUnit}. 
 *    
 * @author dmle
 */
public class ProcessApplication {
  public static final String A_id = "id";
  public static final String A_process = "process";
  public static final String A_orgUnit = "orgUnit";
  public static final String A_semester = "semester";
  public static final String A_year = "year";
  public static final String[] A_retrievalByOrgUnit = 
    {ProcessApplication.A_orgUnit, ProcessApplication.A_semester, ProcessApplication.A_year};
  
  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
  /**association {@link OrgUnit#Assoc_OrgUnitAndProcessApplication}*/
  @DAssoc(ascName = OrgUnit.Assoc_OrgUnitAndProcessApplication, role = "processApplication", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = OrgUnit.class, cardMin = 1, cardMax = 1))
  @DAttr(name=A_orgUnit,type=Type.Domain, optional=false)
  private OrgUnit orgUnit;  /**the {@link Process} object */
  
  @DAssoc(ascName = Process.Assoc_ProcessAndProcessApplication, role = "processApplication", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Process.class, cardMin = 1, cardMax = 1),
      dependsOn=true)
  @DAttr(name=A_process,type=Type.Domain, optional=false)
  private Process process;
  
  @DAttr(name=A_semester, type=Type.Domain,length=50, optional=false)
  private Semester semester;
  
  @DAttr(name=A_year, type=Type.Integer, defaultValueFunction=true, optional=false)
  private Integer year;

//  @Association(name = Process.Assoc_ProcessAndProcessApplication, role = "processApplication", 
//      type = AssocType.One2Many, endType = AssocEndType.Many, 
//      associate = @AssocEnd(type = Process.class, cardMin = 1, cardMax = 1))
//  @DomainConstraint(name=A_process,type=Type.Domain, optional=false)
//  private Process processDetails;

//  /**derived from {@link #process}*/
//  @DomainConstraint(name=Process.A_code, type=Type.String, length=100, auto=true, mutable=false, serialisable=false)
//  private String code;
//  
//  /**derived from {@link #process}*/
//  @DomainConstraint(name=Process.A_name, type=Type.String, length=255, auto=true, mutable=false, serialisable=false)
//  private String name;
  
  // construct: from data source
  public ProcessApplication(Integer id, OrgUnit orgUnit, Process process, Semester semester, Integer year) {
    this.id = nextID(id);
    this.orgUnit = orgUnit;
    this.process = process;
    
    this.semester = semester;
    this.year = year;
    
//    this.code = process.getCode();
//    this.name = process.getName();
  }

  // construct: from object form
  public ProcessApplication(OrgUnit orgUnit, Process process, Semester semester, Integer year) {
    this(null, orgUnit, process, semester, year);
  }

  // construct: from object form (process last)
  public ProcessApplication(OrgUnit orgUnit, Semester semester, Integer year, Process process) {
    this(null, orgUnit, process, semester, year);
  }
  
  public Process getProcess() {
    return process;
  }

  public void setProcess(Process process) {
    this.process = process;
    
//    this.code = process.getCode();
//    this.name = process.getName();
  }

  public OrgUnit getOrgUnit() {
    return orgUnit;
  }

  public void setOrgUnit(OrgUnit orgUnit) {
    this.orgUnit = orgUnit;
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

  @DOpt(type=DOpt.Type.DefaultValueFunction) @AttrRef(value=A_year)
  public static int getDefaultYear() {
    return Toolkit.getCurrentYear();
  }
  
//  public String getCode() {
//    return code;
//  }
//
//  public String getName() {
//    return name;
//  }

  public int getId() {
    return id;
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
    ProcessApplication other = (ProcessApplication) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ProcessApplication (" + id + ", " + process + ", " + orgUnit + ")";
  }

}
