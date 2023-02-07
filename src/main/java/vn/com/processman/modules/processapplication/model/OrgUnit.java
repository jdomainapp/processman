package vn.com.processman.modules.processapplication.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.expression.Op;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dodm.dom.DOMBasic;
import vn.com.processman.modules.processstructure.model.Process;

/**
 * @overview 
 *  Represents an organisational unit to which certain {@link Process}(es) are being applied. 
 *  
 * @author dmle
 */
public class OrgUnit {
  public static final String A_processApplications = "processApplications";
  public static final String Assoc_OrgUnitAndProcessApplication = "OrgUnit-has-ProcessApplication";
  public static final String Assoc_OrgUnitCreateProcess = "OrgUnit-Create-Process";
  public static final String A_id = "id";
  public static final String A_name = "name";
  public static final String A_processes = "processes"; 
  //public static final String A_self = "self";

  /** the default org. unit to be used */ 
  private static final int DefaultOrgUnitId = 1;
  private static OrgUnit defaultOrgUnit;
  
  // attributes
  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
  @DAttr(name=A_name, type=Type.String, length=255, unique=true, optional=false)
  private String name;
  
  /** the {@link ProcessApplication}s of this */
  @DAssoc(ascName =Assoc_OrgUnitAndProcessApplication, role = "orgUnit", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = ProcessApplication.class, cardMin = 1, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_processApplications, type=Type.Collection, serialisable=false, 
    filter=@Select(clazz=ProcessApplication.class))
  private List<ProcessApplication> processApplications;
  private int processApplicationsCount;

  @DAssoc(ascName = Assoc_OrgUnitCreateProcess, role = "orgUnit", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = Process.class, cardMin = 1, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_processes, type=Type.Collection, optional=false, serialisable=false, 
      filter=@Select(clazz=Process.class))
  private List<Process> processes;
  private int processesCount;
  
//  /**virtual attribute used to refer to this object. It is specifically needed for this class to allow views to provide look-up 
//   * on objects of the sub-types so that users can view them on the viewers specific for those sub-types.
//   */
//  @DomainConstraint(name=A_self, type=Type.Domain, auto=true, mutable=false, serialisable=false)
//  private OrgUnit self;
  
//  /**non-data links*/
//  @DomainConstraint(name="procExec",type=Type.Domain,serialisable=false)
//  private ProcessExecution procExec;
  
  // constructor: from data source
  public OrgUnit(Integer id, String name) {
    this.id = nextID(id);
    this.name = name;
    
    //this.self = this;
    
    processes = new ArrayList();
  }

  // constructor: from object form
  public OrgUnit(String name) {
    this(null, name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /** ASSOCIATION methods: {@link #processApplications} */
  public Collection<ProcessApplication> getProcessApplications() {
    return processApplications;
  }

  public void setProcessApplications(List<ProcessApplication> procApps) {
    this.processApplications = procApps;
    processApplicationsCount = procApps.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getProcessApplicationsCount() {
    return processApplicationsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setProcessApplicationsCount(int processApplicationCount) {
    this.processApplicationsCount = processApplicationCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_processApplications)
  public boolean addProcessApplication(Collection<ProcessApplication> processApplications) {
    boolean updated = false;
    
    for (ProcessApplication processApplication : processApplications) {
      if (!this.processApplications.contains(processApplication)) {
        this.processApplications.add(processApplication);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_processApplications)
  public boolean addProcessApplication(ProcessApplication processApplication) {
    boolean updated = false;
    
    if (!this.processApplications.contains(processApplication)) {
      this.processApplications.add(processApplication);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_processApplications)
  public boolean addNewProcessApplication(Collection<ProcessApplication> processApplications) {
    boolean updated = false;
    
    this.processApplications.addAll(processApplications);
    processApplicationsCount += processApplications.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_processApplications)
  public boolean addNewProcessApplication(ProcessApplication processApplication) {
    boolean updated = false;
    
    this.processApplications.add(processApplication);
    processApplicationsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_processApplications)  
  public boolean removeProcessApplication(ProcessApplication processApplication) {
    boolean removed = processApplications.remove(processApplication);
    if (removed) {
      processApplicationsCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #processApplications}*/
  
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

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_processes)
  public boolean addNewProcess(Collection<Process> processes) {
    boolean updated = false;
    
    this.processes.addAll(processes);
    processesCount += processes.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_processes)
  public boolean addNewProcess(Process process) {
    boolean updated = false;
    
    this.processes.add(process);
    processesCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_processes)  
  public boolean removeProcess(Process process) {
    boolean removed = processes.remove(process);
    if (removed) {
      processesCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
//  public OrgUnit getSelf() {
//    return self;
//  }

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
    OrgUnit other = (OrgUnit) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "OrgUnit (" + id + ", " + name + ")";
  }

  /**
   * @effects 
   *  retrieve (if not already) and return {@link OrgUnit} that is used as the default for 
   *  object forms
   */
  public static OrgUnit getDefaultOrgUnit(DOMBasic dom) throws NotFoundException, DataSourceException {
    if (defaultOrgUnit == null) {
      defaultOrgUnit = dom.retrieveObject(OrgUnit.class, OrgUnit.A_id, Op.EQ, DefaultOrgUnitId);
    }
    
    return defaultOrgUnit;
  }
}
