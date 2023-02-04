package vn.com.processman.modules.processstructure.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vn.com.processman.util.model.StatusCode;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.config.module.containment.ScopeDef;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.query.AttribExp;
import domainapp.basics.model.meta.query.QueryDef;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.util.Tuple;

/**
 * @overview
 * 	Represents a task
 * 
 * @author dmle
 */
public class Task {
  // extended scope definition: 
  // TODO: move these out of this class if too many
  public static final ScopeDef ScopeDefActionExec = new ScopeDef(
      Action.class,
      new String[] {Action.A_self, Action.A_codeDef, Action.A_nameDef},Boolean.FALSE); // end ScopeDefActionExec
  
  // for use ONLY with Action (to make Action.status editable)
  public static final ScopeDef ScopeDefActionEmbeddedExec = new ScopeDef(
      Action.class,
      new String[] {Action.A_id, Action.A_codeDef, Action.A_nameDef, 
        Action.A_preConds,
        Action.A_descriptionDef,  
        Action.A_output, 
        Action.A_postConds,
        Action.A_status},Boolean.FALSE) {
    @Override // enable Action.A_status
    public Boolean isEditable(String attribName) {
      if (attribName.equals(Action.A_status)) {
        return Boolean.TRUE;
      } else {
        return null;
      }
    }
  }; // end ScopeDefActionEmbeddedExec
  
  public static final ScopeDef ScopeDefFileExec = Action.ScopeDefFileExec;
  
  // END: extended scope def
  
  public static final String A_code = "code";
  public static final String A_name = "name";
  public static final String A_def = "def";
  public static final String A_id = "id";
  public static final String A_description = "description";
  public static final String A_startWeek = "startWeek";
  public static final String A_endWeek = "endWeek";
  public static final String A_process = "process";
  public static final String A_prev = "prev";
  public static final String A_status = "status";
  public static final String A_self = "self";

  public static final String Assoc_TaskAndAction = "task-has-action";
  public static final String A_actions = "actions";
  
  public static final String A_codeDef = "codeDef";
  public static final String A_nameDef = "nameDef";
  public static final String A_descriptionDef = "descriptionDef";
//  public static final String A_prevDef = "prevDef";

  private static final String A_prevSourceTasks = "prevSourceTasks";

  /**attributes for use when this is part of the containment tree of a process application */
  public static final String[] A_application = {Task.A_id, Task.A_codeDef, Task.A_nameDef, Task.A_descriptionDef, Task.A_process, Task.A_actions};

  // attributes
  @DAttr(name=A_id, type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;

  @DAttr(name=A_code, type=Type.String, unique=true, length=100)
  private String code;
  
  @DAttr(name=A_name, type=Type.String, length=255)
  private String name;
  
  @DAttr(name=A_description, type=Type.String, length=255)
  private String description;

  @DAttr(name=A_startWeek, type=Type.Integer, min=1)
  private Integer startWeek;
  
  @DAttr(name=A_endWeek, type=Type.Integer, min=1)
  private Integer endWeek;
  
  /** the {@link Process} of which this is a part */
  @DAssoc(ascName = Process.Assoc_ProcessAndTask, role = "task", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Process.class, cardMin = 1, cardMax = 1),
      dependsOn=true)
  @DAttr(name=A_process, type=Type.Domain)
  private Process process;
  
  /** the {@link Task} from which this is derived */
  @DAttr(name=A_def, type=Type.Domain)
  private Task def;

  /**
   * A virtual attribute used to create data source for {@link #prev}. In this special case, we are only interested
   * in retrieving <tt>non-null</tt> values for this attribute, i.e. those values that point to 
   * the original {@link Task}s. 
   */
  @DAttr(name=A_prevSourceTasks,type=Type.Collection,auto=true,serialisable=false,
      sourceQuery=true
  )  
  @QueryDef(clazz=Task.class,
      attributeExps={
        @AttribExp(attrib=Task.A_code, op=Op.NOTEQ, value=MetaConstants.NullValue)
      }
  )
  private Collection<Task> prevSourceTasks;
  
  /** realises the ordering of tasks */
  @DAttr(name=A_prev, type=Type.Domain, sourceAttribute=A_prevSourceTasks)
  private Task prev;
  
  /** derived from status of all {@link Action} in {@link #actions} */
  @DAttr(name=A_status, type=Type.Domain, mutable=false)
  private StatusCode status;
  
  @DAssoc(ascName = Assoc_TaskAndAction, role = "task", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = Action.class, cardMin = 1, cardMax = MetaConstants.CARD_MORE))
  @DAttr(name=A_actions, type=Type.Collection, optional=false, serialisable=false, 
      filter=@Select(clazz=Action.class))
  private List<Action> actions;
  private int actionsCount;
  
  // END essential attributes
  
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
  
//  /**virtual attribute derived from {@link #def},
//   * value is obtained directly via {@link #getPrevDef()}
//   */
//  @DAttr(name=A_prevDef, type=Type.Domain, length=30, auto=true, mutable=false, serialisable=false)
//  private Task prevDef;
  
  /**virtual attribute used to refer to this object. It is specifically needed for this class to allow views to provide look-up 
   * on objects of the sub-types so that users can view them on the viewers specific for those sub-types.
   */
  @DAttr(name=A_self, type=Type.Domain, auto=true, mutable=false, serialisable=false)
  private Task self;

  
  // constructor: create from data source
  public Task(Integer id, String code, String name, String desc, Integer startWeek,
      Integer endWeek, Process process, Task def, Task prev, StatusCode status) throws ConstraintViolationException {
    validateInputDependency(name, code, desc, startWeek, endWeek, process, def, prev, status);
    
    this.id = nextID(id);
    this.name = name;
    this.code = code;
    this.description = desc;
    this.startWeek = startWeek;
    this.endWeek = endWeek;
    this.process=process;
    this.def = def;
    this.prev = prev;
    this.status = status;
    
    actions = new ArrayList();
    
    this.self = this;
  }

  // constructor: create from object form
  public Task(String code, String name, String desc, Integer startWeek,
      Integer endWeek, Process process, Task def, Task prev, StatusCode status) {
    this(null, code, name, desc, startWeek, endWeek, process, def, prev, status);
  }

  // constructor: create from object form (no def, status)
  public Task(String code, String name, String desc, Integer startWeek,
      Integer endWeek, Process process, 
      //Task def, 
      Task prev 
      //StatusCode status
      ) {
    this(null, code, name, desc, startWeek, endWeek, process, 
        //def,
        null,
        prev, 
        //status
        null
        );
  }
  
  // constructor: create derived object
  public Task(
      //String code, String name, String desc, Integer startWeek,Integer endWeek, 
      Process process, 
      Task def, 
      Task prev 
      //StatusCode status
      ) {
    this(null, 
        //code, name, desc, startWeek, endWeek,
        null, null, null, null, null,
        process,
        def,
        prev, 
        //status
        getDefaultDerivedTaskStatus()
        );
  }
  
  /**
   * @effects 
   *  return the default {@link StatusCode} for <b>newly-created, derived</t> {@link Task}
   */
  private static StatusCode getDefaultDerivedTaskStatus() {
    return StatusCode.NotDone;
  }

  private void validateInputDependency(String name, String code, String desc,
      Integer startWeek, Integer endWeek, Process process, Task def, Task prev, StatusCode status) throws ConstraintViolationException {    
    if (def == null) {
      // status is optional, other attributes are required
      if (name == null || code == null || desc == null || startWeek == null || endWeek == null || process == null) {
        throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
            new Object[] {""});
      }
    } else {
      // status is required, other attributes are optional
      if (status == null) {
        throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
            new Object[] {"status"});        
      }
    }
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

  public Integer getStartWeek() {
    return startWeek;
  }

  public Integer getStartWeekDef() {
    if (def != null) {
      // forward
      return def.getStartWeek();
    } else 
      return startWeek;
  }
  
  /**
   * @requires 
   *  {@link #def} is null
   */
  public void setStartWeek(Integer startWeek) {
    this.startWeek = startWeek;
  }

  public Integer getEndWeek() {
    return endWeek;
  }

  public Integer getEndWeekDef() {
    if (def != null) {
      // forward
      return def.getEndWeek();
    } else 
      return endWeek;
  }
  
  /**
   * @requires 
   *  {@link #def} is null
   */
  public void setEndWeek(Integer endWeek) {
    this.endWeek = endWeek;
  }

  public StatusCode getStatus() {
    return status;
  }

  public void setStatus(StatusCode status) {
    this.status = status;
  }

  /**
   * This method is invoked automatically by the system to refresh the state of this. 
   * We use this mechanism to perform {@link #evaluateStatus()} and update {@link #status}
   */
  @DOpt(type=DOpt.Type.ObjectStateRefresher)
  public boolean refresh() {
    boolean statusChanged = evaluateStatus();
    return statusChanged;
  }
  
  /**
   * Determine {@link #status} from those of {@link Action} in {@link #actions}
   * @return 
   * 
   * @requires 
   *  {@link #actions} is initialised with all {@link Action}s of this /\ 
   *  all {@link Action} in {@link #actions} have been invoked with {@link Action#evaluateStatus()} 
   * 
   * @modifies {@link #status}
   * 
   * @effects <pre> 
   *  for each {@link Action} a in {@link #actions}
   *    set s = a.evaluateStatus()
   *    if s is {@link StatusCode#NotDone}
   *      set {@link #status} = {@link StatusCode#NotDone}
   *      return
   *   
   *   set {@link #status} = {@link StatusCode#Done} </pre>
   */
  protected boolean evaluateStatus() {
    StatusCode old = status;
    
    StatusCode as;
    StatusCode s = StatusCode.Done;
    for (Action a : actions) {
      // ASSUME: a.evaluateStatus() has been invoked
      as = a.getStatus();
      
      if (as.equals(StatusCode.NotDone)) {
        s = StatusCode.NotDone;
        break;
      }
    }
    
    if (old == null || !s.equals(old)) {
      // status is changed
      status = s;
      return true;
    } else {
      // status not changed
      return false;
    }
  }
  
  public Task getDef() {
    return def;
  }

  public void setDef(Task def) {
    this.def = def;
  }

  public Task getPrev() {
    return prev;
  }

//  public Task getPrevDef() {
//    if (def != null) {
//      // forward
//      return def.getPrev();
//    } else 
//      return prev;
//  }
  
  public void setPrev(Task prev) {
    this.prev = prev;
  }

  public Process getProcess() {
    return process;
  }

  public void setProcess(Process process) {
    this.process = process;
  }

  /** ASSOCIATION methods: {@link #actions} */
  public Collection<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
    actionsCount = actions.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getActionsCount() {
    return actionsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setActionsCount(int actionCount) {
    this.actionsCount = actionCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_actions)
  public boolean addAction(Collection<Action> actions) {
    boolean updated = false;
    
    for (Action action : actions) {
      if (!this.actions.contains(action)) {
        this.actions.add(action);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_actions)
  public boolean addAction(Action action) {
    boolean updated = false;
    
    if (!this.actions.contains(action)) {
      this.actions.add(action);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_actions)
  public boolean addNewAction(Collection<Action> actions) {
    boolean updated = false;
    
    this.actions.addAll(actions);
    actionsCount += actions.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_actions)
  public boolean addNewAction(Action action) {
    boolean updated = false;
    
    this.actions.add(action);
    actionsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_actions)  
  public boolean removeAction(Action action) {
    boolean removed = actions.remove(action);
    if (removed) {
      actionsCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #actions}*/

  /**
   * @effects 
   *  if this has some {@link Action} 
   *    return true
   *  else
   *    return false
   */
  public boolean hasActions() {
    return actions != null && actions.size() > 0;
  }

  public Task getSelf() {
    return self;
  }

  /**
   * @effects 
   *  if exists in this {@link Action} whose <tt>codeDef</tt> is <tt>actionCode</tt>
   *    return it
   *  else
   *    return <tt>null</tt> 
   */
  public Action lookUpAction(String actionCode) {
    for (Action a : actions) {
      if (a.getCodeDef().equals(actionCode)) {
        return a;
      }
    }
    
    // not found
    return null;
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
    Task other = (Task) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " (" + id + ", " + getNameDef() + ")";
  }

}
