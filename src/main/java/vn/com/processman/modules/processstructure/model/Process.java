package vn.com.processman.modules.processstructure.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dodm.dom.DOMBasic;
import vn.com.processman.modules.dsecurity.model.RolePerfProcess;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processexec.base.model.ProcessExecution;
import vn.com.processman.modules.processmanager.processapplication.model.ProcessApplicationManager;

/**
 * @overview
 * 	Represents a process
 * 
 * @author dmle
 */
public class Process {
  public static final String A_code = "code";
  public static final String A_name = "name";
  public static final String A_type = "type";
  public static final String A_id = "id";
  public static final String A_description = "description";
  public static final String A_def = "def";
  public static final String A_tasks = "tasks";
  public static final String A_createBy = "createBy";
  public static final String A_processApplications = "processApplications";
  public static final String A_codeDef = "codeDef";
  public static final String A_nameDef = "nameDef";
  public static final String A_descriptionDef = "descriptionDef";
  public static final String A_processPerfByRoles = "processPerfByRoles";
  
  public static final String Assoc_ProcessAndTask = "process-has-task";
  public static final String Assoc_ProcessAndProcessApplication = "process-has-processapplication";
  public static final String Assoc_ProcessAndRolePerfProcess = "ProcessAndRolePerfProcess";
  

  /**
   * non-id, essential attributes needed for the non-id constructor to create objects
   */
  public static final String[] A_EssentialNonIds = {
    Process.A_code, Process.A_name, Process.A_type, Process.A_description, Process.A_def, 
    Process.A_tasks
  };

  /**
   * non-id, essential attributes needed for viewing and creating new Process objects
   */
  public static final String[] A_EssentialCreateNews = {
    Process.A_code, Process.A_name, Process.A_type, Process.A_description, Process.A_createBy, Process.A_tasks
  };

  // attributes
  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;
  
  @DAttr(name=A_code, type=Type.String, unique=true, length=100)
  private String code;
  
  @DAttr(name=A_name, type=Type.String, length=255)
  private String name;
  
  @DAttr(name=A_type, type=Type.Domain, length=5)
  private ProcessType type;
  
  @DAttr(name=A_description, type=Type.String, length=255)
  private String description;

  /** the {@link Process} from which this is derived*/
  @DAttr(name=A_def, type=Type.Domain)
  private Process def;
  
  @DAssoc(ascName = Assoc_ProcessAndTask, role = "process", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = Task.class, cardMin = 1, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_tasks, type=Type.Collection, optional=false, serialisable=false, 
      filter=@Select(clazz=Task.class))
  private List<Task> tasks;
  private int tasksCount;
  
  @DAssoc(ascName = OrgUnit.Assoc_OrgUnitCreateProcess, role = "process", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = OrgUnit.class, cardMin = 1, cardMax = 1),
      dependsOn=true)
  @DAttr(name=A_createBy, type=Type.Domain, defaultValueFunction=true)
  private OrgUnit createBy;
  
  // END essential attributes
 
  /** association with {@link RolePerfProcess} */
  // TODO: add link methods for this association if it is manipulated on the UI
  @DAttr(name=A_processPerfByRoles,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=RolePerfProcess.class))
  @DAssoc(ascName=Assoc_ProcessAndRolePerfProcess,role="process",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=RolePerfProcess.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE)) 
  private Collection<RolePerfProcess> processPerfByRoles;

  /** non-data associations */
  @DAttr(name="processExec", type=Type.Domain, serialisable=false)
  private ProcessExecution processExec;

  @DAttr(name="processAppMan", type=Type.Domain, serialisable=false)
  private ProcessApplicationManager processAppMan;

  @DAssoc(ascName = Assoc_ProcessAndProcessApplication, role = "process", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = ProcessApplication.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_processApplications, type=Type.Collection, optional=false, serialisable=false, 
      filter=@Select(clazz=ProcessApplication.class))
  private Collection<ProcessApplication> processApplications;
  
  /**virtual attribute derived from {@link #def}, 
   * value is obtained directly via {@link #getCodeDef()}
   *  */
  @DAttr(name=A_codeDef, type=Type.String, length=30, auto=true, mutable=false, serialisable=false)
  private String codeDef;
  
  /**virtual attribute derived from {@link #def}, 
   * value is obtained directly via {@link #getNameDef()} */
  @DAttr(name=A_nameDef, type=Type.String, length=30, auto=true, mutable=false, serialisable=false)
  private String nameDef;
  
  /**virtual attribute derived from {@link #def},
   * value is obtained directly via {@link #getDescriptionDef()}
   */
  @DAttr(name=A_descriptionDef, type=Type.String, length=30, auto=true, mutable=false, serialisable=false)
  private String descriptionDef;
  
  // constructor: create object from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Process(Integer id, String code, String name, ProcessType type, String desc, Process def, OrgUnit createBy) {
    this.id = nextID(id);
    this.code = code;
    this.name = name;
    this.type = type;
    this.description = desc;
    this.def = def;
    this.createBy = createBy;
    
    this.tasks = new ArrayList();
  }

  // constructor: create object from object form
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Process(String code, String name, ProcessType type, String desc, Process def, OrgUnit createBy) {
    this(null, code, name, type, desc, def, createBy);
  }

  // constructor: create object from object form (no def)
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Process(String code, String name, ProcessType type, String desc, 
      //Process def, 
      OrgUnit createBy) {
    this(null, code, name, type, desc, 
        //def,
        null,
        createBy);
  }

  // constructor: create derived object (only def is specified)
  public Process(Process def) {
    this(null, 
        //code, 
        null, 
        //name, 
        null, 
        // type
        null,
        //desc, 
        null, 
        def, 
        //createBy
        null
        );
  }

  public String getName() {
    return name;
  }

  public String getNameDef() {
    if (def != null) {
      // forward
      return def.getName();
    } else 
      return name;
  }
  
  /**
   * @requires 
   *  {@link #def} is null
   */
  public void setName(String name) {
    this.name = name;
  }

  public ProcessType getType() {
    return type;
  }

  public ProcessType getTypeDef() {
    if (def != null) {
      // forward
      return def.getType();
    } else 
      return type;
  }
  
  /**
   * @requires 
   *  {@link #def} is null
   */
  public void setType(ProcessType type) {
    this.type = type;
  }

  public String getCode() {
    return code;
  }

  public String getCodeDef() {
    if (def != null) {
      // forward
      return def.getCode();
    } else 
      return code;
  }
  
  /**
   * @requires 
   *  {@link #def} is null
   */
  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public String getDescriptionDef() {
    if (def != null) {
      // forward
      return def.getDescription();
    } else 
      return description;
  }
  
  /**
   * @requires 
   *  {@link #def} is null
   */
  public void setDescription(String desc) {
    this.description = desc;
  }

  public Process getDef() {
    return def;
  }

  public void setDef(Process def) {
    this.def = def;
  }

  public OrgUnit getCreateBy() {
    return createBy;
  }

  @DOpt(type=DOpt.Type.DefaultValueFunction)
  @AttrRef(value=A_createBy)
  public static OrgUnit getDefaultCreateBy(DOMBasic dom) throws NotFoundException, DataSourceException {
    return OrgUnit.getDefaultOrgUnit(dom);
  }
  
  public void setCreateBy(OrgUnit createBy) {
    this.createBy = createBy;
  }

  /** ASSOCIATION methods: {@link #tasks} */
  public Collection<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
    tasksCount = tasks.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getTasksCount() {
    return tasksCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setTasksCount(int taskCount) {
    this.tasksCount = taskCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_tasks)
  public boolean addTask(Collection<Task> tasks) {
    boolean updated = false;
    
    for (Task task : tasks) {
      if (!this.tasks.contains(task)) {
        this.tasks.add(task);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_tasks)
  public boolean addTask(Task task) {
    boolean updated = false;
    
    if (!this.tasks.contains(task)) {
      this.tasks.add(task);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_tasks)
  public boolean addNewTask(Collection<Task> tasks) {
    boolean updated = false;
    
    this.tasks.addAll(tasks);
    tasksCount += tasks.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_tasks)
  public boolean addNewTask(Task task) {
    boolean updated = false;
    
    this.tasks.add(task);
    tasksCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_tasks)  
  public boolean removeTask(Task task) {
    boolean removed = tasks.remove(task);
    if (removed) {
      tasksCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #tasks}*/
  
  /**
   * @effects 
   *   if exists in this {@link Task} whose <tt>codeDef</tt> is <tt>taskCode</tt>
   *    return it
   *   else
   *    return <tt>null</tt> 
   */
  public Task lookUpTask(String taskCode) {
    for (Task t : tasks) {
      if (t.getCodeDef().equals(taskCode)) {
        return t;
      }
    } 
    
    // not found
    return null;
  }

  /**
   * @effects 
   *   if exists in this {@link Task} that preceeds the one whose <tt>codeDef</tt> is <tt>taskCode</tt>
   *    return it
   *   else
   *    return <tt>null</tt> 
   */
  public Task lookUpPrevTask(String taskCode) {
    Task t = lookUpTask(taskCode);
    if (t != null) {
      return t.getPrev();
    } else {
      // not found
      return null;
    }
  }

  /**
   * @effects 
   *  if this has some {@link Task} 
   *    return true
   *  else
   *    return false
   */
  public boolean hasTasks() {
    return tasks != null && tasks.size() > 0;
  }

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

  // ducmle: 20230215
  @DOpt(type = DOpt.Type.Getter)
  public Object getProcessExec() { return null; }
  
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
    Process other = (Process) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Process (" + id + ", " + getNameDef() + ")";
  }

}
