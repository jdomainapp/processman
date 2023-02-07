package vn.com.processman.modules.processstructure.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.ConstraintViolationException;
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
import jda.modules.dcsl.syntax.query.AttribExp;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import vn.com.processman.modules.dsecurity.model.Role;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview
 * 	Represents an action
 * 
 * @author dmle
 */
public class Action {
  
  public static final ScopeDef ScopeDefFileExec = new ScopeDef(
      FileWrapper.class,
      FileWrapper.A_EssentialCreateNews,
      Boolean.TRUE); // end ScopeDefFileExec

  public static final String A_id = "id";
  public static final String A_code = "code";
  public static final String A_name = "name";
  public static final String A_description = "description";
  public static final String A_def = "def";
  public static final String A_prev = "prev";
  public static final String A_task = "task";
  public static final String A_preConds = "preConds";
  public static final String A_postConds = "postConds";
  public static final String A_output = "output";
  public static final String A_status = "status";
  public static final String A_self = "self";

  public static final String A_codeDef = "codeDef";
  public static final String A_nameDef = "nameDef";
  public static final String A_descriptionDef = "descriptionDef";
//  public static final String A_prevDef = "prevDef";
  private static final String A_prevSourceActions = "prevSourceActions";
  public static final String A_role = "role";
  
  public static final String Association_ActionPreconditions = "action-preconditions";
  public static final String Association_ActionPostconditions = "action-postconditions";
  public static final String Assoc_ActionAndFile = "action-hasoutput-file";
  public static final String Association_RoleActions = "RoleActions";
  public static final String Assoc_ActionDefAction = "ActionDefAction";

  /**attributes for use when this is part of the containment tree of a process application */
  public static final String[] A_application = {Action.A_id, Action.A_codeDef, Action.A_nameDef, Action.A_descriptionDef, Action.A_task};

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

  /**the {@link Action} from which this is derived */
  @DAttr(name=A_def, type=Type.Domain)
//  @DAssoc(ascName=Assoc_ActionDefAction, role="action", 
//    ascType=AssocType.One2Many, endType=AssocEndType.Many, 
//    associate=@Associate(type=Action.class, cardMin=1, cardMax=1),
//    reflexive=true,dependsOn=true)
  private Action def;

  /**
   * A virtual attribute used to create data source for {@link #prev}. In this special case, we are only interested
   * in retrieving <tt>non-null</tt> values for this attribute, i.e. those values that point to 
   * the original {@link Action}s. 
   */
  @DAttr(name=A_prevSourceActions,type=Type.Collection,auto=true,serialisable=false,
      sourceQuery=true
  )  
  @QueryDef(clazz=Action.class,
      exps={
        @AttribExp(attrib=Action.A_code, op=Op.NOTEQ, value=CommonConstants.NullValue)
      }
  )
  private Collection<Action> prevSourceActions;
  
  /** realises the ordering of actions*/
  @DAttr(name=A_prev, type=Type.Domain, sourceAttribute=A_prevSourceActions)
  private Action prev;
  
  /** the {@link Task} to which this belongs */
  @DAssoc(ascName = Task.Assoc_TaskAndAction, role = "action", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Task.class, cardMin = 1, cardMax = 1),
      dependsOn=true)  
  @DAttr(name=A_task, type=Type.Domain, optional=false)
  private Task task;

  /** the {@link FileWrapper} which contains the output of this*/
  @DAssoc(ascName = Assoc_ActionAndFile, role = "action", 
      ascType = AssocType.One2One, endType = AssocEndType.One, 
      associate = @Associate(type = FileWrapper.class, cardMin = 0, cardMax = 1))  
  @DAttr(name=A_output, type=Type.Domain)
  private FileWrapper output;
  
  @DAttr(name=A_status, type=Type.Domain)
  private StatusCode status;
  
  /** the pre-conditions (if any) */
  @DAssoc(ascName = Association_ActionPreconditions, role = "action", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = BooleanExpression.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_preConds, type=Type.Collection, serialisable=false, 
    filter=@Select(clazz=BooleanExpression.class))
  private Collection<BooleanExpression> preConds;
  
  /** the post-conditions (if any) */
  @DAssoc(ascName = Association_ActionPostconditions, role = "action", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = BooleanExpression.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_postConds, type=Type.Collection, serialisable=false, 
    filter=@Select(clazz=BooleanExpression.class))
  private Collection<BooleanExpression> postConds;
  
  /** the user's {@link Role} that is allowed to perform this action */
  @DAssoc(ascName = Association_RoleActions, role = "action", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Role.class, cardMin = 1, cardMax = 1))
  @DAttr(name=A_role, type=Type.Domain, optional=true)
  private Role role;
  
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
//  private Action prevDef;

  /**virtual attribute used to refer to this object. It is specifically needed for this class to allow views to provide look-up 
   * on objects of the sub-types so that users can view them on the viewers specific for those sub-types.
   */
  @DAttr(name=A_self, type=Type.Domain, auto=true, mutable=false, serialisable=false)
  private Action self;
  
  // constructor: create object from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Action(Integer id, String code, String name, String desc, Action def, Action prev, Task task, FileWrapper output, StatusCode status, Role role) {
    this.id = nextID(id);
    this.name = name;
    this.code = code;
    this.description = desc;
    this.def = def;
    this.prev = prev;
    this.task = task;
    this.output = output;
    this.status = status;
    
    this.role = role;
    
    this.self = this;
  }

  // constructor: create object from object form
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Action(String code, String name,  String desc, Action def, Action prev, Task task, FileWrapper output, StatusCode status, Role role) {
    this(null, code, name, desc, def, prev, task, output, status, role);
  }
  
  // constructor: create object from object form (without output)
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Action(String code, String name,  String desc, Action def, Action prev, Task task, StatusCode status, Role role) {
    this(null, code, name, desc, def, prev, task, 
        // output
        null, 
        status, 
        role);
  }
  
  // constructor: create object from object form (without def, output, status)
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Action(String code, String name,  String desc 
      //, Action def, 
      ,Action prev, Task task
      //, StatusCode status
      , Role role) {
    this(null, code, name, desc
        // def
        ,null
        , prev, task, 
        // output
        null, 
        //status,
        null,
        role);
  }
  
  // constructor: create object from object form (without output, role)
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Action(String code, String name,  String desc, Action def, Action prev, Task task, StatusCode status) {
    this(null, code, name, desc, def, prev, task, 
        // output
        null, 
        status, 
        //role
        null
        );
  }
  
  //constructor: create derived object
  public Action(//String code, String name,  String desc, 
      Action def, Action prev, Task task
      //, StatusCode status
      ) {
    this(null, 
        //code, name, desc,
        null, null, null,
        def, prev, task, 
       // output
       null, 
       //status
       null, 
       // role
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

  public Action getDef() {
    return def;
  }

  public void setDef(Action def) {
    this.def = def;
  }

  public Action getPrev() {
    return prev;
  }

//  public Action getPrevDef() {
//    if (def != null) {
//      // forward
//      return def.getPrev();
//    } else 
//      return prev;
//  }
  
  public void setPrev(Action prev) {
    this.prev = prev;
  }

  public Task getTask() {
    return task;
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public FileWrapper getOutput() {
    return output;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_output)
  public void setNewOutput(FileWrapper output) {
    setOutput(output);
  }
  
  public void setOutput(FileWrapper output) {
    this.output = output;
    
    evaluateStatus();
  }

//  @Metadata(type=Metadata.Type.MethodValueRemover)
//  @MemberRef(name=A_output)
//  public void removeOutput(File output) {
//    this.output = null;
//  }
  
  /**
   * @effects 
   *  if {@link #output} is not null
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  private boolean hasOutput() {
    return output != null;
  }

  public Action getSelf() {
    return self;
  }

  public int getId() {
    return id;
  }

  /** association methods: {@link #preConds} */
  
  /**
   * @requires
   *  {@link #def} is <tt>null</tt>
   */
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_preConds)
  public boolean addPreConditions(Collection<BooleanExpression> preConds) {
    if (def != null) {
      // forward 
      return def.addPreConditions(preConds);
    } else {
      if (this.preConds == null) {
        this.preConds = new ArrayList();
      }
      
      boolean updated = false;
      
      for (BooleanExpression exp : preConds) {
        if (!this.preConds.contains(exp)) {
          this.preConds.add(exp);
        }
      }
      return updated;
    }
  }
  

  /**
   * @requires
   *  {@link #def} is <tt>null</tt>
   */
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_preConds)
  public boolean addPreCondition(BooleanExpression cond) {
    if (def != null) {
      // forward 
      return def.addPreCondition(cond);
    } else {
      if (this.preConds == null) {
        this.preConds = new ArrayList();
      }
      
      boolean updated = false;
      
      if (!this.preConds.contains(cond)) {
        this.preConds.add(cond);
      }
      return updated;
    }
  }
  
  /**
   * @effects 
   *  if {@link #preConds} is initialised AND {@link #preConds}<tt>.size() > index</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean hasPreCondition(int index) {
    if (def != null) {
      // forward
      return def.hasPreCondition(index);
    } else {
      return preConds != null && preConds.size() > index;
    }
  }

  /**
   * @effects 
   *  return {@link BooleanExpression} element at <tt>index</tt> in the {@link Iterator} of {@link #preConds}; or 
   *  return <tt>null</tt> if <tt>index</tt> is invalid
   */
  public BooleanExpression getPreCondition(int index) {
    if (def != null) {
      // forward
      return def.getPreCondition(index);
    } else {
      int i = 0;
      Iterator<BooleanExpression> it = preConds.iterator();
      BooleanExpression exp;
      while (it.hasNext()) {
        exp = it.next();
        if (i == index)
          return exp;
        else
          i++;
      }
      
      return null;      
    }
  }
  
  /**
   * @effects 
   *  return pre-conditions defined for this or <tt>null</tt> if no conditions are defined
   */
  public Collection<BooleanExpression> getPreConds() {
    if (def != null) {
      // forward
      return def.getPreConds();
    } else {
      return preConds;
    }
  }
  
  /**
   * @modifies {@link #preConds}
   * @effects 
   *  if {@link #preConds} is initialised 
   *    evaluate each {@link BooleanExpression} in {@link #preConds}
   *    if all are <tt>true</tt>
   *      return <tt>true</tt>
   *    else
   *      return <tt>false</tt>
   */
  protected boolean evaluatePreConditions() {
    return evaluateConditions(getPreConds());
  }
  
  /** end association: {@link #preConds}*/
  
  /** association methods: {@link #postConds} */
  
  /**
   * @requires
   *  {@link #def} is <tt>null</tt>
   */
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_postConds)
  public boolean addPostConditions(Collection<BooleanExpression> postConds) {
    if (def != null) {
      // forward 
      return def.addPostConditions(postConds);
    } else {
      if (this.postConds == null) {
        this.postConds = new ArrayList();
      }
      
      boolean updated = false;
      
      for (BooleanExpression exp : postConds) {
        if (!this.postConds.contains(exp)) {
          this.postConds.add(exp);
        }
      }
      
      return updated;
    }
  }
  
  /**
   * @effects 
   *  if {@link #postConds} is initialised AND {@link #postConds}<tt>.size() > index</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean hasPostCondition(int index) {
    if (def != null) {
      // forward
      return def.hasPostCondition(index);
    } else {
      return postConds != null && postConds.size() > index;
    }
  }

  /**
   * @effects 
   *  return {@link BooleanExpression} element at <tt>index</tt> in the {@link Iterator} of {@link #postConds}; or 
   *  return <tt>null</tt> if <tt>index</tt> is invalid
   */
  public BooleanExpression getPostCondition(int index) {
    if (def != null) {
      // forward
      return def.getPostCondition(index);
    } else {
      int i = 0;
      Iterator<BooleanExpression> it = postConds.iterator();
      BooleanExpression exp;
      while (it.hasNext()) {
        exp = it.next();
        if (i == index)
          return exp;
        else
          i++;
      }
      
      return null;      
    }
  }
  
  /**
   * @effects 
   *  return post-conditions defined for this or <tt>null</tt> if no conditions are defined
   */
  public Collection<BooleanExpression> getPostConds() {
    if (def != null) {
      // forward
      return def.getPostConds();
    } else {
      return postConds;
    }
  }
  
  /**
   * @modifies {@link #postConds}
   * @effects 
   *  if {@link #postConds} is initialised 
   *    evaluate each {@link BooleanExpression} in {@link #postConds}
   *    if all are <tt>true</tt>
   *      return <tt>true</tt>
   *    else
   *      return <tt>false</tt>
   */
  protected boolean evaluatePostConditions() {
    return evaluateConditions(getPostConds());
  }
  
  /**
   * @effects 
   *  if {@link #conds} is initialised 
   *    evaluate each {@link BooleanExpression} in {@link #conds}
   *    if all are <tt>true</tt>
   *      return <tt>true</tt>
   *    else
   *      return <tt>false</tt>
   */
  private boolean evaluateConditions(Collection<BooleanExpression> conds) {
    boolean allTrue = true;
    
    if (conds != null) {
      for (BooleanExpression cond : conds) {
        try {
          allTrue = allTrue & cond.eval();
        } catch (Exception e) {
          // failed to evaluate
          allTrue = false;
          //TODO: log
          System.err.println(e.getClass().getSimpleName()+ ": " + e.getMessage());
          //e.printStackTrace();
        }
        
        /* use this to stop evaluation immediately when false
        if (!allTrue) // stop evaluation immediately when false 
          break;
        */
      }
    }
    
    return allTrue;
  }

  /** end association: {@link #postConds}*/
  
  /**
   * @modifies this.{@link #status}
   * 
   * @effects 
   *  if {@link #status} = null (i.e. not set)
   *    set it by calling {@link #evaluateStatus()}
   *    
   *  <p>Return the value of {@link #status}
   *  
   * @version 1.2
   */
  public StatusCode ensureStatusIsSet() {
    if (status == null) {
      evaluateStatus();
    } 
    
    return status;
  }
  
  protected StatusCode getCurrentStatus() {
    return status;
  }

  public StatusCode getStatus() {
    return status;
  }

  public void setStatus(StatusCode status) {
    this.status = status;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * @effects 
   *  if {@link #def} != null
   *    return the {@link Role} that is defined in the {@link #def}
   *  else
   *    return {@link #role}
   *    
   * @version 1.2
   */
  public Role getRoleDef() {
    if (def != null) {
      // forward
      return def.getRole();
    } else {
      return role;
    }
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
   * Determine {@link #status} from {@link #preConds} and {@link #postConds}
   * 
   * @modifies this.{@link #status}
   * @effects  <pre>
   *  if {@link #hasOutput()} AND {@link #evaluatePreConditions()} AND {@link #evaluatePostConditions()} 
   *    set {@link #status} = {@link StatusCode#Done}
   *  else
   *    set {@link #status} = {@link StatusCode#NotDone}
   *    
   *  <br>if {@link #status} is changed (from the previous state)
   *    return <tt>true</tt>
   *    else return <tt>false</tt>
   * </pre>
   */
  public boolean evaluateStatus() {
    StatusCode old = status;
    
    //if (hasOutput()) {
    if (evaluatePreConditions() & evaluatePostConditions()) {
      status = StatusCode.Done;
    } else {
      status = StatusCode.NotDone;
    }
//    } else {
//      status = StatusCode.NotDone;
//    }
    
    return old == null || !status.equals(old);
  }
  
  /**
   * 
   */
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
    Action other = (Action) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " (" + id + ", " + getNameDef() + ", " + getCodeDef() + ")";
  }
}
