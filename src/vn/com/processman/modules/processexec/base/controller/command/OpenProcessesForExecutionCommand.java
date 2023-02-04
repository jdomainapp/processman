package vn.com.processman.modules.processexec.base.controller.command;

import java.util.Collection;

import vn.com.processman.modules.processexec.base.model.ProcessExecution;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.model.Semester;
import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.controller.util.MessageCode;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.ControllerBasic.DataController;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.model.Oid;
import domainapp.basics.model.security.DomainUser;

/**
 * @overview
 *  A {@link DataControllerCommand} that customises the <tt>Open</tt> operation to retrieve from the system 
 *  {@link Process} objects matching user-specified input specified by {@link ProcessExecution}.  
 *  
 *  <p>Input parameters: OrgUnit orgUnit (o), String processName (n), Semester semester (s), Integer year (y)
 *  
 *  <p>Object query:
 *  <pre> 
 *  A/ If security is NOT enabled:
 *    Select ProcessApplication
 *    Where ProcessApplication join Process and 
 *            Process.def in (select Process.id where Process.name matches n) and 
 *          ProcessApplication.orgUnit = o and
 *          ProcessApplication.semester = s and
 *          ProcessApplication.year = y
 *           
 *  B/ (v3.3) If security is enabled and user is a process user
 *      if user is a Teacher AND user module uses Task4Subject AND 
 *      user is NOT teaching any subjects in the semester, year
 *        display message "Teacher does not teach in"
 *      else
 *        find processes that user is allowed to perform, as follow:  
 *        Select ProcessApplication
 *        Where ProcessApplication join Process and
 *            Process.def in (select Process.id where Process.name matches n) and
 *          RolePerfProcess join Process and  
 *            Process.def = RolePerfProcess.process and       
 *            RolePerfProcess.role in (select UserRole.role where UserRole.user = currentUser) and
 *          ProcessApplication.orgUnit = o and
 *          ProcessApplication.semester = s and
 *          ProcessApplication.year = y 
 *           
 *  </pre>
 * @author dmle
 * 
 * @version 
 * - 3.3: improved to support user's role permissions on the processes (through the actions that they are allowed to perform)
 */
public class OpenProcessesForExecutionCommand<C> extends DataControllerCommand {

//  private static final Class<ProcessApplication> ProcessApplicationCls = ProcessApplication.class;
//  private static final Class<Process> ProcessCls = Process.class;
//  private static final Class RolePerfProcessCls = RolePerfProcess.class;
//  private static final Class UserRoleCls = UserRole.class;
//  
//  /** the parameterised query used to retrieve objects, it is created once and updated each time this is run*/
//  private static FlexiQuery query;
//  
//  /**nested query: select Process.id where Process.name matches n*/
//  private static FlexiQuery nestedQuery;
//  
//  /** nestedRoleQuery: select UserRole.role where UserRole.user = currentUser */
//  private static FlexiQuery nestedRoleQuery;
  
  public OpenProcessesForExecutionCommand(DataController dctl) {
    super(dctl);
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    
    DataController<Process> dctl = getDataController();
    DataController<ProcessExecution> parentDctl = dctl.getParent();
    final ProcessExecution parentObj = parentDctl.getCurrentObject(); 
    
    DODMBasic dodm = getDodm();
//    DSMBasic dsm = dodm.getDsm();
//    DOMBasic dom = dodm.getDom();
    
    /* if user is a Teacher AND user module uses Task4Subject AND 
     * user is NOT teaching any subjects in the semester, year
     *        display message "Teacher does not teach in"
     */
    DomainUser user = null;
    boolean isProcessUser = false;
    Semester semester = parentObj.getSemester();
    Integer year = parentObj.getYear();

    if (isLoggedIn()) { 
      // validate some assumptions about user
      user = getCurrentUser();
      isProcessUser = DomainToolKit.isProcessUserStrictly(user);
      
//      // ensures that user plays a current role
//      if (isProcessUser && !DomainToolKit.doesUserPlayARoleIn(dodm, user, semester, year)) {
//        ControllerBasic parentCtl = parentDctl.getCreator();
//        parentCtl.displayMessageFromCode(vn.com.processman.util.message.MessageCode.USER_PLAYS_NO_ROLE_IN_SEMESTER, dctl,
//            new Object[] {user.getName(), semester.name(), year+""}
//            );
//        return;        
//      }
      
      if (isProcessUser && DomainToolKit.isTeacherUser(user)) {
        // user is a Teacher
        if (!DomainToolKit.doesTeacherTeachIn(dodm, user, semester, year)) {
          // user is NOT teaching any subjects in the semester, year
          ControllerBasic parentCtl = parentDctl.getCreator();
          parentCtl.displayMessageFromCode(vn.com.processman.util.message.DomainMessageCode.TEACHER_NOT_TEACHING_IN_SEMESTER, dctl,
              new Object[] {user.getName(), semester.name(), year+""}
              );
          return;
        }
      }
    }
    
    /* find processes that user is allowed to perform */
//    Boolean isCurrentProcessUser;
//    if (user == null) {
//      isCurrentProcessUser = null; 
//    } else {
//      isCurrentProcessUser = isProcessUser;
//    }
    
    Collection<Oid> oids = DomainToolKit.retrieveProcessApplicationIds(dodm, user, isProcessUser, parentObj.getOrgUnit(), parentObj.getProcessName(), 
        semester, year);
    
//    // 1. formulate the query 
//    if (query == null) {
//      query = new FlexiQuery(dsm, ProcessApplicationCls);
//
//      // ProcessApplication join Process
//      Class[] joinClasses = new Class[] { ProcessApplicationCls, ProcessCls };
//      String[] assocNames = new String[] { Process.Assoc_ProcessAndProcessApplication};
//      
//      query.addJoinExpression(joinClasses, assocNames);
//    }
//    
//    // nested query: select Process.id where Process.name matches n
//    if (nestedQuery == null) {
//      nestedQuery = new FlexiQuery(dsm, ProcessCls);
//      nestedQuery.addDomainAttribute(Process.A_id);
//    }
//    String attrib = Process.A_name;
//    Op op = Op.MATCH;
//    String val = parentObj.getProcessName();
//    nestedQuery.addConstraintExpression(attrib, op, val);
//    
//    Expression exp1, exp2, exp3, exp4, exp5 = null;
//    
//    //... Process.def in (nestedQuery)
//    exp1 = query.addConstraintExpression(ProcessCls, Process.A_def, Op.IN, nestedQuery);
//
//    // v3.3: support user's permission
//    if (isProcessUser) {
//      if (nestedRoleQuery == null) {
//        // RolePerfProcess join Process on RolePerfProcess.process = Process.def  
//        query.addJoinOnAttributes(new Class[] {RolePerfProcessCls, ProcessCls}, 
//            new String[] {RolePerfProcess.A_process, Process.A_def});
//        
//        //nestedRoleQuery: select UserRole.role where UserRole.user = currentUser
//        nestedRoleQuery = new FlexiQuery(dsm, UserRoleCls);
//        nestedRoleQuery.addDomainAttribute(UserRole.A_role);  // select
//      }
//      
//      // nestedRoleQuery: update with current user
//      nestedRoleQuery.addConstraintExpression(UserRole.A_user, Op.EQ, user); // where
//      
//      
//      //... RolePerfProcess.role in (nestedRoleQuery)
//      exp5 = query.addConstraintExpression(RolePerfProcessCls, RolePerfProcess.A_role, Op.IN, nestedRoleQuery);
//    }
//
//    // ProcessApplication.orgUnit = o
//    exp2 = query.addConstraintExpression(ProcessApplication.A_orgUnit, Op.EQ, parentObj.getOrgUnit());
//    
//    // ProcessApplication.semester = s
//    exp3 = query.addConstraintExpression(ProcessApplication.A_semester, Op.EQ, semester);
//    
//    // ProcessApplication.year = y
//    exp4 = query.addConstraintExpression(ProcessApplication.A_year, Op.EQ, year);
//    
//    // 2. execute query to retrieve data 
//    Collection<Oid> oids = null;
//    
//    // debug
//    dom.setDebugOn(true);
//    
//    try {
//      oids = dom.retrieveObjectOids(ProcessApplicationCls, query);
//    } catch (Exception e) {
//      throw e;
//    } finally {
//      query.remove(exp1,exp2,exp3,exp4); 
//      if (exp5 != null) query.remove(exp5);
//      nestedQuery.removeAll();
//      if (nestedRoleQuery != null) nestedRoleQuery.removeAll();
//      
//      // debug
//      dom.setDebugOn(false);
//    }

    // 3. display result
    if (oids != null) {
      // retrieve the objects
//      final DAttr refAttrib = dsm.getDomainConstraint(ProcessApplicationCls, ProcessApplication.A_process);
//      Collection<Oid> procIds = new ArrayList();
//
//      Oid procId;
//      for (Oid oid : oids) {
//        try {
//          // load the ids
//  
//          // classes
//          procId = dom.retrieveAssociateOid(ProcessApplicationCls, oid, refAttrib,
//              ProcessCls);
//  
//          if (procId != null) {
//            procIds.add(procId);
//          }
//        } catch (Exception e) {
//          // dctl.displayError(this.getClass().getSimpleName()+"filter(): ", e);
//          e.printStackTrace();
//        }
//      }
      Collection<Oid> procIds = DomainToolKit.retrieveAppliedProcessIds(dodm, oids);
      
      // open result on the data container
      dctl.open(procIds);
    } else {
      ControllerBasic parentCtl = parentDctl.getCreator();
      String parentObjName = parentCtl.getDomainClassLabel() + " (" + parentObj.getId() + ")";
      String functionTitle = parentCtl.getModuleTitle();
      parentCtl.displayMessageFromCode(MessageCode.NO_OBJECTS_FOUND_FOR_FUNCTION, dctl,
          new Object[] {parentObjName, functionTitle}
          );
    }  
  }
}
