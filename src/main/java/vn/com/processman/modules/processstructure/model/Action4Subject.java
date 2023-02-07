package vn.com.processman.modules.processstructure.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import vn.com.processman.modules.dsecurity.model.Role;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview 
 *  An sub-type of {@link Action} that is applied to course subjects. 
 *  
 * @author dmle
 *
 */
public class Action4Subject extends Action {

  //extended scope definition: 
  // TODO: move these out of this class if too many
  /* v1.2: reverse config b/w panel and field */
  public static final ScopeDef ScopeDefSubjectActionExec = new ScopeDef(
      SubjectAction.class,
      new String[] {"*"},Boolean.FALSE) {
    @Override // enable SubjectAction.A_subjStatus
    public Boolean isEditable(String attribName) {
      if (attribName.equals(SubjectAction.A_subjStatus)) {
        return Boolean.TRUE;
      } else {
        return null;
      }
    }
  }; 
//  public static final ScopeDef ScopeDefSubjectActionExec = new ScopeDef(
//      SubjectAction.class,
//      new String[] {"*"},Boolean.FALSE) 
//  {
//    @Override // enable SubjectAction.A_subjStatus
//    public Boolean isEditable(String attribName) {
//      if (attribName.equals(SubjectAction.A_subject)) {
//        return Boolean.FALSE;
//      } else if (attribName.equals(SubjectAction.A_subjStatus)) {
//        return Boolean.TRUE;
//      } else {
//        return null;
//      }
//    }
//  }
  ; 
  // end ScopeDefSubjectActionExec

 // END: extended scope def
  
  public static final String A_activeSubject = "activeSubject";
  public static final String A_activeAction = "activeAction";
  public static final String A_activeTask = "activeTask";
  public static final String A_activeSubjectAction = "activeSubjectAction";
  
  public static final String A_subjectActions = "subjectActions";
  public static final String Assoc_Action4SubjectAndSubjectAction = "action4subject-has-subjectaction";

  /**
   * non-id essential attributes that are used for the non-id constructor to create objects
   */
  public static final String[] A_EssentialNonIds = new String[] {
    //Action4Subject.A_id, 
    Action4Subject.A_code, Action4Subject.A_name, 
    //Action.A_preConds,
    Action4Subject.A_description, Action4Subject.A_def, Action4Subject.A_prev, 
    Action4Subject.A_task,
    Action4Subject.A_subjectActions,
    //Action.A_postConds,
  };

  /**
   * non-id essential attributes that are used for displaying and creating a new object
   */
  public static final String[] A_EssentialCreateNews = new String[] {
    //Action4Subject.A_id, 
    Action4Subject.A_code, Action4Subject.A_name, 
    //Action.A_preConds,
    Action4Subject.A_description, Action4Subject.A_prev, 
    Action4Subject.A_task,
//    Action4Subject.A_subjectActions,
    //Action.A_postConds,
  };
  
  /**attributes for use when this is part of the containment tree of a process application */
  public static final String[] A_application = {
    Action.A_id, Action.A_codeDef, Action.A_nameDef, Action.A_descriptionDef, 
    Action.A_task, 
    Action4Subject.A_subjectActions
  };

  /** 
   * The *active* {@link SubjectAction} among {@link #subjectActions} that user is current performing.  
   * It is used to derive other attributes (below)
   */
  @DAttr(name=A_activeSubjectAction,type=Type.Domain,auto=true,serialisable=false)
  private static SubjectAction activeSubjectAction;
  
  /** 
   * the {@link SubjectBySemester} of (that is derived from) {@link #activeSubjectAction}  
   */
  @DAttr(name=A_activeSubject,type=Type.Domain,auto=true,serialisable=false)
  private static SubjectBySemester activeSubject;

  /** 
   * The {@link Action} of (that is derived from) {@link #activeSubjectAction} 
   */
  @DAttr(name=A_activeAction,type=Type.Domain,auto=true,serialisable=false)
  private static Action activeAction;
  
  /** 
   * The {@link Task} of (that is derived from) {@link #activeAction} 
   */
  @DAttr(name=A_activeTask,type=Type.Domain,auto=true,serialisable=false)
  private static Task activeTask;

  /**association {@link #Assoc_Action4SubjectAndSubjectAction}*/
  @DAssoc(ascName = Assoc_Action4SubjectAndSubjectAction, role = "action4subject", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = SubjectAction.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_subjectActions, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=SubjectAction.class))
  private Collection<SubjectAction> subjectActions;
  private int subjectActionsCount;
  
  // constructor: from data source
  public Action4Subject(Integer id, String code, String name, String desc, Action def, Action prev, 
      Task task, FileWrapper output, StatusCode status, Role role) {
    super(id, code, name, desc, def, prev, task, output, status, role);
    
    subjectActions = new ArrayList();
  }

  // constructor: from object form 
  public Action4Subject(String code, String name, String desc, Action def, Action prev, Task task, FileWrapper output, 
      StatusCode status, Role role) {
    this(null, code, name, desc, def, prev, task, output, status, role);
  }

  // constructor: from object form (without output)
  public Action4Subject(String code, String name, String desc, Action def, Action prev, Task task,
      // File output, 
      StatusCode status, Role role) {
    this(null, code, name, desc, def, prev, task, 
        //output,
        null,
        status, 
        role);
  }

  // constructor: from nested object form (no output, status)
  public Action4Subject(String code, String name, String desc, Action def, Action prev, Task task
      // File output, 
      //StatusCode status
      , Role role
      ) {
    this(null, code, name, desc, def, prev, task,
        //output,
        null,
        //status
        null,
        role
        );
  }

  // constructor: from nested object form (no def, output, status)
  public Action4Subject(String code, String name, String desc, 
      //Action def, 
      Action prev, Task task
      // File output, 
      //StatusCode status
      , Role role
      ) {
    this(null, code, name, desc, 
        //def,
        null,
        prev, task,
        //output,
        null,
        //status
        null,
        role
        );
  }
  
  // constructor: create derived object
  public Action4Subject(Action def, Action prev, Task task) {
    super(def, prev, task);
    
    subjectActions = new ArrayList();
  }

  /** ASSOCIATION methods: {@link #subjectActions} */
  public Collection<SubjectAction> getSubjectActions() {
    return subjectActions;
  }

  public void setSubjectActions(Collection<SubjectAction> subjectActions) {
    this.subjectActions = subjectActions;
    subjectActionsCount = subjectActions.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getSubjectActionsCount() {
    return subjectActionsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setSubjectActionsCount(int subjectActionCount) {
    this.subjectActionsCount = subjectActionCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_subjectActions)
  public boolean addSubjectAction(Collection<SubjectAction> subjectActions) {
    boolean updated = false;
    
    for (SubjectAction subjectAction : subjectActions) {
      if (!this.subjectActions.contains(subjectAction)) {
        this.subjectActions.add(subjectAction);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_subjectActions)
  public boolean addSubjectAction(SubjectAction subjectAction) {
    boolean updated = false;
    
    if (!this.subjectActions.contains(subjectAction)) {
      this.subjectActions.add(subjectAction);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_subjectActions)
  public boolean addNewSubjectAction(Collection<SubjectAction> subjectActions) {
    boolean updated = false;
    
    this.subjectActions.addAll(subjectActions);
    subjectActionsCount += subjectActions.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_subjectActions)
  public boolean addNewSubjectAction(SubjectAction subjectAction) {
    boolean updated = false;
    
    this.subjectActions.add(subjectAction);
    subjectActionsCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_subjectActions)  
  public boolean removeSubjectAction(SubjectAction subjectAction) {
    boolean removed = subjectActions.remove(subjectAction);
    if (removed) {
      subjectActionsCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #subjectActions}*/
  
  public SubjectBySemester getActiveSubject() {
//    if (activeSubject == null && activeSubjectAction != null)
//      activeSubject = activeSubjectAction.getSubject();
    
    return activeSubject;
  }

  public Action getActiveAction() {
//  if (activeTask == null && activeSubjectAction != null)
//    activeTask = activeSubjectAction.getAction().getTask();
//  
    return activeAction;
  }
  
  public Task getActiveTask() {
//    if (activeTask == null && activeSubjectAction != null)
//      activeTask = activeSubjectAction.getAction().getTask();
//    
    return activeTask;
  }
  
  public SubjectAction getActiveSubjectAction() {
    return activeSubjectAction;
  }

  /**
   * @effects 
   *  set {@link #activeSubjectAction}  = <tt>subjectAction</tt> and update other attributes that are derived from this.
   *  
   *  <p>if <tt>subjectAction = null</tt> then nullify the above. 
   *    
   *  <p>return the former value of {@link #activeSubjectAction}  
   */
  public SubjectAction setActiveSubjectAction(SubjectAction subjectAction) {
    SubjectAction formerActive = this.activeSubjectAction;
    
    this.activeSubjectAction = subjectAction;
    
    if (subjectAction != null) {
      activeAction = activeSubjectAction.getAction();
      activeSubject = activeSubjectAction.getSubject();
      activeTask = activeAction.getTask();
    } else {
      activeAction = null; activeSubject = null; activeTask = null;
    }
    
    // also update the definition Action (needed for evaluating expressions)
    Action4Subject def = (Action4Subject) getDef();
    if (def != null) {
      def.setActiveSubjectAction(subjectAction);
    }
    
    return formerActive;
  }

  /**
   * @effects 
   *  if {@link #subjectActions} is initialised to contain elements
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 1.2
   */
  public boolean hasSubjectActions() {
    return (subjectActions != null // not needed this check but just to be sure! 
              && !subjectActions.isEmpty());
  }
  
  /**
   * @effects 
   *  if {@link #subjectActions} is initialised AND {@link #subjectActions}<tt>.size() > index</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean hasSubjectActionAt(int index) {
    return subjectActions.size() > index;
  }

  /**
   * @effects 
   *  return {@link SubjectAction} element at <tt>index</tt> in the {@link Iterator} of {@link #subjectActions}; or 
   *  return <tt>null</tt> if <tt>index</tt> is invalid
   */
  public SubjectAction getSubjectAction(int index) {
    int i = 0;
    Iterator<SubjectAction> it = subjectActions.iterator();
    SubjectAction sa;
    while (it.hasNext()) {
      sa = it.next();
      if (i == index)
        return sa;
      else
        i++;
    }
    
    return null;
  }
  
  /**
   * @requires 
   *  forall {@link SubjectAction} a in {@link #subjectActions}. a.subjStatus is up-to-date
   *  (this is achieved via {@link #evaluateSubjStatus() on each of them} 
   *  
   * @modifies this.{@link #status}
   *  
   * @effects 
   *  if for all {@link SubjectAction} a in {@link #subjectActions}. a.subjStatus = {@link StatusCode#Done}
   *    call {@link #setStatus(StatusCode)} -> <tt>({@link StatusCode#Done})</tt>
   *  else 
   *    call {@link #setStatus(StatusCode)} -> <tt>({@link StatusCode#NotDone})</tt>
   *    
   *  <br>if <tt>status</tt> was changed return <tt>true</tt>; else return <tt>false</tt>
   */
  @Override
  public boolean evaluateStatus() {
    StatusCode old = getCurrentStatus();
    StatusCode status;
    
    if (subjectActions != null 
        && !subjectActions.isEmpty()  // v1.2: added this check
        ) {
      
      status = StatusCode.Done;
      for (SubjectAction a : subjectActions) {
        if (!a.isSubjStatusEqual(StatusCode.Done)) {
          // not done
          status = StatusCode.NotDone;
          break;
        }
      }
      

    } else {
      // no subject actions
      status = StatusCode.NotDone;
    }

    if (old == null || !status.equals(old)) {
      // update
      setStatus(status);
      return true;
    } else {
      return false;
    }
  }

  /**
   * IMPORTANT: this method must not change any state of this object!
   * 
   * @effects 
   *   if {@link #activeSubjectAction} is not null
   *     evaluate the pre and post conditions on {@link #activeSubjectAction} and return the corresponding 
   *     {@link StatusCode}
   *   else
   *    return <tt>null</tt>  
   */
  public StatusCode evaluateSubjStatus() {
    if (activeSubjectAction != null) {
      
      //if (activeSubjectAction.hasOutput()) {
      StatusCode status;
      
      if (evaluatePreConditions() & evaluatePostConditions()) {
        status = StatusCode.Done;
      } else {
        status = StatusCode.NotDone;
      }
      
      return status;
//      } else {
//        return StatusCode.NotDone;
//      }
    } else {
      return null;
    }
  }

}
