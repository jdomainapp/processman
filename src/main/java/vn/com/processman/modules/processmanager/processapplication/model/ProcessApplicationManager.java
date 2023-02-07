/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.processmanager.processapplication.model;

import java.util.Collection;
import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.mosa.model.DDataValidator;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  A generic manager domain class for modules that are designed for managing {@link Process}, {@link ProcessApplication}, 
 *  {@link Task}s and {@link Action}s that are associated to a particular subject.
 *  
 * @author dmle
 */
@DClass(serialisable=false)
@DDataValidator(type=ValidateProcessApplication.class)
public class ProcessApplicationManager {
  
  /**extended scope definitions */
  public static final ScopeDef ScopeDefTaskApplication = new ScopeDef(
      Task.class,
      Task.A_application,Boolean.FALSE); // end ScopeDefTaskApplication
  
  public static final ScopeDef ScopeDefActionApplication = new ScopeDef(
      Action.class,
      Action.A_application,Boolean.FALSE); // end ScopeDefTaskApplication

  
  public static final String A_orgUnit = "orgUnit";
  public static final String A_semester = "semester";
  public static final String A_year = "year";
  public static final String A_processCode = "processCode";
  public static final String A_processes = "processes";
  public static final String Assoc_ProcessApplicationManagerAndProcess = "ProcessApplicationManager-shows-Process";
  
  //public static final String Assoc_ProcessExecutionAndOrgUnit = "ProcessExecution-isfor-OrgUnit";
  
  // attributes
  @DAttr(name="id", type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
  /** the {@link OrgUnit} which contains the output of this*/
//  @Association(name=Assoc_ProcessExecutionAndOrgUnit, role="processExec", 
//      type = AssocType.One2One, endType = AssocEndType.One, 
//      associate = @AssocEnd(type = File.class, cardMin = 1, cardMax = 1))  
  @DAttr(name=A_orgUnit, type=Type.Domain, optional=false, defaultValueFunction=true)
  private OrgUnit orgUnit;
  
  @DAttr(name=A_processCode, type=Type.String, optional=false, length=100)
  private String processCode;
  
  @DAttr(name=A_semester, type=Type.Domain,length=50, optional=false, defaultValueFunction=true)
  private Semester semester;
  
  @DAttr(name=A_year, type=Type.Integer, defaultValueFunction=true, optional=false)
  private Integer year;
  
  @DAssoc(ascName = Assoc_ProcessApplicationManagerAndProcess, role = "processApplicationMgr", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = Process.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE, 
      updateLink=false))
  @DAttr(name=A_processes, type=Type.Collection, serialisable=false, 
      filter=@Select(clazz=Process.class))
  private List<Process> processes;
  private int processesCount;
  
//  // constructor: from data source
//  public ProcessApplicationManager(Integer id, OrgUnit orgUnit, Semester semester, Integer year) {
//    this.id = nextID(id);
//    this.orgUnit = orgUnit;
//    this.semester = semester;
//    this.year = year;
//  }

  // constructor: from form
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public ProcessApplicationManager(OrgUnit orgUnit, String processCode, Semester semester, Integer year) {
    this.id = nextID(null);
    this.orgUnit = orgUnit;
    this.processCode = processCode;
    this.semester = semester;
    this.year = year;
  }
  
  public OrgUnit getOrgUnit() {
    return orgUnit;
  }

  public void setOrgUnit(OrgUnit orgUnit) {
    this.orgUnit = orgUnit;
  }

  @DOpt(type=DOpt.Type.DefaultValueFunction)
  @AttrRef(value=A_orgUnit)
  public static OrgUnit getDefaultOrgUnit(DOMBasic dom) throws NotFoundException, DataSourceException {
    return OrgUnit.getDefaultOrgUnit(dom);
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
  @DOpt(type=DOpt.Type.DefaultValueFunction)
  @AttrRef(value=A_semester)
  public static Semester getDefaultSemester() {
    return Semester.getSemesterFor(Toolkit.getCurrentSeason());
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }
  
  @DOpt(type=DOpt.Type.DefaultValueFunction)
  @AttrRef(value=A_year)
  public static int getDefaultYear() {
    return Toolkit.getCurrentYear();
  }
  
//  public String getProcessName() {
//    return processName;
//  }
//
//  public void setProcessName(String processName) {
//    this.processName = processName;
//  }

  public String getProcessCode() {
    return processCode;
  }

  public void setProcessCode(String processCode) {
    this.processCode = processCode;
  }

  /** ASSOCIATION methods: {@link #processes} */
  public Collection<Process> getProcesses() {
    return processes;
  }

  public void setProcesses(List<Process> processes) {
    this.processes = processes;
    processesCount = processes.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getProcessesCount() {
    return processesCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setProcessesCount(int processCount) {
    this.processesCount = processCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_processes)
  public boolean addProcess(Collection<Process> processes) {
    boolean updated = false;
    
    for (Process process : processes) {
      if (!this.processes.contains(process)) {
        this.processes.add(process);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_processes)
  public boolean addProcess(Process process) {
    boolean updated = false;
    
    if (!this.processes.contains(process)) {
      this.processes.add(process);
    }
    
    return updated;
  }

/*  @Metadata(type=Metadata.Type.MethodValueAdderNew)
  @MemberRef(name=A_processes)
  public boolean addNewProcess(Collection<Process> processes) {
    boolean updated = false;
    
    this.processes.addAll(processes);
    processesCount += processes.size();
    
    return updated;
  }

  @Metadata(type=Metadata.Type.MethodValueAdderNew)
  @MemberRef(name=A_processes)
  public boolean addNewProcess(Process process) {
    boolean updated = false;
    
    this.processes.add(process);
    processesCount++;
    
    return updated;
  }
  
  @Metadata(type = Metadata.Type.MethodValueRemover)
  @MemberRef(name=A_processes)  
  public boolean removeProcess(Process process) {
    boolean removed = processes.remove(process);
    if (removed) {
      processesCount--;
    }
    
    return false;
  }
  */
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #processes}*/
  
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
    ProcessApplicationManager other = (ProcessApplicationManager) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+" (" + id + ", " + orgUnit
        + ", " + semester + ", " + year + ")";
  }
}
