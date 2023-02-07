package vn.com.processman.modules.processstructure.model;

import java.util.ArrayList;
import java.util.Collection;

import jda.modules.common.exceptions.ConstraintViolationException;
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
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview
 *  A sub-type of {@link Task} that is applied to course subjects.
 *  
 * @author dmle
 */
public class Task4Subject extends Task {
  
  public static final ScopeDef ScopeDefAction4SubjectEmbeddedExec = new ScopeDef(
    Action4Subject.class,
    new String[] {
      Action.A_codeDef, Action.A_nameDef, //Action.A_task, 
      Action.A_preConds,
      //Action.A_output, 
      Action.A_postConds,
      Action.A_status,
      Action4Subject.A_subjectActions
      },Boolean.FALSE); // end ScopeDefAction4Subject
  
  public static final ScopeDef ScopeDefSubjectTaskExec = new ScopeDef(
      SubjectTask.class,
      new String[] {SubjectTask.A_subject, SubjectTask.A_subjStatus},Boolean.FALSE); // end ScopeDefSubjectTask

  public static final ScopeDef ScopeDefSubjectActionExec = Action4Subject.ScopeDefSubjectActionExec;
  
  public static final ScopeDef ScopeDefFileExec = Action.ScopeDefFileExec;

  // END: extended scope def

  public static final String A_subjectTasks = "subjectTasks";
  public static final String Assoc_Task4SubjectAndSubjectTask = "Task4Subject-has-SubjectTask";

  /**
   * non-id, essential attributes needed for the non-id constructor to create objects
   */
  public static final String[] A_EssentialNonIds = {
    Task.A_code, Task.A_name, Task.A_description, 
    Task.A_startWeek, Task.A_endWeek,
    Task.A_def, Task.A_prev, 
    Task.A_actions, 
    Task4Subject.A_subjectTasks
  };
  
  /**
   * non-id, essential attributes needed for displaying the view of and creating a new object
   */
  public static final String[] A_EssentialCreateNews = {
    Task.A_code, Task.A_name, Task.A_description, 
    Task.A_startWeek, Task.A_endWeek,
    Task.A_prev, 
    Task.A_actions, 
    Task4Subject.A_subjectTasks
  };

  /**attributes for use when this is part of the containment tree of a process application */
  public static final String[] A_application = {
    Task.A_id, Task.A_codeDef, Task.A_nameDef, 
    Task.A_descriptionDef, Task.A_process, Task.A_actions, 
    Task4Subject.A_subjectTasks};

  /**association {@link #Assoc_Task4SubjectAndSubjectTask}*/
  @DAssoc(ascName = Assoc_Task4SubjectAndSubjectTask, role = "task4subject", 
      ascType = AssocType.One2Many, endType = AssocEndType.One, 
      associate = @Associate(type = SubjectTask.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
  @DAttr(name=A_subjectTasks, type=Type.Collection, optional=false, serialisable=false, 
    filter=@Select(clazz=SubjectTask.class))
  private Collection<SubjectTask> subjectTasks;
  private int subjectTasksCount;
  
  public Task4Subject(Integer id,String code,  String name, String desc,
      Integer startWeek, Integer endWeek, Process process, Task def, Task prev, 
      StatusCode status) throws ConstraintViolationException {
    super(id, code, name, desc, startWeek, endWeek, process, def, prev, status);
    
    subjectTasks = new ArrayList();
  }

  public Task4Subject(String code, String name, String desc, Integer startWeek,
      Integer endWeek, Process process, Task def, Task prev, StatusCode status) {
    //super(code, name, desc, startWeek, endWeek, process, def, prev, status);
    this(null, code, name, desc, startWeek, endWeek, process, def, prev, status);
  }
  
  // constructor: object form (no status)
  public Task4Subject(String code, String name, String desc, Integer startWeek,
      Integer endWeek, 
      Process process, 
      Task def, Task prev
      //, StatusCode status
      ) {
    this(null, code, name, desc, startWeek, endWeek, 
        process,
        def, prev,
        //status
        null
        );
  }
  
  // constructor: object form (no def, status)
  public Task4Subject(String code, String name, String desc, Integer startWeek,
      Integer endWeek, 
      Process process, 
      //Task def, 
      Task prev
      //, StatusCode status
      ) {
    this(null, code, name, desc, startWeek, endWeek, 
        process,
        //def,
        null,
        prev,
        //status
        null
        );
  }
  
  // constructor: create derived object
  public Task4Subject(Process process, Task def, Task prev) {
    super(process, def, prev);
    
    subjectTasks = new ArrayList();
  }

  /** ASSOCIATION methods: {@link #subjectTasks} */
  public Collection<SubjectTask> getSubjectTasks() {
    return subjectTasks;
  }

  public void setSubjectTasks(Collection<SubjectTask> subjectTasks) {
    this.subjectTasks = subjectTasks;
    subjectTasksCount = subjectTasks.size();
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getSubjectTasksCount() {
    return subjectTasksCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setSubjectTasksCount(int subjectTaskCount) {
    this.subjectTasksCount = subjectTaskCount;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_subjectTasks)
  public boolean addSubjectTask(Collection<SubjectTask> subjectTasks) {
    boolean updated = false;
    
    for (SubjectTask subjectTask : subjectTasks) {
      if (!this.subjectTasks.contains(subjectTask)) {
        this.subjectTasks.add(subjectTask);
      }
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=A_subjectTasks)
  public boolean addSubjectTask(SubjectTask subjectTask) {
    boolean updated = false;
    
    if (!this.subjectTasks.contains(subjectTask)) {
      this.subjectTasks.add(subjectTask);
    }
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_subjectTasks)
  public boolean addNewSubjectTask(Collection<SubjectTask> subjectTasks) {
    boolean updated = false;
    
    this.subjectTasks.addAll(subjectTasks);
    subjectTasksCount += subjectTasks.size();
    
    return updated;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  @AttrRef(value=A_subjectTasks)
  public boolean addNewSubjectTask(SubjectTask subjectTask) {
    boolean updated = false;
    
    this.subjectTasks.add(subjectTask);
    subjectTasksCount++;
    
    return updated;
  }
  
  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value=A_subjectTasks)  
  public boolean removeSubjectTask(SubjectTask subjectTask) {
    boolean removed = subjectTasks.remove(subjectTask);
    if (removed) {
      subjectTasksCount--;
    }
    
    return false;
  }
  
  //TODO: method updater (?)
  
  /** end ASSOCIATION: {@link #subjectTasks}*/  
  
  /**
   * @requires 
   *  forall {@link SubjectTask} a in {@link #subjectTasks}. a.subjStatus is up-to-date
   *  (this is achieved via {@link #evaluateSubjStatus() on each of them} 
   *  
   * @effects 
   *  if all {@link SubjectTask} a in {@link #subjectTasks}. a.subjStatus = {@link StatusCode#Done}
   *    call {@link #setStatus(StatusCode)} -> <tt>({@link StatusCode#Done})</tt>
   *  else 
   *    call {@link #setStatus(StatusCode)} -> <tt>({@link StatusCode#NotDone})</tt>
   *    
   *  <br>if <tt>status</tt> was changed return <tt>true</tt>; else return <tt>false</tt>
   */
  @Override
  public boolean evaluateStatus() {
    /* v1.2: improved to conform to the semantics of the supertype's method by evaluating and updating 
     * status regardless of whether there are subject tasks

    if (subjectTasks != null) {
      StatusCode old = getStatus();
      
      StatusCode status = StatusCode.Done;
      for (SubjectTask a : subjectTasks) {
        if (!a.getSubjStatus().equals(StatusCode.Done)) {
          // not done
          status = StatusCode.NotDone;
          break;
        }
      }
      
      if (!status.equals(old)) {
        // update
        setStatus(status);
        return true;
      } else {
        return false;
      }
    } else {
      // no subject tasks
      return false;
    }
    */
    
    StatusCode old = getStatus();
    StatusCode status;
    
    if (subjectTasks != null && !subjectTasks.isEmpty()) {
      status = StatusCode.Done;
      for (SubjectTask a : subjectTasks) {
        if (!a.getSubjStatus().equals(StatusCode.Done)) {
          // not done
          status = StatusCode.NotDone;
          break;
        }
      }
    } else {
      // no subject tasks
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
}
