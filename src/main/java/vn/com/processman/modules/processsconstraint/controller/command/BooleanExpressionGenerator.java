package vn.com.processman.modules.processsconstraint.controller.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.ds.DataObjectGenerator;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processdeliverables.model.FileType;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.modules.processsconstraint.model.function.AttribValue;
import vn.com.processman.modules.processsconstraint.model.function.Function;
import vn.com.processman.modules.processsconstraint.model.function.InstanceOf;
import vn.com.processman.modules.processsconstraint.model.function.IsNull;
import vn.com.processman.modules.processsconstraint.model.function.domain.SearchPreviousTaskInSameProcess;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview
 *  A {link DataObjectGenerator} for {@link BooleanExpression}. 
 *  
 * @author dmle
 */
public class BooleanExpressionGenerator extends DataObjectGenerator<BooleanExpression> {

  @Override
  public void init(DODMBasic dodm) throws ApplicationRuntimeException {
    Class c = getDomainClass();
    if (!dodm.isRegistered(c)) {
      dodm.registerClass(c);
    }
  }
  
  @Override
  public Class<BooleanExpression> getDomainClass() {
    return BooleanExpression.class;
  }

  @Override
  public Collection<BooleanExpression> genObjects(final DODMBasic dodm) throws NotFoundException, DataSourceException {
    List<BooleanExpression> exps = new ArrayList();
    
    /**
     * define pre-/post-conditions for Actions of interest
     */
    
    // Action 5.4.1
    //exps.addAll(genAction5_4_1Conds(dodm));
    
    // Action 5.5.1
    exps.addAll(genAction5_5_1Conds(dodm));

    // Action 5.5.2
    exps.addAll(genAction5_5_2Conds(dodm));

    // Action 5.5.3
    exps.addAll(genAction5_5_3Conds(dodm));

    // Action 5.5.4
    exps.addAll(genAction5_5_4Conds(dodm));

    // Action 5.6.1
    exps.addAll(genAction5_6_1Conds(dodm));

    // Action 5.7.1
    exps.addAll(genAction5_7_1Conds(dodm));

    // Action 5.7.2
    exps.addAll(genAction5_7_2Conds(dodm));

    // Action 5.8.1
    exps.addAll(genAction5_8_1Conds(dodm));

    /**
     * add expressions to the system
     */
    addObjects(dodm, exps);
    
    return exps;
  }

  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.4.1 
   */
  private List<BooleanExpression> genAction5_4_1Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action act = dom.retrieveObject(Action.class, Action.A_code, Op.EQ, "5.4.1");
    
    // pre-conditions: Nil
    //    List<BooleanExpression> preConds = genAction5_5_1PreConds(dodm, act, funcCurrentSubject);
    
    // post-conditions
    List<BooleanExpression> postConds = genAction5_4_1PostConds(dodm, act);
    
    List<BooleanExpression> allConds = postConds;
    
    return allConds;
  }

  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s for use as <b>post-conditions</b> of {@link Action} 5.4.1 
   */
  private List<BooleanExpression> genAction5_4_1PostConds(DODMBasic dodm, final Action act) throws NotFoundException, DataSourceException {
    List<BooleanExpression> conds = new ArrayList();
    Collections.addAll(conds, 
      new BooleanExpression[] {
        /*
         * Danh sach các một duyệt thi duoc tạo? (i.e. action.output is not null)
         */
        new BooleanExpression(
            dodm,
            act,
            // root function
            new IsNull(
                new AttribValue(act, Action.A_output)),
            // value
            false,
            // description
            "Đã có tệp danh sách các môn được duyệt?"
            ),
      }); // end array: exps   
    
    // add expressions to action as pre-/post conditions
    act.addPostConditions(conds);
    
    // return 
    return conds;
  }

  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.5.1 
   */
  private List<BooleanExpression> genAction5_5_1Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.5.1");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = genAction5_5_1PreConds(dodm, act, funcCurrentSubject);
    
    // post-conditions: Nil
    //List<BooleanExpression> postConds = genAction5_5_1PostConds(dodm, act, funcCurrentSubject);
    
    List<BooleanExpression> allConds = preConds;
    //allConds.addAll(postConds);
    
    return allConds;
  }

  /**
   *  @param funcCurrentSubject2 
   * @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s for use as <b>pre-conditions</b> of {@link Action} 5.5.1 
   */
  private List<BooleanExpression> genAction5_5_1PreConds(DODMBasic dodm, final Action4Subject act, final Function funcCurrentSubject) throws NotFoundException, DataSourceException {
    // a function that retrieves the previous task of the current Task of the *active* derived action of act 
    // this task is needed to evaluate some expression(s) (below)
    //final Function funcPrevOfCurrentTask = new AttribValue(new AttribValue(act, Action4Subject.A_activeTask), Task.A_prev);

    List<BooleanExpression> conds = new ArrayList();
    Collections.addAll(conds, 
      new BooleanExpression[] {
        /* use shared method 
        new BooleanExpression(
            dodm,
            act,
            // root function
            new AttribValue(
              new InstanceOf(SubjectTask.class, 
                  SubjectTask.A_subject, funcCurrentSubject,  
                  SubjectTask.A_task, funcPrevOfCurrentTask),
              SubjectTask.A_subjStatus),
              // value
              StatusCode.Done, 
              // description
              "Nhiệm vụ liền trước đã hoàn thành?"
              ) */
          genPrevSubjectTaskOfSameSubjectPreCond(dodm, act, funcCurrentSubject),
        /*
         * attribValue(S:=getSubjectInstance(),"isApprovedForExam") = true
         */
        new BooleanExpression(
          dodm,
          act,
          // root function
          new AttribValue(funcCurrentSubject, SubjectBySemester.A_approvedForExam),
          // value
          true,
          // description 
          "Môn học đã được duyệt thi cuối kì?"
        ),
        /*
         isNull(
                  attribValue(
                    instanceOf(File, "type", "FeeList"), "url")) = false
         */
        new BooleanExpression(
            dodm,
            act,
            // root function
            new IsNull(
                new AttribValue(
                    new InstanceOf(FileWrapper.class, 
                        FileWrapper.A_type, FileType.FeeList, 
                        FileWrapper.A_subj, funcCurrentSubject), 
                    FileWrapper.A_name)),
            // value
            false,
            // description
            "Danh sách đóng học phí của môn?"
            ),

        /*
          isNull(
            attribValue(
              instanceOf(File, "type", "F009"), "url")) = false
         */
        new BooleanExpression(
            dodm,
            act,
            // root function
            new IsNull(
                new AttribValue(
                    new InstanceOf(FileWrapper.class, 
                        FileWrapper.A_type, FileType.Template_009), 
                    FileWrapper.A_name)), 
            // value
            false,
            // description
            "Tệp mẫu đầu ra F009?"
            ),
        /*
          isNull(
              attribValue(
            instanceOf(File, "type", "IMT", "subj",S), "url")) = false
        */
        new BooleanExpression(
            dodm,
            act,
            // root function
            new IsNull(
              new AttribValue(
                  new InstanceOf(FileWrapper.class, 
                      FileWrapper.A_type, FileType.InternalMarkTable, 
                      FileWrapper.A_subj, funcCurrentSubject
                      ), 
                  FileWrapper.A_name)),
            // value
            false,
            // description
            "Bảng điểm thành phần của môn?"
            ), 
      }); // end array: exps   
    
    // add expressions to action as pre-/post conditions
    act.addPreConditions(conds);
    
    // return 
    return conds;
  }

  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.5.2 
   */
  private List<BooleanExpression> genAction5_5_2Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.5.2");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = genActionDependencyPreCond(dodm, act, funcCurrentSubject);
    
    // post-conditions
    List<BooleanExpression> postConds = genActiveSubjectActionOutputPostCond(dodm, act);
    
    List<BooleanExpression> allConds = preConds;
    allConds.addAll(postConds);
    
    return allConds;
  }

  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.5.3 
   */
  private List<BooleanExpression> genAction5_5_3Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.5.3");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = genActionDependencyPreCond(dodm, act, funcCurrentSubject);
    
    // post-conditions
    //List<BooleanExpression> postConds = genActiveSubjectActionOutputPostCond(dodm, act);
    
    List<BooleanExpression> allConds = preConds;
    //allConds.addAll(postConds);
    
    return allConds;
  }
  
  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.5.4 
   */
  private List<BooleanExpression> genAction5_5_4Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.5.4");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = genActionDependencyPreCond(dodm, act, funcCurrentSubject);
    
    // post-conditions
    //List<BooleanExpression> postConds = genActiveSubjectActionOutputPostCond(dodm, act);
    
    List<BooleanExpression> allConds = preConds;
    //allConds.addAll(postConds);
    
    return allConds;
  }
  
  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.6.1 
   */
  private List<BooleanExpression> genAction5_6_1Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.6.1");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = new ArrayList<>();
    preConds.add(genPrevSubjectTaskOfSameSubjectPreCond(dodm, act, funcCurrentSubject));
    
    // post-conditions: Nil
    //List<BooleanExpression> postConds = genAction5_5_1PostConds(dodm, act, funcCurrentSubject);
    
    List<BooleanExpression> allConds = preConds;
    //allConds.addAll(postConds);
    
    return allConds;
  }
  
  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.7.1 
   */
  private List<BooleanExpression> genAction5_7_1Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.7.1");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = new ArrayList<>();
    preConds.add(genSubjectTaskOfPriorTaskOfSameSubjectPreCond(dodm, act, funcCurrentSubject, "5.5"));
    
    // post-conditions: Nil
    //List<BooleanExpression> postConds = genAction5_5_1PostConds(dodm, act, funcCurrentSubject);
    
    List<BooleanExpression> allConds = preConds;
    //allConds.addAll(postConds);
    
    return allConds;
  }
 
  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.7.2 
   */
  private List<BooleanExpression> genAction5_7_2Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.7.2");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = genActionDependencyPreCond(dodm, act, funcCurrentSubject);
    
    // post-conditions
    //List<BooleanExpression> postConds = genActiveSubjectActionOutputPostCond(dodm, act);
    
    List<BooleanExpression> allConds = preConds;
    //allConds.addAll(postConds);
    
    return allConds;
  }
  
  
  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s as pre- and post-conditions for {@link Action} 5.8.1 
   */
  private List<BooleanExpression> genAction5_8_1Conds(DODMBasic dodm) throws NotFoundException, DataSourceException {
    final DOMBasic dom = dodm.getDom();
    
    // the action object
    final Action4Subject act = dom.retrieveObject(Action4Subject.class, Action4Subject.A_code, Op.EQ, "5.8.1");
    
    // a shared function to obtain the current subject from the *active* derived action of act (that is being performed by user) 
    // this subject is needed to evaluate some of the expressions (below)
    final Function funcCurrentSubject = new AttribValue(act, Action4Subject.A_activeSubject);
    
    // pre-conditions
    List<BooleanExpression> preConds = new ArrayList<>();
    preConds.add(genPrevSubjectTaskOfSameSubjectPreCond(dodm, act, funcCurrentSubject));
    
    // post-conditions: Nil
    //List<BooleanExpression> postConds = genAction5_5_1PostConds(dodm, act, funcCurrentSubject);
    
    List<BooleanExpression> allConds = preConds;
    //allConds.addAll(postConds);
    
    return allConds;
  }
  
  /**
   *  @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return a {@link BooleanExpression} for use as <b>pre-conditions</b> of {@link Action4Subject} <tt>act</tt>
   *  that the previous action of the same subject as the active {@link SubjectAction} has been completed.
   */  
  private List<BooleanExpression> genActionDependencyPreCond(DODMBasic dodm,
      Action4Subject act, final Function funcCurrentSubject) {
    // a function that retrieves the previous action of act
    final Function funcPrevOfCurrentAction = new AttribValue(new AttribValue(act, Action4Subject.A_activeAction), Action.A_prev);

    List<BooleanExpression> conds = new ArrayList();
    Collections.addAll(conds, 
      new BooleanExpression[] {
        /*
         * Previous action has completed 
         */
        new BooleanExpression(
            dodm,
            act,
            // root function
            new AttribValue(
                new InstanceOf(SubjectAction.class, 
                    SubjectAction.A_subject, funcCurrentSubject,  
                    SubjectAction.A_action, funcPrevOfCurrentAction),
                SubjectAction.A_subjStatus),
              // value
              StatusCode.Done, 
              // description
              "Bước liền trước đã hoàn thành?"
              ),
      }); // end array: exps   
    
    // add expressions to action as pre-/post conditions
    act.addPreConditions(conds);
    
    // return 
    return conds;
  }

  /**
   * @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression}s for use as <b>post-conditions</b> of {@link Action} <tt>act</tt>
   *  that the output file of its active {@link SubjectAction} has been uploaded into the system.
   */
  private List<BooleanExpression> genActiveSubjectActionOutputPostCond(DODMBasic dodm, final Action4Subject act) throws NotFoundException, DataSourceException {
    
    final Function funcCurrentSubjectAction = new AttribValue(act, Action4Subject.A_activeSubjectAction);

    List<BooleanExpression> conds = new ArrayList();
    Collections.addAll(conds, 
      new BooleanExpression[] {
        /*
         * Danh sach thi của môn học duoc tao (i.e. action.output is not null)
         */
        new BooleanExpression(
            dodm,
            act,
            // root function
            new IsNull(
                new AttribValue(funcCurrentSubjectAction, SubjectAction.A_output)),
            // value
            false,
            // description
            "Đã cập nhật tệp kết quả?"
            ),
        /*
         * Danh sach thi da duoc thong bao tren FIT (?)
         */
        //TODO: do we need this expression
      }); // end array: exps   
    
    // add expressions to action as pre-/post conditions
    act.addPostConditions(conds);
    
    // return 
    return conds;
  }
  
  
  /**
   * @modifies act.preConds
   * 
   * @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression} for use as pre-condition of <tt>act</tt> that requires that
   *  the previous {@link SubjectTask} of the same subject as <tt>funcCurrentSubject</tt> has been done. 
   */
  private BooleanExpression genPrevSubjectTaskOfSameSubjectPreCond(DODMBasic dodm, final Action4Subject act, final Function funcCurrentSubject) throws NotFoundException, DataSourceException {
    // a function that retrieves the previous task of the current Task of the *active* derived action of act 
    // this task is needed to evaluate some expression(s) (below)
    final Function funcPrevOfCurrentTask = new AttribValue(new AttribValue(act, Action4Subject.A_activeTask), Task.A_prev);

    BooleanExpression cond = 
        new BooleanExpression(
            dodm,
            act,
            // root function
            new AttribValue(
              new InstanceOf(SubjectTask.class, 
                  SubjectTask.A_subject, funcCurrentSubject,  
                  SubjectTask.A_task, funcPrevOfCurrentTask),
              SubjectTask.A_subjStatus),
              // value
              StatusCode.Done, 
              // description
              "Nhiệm vụ liền trước đã hoàn thành?"
              );   
    
    // add expressions to action as pre-/post conditions
    act.addPreCondition(cond);
    
    // return 
    return cond;
  }
  
  /**
   * @modifies act.preConds
   * 
   * @requires 
   *    Action objects have been created in the system by <tt>dodm</tt>
   *  
   *  @effects create and return {@link BooleanExpression} for use as pre-condition of <tt>act</tt> that requires that
   *  the {@link SubjectTask} of the {@link Task} whose code is <tt>taskCode</tt> and of same subject as <tt>funcCurrentSubject</tt> has been done. 
   */
  private BooleanExpression genSubjectTaskOfPriorTaskOfSameSubjectPreCond(DODMBasic dodm, final Action4Subject act, final Function funcCurrentSubject, final String taskCode) throws NotFoundException, DataSourceException {
    // retrieve the current task
    Function funcCurrentTask = new AttribValue(act, Action4Subject.A_activeTask);

    Function funcPreviousTaskOfSameProcess = new SearchPreviousTaskInSameProcess(funcCurrentTask, taskCode); 
    
    BooleanExpression cond = 
        new BooleanExpression(
            dodm,
            act,
            // root function
            new AttribValue(
              new InstanceOf(SubjectTask.class, 
                  SubjectTask.A_subject, funcCurrentSubject,  
                  SubjectTask.A_task, funcPreviousTaskOfSameProcess),
              SubjectTask.A_subjStatus),
              // value
              StatusCode.Done, 
              // description
              "Nhiệm vụ "+taskCode+" của môn học đã hoàn thành?"
              );   
    
    // add expressions to action as pre-/post conditions
    act.addPreCondition(cond);
    
    // return 
    return cond;
  }
  
//  /**
//   * @effects 
//   *  return a {@link Function} responsible for obtaining a {@link SubjectBySemester} instance from user's input
//   */
//  protected Function getSubjectInstanceFunction() {
//    // return the function that retrieves Subject from the owner expression
//    return new GetSubjectInstance();
//  }
}