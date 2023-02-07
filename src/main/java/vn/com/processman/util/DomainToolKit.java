/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.FlexiQuery;
import jda.modules.oql.def.Query;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.Role;
import jda.modules.security.def.UserRole;
import jda.mosa.model.Oid;
import vn.com.processman.modules.dsecurity.model.RolePerfProcess;
import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.modules.dsecurity.model.UserRoleHistory;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.ProcessType;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.model.TeachingBySemester;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.Semester;


/**
 * @overview
 *  A domain-specific tool kit for shared functionality.
 *  
 * @author dmle
 * 
 * @version 
 * - 1.1: created <br>
 * - 3.3: updated
 */
public class DomainToolKit {
  private static final Class<SubjectBySemester> SubjectBySemCls = SubjectBySemester.class;
  private static final Class<TeachingBySemester> TeachingBySemCls = TeachingBySemester.class;
  private static final Class<Task4Subject> Task4SubjectCls = Task4Subject.class;
  private static final Class<SubjectTask> SubjectTaskCls = SubjectTask.class;
  private static final Class<SubjectAction> SubjectActionCls = SubjectAction.class;
  private static final Class<ProcessApplication> ProcessApplicationCls = ProcessApplication.class;
  private static final Class<Process> ProcessCls = Process.class;
  private static final Class<RolePerfProcess> RolePerfProcessCls = RolePerfProcess.class;
  private static final Class<UserRole> UserRoleCls = UserRole.class;
  private static final Class<Action> ActionCls = Action.class;
  private static final Class<Action4Subject> Action4SubjectCls = Action4Subject.class;
  private static final Class<Task> TaskCls = Task.class;
  private static final Class<Task4Subject> Task4SubjCls = Task4Subject.class;
  private static final Class<UserRoleHistory> UserRoleHistoryCls = UserRoleHistory.class;
  private static final Class<DomainUser> DomainUserCls = DomainUser.class;
  private static final Class<Teacher> TeacherCls = Teacher.class;
  private static final Class<Subject> SubjectCls = Subject.class;
  
  // the query used to retrieve objects under security restriction
  private static FlexiQuery queryAllowedTasks;

  // the query used to retrieve objects under security restriction
  private static FlexiQuery queryAllowedActions;
  
  /** the parameterised query used to retrieve objects, it is created once and updated each time this is run*/
  private static FlexiQuery queryProcAppsByProcName;
  
  /**nested query: select Process.id where Process.name matches n*/
  private static FlexiQuery queryProcessIdByName;
  /** nestedRoleQuery: select UserRole.role where UserRole.user = currentUser */
  private static FlexiQuery queryRolePerfProcess;
  
  private static FlexiQuery querySubjectTaskBySubjSem;
  private static FlexiQuery queryUserSubjectSem;
  private static FlexiQuery queryTask4SubjectBySubjSem;
  private static FlexiQuery querySubjectActionBySubjSem;
  private static FlexiQuery queryUserPlaysRoles;
  private static FlexiQuery queryCurrentUserRoleIds;
  private static FlexiQuery queryUserByLogin = null;
  
  private static FlexiQuery queryProcApps;
  private static FlexiQuery queryRolePerfProcess2;
  
  private static FlexiQuery queryAppliedActions;
  
  private static FlexiQuery queryUserByRole;
  
  private static FlexiQuery queryAppliedActions4Subj;
  private static FlexiQuery queryTeachersOfAct;
  private static FlexiQuery queryTeacherOfSubjAct;
  private static FlexiQuery querySubjActOfTeacher;
  private static FlexiQuery querySubjectActBySubject;
  private static FlexiQuery queryProcessAppExist;
  private static FlexiQuery queryProcessExist;
  
  private DomainToolKit() {}

  /*** 
   * Custom Scope Definitions for some modules 
   */
  
//  /**
//   * {@link SubjectBySemester} reference {@link Subject}<tt>.code</tt>
//   */
//  public static final Select SubjectBySemester_Ref_SubjectCode = new Select() {
//    
//    private final String[] attributesArr = {SubjectBySemester.A_code};
//    
//    @Override
//    public Class<? extends Annotation> annotationType() {
//      return Select.class;
//    }
//    
//    @Override
//    public Class clazz() {
//      return SubjectBySemester.class;
//    }
//    
//    @Override
//    public String[] attributes() {
//      return attributesArr;
//    }
//  };
//  /** END Custom Scope Definitions */
  
  /**
   * @effects 
   *  if exists in <tt>dodm</tt> {@link SubjectBySemester} <tt>s</tt> st.: <tt>s.subject=subj, s.semester=sem, s.year=year</tt>
   *    return <tt>s</tt>
   *  else
   *    return <tt>null</tt> 
   */
  public static SubjectBySemester retrieveSubjectBySemester(final DODMBasic dodm, final Subject subj,
      final Semester sem, final Integer year) throws NotFoundException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    SubjectBySemester s = dom.retrieveObject(SubjectBySemester.class, 
        SubjectBySemester.A_candidateKey, 
        new Op[] {Op.EQ, Op.EQ, Op.EQ}, 
        new Object[] {subj, sem, year});
    
    return s;
  }

  /**
   * @requires
   *  r != null /\ p != null /\ p is a base process
   *  
   * @effects <pre>
   *  if exists in the underlying class store of {@link Action} a record for <tt>(r,a)</tt> for 
   *  some {@link Action} <tt>a</tt> of <tt>p</tt> 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  </pre>
   *  
   * @version 1.2
   */
  public static boolean existRolePerformsAnyActionOf(final DODMBasic dodm, final Role r, final Process p) {
    
    boolean perf = false;
    
    Collection<Task> tasks = p.getTasks();
    
    if (tasks != null && !tasks.isEmpty()) {
      Collection<Action> acts;
      DOMBasic dom = dodm.getDom();
      DSMBasic dsm = dodm.getDsm();
      Class<Action> ActionCls = Action.class;
      DAttr[] attribs = {
          dsm.getDomainConstraint(ActionCls, Action.A_id),
          dsm.getDomainConstraint(ActionCls, Action.A_role)
      };
      Object[] vals = new Object[attribs.length];
      vals[1] = r;
      
      for (Task t : tasks) {
        acts = t.getActions();
        
        if (acts != null && !acts.isEmpty()) {
          for (Action a : acts) {
            vals[0] = a.getId();
            if (dom.existObject(ActionCls, attribs, vals)) {
              // exists
              perf = true; break;
            }
          }
        }
      }
    }
    
    return perf;
  }

  /**
   * @requires
   *  r != null /\ p != null /\ p is a base process
   *  
   * @effects <pre>
   *  if exists in the underlying class store of {@link RolePerfProcess} a record for <tt>(r,p)</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  </pre>
   *  
   * @version 1.2
   */
  public static boolean existRolePerformsProcess(final DODMBasic dodm, final Role r, final Process p) {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    Class<RolePerfProcess> RolePerfProcessCls = RolePerfProcess.class;
    DAttr[] attribs = {
        dsm.getDomainConstraint(RolePerfProcessCls, RolePerfProcess.A_role),
        dsm.getDomainConstraint(RolePerfProcessCls, RolePerfProcess.A_process)
    };
    Object[] vals = {r, p};
    
    boolean perf = dom.existObject(RolePerfProcessCls, attribs, vals);
    
    return perf;
  }

  /**
   * @requires
   *  r != null /\ p != null /\ p is a base process
   *  
   * @effects 
   *  delete from the underlying class store of {@link RolePerfProcess} the record for <tt>(r,p)</tt>
   *  
   *  <p>throws DataSourceException  if failed
   * @version 1.2
   */
  public static void deleteRolePerformsProcess(final DODMBasic dodm, final Role r, final Process p) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    Class<RolePerfProcess> RolePerfProcessCls = RolePerfProcess.class;
    String[] attribNames = { RolePerfProcess.A_role, RolePerfProcess.A_process };
    Op[] opts = {Op.EQ, Op.EQ};
    Object[] vals = {r, p};
    
    Query q = QueryToolKit.createSearchQuery(dsm, RolePerfProcessCls, attribNames, opts, vals);
    
    dom.deleteObjectsDS(RolePerfProcess.class, q);
  }

  /**
   * @requires 
   *  user is not null /\ user.roles != null
   *  
   * @effects 
   *  if user is <b>strictly</b> a process user (i.e. a user that uses a {@link Process} and not a process-admin user) 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  
   * @version 1.2
   */
  public static boolean isProcessUserStrictly(final DomainUser user) {
    Collection<jda.modules.security.def.Role> roles = user.getTheRoles();
    
    boolean isProcUser = false;
    //boolean isProcAdminUser = false;
    
    for (Role r : roles) {
      if (!isProcUser && vn.com.processman.modules.dsecurity.model.Role.isProcessUserRole(r)) {
        // found a role that is a process user
        isProcUser = true;
      } else if (//!isProcAdminUser && 
          vn.com.processman.modules.dsecurity.model.Role.isProcessAdminRole(r)) {
        //isProcAdminUser = true;
        return false;
      }
    }
    
    //    if (isProcAdminUser)
    //      return false;
    //    else
    return isProcUser;
  }

  /**
   * @requires 
   *  user != null /\ user.roles != null
   * @effects 
   *  if user's role contains teacher
   *    return true
   *  else
   *    return false 
   *  
   * @version 1.2
   */
  public static boolean isTeacherUser(final DomainUser user) {
    Collection<jda.modules.security.def.Role> roles = user.getTheRoles();
    if (roles != null) {
      for (jda.modules.security.def.Role role : roles) {
        if (vn.com.processman.modules.dsecurity.model.Role.isTeacherRole(role)) {
          return true;
        }
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
   * @version 1.2
   */
  public static boolean isTeacherRole(Role role) {
    if (role == null) return false;
    
    return vn.com.processman.modules.dsecurity.model.Role.isTeacherRole(role);
  }

  /**
   * This works similar to {@link #retrieveUserSubjectSemIds(DODMBasic, DomainUser, Semester, Integer)} except that it only counts
   * the ids instead of retrieving them
   * 
   * @effects 
   *  If <tt>teacher</tt> teaches a {@link Subject} in the given <tt>semester<tt> in the given <tt>year</tt>
   *    return true
   *  else
   *    return false
   * @version 1.2
   */
  public static boolean doesTeacherTeachIn(final DODMBasic dodm, final DomainUser teacher, final Semester semester, final Integer year) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryUserSubjectSem == null) {
      // create query with unchanged parts
      queryUserSubjectSem = new FlexiQuery(dsm, SubjectBySemCls);
      queryUserSubjectSem.addJoinExpression(
          new Class[] {TeachingBySemCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndTeaching});
    }
    
    // variable query parts 
    Expression exp1 = queryUserSubjectSem.addConstraintExpression(SubjectBySemester.A_semester, Op.EQ, semester);
    Expression exp2 = queryUserSubjectSem.addConstraintExpression(SubjectBySemester.A_year, Op.EQ, year);
    Expression exp3 = queryUserSubjectSem.addConstraintExpression(TeachingBySemCls, TeachingBySemester.A_teacher, Op.EQ, teacher);
    
    try {
      int count = dom.loadObjectCount(SubjectBySemCls, queryUserSubjectSem);
      
      return (count > 0);
    } catch (Exception e) {
      throw e;
    } finally {
      queryUserSubjectSem.remove(exp1, exp2, exp3);
    }
  }

  /**
   * @effects 
   *  if <tt>user</tt> plays a {@link Role} in the given <tt>semester</tt> of the given <tt>year</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to perform operation on the data source. 
   *    
   * @version 1.2
   */
  public static boolean doesUserPlayARoleInSem(DODMBasic dodm, DomainUser user,
      Semester semester, Integer year) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryUserPlaysRoles == null) {
      // create query with unchanged parts
      queryUserPlaysRoles = new FlexiQuery(dsm, UserRoleHistoryCls);
    }
    
    // variable query parts 
    Expression exp1 = queryUserPlaysRoles.addConstraintExpression(UserRoleHistory.A_user, Op.EQ, user);
    Expression exp2 = queryUserPlaysRoles.addConstraintExpression(UserRoleHistory.A_semester, Op.EQ, semester);
    Expression exp3 = queryUserPlaysRoles.addConstraintExpression(UserRoleHistory.A_year, Op.EQ, year);
    
    try {
      int count = dom.loadObjectCount(UserRoleHistoryCls, queryUserPlaysRoles);
      
      return (count > 0);
    } catch (Exception e) {
      throw e;
    } finally {
      queryUserPlaysRoles.remove(exp1, exp2, exp3);
    }
  }

  /**
   * @effects 
   *  if exists {@link UserRoleHistory}s matching <tt>(user, semester, year)</tt>
   *    return values (as {@link Collection}) of the attribute {@link UserRoleHistory}<tt>.role</tt> of those objects 
   *  else
   *    return null
   *    
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read values from the data source. 
   *    
   * @pseudocode
   *  select DomainUserRole.role 
   *  where  DomainUserRole.user = currentUser and
   *         DomainUserRole.semester = semester and DomainUserRole.year = year
   * @version 1.2
   */
  public static Collection retrieveDomainUserRoleIdsBySem(DODMBasic dodm, DomainUser user,
      Semester semester, Integer year) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    DAttr attribRole = dsm.getDomainConstraint(UserRoleHistoryCls, UserRoleHistory.A_role);
    
    // 1. formulate the query 
    if (queryCurrentUserRoleIds == null) {
      queryCurrentUserRoleIds = new FlexiQuery(dsm, UserRoleHistoryCls);
      queryCurrentUserRoleIds.addDomainAttribute(attribRole);
    }
    
    Expression exp1 = queryCurrentUserRoleIds.addConstraintExpression(UserRoleHistory.A_user, Op.EQ, user);
    Expression exp2 = queryCurrentUserRoleIds.addConstraintExpression(UserRoleHistory.A_semester, Op.EQ, semester);
    Expression exp3 = queryCurrentUserRoleIds.addConstraintExpression(UserRoleHistory.A_year, Op.EQ, year);
    
    // 2. execute query to retrieve data 
    Collection values = null;
    
    // debug
    //dom.setDebugOn(true);
    
    try {
      values = dom.loadAttributeValues(UserRoleHistoryCls, attribRole, queryCurrentUserRoleIds);
    } catch (Exception e) {
      throw e;
    } finally {
      queryCurrentUserRoleIds.remove(exp1,exp2,exp3); 
      
      // debug
      //dom.setDebugOn(false);
    }
    
    return values;
  }


  /**
   * @effects 
   *  if exists {@link DomainUser} whose login is <tt>login</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   *    
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   *    
   * @version 1.2
   */
  public static DomainUser retrieveDomainUser(DODMBasic dodm, String login) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    // retrieve all Tasks that user is allowed to perform
    if (queryUserByLogin == null) {
      queryUserByLogin = new FlexiQuery(dsm, DomainUserCls);
    }

    // DomainUser.login = login
    Expression exp1 = queryUserByLogin.addConstraintExpression(DomainUser.Attribute_login, Op.EQ, login); 
    
    // debug on
    //dom.setDebugOn(true);
    
    Map<Oid,DomainUser> result = null;
    try {
      result = dom.retrieveObjectsWoutSubTypes(DomainUserCls, queryUserByLogin);
    } catch (Exception e) {
      throw e;
    } finally { 
      queryUserByLogin.remove(exp1);
    
      // debug off
      //dom.setDebugOn(false);
    }
    
    if (result != null)
      return result.values().iterator().next();
    else
      return null;
  }


  /**
   * @requires 
   *  actionsMap != null /\ actionsMap maps {@link Oid} to {@link Action}s
   *  
   * @effects 
   *  if exists {@link DomainUser}s that perform the {@link Action}s in <tt>actionsMap</tt> 
   *    return them in a {@link Map} with the {@link Action}s being used as keys
   *  else
   *    return null
   *    
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   * 
   * @version 1.2
   */
  public static <A extends Action> Map<A, Collection<DomainUser>> retrieveActionUsers(DODMBasic dodm,
      Map<Oid, A> actionsMap) throws NotPossibleException, DataSourceException {
    if (actionsMap == null)
      return null;
    
    Map<A, Collection<DomainUser>> actUserMap = new HashMap<>();
    
    Collection<A> actions = actionsMap.values();
    Role role;
    Collection<DomainUser> users;
    for (A action : actions) {
      // get the action's Role and use this to get the User
      role = action.getRoleDef();
      
      users = retrieveUsersOfRole(dodm, role);
      
      if (users != null)
        actUserMap.put(action, users);
    }
    
    return actUserMap;
  }

  /**
   * @requires 
   *  <b>only one</b> {@link DomainUser} can perform an {@link Action}  
   *  
   * @effects 
   *  if exists {@link DomainUser} that performs the {@link Action} <tt>act</tt> 
   *    return it
   *  else
   *    return null
   *    
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source.
   *  
   * @version 1.2
   */
  public static <A extends Action> DomainUser retrieveActionUser(DODMBasic dodm, A action) throws NotPossibleException, DataSourceException {
    // get the action's Role and use this to get the User
    Role role = action.getRoleDef();
    
    DomainUser user = retrieveUserOfRole(dodm, role);
    
    return user;
  }
  

  /**
   * This is similar to {@link #retrieveActionUsers(DODMBasic, Map)} except that it retrieve all {@link Teacher}-typed
   * users who are involved in teaching some {@link Subject}s concerning the specified {@link Action4Subject}s. 
   * 
   * @requires 
   *  actionsMap != null /\ actionsMap maps {@link Oid} to {@link Action4Subject}s
   *  
   * @effects 
   *  if exists {@link Teacher}s that perform the {@link Action4Subject}s in <tt>actionsMap</tt> 
   *    return them in a {@link Map} with the {@link Action4Subject}s being used as keys
   *  else
   *    return null
   *    
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   *
   * @pseudocode
   *  for each act4subj in actionsMap.values
   *    retrieve Teachers that perform act4subj as follow:
   *      select Teacher
   *      where  TeachingBySemester join Teacher and 
   *             TeachingBySemester join SubjectBySemester and 
   *             SubjectAction join SubjectBySemester and 
   *             SubjectAction.action = act4subj
   * 
   * @version 1.2
   */
  public static Map<Action4Subject, Collection<Teacher>> retrieveAction4SubjectTeachers(DODMBasic dodm,
      Map<Oid, Action4Subject> actionsMap) throws NotPossibleException, DataSourceException {
    if (actionsMap == null)
      return null;
    
    Map<Action4Subject, Collection<Teacher>> actTeachersMap = new HashMap<>();
    
    Collection<Action4Subject> actions = actionsMap.values();
    Map<Oid,Teacher> teachers;
    for (Action4Subject act4Subj : actions) {
      // retrieve Teachers that perform act4subj 
      
      teachers = retrieveTeachersOfAction(dodm, act4Subj);
      
      if (teachers != null)
        actTeachersMap.put(act4Subj, teachers.values());
    }
    
    return actTeachersMap;
  }
  
  /**
   * @effects 
   *  if exists {@link Teacher}s that perform the specified {@link Action4Subject} <tt>act4Subj</tt> 
   *    return them in a {@link Collection}
   *  else
   *    return null
   *    
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   *
   * @pseudocode
   *      select Teacher
   *      where  TeachingBySemester join Teacher and 
   *             TeachingBySemester join SubjectBySemester and 
   *             SubjectAction join SubjectBySemester and 
   *             SubjectAction.action = act4subj
   *  
   * @version 1.2
   */
  public static Map<Oid, Teacher> retrieveTeachersOfAction(DODMBasic dodm, Action4Subject act4Subj) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryTeachersOfAct == null) {
      queryTeachersOfAct = new FlexiQuery(dsm, TeacherCls);
      
      // TeachingBySemester join Teacher
      queryTeachersOfAct.addJoinExpression(new Class[] {TeachingBySemCls, TeacherCls}, new String[] {Teacher.Assoc_TeacherAndTeachingBySemester});
      
      //TeachingBySemester join SubjectBySemester
      queryTeachersOfAct.addJoinExpression(new Class[] {TeachingBySemCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndTeaching});
      
      //SubjectAction join SubjectBySemester 
      queryTeachersOfAct.addJoinExpression(new Class[] {SubjectActionCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndSubjectAction});
    }

    // SubjectAction.action = act4subj
    Expression exp1 = queryTeachersOfAct.addConstraintExpression(SubjectActionCls, SubjectAction.A_action, Op.EQ, act4Subj); 
    
    // debug on
    //dom.setDebugOn(true);
    
    Map<Oid, Teacher> teachers = null;
    try {
      teachers = dom.retrieveObjects(TeacherCls, queryTeachersOfAct);
    } catch (Exception e) {
      throw e;
    } finally { 
      queryTeachersOfAct.remove(exp1);
    
      // debug off
      //dom.setDebugOn(false);
    }
    
    return teachers;
  }


  /**
   * @requires 
   *  subjAction != null /\ at most one {@link Teacher} teaches a {@link SubjectBySemester} that is referenced in a {@link Action4Subject} 
   *  
   * @effects 
   *  retrieve and return the {@Teacher} that teaches the {@link SubjectBySemester} referenced in <tt>subjAct</tt>
   *  
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   *  
   * @pseudocode
   *  select Teacher
   *  where  TeachingBySemester join Teacher and 
   *         TeachingBySemester join SubjectBySemester and 
   *         SubjectAction join SubjectBySemester and 
   *         SubjectAction = subjAct
   * @version 1.2
   */
  public static Teacher retrieveTeacherForSubjectAction(DODMBasic dodm, SubjectAction subjAct) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryTeacherOfSubjAct == null) {
      queryTeacherOfSubjAct = new FlexiQuery(dsm, TeacherCls);
      
      // TeachingBySemester join Teacher
      queryTeacherOfSubjAct.addJoinExpression(new Class[] {TeachingBySemCls, TeacherCls}, new String[] {Teacher.Assoc_TeacherAndTeachingBySemester});
      
      //TeachingBySemester join SubjectBySemester
      queryTeacherOfSubjAct.addJoinExpression(new Class[] {TeachingBySemCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndTeaching});
      
      //SubjectAction join SubjectBySemester 
      queryTeacherOfSubjAct.addJoinExpression(new Class[] {SubjectActionCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndSubjectAction});
    }

    // SubjectAction = subjAct
    Comparable idVal = getObjectIdValue(dom, SubjectActionCls, subjAct);
    Expression exp1 = queryTeacherOfSubjAct.addConstraintExpression(SubjectActionCls, SubjectAction.A_id, Op.EQ, idVal); 
    
    // debug on
    //dom.setDebugOn(true);
    
    Teacher teacher = null;
    try {
      teacher = dom.retrieveObject(TeacherCls, queryTeacherOfSubjAct);
      
    } catch (Exception e) {
      throw e;
    } finally { 
      queryTeacherOfSubjAct.remove(exp1);
    
      // debug off
      //dom.setDebugOn(false);
    }
    
    return teacher;
  }
  
  /**
   * @effects 
   *  if exists {@link DomainUser}(s) of role <tt>role</tt>
   *    return the <b>first</b> such user
   *  else
   *    return null
   *     
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   *  
   * @version 1.2
   */
  public static DomainUser retrieveUserOfRole(DODMBasic dodm, Role role) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryUserByRole == null) {
      queryUserByRole = new FlexiQuery(dsm, UserRoleCls);
    }

    // UserRole.role = role
    Expression exp1 = queryUserByRole.addConstraintExpression(UserRole.A_role, Op.EQ, role); 
    
    // debug on
    //dom.setDebugOn(true);
    
    DomainUser user = null;
    UserRole userRole = null;
    try {
      userRole = dom.retrieveObject(UserRoleCls, queryUserByRole);
      
      if (userRole != null) {
        user = userRole.getUser();
      }
    } catch (Exception e) {
      throw e;
    } finally { 
      queryUserByRole.remove(exp1);
    
      // debug off
      //dom.setDebugOn(false);
    }
    
    return user;
  }
  
  /**
   * @effects 
   *  if exists {@link DomainUser}(s) of role <tt>role</tt>
   *    return <b>all</b> in {@link Collection}
   *  else
   *    return null
   *     
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   *  
   * @version 1.2
   */
  public static Collection<DomainUser> retrieveUsersOfRole(DODMBasic dodm, Role role) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryUserByRole == null) {
      queryUserByRole = new FlexiQuery(dsm, UserRoleCls);
    }

    // UserRole.role = role
    Expression exp1 = queryUserByRole.addConstraintExpression(UserRole.A_role, Op.EQ, role); 
    
    // debug on
    //dom.setDebugOn(true);
    
    Collection<DomainUser> users = null;
    Map<Oid,UserRole> userRoles;
    try {
      userRoles = dom.retrieveObjects(UserRoleCls, queryUserByRole);
      
      if (userRoles != null) {
        users = new ArrayList<>();
        Collection<UserRole> userRoleCol = userRoles.values();
        for (UserRole userRole : userRoleCol) {
          users.add(userRole.getUser());
        }
      }
    } catch (Exception e) {
      throw e;
    } finally { 
      queryUserByRole.remove(exp1);
    
      // debug off
      //dom.setDebugOn(false);
    }
    
    return users;
  }
  
  /**
   * @requires 
   *  subjActUsers != null
   *  
   * @modifies subjActUsers
   * @effects <pre>
   *  for each SubjectAction sa in subjActs
   *    user =  {@link #retrieveUserForSubjectAction(DODMBasic, SubjectAction)} (dodm, sa)
   *    add (sa, user) to subjActUsers
   *    </pre> 
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   * @version 1.2
   */
  public static void retrieveUsersForSubjectActions(DODMBasic dodm,
      Collection<SubjectAction> subjActs,
      Map<SubjectAction, DomainUser> subjActUsers) throws NotPossibleException, DataSourceException {
    
    if (subjActUsers == null) return;
    
    DomainUser user;
    for (SubjectAction subjAct : subjActs) {
      user = retrieveUserForSubjectAction(dodm, subjAct);
      if (user != null)
        subjActUsers.put(subjAct, user);
    }
  }
  
  /**
   * @requires 
   *  subjAct != null
   * @effects <pre>
   *  let action = subjAct.action
   *  if action.role != null
   *    if action.role is a teacher role
   *      return {@link #retrieveTeacherForSubjectAction(DODMBasic, SubjectAction)}
   *    else
   *      return {@link #retrieveActionUser(DODMBasic, Action)}
   *  else
   *    return null
   * </pre>
   * <p> throws NotPossibleException if failed to generate data source query, DataSourceException if fails to read from the data source. 
   *  
   * @version 1.2
   */
  public static DomainUser retrieveUserForSubjectAction(DODMBasic dodm,
      SubjectAction subjAct) throws NotPossibleException, DataSourceException {
    if (subjAct == null) return null;
    
    Action act = subjAct.getAction();
    Role role = act.getRoleDef();
    
    if (role != null) {
      if (vn.com.processman.modules.dsecurity.model.Role.isTeacherRole(role)) {
        return retrieveTeacherForSubjectAction(dodm, subjAct);
      } else {
        return retrieveActionUser(dodm, act);
      }
    } else {
      return null;
    }
  }
  
  /**
   * @requires 
   *  <tt>dodm</tt> is capable of loading many-many associations.
   *  
   * @effects 
   *  if <tt>user.theRoles == null</tt>
   *    load them from data source and add to <tt>user</tt>
   *  else
   *    do nothing
   * @version 1.2
   */
  public static void ensureUserHasRoles(DODMBasic dodm, DomainUser user) throws NotFoundException, DataSourceException {
    DOM dom = (DOM) dodm.getDom();
    
    dom.ensureObjectLoadedWithManyManyAssociates(DomainUserCls, user, DomainUser.Association_WithRole);
  }
  
  /**
   * This works similar to {@link #doesTeacherTeachIn(DODMBasic, DomainUser, Semester, Integer)} except that it retrieve the object ids
   * instead of only counting them.
   * 
   * @effects 
   *  if exist {@link SubjectBySemester}s that are taught by <tt>teacher</tt> in the given <tt>semester<tt> in the given <tt>year</tt>
   *    return their {@link Oid}s as {@link Collection}
   *  else
   *    return null
   * 
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   * 
   * @pseudocode
   * Object query
   *  <pre>
   *  select SubjectBySem.id
   *  where  SubjectBySemester.semester = semester and SubjectBySemester.year = year and 
   *         TeachingBySemester join SubjectBySemester and 
   *         TeachingBySemester.teacher = teacher
   *  </pre>
   * @version 1.2
   */
  public static Collection<Oid> retrieveUserSubjectSemIds(final DODMBasic dodm, final DomainUser teacher, final Semester semester, final Integer year) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryUserSubjectSem == null) {
      // create query with unchanged parts
      queryUserSubjectSem = new FlexiQuery(dsm, SubjectBySemCls);
      queryUserSubjectSem.addJoinExpression(
          new Class[] {TeachingBySemCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndTeaching});
    }
    
    // variable query parts 
    Expression exp1 = queryUserSubjectSem.addConstraintExpression(SubjectBySemester.A_semester, Op.EQ, semester);
    Expression exp2 = queryUserSubjectSem.addConstraintExpression(SubjectBySemester.A_year, Op.EQ, year);
    Expression exp3 = queryUserSubjectSem.addConstraintExpression(TeachingBySemCls, TeachingBySemester.A_teacher, Op.EQ, teacher);
    
    try {
      return dom.retrieveObjectOids(SubjectBySemCls, queryUserSubjectSem);
    } catch (Exception e) {
      throw e;
    } finally {
      queryUserSubjectSem.remove(exp1, exp2, exp3);
    }
  }

  /**
   * @modifies <tt>o</tt>
   * @effects 
   *  if <tt>o</tt> (of <tt>c</tt>) has not been loaded with link count to the associated objects via the association named <tt>assocName</tt> (of <tt>c</tt>)
   *    load it and update <tt>o</tt>
   *  else
   *    do nothing
   *    
   *  <p>return the link count.
   *  
   * @version 3.3
   * @return 
   */
  public static int ensureObjectHasLinkCount(DODMBasic dodm, Class c, Object o, String assocName) {
    DOMBasic dom = dodm.getDom();
    
    return dom.ensureObjectHasLinkCount(c, o, assocName);
  }
  
  /**
   * @effects 
   *  if exists {@link Task4Subject}s that are for {@link SubjectBySemester}s identified by <tt>subjSemIds</tt> 
   *    return their {@link Oid}s as {@link Collection}
   *  else
   *    return null
   *    
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *    
   * @pseudocode
   * Object query:
   * <pre>
   *  select distinct(Task4Subject.id)
   *  where SubjectTask join Task4Subject and 
   *        SubjectTask join SubjectBySemester and 
   *        SubjectBySemester.id in subjSemIds
   *
   * </pre>
   * @version 1.2
   */
  public static Map<Oid, Task4Subject> retrieveTask4SubjectBySubjectSemesters(final DODMBasic dodm, final Collection<Oid> subjSemIds)  
        throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();

    if (queryTask4SubjectBySubjSem == null) {
      // create query with unchanged parts
      queryTask4SubjectBySubjSem = new FlexiQuery(dsm, Task4SubjectCls);
      queryTask4SubjectBySubjSem.addDomainAttributeWithFunction(Task4Subject.A_id, 
          jda.modules.dcsl.syntax.function.Function.distinct);
      queryTask4SubjectBySubjSem.addJoinExpression(
          new Class[] {SubjectTaskCls, Task4SubjectCls}, new String[] {Task4Subject.Assoc_Task4SubjectAndSubjectTask});
      queryTask4SubjectBySubjSem.addJoinExpression(
          new Class[] {SubjectTaskCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndSubjectTask});
    }
    
    // variable query parts 
    Comparable[] idArr = getIdValuesFromOids(subjSemIds);
    
    Expression exp1 = queryTask4SubjectBySubjSem.addConstraintExpression(SubjectBySemCls, SubjectBySemester.A_id, Op.IN, idArr);
    
    try {
      return dom.retrieveObjects(Task4SubjectCls, queryTask4SubjectBySubjSem);
    } catch (Exception e) {
      throw e;
    } finally {
      queryTask4SubjectBySubjSem.remove(exp1);
    }
  }

  /**
   * @effects 
   *  if exists {@link SubjectTask}s of <tt>task4Subj</tt> that are for the {@link SubjectBySemester} in <tt>subjSemIds</tt>
   *    return them in {@link Map}
   *  else
   *    return null
   *    
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *  
   * @pseudocode
   *  select SubjectTask.id
   *  where SubjectTask.task = task4Subj and 
   *        SubjectTask join SubjectBySemester and 
   *        SubjectBySemester.id in subjSemIds   
   * @version
   */
  public static Map<Oid, SubjectTask> retrieveSubjectTasksBySubjectSemesters(
      DODMBasic dodm, Task4Subject task4Subj, Collection<Oid> subjSemIds) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (querySubjectTaskBySubjSem == null) {
      // create query with unchanged parts
      querySubjectTaskBySubjSem = new FlexiQuery(dsm, SubjectTaskCls);
      
      // SubjectTask join SubjectBySemester
      querySubjectTaskBySubjSem.addJoinExpression(
          new Class[] {SubjectTaskCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndSubjectTask});
    }
    
    // variable query parts
    
    // SubjectTask.task = task4Subj
    Expression exp1 = querySubjectTaskBySubjSem.addConstraintExpression(SubjectTask.A_task, Op.EQ, task4Subj);
    
    // SubjectBySemester.id in subjSemIds
    Comparable[] idArr = getIdValuesFromOids(subjSemIds);
    
    Expression exp2 = querySubjectTaskBySubjSem.addConstraintExpression(SubjectBySemCls, SubjectBySemester.A_id, Op.IN, idArr);
    
    try {
      // debug
      //dom.setDebugOn(true);
      
      return dom.retrieveObjects(SubjectTaskCls, querySubjectTaskBySubjSem);
      
      // debug
      //dom.setDebugOn(false);
    } catch (Exception e) {
      throw e;
    } finally {
      querySubjectTaskBySubjSem.remove(exp1, exp2);
    }    
  }

  /**
   * @effects 
   *  if exists {@link SubjectAction}s of <tt>act4Subj</tt> that are for the {@link SubjectBySemester} in <tt>subjSemIds</tt>
   *    return them in {@link Map}
   *  else
   *    return null
   *
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *
   * @pseudocode
   *  select SubjectAction.id
   *  where SubjectAction.action = act4Subj and 
   *        SubjectAction join SubjectBySemester and 
   *        SubjectBySemester.id in subjSemIds
   * @version
   */
  public static Map<Oid, SubjectAction> retrieveSubjectActionsBySubjectSemesters(
      DODMBasic dodm, Action4Subject act4Subj, Collection<Oid> subjSemIds) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (querySubjectActionBySubjSem == null) {
      // create query with unchanged parts
      querySubjectActionBySubjSem = new FlexiQuery(dsm, SubjectActionCls);
      
      // SubjectAction join SubjectBySemester
      querySubjectActionBySubjSem.addJoinExpression(
          new Class[] {SubjectActionCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndSubjectAction});
    }
    
    // variable query parts
    
    // SubjectAction.action = act4Subj
    Expression exp1 = querySubjectActionBySubjSem.addConstraintExpression(SubjectAction.A_action, Op.EQ, act4Subj);
    
    // SubjectBySemester.id in subjSemIds
    Comparable[] idArr = getIdValuesFromOids(subjSemIds);
    
    Expression exp2 = querySubjectActionBySubjSem.addConstraintExpression(SubjectBySemCls, SubjectBySemester.A_id, Op.IN, idArr);
    
    try {
      // debug
      //dom.setDebugOn(true);
      
      return dom.retrieveObjects(SubjectActionCls, querySubjectActionBySubjSem);
      
      // debug
      //dom.setDebugOn(false);
    } catch (Exception e) {
      throw e;
    } finally {
      querySubjectActionBySubjSem.remove(exp1, exp2);
    } 
  }

  /**
   * @effects 
   *  If exists {@link Process} <tt>p</tt> s.t <tt>p.code = processCode /\ p.type = {@link ProcessType#NotForSubject}</tt>
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   *    
   * @pseudocode
   *  select Process
   *  where  Process.code = processCode and
   *         Process.type = {@link ProcessType#NotForSubject}
   *         
   * @version 1.2
   */
  public static boolean existProcess4NonSubject(DODMBasic dodm,
      String processCode) {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryProcessExist == null) {
      // create query with unchanged parts
      queryProcessExist = new FlexiQuery(dsm, ProcessCls);
    }
    
    // variable query parts
    
    // Process.code = processCode 
    Expression exp1 = queryProcessExist.addConstraintExpression(Process.A_code, Op.EQ, processCode);
    
    // Process.type = {@link ProcessType#NotForSubject}
    Expression exp2 = queryProcessExist.addConstraintExpression(Process.A_type, Op.EQ, ProcessType.NotForSubject);
    
    boolean exist;
    
    try {
      // debug
      //dom.setDebugOn(true, "DomainToolKit.existsProcessApplication");
      
      exist = dom.existObject(ProcessCls, queryProcessExist);
      
    } catch (Exception e) {
      exist = false;
    } finally {
      queryProcessExist.remove(exp1, exp2);
      
      // debug
      //dom.setDebugOn(false);
    }
    
    return exist;
  }

  /**
   * @effects 
   *  If exists {@link Process} <tt>p</tt> s.t <tt>p.code = processCode /\ p.type = {@link ProcessType#ForSubject}</tt>
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   * @version 1.2
   */
  public static boolean existProcess4Subject(DODMBasic dodm, String processCode) {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryProcessExist == null) {
      // create query with unchanged parts
      queryProcessExist = new FlexiQuery(dsm, ProcessCls);
    }
    
    // variable query parts
    
    // Process.code = processCode 
    Expression exp1 = queryProcessExist.addConstraintExpression(Process.A_code, Op.EQ, processCode);
    
    // Process.type = {@link ProcessType#NotForSubject}
    Expression exp2 = queryProcessExist.addConstraintExpression(Process.A_type, Op.EQ, ProcessType.ForSubject);
    
    boolean exist;
    
    try {
      // debug
      //dom.setDebugOn(true, "DomainToolKit.existsProcessApplication");
      
      exist = dom.existObject(ProcessCls, queryProcessExist);
      
    } catch (Exception e) {
      exist = false;
    } finally {
      queryProcessExist.remove(exp1, exp2);
      
      // debug
      //dom.setDebugOn(false);
    }
    
    return exist;
  }
  
  /**
   * @effects 
   *  If exists {@link ProcessApplication} that is created for the period <tt>semester, year</tt> and 
   *  is for the {@link Process} whose code is <tt>processCode</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * @pseudocode
   *  select ProcessApplication
   *  where  ProcessApplication (pa) join Process (p1) and 
   *         Process (p1) join Process (p2) on p1.def = p2.id and 
   *         p2.code = processCode and 
   *         pa.semester = semester and 
   *         pa.year = year
   *  
   * @version 1.2
   */
  public static boolean existProcessApplication(DODMBasic dodm,
      String processCode, Semester semester, Integer year) {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryProcessAppExist == null) {
      // create query with unchanged parts
      queryProcessAppExist = new FlexiQuery(dsm, ProcessApplicationCls);
      
      // ProcessApplication (pa) join Process (p1)
      queryProcessAppExist.addJoinExpression(
          new Class[] {ProcessApplicationCls, ProcessCls}, new String[] {Process.Assoc_ProcessAndProcessApplication});
      
    }
    
    // variable query parts
    
    // Process (p1) join Process (p2) on p1.def = p2.id
    // and p2.code = processCode 
    Expression exp1 = queryProcessAppExist.addJoinOnAttributesWithValueConstraint(
        new Class[] {ProcessCls, ProcessCls}, new String[] {Process.A_def, Process.A_id}, 
        Process.A_code, Op.EQ, processCode);
    
    // ProcessApplication.semester = semester
    Expression exp2 = queryProcessAppExist.addConstraintExpression(ProcessApplication.A_semester, Op.EQ, semester);
    
    // ProcessApplication.year = year
    Expression exp3 = queryProcessAppExist.addConstraintExpression(ProcessApplication.A_year, Op.EQ, year);
    
    boolean exist;
    
    try {
      // debug
      //dom.setDebugOn(true, "DomainToolKit.existsProcessApplication");
      
      exist = dom.existObject(ProcessApplicationCls, queryProcessAppExist);
      
    } catch (Exception e) {
      exist = false;
    } finally {
      queryProcessAppExist.remove(exp1, exp2, exp3);
      
      // debug
      //dom.setDebugOn(false);
    }
    
    return exist;
  }
  
  /**
   * This method differs from {@link #retrieveProcessApplicationIds(DODMBasic, DomainUser, boolean, OrgUnit, String, Semester, Integer)} in 
   * that it uses the <b>user-role history</b> of the specified user to retrieve the roles that the user 
   * was assigned to in a given semester.  
   * 
   * @requires 
   *  user != null => user is a valid logged-in user /\ isCurrentProcessUser != null /\ user plays a role in the given semester of the given year 
   *  
   * @effects 
   *  if exists {@link ProcessApplication} that are applied in the specified <tt>semester, year</tt>, for <tt>orgUnit</tt>, and 
   *  and {@link Process} named <tt>processName</tt>
   *    return their {@link Oid}s as {@link Collection}
   *  else
   *    return <tt>null</tt>
   * 
   * <p>If <tt>user != null and a process user</tt> then the above is constrained further by the {@link Process}es 
   * that the user is <b>historically</b> allowed to perform (through her roles) in the specified semester.  
   * 
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *    
   * @pseudocode
   *  <pre> 
   *  A/ If user = null OR user is not a process user
   *    Select ProcessApplication
   *    Where ProcessApplication join Process and 
   *            Process.def in (select Process.id where Process.name matches n) and 
   *          ProcessApplication.orgUnit = o and
   *          ProcessApplication.semester = s and
   *          ProcessApplication.year = y
   *           
   *  B/ (v3.3) If user != null and user is a process user
   *        find processes that user is allowed to perform, as follow:
   *        
   *        let userRoleIds = 
   *          select DomainUserRole.role 
   *          where DomainUserRole.user = currentUser and
   *                DomainUserRole.semester = semester and DomainUserRole.year = year
   *        
   *        Select ProcessApplication
   *        Where ProcessApplication join Process and
   *            Process.def in (select Process.id where Process.name matches n) and
   *          RolePerfProcess join Process on Process.def = RolePerfProcess.process and       
   *            RolePerfProcess.role in userRoleIds and
   *          ProcessApplication.orgUnit = o and
   *          ProcessApplication.semester = s and
   *          ProcessApplication.year = y 
   *           
   *  </pre> 
   * @version 1.2
   */
  public static Collection<Oid> retrieveProcessApplicationIdsByHistory(final DODMBasic dodm, 
      final DomainUser user, final Boolean isCurrentProcessUser, 
      final OrgUnit orgUnit, final String processName, final Semester semester, final Integer year) 
      throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    // 1. formulate the query 
    if (queryProcAppsByProcName == null) {
      queryProcAppsByProcName = new FlexiQuery(dsm, ProcessApplicationCls);

      // ProcessApplication join Process
      Class[] joinClasses = new Class[] { ProcessApplicationCls, ProcessCls };
      String[] assocNames = new String[] { Process.Assoc_ProcessAndProcessApplication};
      
      queryProcAppsByProcName.addJoinExpression(joinClasses, assocNames);
    }
    
    // nested query: select Process.id where Process.name matches n
    if (queryProcessIdByName == null) {
      queryProcessIdByName = new FlexiQuery(dsm, ProcessCls);
      queryProcessIdByName.addDomainAttribute(Process.A_id);
    }
    String attrib = Process.A_name;
    Op op = Op.MATCH;
    //String val = parentObj.getProcessName();
    queryProcessIdByName.addConstraintExpression(attrib, op, processName);
    
    Expression exp1, exp2, exp3, exp4, exp5 = null;
    
    //... Process.def in (nestedQuery)
    exp1 = queryProcAppsByProcName.addConstraintExpression(ProcessCls, Process.A_def, Op.IN, queryProcessIdByName);

    // v3.3: support user's permission
    if (user != null && isCurrentProcessUser != null && isCurrentProcessUser.equals(true)) {
      if (!queryProcAppsByProcName.containsJoinExpression(RolePerfProcessCls, ProcessCls)) {
        // RolePerfProcess join Process on RolePerfProcess.process = Process.def  
        queryProcAppsByProcName.addJoinOnAttributes(new Class[] {RolePerfProcessCls, ProcessCls}, 
            new String[] {RolePerfProcess.A_process, Process.A_def});
      }
      
      // retrieve userRoleIds
      Collection userRolesIds = retrieveDomainUserRoleIdsBySem(dodm, user, semester, year);
      
      Object[] userRoleIdArr = new Object[userRolesIds.size()];
      userRolesIds.toArray(userRoleIdArr);
      
      //... RolePerfProcess.role in userRoleIds
      exp5 = queryProcAppsByProcName.addConstraintExpression(RolePerfProcessCls, RolePerfProcess.A_role, Op.IN, userRoleIdArr);
    }

    // ProcessApplication.orgUnit = o
    exp2 = queryProcAppsByProcName.addConstraintExpression(ProcessApplication.A_orgUnit, Op.EQ, orgUnit);
    
    // ProcessApplication.semester = s
    exp3 = queryProcAppsByProcName.addConstraintExpression(ProcessApplication.A_semester, Op.EQ, semester);
    
    // ProcessApplication.year = y
    exp4 = queryProcAppsByProcName.addConstraintExpression(ProcessApplication.A_year, Op.EQ, year);
    
    // 2. execute query to retrieve data 
    Collection<Oid> oids = null;
    
    // debug
    //dom.setDebugOn(true);
    
    try {
      oids = dom.retrieveObjectOids(ProcessApplicationCls, queryProcAppsByProcName);
    } catch (Exception e) {
      throw e;
    } finally {
      queryProcAppsByProcName.remove(exp1,exp2,exp3,exp4); 
      if (exp5 != null) queryProcAppsByProcName.remove(exp5);
      queryProcessIdByName.removeAll();
      
      // debug
      //dom.setDebugOn(false);
    }
    
    return oids;
  }
  
  /**
   * This method differs from {@link #retrieveProcessApplicationIdsByHistory(DODMBasic, DomainUser, Boolean, OrgUnit, String, Semester, Integer)} in that 
   * it uses the <b>current</b> user-role assignments of the specified user (instead of her user-role history). 
   * 
   * @requires 
   *  user != null => user is a valid logged-in user /\ user plays a role in the given semester of the given year 
   *  
   * @effects 
   *  if exists {@link ProcessApplication} that are applied in the specified <tt>semester, year</tt>, for <tt>orgUnit</tt>, and 
   *  and {@link Process} named <tt>processName</tt>
   *    return their {@link Oid}s as {@link Collection}
   *  else
   *    return <tt>null</tt>
   * 
   * <p>If <tt>user != null</tt> then the above is constrained further by the {@link Process}es 
   * that the user is allowed to perform (through its roles). 
   * 
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *    
   * @pseudocode
   *  <pre> 
   *  A/ If user = null OR isProcessUser = false
   *    Select ProcessApplication
   *    Where ProcessApplication join Process and 
   *            Process.def in (select Process.id where Process.name matches n) and 
   *          ProcessApplication.orgUnit = o and
   *          ProcessApplication.semester = s and
   *          ProcessApplication.year = y
   *           
   *  B/ (v3.3) If user != null and isProcessUser = true
   *        find processes that user is allowed to perform, as follow:
   *        Select ProcessApplication
   *        Where ProcessApplication join Process and
   *            Process.def in (select Process.id where Process.name matches n) and
   *          RolePerfProcess join Process on Process.def = RolePerfProcess.process and       
   *            RolePerfProcess.role in (select UserRole.role where UserRole.user = user) and
   *          ProcessApplication.orgUnit = o and
   *          ProcessApplication.semester = s and
   *          ProcessApplication.year = y 
   *           
   *  </pre> 
   * @version 1.2
   */
  public static Collection<Oid> retrieveProcessApplicationIds(final DODMBasic dodm, 
      final DomainUser user, final boolean isProcessUser, 
      final OrgUnit orgUnit, final String processName, final Semester semester, final Integer year) 
      throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    // 1. formulate the query 
    if (queryProcAppsByProcName == null) {
      queryProcAppsByProcName = new FlexiQuery(dsm, ProcessApplicationCls);

      // ProcessApplication join Process
      Class[] joinClasses = new Class[] { ProcessApplicationCls, ProcessCls };
      String[] assocNames = new String[] { Process.Assoc_ProcessAndProcessApplication};
      
      queryProcAppsByProcName.addJoinExpression(joinClasses, assocNames);
    }
    
    // nested query: select Process.id where Process.name matches n
    if (queryProcessIdByName == null) {
      queryProcessIdByName = new FlexiQuery(dsm, ProcessCls);
      queryProcessIdByName.addDomainAttribute(Process.A_id);
    }
    String attrib = Process.A_name;
    Op op = Op.MATCH;
    //String val = parentObj.getProcessName();
    queryProcessIdByName.addConstraintExpression(attrib, op, processName);
    
    Expression exp1, exp2, exp3, exp4, exp5 = null;
    
    //... Process.def in (nestedQuery)
    exp1 = queryProcAppsByProcName.addConstraintExpression(ProcessCls, Process.A_def, Op.IN, queryProcessIdByName);

    // v3.3: support user's permission
    if (user != null && isProcessUser) {
      if (queryRolePerfProcess == null) {
        // RolePerfProcess join Process on RolePerfProcess.process = Process.def  
        queryProcAppsByProcName.addJoinOnAttributes(new Class[] {RolePerfProcessCls, ProcessCls}, 
            new String[] {RolePerfProcess.A_process, Process.A_def});
        
        //nestedRoleQuery: select UserRole.role where UserRole.user = user
        queryRolePerfProcess = new FlexiQuery(dsm, UserRoleCls);
        queryRolePerfProcess.addDomainAttribute(UserRole.A_role);  // select
      }
      
      // nestedRoleQuery: update with current user
      queryRolePerfProcess.addConstraintExpression(UserRole.A_user, Op.EQ, user); // where
      
      
      //... RolePerfProcess.role in (nestedRoleQuery)
      exp5 = queryProcAppsByProcName.addConstraintExpression(RolePerfProcessCls, RolePerfProcess.A_role, Op.IN, queryRolePerfProcess);
    }

    // ProcessApplication.orgUnit = o
    exp2 = queryProcAppsByProcName.addConstraintExpression(ProcessApplication.A_orgUnit, Op.EQ, orgUnit);
    
    // ProcessApplication.semester = s
    exp3 = queryProcAppsByProcName.addConstraintExpression(ProcessApplication.A_semester, Op.EQ, semester);
    
    // ProcessApplication.year = y
    exp4 = queryProcAppsByProcName.addConstraintExpression(ProcessApplication.A_year, Op.EQ, year);
    
    // 2. execute query to retrieve data 
    Collection<Oid> oids = null;
    
    // debug
    //dom.setDebugOn(true);
    
    try {
      oids = dom.retrieveObjectOids(ProcessApplicationCls, queryProcAppsByProcName);
    } catch (Exception e) {
      throw e;
    } finally {
      queryProcAppsByProcName.remove(exp1,exp2,exp3,exp4); 
      if (exp5 != null) queryProcAppsByProcName.remove(exp5);
      queryProcessIdByName.removeAll();
      if (queryRolePerfProcess != null) queryRolePerfProcess.removeAll();
      
      // debug
      //dom.setDebugOn(false);
    }
    
    return oids;
  }
  
  /**
   * This method differs from {@link #retrieveProcessApplicationIds(DODMBasic, DomainUser, boolean, OrgUnit, String, Semester, Integer)} in that 
   * it does not use process name as input (and thus will retrieve all relevant processes) and that it does not 
   * care whether user is a process user (it simply returns null if no results are found for some user).  
   * 
   * @requires 
   *  user != null => user is a valid user /\ user plays a role in the given semester of the given year 
   *  
   * @effects 
   *  if exists {@link ProcessApplication}s that are applied in the specified <tt>semester, year</tt> and for <tt>orgUnit</tt>
   *    return their {@link Oid}s as {@link Collection}
   *  else
   *    return <tt>null</tt>
   * 
   * <p>If <tt>user != null</tt> then the above is constrained further by the {@link Process}es 
   * that the user is allowed to perform (through its roles). 
   * 
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *    
   * @pseudocode
   *  <pre> 
   *  A/ If user = null
   *    Select ProcessApplication
   *    Where ProcessApplication.orgUnit = o and
   *          ProcessApplication.semester = s and
   *          ProcessApplication.year = y
   *           
   *  B/ If user != null 
   *        find processes that user is allowed to perform, as follow:
   *        Select ProcessApplication
   *        Where RolePerfProcess join Process on Process.def = RolePerfProcess.process and       
   *            RolePerfProcess.role in (select UserRole.role where UserRole.user = user) and
   *          ProcessApplication.orgUnit = o and
   *          ProcessApplication.semester = s and
   *          ProcessApplication.year = y 
   *           
   *  </pre> 
   * @version 1.2
   */
  public static Collection<Oid> retrieveProcessApplicationIds(final DODMBasic dodm, 
      final DomainUser user, 
      final OrgUnit orgUnit, final Semester semester, final Integer year) 
      throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    // 1. formulate the query 
    if (queryProcApps == null) {
      queryProcApps = new FlexiQuery(dsm, ProcessApplicationCls);
    }
    
    Expression exp1 = null, exp2, exp3, exp4, exp5 = null;
    
    if (user != null) {
      if (queryRolePerfProcess2 == null) {
        // RolePerfProcess join Process on RolePerfProcess.process = Process.def  
        exp1 = queryProcApps.addJoinOnAttributes(new Class[] {RolePerfProcessCls, ProcessCls}, 
            new String[] {RolePerfProcess.A_process, Process.A_def});
        
        //nestedRoleQuery: select UserRole.role where UserRole.user = user
        queryRolePerfProcess2 = new FlexiQuery(dsm, UserRoleCls);
        queryRolePerfProcess2.addDomainAttribute(UserRole.A_role);  // select
      }
      
      // nestedRoleQuery: update with current user
      queryRolePerfProcess2.addConstraintExpression(UserRole.A_user, Op.EQ, user); // where
      
      
      //... RolePerfProcess.role in (nestedRoleQuery)
      exp5 = queryProcApps.addConstraintExpression(RolePerfProcessCls, RolePerfProcess.A_role, Op.IN, queryRolePerfProcess2);
    }

    // ProcessApplication.orgUnit = o
    exp2 = queryProcApps.addConstraintExpression(ProcessApplication.A_orgUnit, Op.EQ, orgUnit);
    
    // ProcessApplication.semester = s
    exp3 = queryProcApps.addConstraintExpression(ProcessApplication.A_semester, Op.EQ, semester);
    
    // ProcessApplication.year = y
    exp4 = queryProcApps.addConstraintExpression(ProcessApplication.A_year, Op.EQ, year);
    
    // 2. execute query to retrieve data 
    Collection<Oid> oids = null;
    
    // debug
    //dom.setDebugOn(true, "retrieveProcessApplicationIds()");
    
    try {
      oids = dom.retrieveObjectOids(ProcessApplicationCls, queryProcApps);
    } catch (Exception e) {
      throw e;
    } finally {
      queryProcApps.remove(exp2,exp3,exp4); 
      if (exp1 != null) queryProcApps.remove(exp1);
      if (exp5 != null) queryProcApps.remove(exp5);
      if (queryRolePerfProcess2 != null) queryRolePerfProcess2.removeAll();
      
      // debug
      //dom.setDebugOn(false);
    }
    
    return oids;
  }
  
  /**
   * @requires 
   *  procAppIds != null
   *  
   * @effects 
   *  Retrieve and return the {@link Oid}s of {@link Process}es that are referred to in the {@link ProcessApplication}s
   *  whose ids are <tt>procAppIds</tt>.
   *  
   *  <p>Return <tt>null</tt> if no such {@link Process}es are found.
   *  
   * @version 1.2
   */
  public static Collection<Oid> retrieveAppliedProcessIds(DODMBasic dodm, Collection<Oid> procAppIds) {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    final DAttr refAttrib = dsm.getDomainConstraint(ProcessApplicationCls, ProcessApplication.A_process);
    
    Collection<Oid> procIds = new ArrayList();

    Oid procId;
    for (Oid oid : procAppIds) {
      try {
        // load the ids

        procId = dom.retrieveAssociateOid(ProcessApplicationCls, oid, refAttrib,
            ProcessCls);

        if (procId != null) {
          procIds.add(procId);
        }
      } catch (Exception e) {
        // dctl.displayError(this.getClass().getSimpleName()+"filter(): ", e);
        e.printStackTrace();
      }
    }
    
    return procIds.isEmpty() ? null : procIds;
  }
  

  /**
   * @requires 
   *  user != null => user is a valid user /\  user.roles != null 
   *  
   * @effects 
   *  if exists {@link Action4Subject}s for the {@link Task4Subject}s of the {@link Process}es that are applied by {@link ProcessApplication}s
   *  in the given <tt>semester, year</tt> and for the given <tt>orgUnit</tt>
   *    return them as {@link Map}
   *  else
   *    return null
   *   
   * <p>If <tt>user != null</tt> then the above is constrained further by the {@link Action4Subject}s 
   * that the user is allowed to perform (through its roles).
   * 
   * <p> throws NotPossibleException if failed to generate query, DataSourceException if fails to read from the data source. 
   *    
   * @pseudocode
   * <pre>
   * (A) user = null
   *    select Action4Subject
   *    where  Action4Subject.id = Action.id and  
   *           Action join Task and 
   *           Task join Process and 
   *           Process join ProcessApplication and 
   *           ProcessApplication.semester = semester and 
   *           ProcessApplication.year = year and 
   *           ProcessApplication.orgUnit = orgUnit
   *           
   * (B) user != null
   *    select Action4Subject
   *    where  Action4Subject.id = Action(a1).id and 
   *           a1 join Task and 
   *           Task join Process and 
   *           Process join ProcessApplication and 
   *           ProcessApplication.semester = semester and
   *           ProcessApplication.year = year and 
   *           ProcessApplication.orgUnit = orgUnit and 
   *           Action (a1) join Action (a2) on a1.def = a2.id and
   *            a2.role in user.roles
   *  
   * </pre>
   * 
   * @version 1.2
   */
  public static Map<Oid, Action4Subject> retrieveAppliedAction4Subjects(
      DODMBasic dodm, DomainUser user, OrgUnit orgUnit, Semester semester,
      Integer year) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryAppliedActions4Subj == null) {
      queryAppliedActions4Subj = new FlexiQuery(dsm, Action4SubjectCls);
      
      // Action4Subject.id = Action(a1).id
      // NOTE: this is not needed because this join will be added anyway b/c Action4Subject is the query class
      queryAppliedActions4Subj.addJoinOnAttributes(new Class[] {Action4SubjectCls, ActionCls}, new String[] {Action4Subject.A_id, Action.A_id});
      
      // Action join Task 
      queryAppliedActions4Subj.addJoinExpression(new Class[] {ActionCls, TaskCls}, new String[] {Task.Assoc_TaskAndAction});
      
      // Task join Process  
      queryAppliedActions4Subj.addJoinExpression(new Class[] {TaskCls, ProcessCls}, new String[] {Process.Assoc_ProcessAndTask});
      
      // ProcessApplication join Process
      queryAppliedActions4Subj.addJoinExpression(new Class[] {ProcessApplicationCls, ProcessCls}, new String[] {Process.Assoc_ProcessAndProcessApplication});
    }

    // ProcessApplication.semester = semester 
    Expression exp1 = queryAppliedActions4Subj.addConstraintExpression(ProcessApplicationCls, ProcessApplication.A_semester, Op.EQ, semester);
    
    // ProcessApplication.year = year
    Expression exp2 = queryAppliedActions4Subj.addConstraintExpression(ProcessApplicationCls, ProcessApplication.A_year, Op.EQ, year);
    
    // ProcessApplication.orgUnit = orgUnit
    Expression exp3 = queryAppliedActions4Subj.addConstraintExpression(ProcessApplicationCls, ProcessApplication.A_orgUnit, Op.EQ, orgUnit);
    
    Expression exp4 = null;
    if (user != null) {
      // user is specified
      // a1 join Action (a2) on a1.def = a2.id and 
      //    a2.role in user.roles
      Role[] userRoles = user.getTheRoles().toArray(new Role[0]);
      exp4 = queryAppliedActions4Subj.addJoinOnAttributesWithValueConstraint(
          new Class[] {ActionCls, ActionCls}, new String[] {Action.A_def, Action.A_id},
          Action.A_role, Op.IN, userRoles
          );
    }
    
    // debug on
    //dom.setDebugOn(true);
    
    Map<Oid, Action4Subject> result = null;
    
    try {
      result = dom.retrieveObjectsWoutSubTypes(Action4SubjectCls, queryAppliedActions4Subj);
    } catch (Exception e) {
      throw e;
    } finally { 
      queryAppliedActions4Subj.remove(exp1, exp2, exp3); 
      if (exp4 != null) queryAppliedActions4Subj.remove(exp4);
      
      // debug off
      //dom.setDebugOn(false);
    }
    
    return result;    
  }
  
  /**
   * @requires 
   *  user != null => user is a valid user /\  user.roles != null 
   *  
   * @effects 
   *  if exists {@link Action}s for the {@link Task}s of the {@link Process}es that are applied by {@link ProcessApplication}s
   *  whose ids are <tt>procAppIds</tt>
   *    return them as {@link Map}
   *  else
   *    return null
   *   
   * <p>If <tt>user != null</tt> then the above is constrained further by the {@link Action}s 
   * that the user is allowed to perform (through its roles).
   * 
   * <p> throws NotPossibleException if failed to generate query, DataSourceException if fails to read from the data source. 
   *    
   * @pseudocode
   * <pre>
   * (A) user = null
   *    select Action
   *    where  Action join Task and 
   *           Task join Process and 
   *           Process join ProcessApplication and 
   *           ProcessApplication.id in procAppIds
   *           
   * (B) user != null
   *    select Action
   *    where  Action (a1) join Task and 
   *           Task join Process and 
   *           Process join ProcessApplication and 
   *           ProcessApplication.id in procAppIds and 
   *           Action (a1) join Action (a2) on a1.def = a2.id and
   *            a2.role in user.roles  
   *  
   * </pre>
   * 
   * @version 1.2
   */
  public static Map<Oid, Action> retrieveAppliedActions(DODMBasic dodm,
      DomainUser user, Collection<Oid> procAppIds) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryAppliedActions == null) {
      queryAppliedActions = new FlexiQuery(dsm, ActionCls);
      
      // Action join Task 
      queryAppliedActions.addJoinExpression(new Class[] {ActionCls, TaskCls}, new String[] {Task.Assoc_TaskAndAction});
      
      // Task join Process  
      queryAppliedActions.addJoinExpression(new Class[] {TaskCls, ProcessCls}, new String[] {Process.Assoc_ProcessAndTask});
      
      // ProcessApplication join Process
      queryAppliedActions.addJoinExpression(new Class[] {ProcessApplicationCls, ProcessCls}, new String[] {Process.Assoc_ProcessAndProcessApplication});
    }

    // ProcessApplication.id in procAppIds
    Comparable[] procAppIdArr = getIdValuesFromOids(procAppIds);
    
    Expression exp1 = queryAppliedActions.addConstraintExpression(ProcessApplicationCls, ProcessApplication.A_id, Op.IN, procAppIdArr);
    
    Expression exp2 = null;
    if (user != null) {
      // user is specified
      // a1 join Action (a2) on a1.def = a2.id and 
      //    a2.role in user.roles
      Role[] userRoles = user.getTheRoles().toArray(new Role[0]);
      exp2 = queryAppliedActions.addJoinOnAttributesWithValueConstraint(
          new Class[] {ActionCls, ActionCls}, new String[] {Action.A_def, Action.A_id},
          Action.A_role, Op.IN, userRoles
          );
    }
    
    // debug on
    //dom.setDebugOn(true, "retrieveAppliedActions");
    
    Map<Oid, Action> result = null;
    
    try {
      result = dom.retrieveObjectsWoutSubTypes(ActionCls, queryAppliedActions);
    } catch (Exception e) {
      throw e;
    } finally { 
      queryAppliedActions.remove(exp1); 
      if (exp2 != null) queryAppliedActions.remove(exp2);
      
      // debug off
      //dom.setDebugOn(false);
    }
    
    return result;
  }

  /**
   * @requires 
   *  user != null /\ user.roles != null /\ ofTask != null
   *  
   * @effects 
   *  If exists {@link Action}s of <tt>ofTask</tt> that <tt>user</tt> is allowed to perform
   *    return them as {@link Map}
   *  else
   *    return <tt>null</tt>
   *    
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *  
   * @pseudocode
   *   select Action.id
   *   where (a1) Action.task = ofTask  and 
   *         a1 join Action (a2) on a1.def = a2.id and  
   *         a2.role in currentUser.roles 
   * @version 1.2
   */
  public static Map<Oid, Action> retrieveAllowedActions(DODMBasic dodm,
      DomainUser user, Task ofTask) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (queryAllowedActions == null) {
      queryAllowedActions = new FlexiQuery(dsm, ActionCls);
    }

    // (a1) Action.task = ofTask
    Expression exp1 = queryAllowedActions.addConstraintExpression(Action.A_task, Op.EQ, ofTask); 
    
    // a1 join Action (a2) on a1.def = a2.id and 
    //    a2.role in currentUser.roles
    Role[] userRoles = user.getTheRoles().toArray(new Role[0]);
    Expression exp2 = queryAllowedActions.addJoinOnAttributesWithValueConstraint(
        new Class[] {ActionCls, ActionCls}, new String[] {Action.A_def, Action.A_id},
        Action.A_role, Op.IN, userRoles
        );
    
    // debug on
    //dom.setDebugOn(true);
    
    Map<Oid, Action> result = null;
    
    try {
      result = dom.retrieveObjectsWoutSubTypes(ActionCls, queryAllowedActions);
    } catch (Exception e) {
      throw e;
    } finally { 
      queryAllowedActions.remove(exp1,exp2); 

      // debug off
      //dom.setDebugOn(false);
    }
    
    return result;
  }

  /**
   * @requires
   *  <tt>action != null</tt>
   *  /\ <tt>user != null</tt> => <tt>user</tt> is a valid user of the system.
   *  
   * @modifies <tt>action</tt>
   * @effects <pre> 
   *  if user != null
   *    retrieve {@link SubjectAction}s of <tt>action</tt> that are performed by <tt>user</tt>
   *  else
   *    retrieve all {@link SubjectAction}s of <tt>action</tt>,  
   *    group these {@link SubjectAction}s by the user(s) that perform them (i.e. those performed by the same user
   *    appear together in the result)  
   *  
   *  <p>Add any newly-loaded {@link SubjectAction}s to <tt>action</tt>
   *  
   *  <p>Return the {@link SubjectAction}s as {@link Collection}. 
   *  
   *  <p>throws DataSourceException if failed to read from data source.
   *  </pre>
   * 
   * @version 1.2
   */
  public static Collection<SubjectAction> retrieveSubjectActionsOf(DODMBasic dodm,
      Action4Subject action, DomainUser user) throws DataSourceException {
    if (action == null) return null;

    if (!isTeacherRole(action.getRoleDef())) {
      // not a teacher role
      return retrieveSubjectActionsOf(dodm, action);
    } else {
      // teacher role
      return retrieveSubjectActionsForTeacherOf(dodm, action, user);
    }
  }
  
  /**
   * @requires
   *  <tt>action != null</tt>
   *  /\ <tt>action.role</tt> is a Teacher
   *  /\ <tt>user != null</tt> => <tt>user</tt> is a valid {@link Teacher} of the system.
   *  
   * @modifies <tt>action</tt>
   * @effects <pre> 
   *  if user != null
   *    retrieve {@link SubjectAction}s of <tt>action</tt> that are performed by <tt>user</tt>
   *  else
   *    retrieve all {@link SubjectAction}s of <tt>action</tt>,  
   *    grouped by the user(s) that perform them (i.e. those performed by the same user
   *    appear together in the result)  
   *  
   *  <p>Add any newly-loaded {@link SubjectAction}s to <tt>action</tt>
   *  
   *  <p>Return the {@link SubjectAction}s as {@link Collection}. 
   *  
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *  </pre>
   * 
   * @pseudocode
   *  (1) user = null: retrieve all SubjectActions and the teachers that perform them (order by Teacher.id) 
   * 
   *      select SubjectAction.id
   *      where SubjectAction join Action4Subject with Action4Subject.id = action.id and  
   *        SubjectAction join SubjectBySemester and 
   *        TeachingBySemester join SubjectBySemster and 
   *        TeachingBySemester join Teacher 
   *      order by Teacher.id
   *  
   *  (2) user != null: select SubjectActions that are only performed by user (teacher)
   *  
   *    select SubjectAction.id
   *    where SubjectAction join Action4Subject with Action4Subject.id = action.id and  
   *        SubjectAction join SubjectBySemester and 
   *        TeachingBySemester join SubjectBySemster and 
   *        TeachingBySemester join Teacher and 
   *        Teacher.id = user.id 
   *        
   * @version 1.2
   */
  public static Collection<SubjectAction> retrieveSubjectActionsForTeacherOf(DODMBasic dodm, Action4Subject action, DomainUser user) 
  throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (querySubjActOfTeacher == null) {
      querySubjActOfTeacher = new FlexiQuery(dsm, SubjectActionCls);
      
      // SubjectAction join Action4Subject
      querySubjActOfTeacher.addJoinExpression(new Class[] {SubjectActionCls, Action4SubjectCls}, 
          new String[] {Action4Subject.Assoc_Action4SubjectAndSubjectAction});

      //SubjectAction join SubjectBySemester 
      querySubjActOfTeacher.addJoinExpression(new Class[] {SubjectActionCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndSubjectAction});

      //TeachingBySemester join SubjectBySemester
      querySubjActOfTeacher.addJoinExpression(new Class[] {TeachingBySemCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndTeaching});

      // TeachingBySemester join Teacher
      querySubjActOfTeacher.addJoinExpression(new Class[] {TeachingBySemCls, TeacherCls}, new String[] {Teacher.Assoc_TeacherAndTeachingBySemester});
      
      // Teacher join DomainUser (needed to use DomainUser in order-by)
      querySubjActOfTeacher.addJoinOnAttributes(new Class[] {TeacherCls, DomainUserCls}, new String[] {Teacher.Attribute_id, DomainUser.Attribute_id});
    }

    // (SubjectAction join Action4Subject) with Action4Subject.id = action.id
    Expression exp1 = querySubjActOfTeacher.addConstraintExpression(Action4SubjectCls, 
        Action4Subject.A_id, Op.EQ, getObjectIdValue(dom, Action4SubjectCls, action));

    Expression exp2 = null;
    if (user != null) {
      // Teacher.id = user.id
      Comparable idVal = getObjectIdValue(dom, DomainUserCls, user);
      exp2 = querySubjActOfTeacher.addConstraintExpression(TeacherCls, Teacher.Attribute_id, Op.EQ, idVal); 
    }
    
    // debug on
    //dom.setDebugOn(true);
    
    Map<Oid, SubjectAction> subjActs = null;
    try {
      Class orderBy = DomainUserCls;
      subjActs = dom.retrieveObjectsWithOrderBy(SubjectActionCls, querySubjActOfTeacher, orderBy);
    } catch (Exception e) {
      throw e;
    } finally { 
      querySubjActOfTeacher.remove(exp1);
      if (exp2 != null) querySubjActOfTeacher.remove(exp2);
      
      // debug off
      //dom.setDebugOn(false);
    }
    
    if (subjActs != null) {
      // add links from action to subjActs
      Collection<SubjectAction> subjActCol = subjActs.values();
      action.addSubjectAction(subjActCol);
      
      return subjActCol;
    } else {
      return null;
    }
  }

  /**
   * 
   * @requires
   *  <tt>action != null</tt>
   *  
   * @modifies <tt>action</tt>
   * @effects <pre> 
   *  if (action has no {@link SubjectAction}s 
   *    retrieve {@link SubjectAction}s of <tt>action</tt> and return them
   *  else
   *    return the {@link SubjectAction}s <tt>action</tt> 
   *  
   *  <p>throws DataSourceException if failed to read from data source.
   *  </pre> 
   *
   * @version 
   * - 1.2<br>
   * - 1.3: improved to retrieve all {@link SubjectAction}s rather than relying on those currently recorded in <tt>action</tt>
   */
  public static Collection<SubjectAction> retrieveSubjectActionsOf(DODMBasic dodm, Action4Subject action) throws DataSourceException {
    if (action == null) return null;
    
    /* v1.3: retrieve all objects rather than relying on those currently recorded in action
    if (!action.hasSubjectActions()) {
      DOMBasic dom = dodm.getDom();
      
      Map<Oid, SubjectAction> subjectActs = dom.retrieveAssociatedObjects(action, Action4SubjectCls, SubjectActionCls, Action4Subject.Assoc_Action4SubjectAndSubjectAction);
      if (subjectActs != null) {
        return subjectActs.values();
      } else {
        return null;
      }
    } else {
      return action.getSubjectActions();
    }
    */
    DOMBasic dom = dodm.getDom();
    
    Map<Oid, SubjectAction> subjectActs = dom.retrieveAssociatedObjects(action, Action4SubjectCls, SubjectActionCls, Action4Subject.Assoc_Action4SubjectAndSubjectAction);
    if (subjectActs != null) {
      return subjectActs.values();
    } else {
      return null;
    }    
  }
  
  /**
   * @requires 
   *  <tt>semester, year != null</tt>
   *  
   * @effects <pre>
   *  if subject != null
   *    if exists {@link SubjectAction}(s) for subject in semester, year
   *      return them as {@link Map}
   *    else 
   *      return null
   *  else
   *    if exists {@link SubjectAction}(s) for <b>all</b> {@link Subject}s in semester, year
   *      return them as {@link Map}
   *    else 
   *      return null
   *  </pre>
   *  
   * <p>The result {@link SubjectAction}(s) are order by {@link SubjectBySemester}
   *  
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *  
   * @pseudocode <pre>
   * (1) subject = null:
   * 
   *  select SubjectAction
   *  where  SubjectAction join SubjectBySemester and 
   *         SubjectBySemester.semester = semester and 
   *         SubjectBySemester.year = year 
   *  orderby SubjectBySemester 
   *         
   * (2) subject != null:
   *  select SubjectAction
   *  where  SubjectAction join SubjectBySemester and 
   *         SubjectBySemester.semester = semester and 
   *         SubjectBySemester.year = year and 
   *         SubjectBySemester join Subject and
   *         Subject.id = subject
   *  orderby SubjectBySemester 
   * </pre>
   * 
   * @version 
   * - 1.2: orgUnit is assumed to be the default {@link OrgUnit}
   * (if not then need to join SubjectAction with Action4Subject...ProcessApplication to identify the right ones
   * for the specified orgUnit)
   */
  public static Map<Oid, SubjectAction> retrieveSubjectActionsBySubject(DODMBasic dodm,
      Subject subject, OrgUnit orgUnit, Semester semester, Integer year) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    if (querySubjectActBySubject == null) {
      querySubjectActBySubject = new FlexiQuery(dsm, SubjectActionCls);
      
      //SubjectAction join SubjectBySemester 
      querySubjectActBySubject.addJoinExpression(new Class[] {SubjectActionCls, SubjectBySemCls}, new String[] {SubjectBySemester.Assoc_SubjectBySemesterAndSubjectAction});
    }

    Expression exp1, exp2, exp3 = null, exp4 = null;

    exp1 = querySubjectActBySubject.addConstraintExpression(SubjectBySemCls, SubjectBySemester.A_semester, Op.EQ, semester);
    
    exp2 = querySubjectActBySubject.addConstraintExpression(SubjectBySemCls, SubjectBySemester.A_year, Op.EQ, year);
    
    if (subject != null) {
      exp3 = querySubjectActBySubject.addJoinExpression(new Class[] {SubjectBySemCls, SubjectCls}, new String[] {Subject.Assoc_SubjectAndSubjectBySems});
      
      Comparable idVal = getObjectIdValue(dom, SubjectCls, subject);
      exp4 = querySubjectActBySubject.addConstraintExpression(SubjectCls, Subject.A_id, Op.EQ, idVal);
    }

    // debug on
    //dom.setDebugOn(true, "retrieveSubjectActionsBySubject()");
    
    Map<Oid, SubjectAction> subjActs = null;
    try {
      // 2-level groupings:
      // (1) by SubjectBySemester: to group together SubjectActions of the same SubjectBySemeter
      // (2) by SubjectAction: to group together SubjectActions of the same Action4Subject (due to the way that they are applied) 
      Class[] orderBy = new Class[] {SubjectBySemCls, SubjectActionCls};
      subjActs = dom.retrieveObjectsWithOrderBy(SubjectActionCls, querySubjectActBySubject, orderBy);
    } catch (Exception e) {
      throw e;
    } finally { 
      querySubjectActBySubject.remove(exp1, exp2);
      if (exp3 != null) querySubjectActBySubject.remove(exp3);
      if (exp4 != null) querySubjectActBySubject.remove(exp4);
      
      // debug off
      //dom.setDebugOn(false);
    }
    
    if (subjActs != null) {
      return subjActs;
    } else {
      return null;
    }
  }
  
  /**
   * @requires 
   *  user != null /\ user.roles != null /\ ofTask != null
   *  
   * @effects 
   *  If exists {@link Tasks}s of <tt>ofProcess</tt> that <tt>user</tt> is allowed to perform
   *  i.e. Tasks that contain at least one Action that user is allowed to perform
   *    return them as {@link Map}
   *  else
   *    return <tt>null</tt>
   *    
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source. 
   *  
   * @pseudocode
   *   select Task.id
   *    where Task.process = ofProcess and 
   *          Action (a1) join Task and
   *          a1 join Action (a2) on a1.def = a2.id and  
   *            a2.role in user.roles 
   * @version 1.2
   */
  public static Map<Oid, Task> retrieveAllowedTasks(DODMBasic dodm,
      DomainUser user, Process ofProcess) throws NotPossibleException, DataSourceException {
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
    
    // retrieve all Tasks that user is allowed to perform
    if (queryAllowedTasks == null) {
      queryAllowedTasks = new FlexiQuery(dsm, TaskCls);
      
      // Action (a1) join Task
      queryAllowedTasks.addJoinExpression(new Class[] {ActionCls, TaskCls}, new String[] {Task.Assoc_TaskAndAction});
    }

    // Task.process = ofProcess
    Expression exp1 = queryAllowedTasks.addConstraintExpression(Task.A_process, Op.EQ, ofProcess); 
    
    // Action (a1) join Task (moved up to add once)  
    // Expression exp3 = query.addJoinExpression(new Class[] {ActionCls, TaskCls}, new String[] {Task.Assoc_TaskAndAction});
    
    // a1 join Action (a2) on a1.def = a2.id and 
    //    a2.role in currentUser.roles
    Role[] userRoles = user.getTheRoles().toArray(new Role[0]);
    Expression exp2 = queryAllowedTasks.addJoinOnAttributesWithValueConstraint(
        new Class[] {ActionCls, ActionCls}, new String[] {Action.A_def, Action.A_id},
        Action.A_role, Op.IN, userRoles
        );
    
    // debug on
    //dom.setDebugOn(true);
    
    Map<Oid,Task> result = null;
    try {
      result = dom.retrieveObjectsWoutSubTypes(TaskCls, queryAllowedTasks);
    } catch (Exception e) {
      throw e;
    } finally { 
      queryAllowedTasks.remove(exp1, exp2);
    
      // debug off
      //dom.setDebugOn(false);
    }
    
    return result; 
  }

  /**
   * @effects 
   *  return an array of the id attribute values that are encapsulated in <tt>oids</tt> 
   *  
   * @version 1.2
   */
  private static Comparable[] getIdValuesFromOids(Collection<Oid> oids) {
    Comparable[] idArr = new Comparable[oids.size()];
    int i = 0;
    for (Oid oid : oids) {
      idArr[i++] = oid.getIdValue(0);
    }
    
    return idArr;
  }
  
  /**
   * @effects 
   *  return the id-value of the {@link Oid} of the object <tt>o</tt> of the class <tt>c</tt>
   * @version 1.2
   */
  private static <T> Comparable getObjectIdValue(DOMBasic dom, Class<T> c, T o) {
    Oid id = dom.lookUpObjectId(c, o);
    return id.getIdValue(0);
  }

//  /**
//   * @effects 
//   *  If exists {@link BooleanExpression}s that are used as pre-conditions for the definitive action of <tt>action</tt>
//   *    return them
//   *  else
//   *    return null;   
//   * @version 1.2
//   */
//  public static Map<Oid, BooleanExpression> retrievePreConditionsOf(final DODMBasic dodm, 
//      final Action action) {
//    if (action == null) return null;
//    
//    Action defAction = action.getDef();
//    if (defAction == null) defAction = action;
//    
//    DOMBasic dom = dodm.getDom();
//    
//    dom.getObjectsMap(cls, query, comparator);
//  }
}
