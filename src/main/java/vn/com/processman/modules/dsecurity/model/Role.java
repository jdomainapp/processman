package vn.com.processman.modules.dsecurity.model;

import java.util.ArrayList;
import java.util.Collection;

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
import vn.com.processman.modules.processstructure.model.Action;

/**
 * @overview
 *  A special sub-type of {@link domainapp.basic.model.security.Role} that implements a <b>domain-specific extension</b> 
 *  of this class. 
 *  
 *  <p>This sub-type has <b>exactly the same name and domain schema</b> as those of the super-type and contains domain-specific associations (e.g. "performs" {@link Action}), 
 *  needed to facilitate the domain requirements. 
 *  
 * @author dmle
 *
 * @version 3.3
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class Role extends jda.modules.security.def.Role {

  public static final String A_actions = "actions";
  public static final String A_rolePerfProcesses = "rolePerfProcesses";
  public static final String Assoc_RoleAndRolePerfProcess = "RoleAndRolePerfProcess";
  
  /** list of role names that do <b>NOT</b> qualify to be a process user */
  private static final String[] NonProcessUserRoleNames = {
    "QLQT"
  };
  
  /**list of role names that are process admin roles */
  private static final String[] ProcessAdminRoleNames = { "QLQT"} ;
  private static final String[] TeacherRoleNames = {"GIAV"};

//  public static final String Assoc_RoleAndRoleAction = "RoleAndRoleAction";

//  /**
//   * many-many association to {@link Action}. It is normalised by {@link #roleActions} 
//   */
//  @DAttr(name=A_actions,type=Type.Collection,serialisable=false,
//      filter=@Select(clazz=Action.class))
//  @DAssoc(ascName=Assoc_RoleAndProcessAction,role="role",
//    ascType=AssocType.Many2Many,endType=AssocEndType.Many,
//    associate=@Associate(type=Action.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE),
//    normAttrib=A_roleActions)  
//  private Collection<Action> actions;
  
  /**association with {@link Action} */
  @DAttr(name=A_actions,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=Action.class))
  @DAssoc(ascName=Action.Association_RoleActions,role="role",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Action.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE)) 
  private Collection<Action> actions;
  private int actionsCount;
  
  /** association with {@link RolePerfProcess} */
  @DAttr(name=A_rolePerfProcesses,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=RolePerfProcess.class))
  @DAssoc(ascName=Assoc_RoleAndRolePerfProcess,role="role",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=RolePerfProcess.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE)) 
  private Collection<RolePerfProcess> rolePerfProcesses;
  private int rolePerfProcessesCount;
  
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Role(String name, String description) {
    super(name, description);
  }

  /** ASSOCIATION methods: {@link #rolePerfProcesses} */
  
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getRolePerfProcessesCount() {
    return rolePerfProcessesCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setRolePerfProcessesCount(int rolePerfProcessesCount) {
    this.rolePerfProcessesCount = rolePerfProcessesCount;
  }

  @DOpt(type=DOpt.Type.Getter) @AttrRef(value=A_rolePerfProcesses)
  public Collection<RolePerfProcess> getRolePerfProcesss() {
    return rolePerfProcesses;
  }

  @DOpt(type=DOpt.Type.Setter) @AttrRef(value=A_rolePerfProcesses)
  public void setRolePerfProcesss(Collection<RolePerfProcess> rolePerfProcesses) {
    this.rolePerfProcesses = rolePerfProcesses;
    rolePerfProcessesCount = rolePerfProcesses.size();
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_rolePerfProcesses)
  public boolean addRolePerfProcess(Collection<RolePerfProcess> rolePerfProcesses) {
    if (this.rolePerfProcesses == null) this.rolePerfProcesses = new ArrayList();
    
    boolean updated = false;
    
    for (RolePerfProcess rolePerfProcess : rolePerfProcesses) {
      if (!this.rolePerfProcesses.contains(rolePerfProcess)) {
        this.rolePerfProcesses.add(rolePerfProcess);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_rolePerfProcesses)
  public boolean addRolePerfProcess(RolePerfProcess rolePerfProcess) {
    if (this.rolePerfProcesses == null) this.rolePerfProcesses = new ArrayList();
    
    boolean updated = false;
    
    if (!this.rolePerfProcesses.contains(rolePerfProcess)) {
      this.rolePerfProcesses.add(rolePerfProcess);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_rolePerfProcesses)
  public boolean addNewRolePerfProcess(Collection<RolePerfProcess> rolePerfProcesses) {
    if (this.rolePerfProcesses == null) this.rolePerfProcesses = new ArrayList();
    
    boolean updated = false;
    
    this.rolePerfProcesses.addAll(rolePerfProcesses);
    
    rolePerfProcessesCount += rolePerfProcesses.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_rolePerfProcesses)
  public boolean addNewRolePerfProcess(RolePerfProcess rolePerfProcess) {
    if (this.rolePerfProcesses == null) this.rolePerfProcesses = new ArrayList();
    
    boolean updated = false;
    
    this.rolePerfProcesses.add(rolePerfProcess);

    rolePerfProcessesCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_rolePerfProcesses)  
  public boolean removeRolePerfProcess(RolePerfProcess rolePerfProcess) {
    if (this.rolePerfProcesses != null) {
      boolean removed = rolePerfProcesses.remove(rolePerfProcess);
      if (removed) {
        rolePerfProcessesCount--;
      }
    }
    
    return false;
  }
  
  /** end {@link #rolePerfProcesses} */

  /** ASSOCIATION methods: {@link #actions} */

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getActionsCount() {
    return actionsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setActionsCount(int actionCount) {
    this.actionsCount = actionCount;
  }
  
  @DOpt(type=DOpt.Type.Getter) @AttrRef(value=A_actions)
  public Collection<Action> getActions() {
    return actions;
  }

  @DOpt(type=DOpt.Type.Setter) @AttrRef(value=A_actions)
  public void setActions(Collection<Action> actions) {
    this.actions = actions;
    actionsCount = actions.size();
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_actions)
  public boolean addAction(Collection<Action> actions) {
    if (this.actions == null) this.actions = new ArrayList();
    
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
    if (this.actions == null) this.actions = new ArrayList();
    
    boolean updated = false;
    
    if (!this.actions.contains(action)) {
      this.actions.add(action);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_actions)
  public boolean addNewAction(Collection<Action> actions) {
    if (this.actions == null) this.actions = new ArrayList();
    
    boolean updated = false;
    
    this.actions.addAll(actions);
    
    actionsCount += actions.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_actions)
  public boolean addNewAction(Action action) {
    if (this.actions == null) this.actions = new ArrayList();
    
    boolean updated = false;
    
    this.actions.add(action);

    actionsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_actions)  
  public boolean removeAction(Action action) {
    if (this.actions != null) {
      boolean removed = actions.remove(action);
      if (removed) {
        actionsCount--;
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  if <tt>role</tt> is a process user role
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * @version 3.3
   */
  public static boolean isProcessUserRole(jda.modules.security.def.Role role) {
    String roleName = role.getName();
    for (String npuRole : NonProcessUserRoleNames) {
      if (npuRole.equals(roleName)) {
        // role does not qualify to be a process user role
        return false;
      }
    }
    
    return true;
  }

  /**
   * @effects 
   *  if <tt>role</tt> is a process admin role
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>  
   * @version 3.3
   */
  public static boolean isProcessAdminRole(jda.modules.security.def.Role role) {
    String roleName = role.getName();
    for (String npuRole : ProcessAdminRoleNames) {
      if (npuRole.equals(roleName)) {
        // role is a process admin role
        return true;
      }
    }
    
    return false;
    
  }

  /**
   * @effects 
   *  if <tt>role</tt> is a teacher role
   *    return true
   *  else
   *    return false
   * @version 3.3
   */
  public static boolean isTeacherRole(jda.modules.security.def.Role role) {
    String roleName = role.getName();
    for (String tRole : TeacherRoleNames) {
      if (tRole.equals(roleName)) {
        // role is a teacher
        return true;
      }
    }
    
    return false;
  }

  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #actions}*/
  
//  /** ASSOCIATION methods: {@link #actions} */
//  
//  
//  @DOpt(type = DOpt.Type.Getter)
//  @AttrRef(name=A_actions)  
//  public Collection<Action> getActions() {
//    return actions;
//  }
//
//  @DOpt(type = DOpt.Type.Setter)
//  @AttrRef(name=A_actions)  
//  public void setActions(Collection<Action> actions) {
//    this.actions = actions;
//  }  
//  
//  private void addAction(Action subj) {
//    if (actions == null) actions = new ArrayList<>();
//    
//    actions.add(subj);
//  }
//
//  private void removeAction(Action subj) {
//    if (actions != null) {    
//      actions.remove(subj);
//    }
//  }
//
//  /** end ASSOCIATION: {@link #actions}*/
}
