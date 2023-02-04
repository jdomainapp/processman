package vn.com.processman.modules.processexec.reports.useractions.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.setup.data.processstructure.Actions;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.model.Semester;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.basics.model.security.DomainUser;
import domainapp.basics.util.Tuple2;
import domainapp.modules.report.model.OutputFilter;
import domainapp.modules.report.model.Report;

/**
 * @overview
 *  A filter that generates the output for {@link ReportUserActions} by retrieving the relevant data from the data source 
 *  to produce the {@link UserActionInfo} objects the output.
 *  
 * @author dmle
 *
 * @version 1.2
 */
public class ReportUserActionsFilter extends OutputFilter {

  private static final Class<UserActionInfo> UserActionInfoCls = UserActionInfo.class;
  private static final Class<Task> TaskCls = Task.class;

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.modules.report.model.OutputFilter#filter(domainapp.basics.core.ControllerBasic, java.util.Collection, domainapp.modules.report.model.Report)
   */
  @Override
  public Collection<Oid> filter(ControllerBasic rptCtl, Collection<Oid> result,
      Report report) throws NotPossibleException {
    DODMBasic dodm = rptCtl.getDodm();
    DOMBasic dom = dodm.getDom();
    //DSMBasic dsm = dodm.getDsm();

    // the actual report object
    ReportUserActions reportObj = (ReportUserActions) report;
    final String userLogin = reportObj.getUserLogin();
    final OrgUnit orgUnit = reportObj.getOrgUnit();
    final Semester semester = reportObj.getSemester();
    final Integer year = reportObj.getYear();
    
    // query the data source for the relevant Actions and then use them to generate 
    // UserActionInfo objects
    
    Map<Oid,Action> actionsMap = null;
    Map<Action,Collection<DomainUser>> actionUsersMap = null;
    DomainUser inputUser = null;
    
    try {
      // (1) retrieve user 
      inputUser = retrieveDomainUser(dodm, userLogin);

      if (inputUser != null) {
        // ensure user has been loaded with roles (only needed here b/c we are working with a 
        // potentially not logged in user)
        DomainToolKit.ensureUserHasRoles(dodm, inputUser);
      }
      
      // (2) retrieve procAppIds: relevant ProcessApplication ids
      actionsMap = retrieveActionsMap(dodm, inputUser, orgUnit, semester, year);

      // (3) if no user is specified then find all the users responsible for performing each action    
      if (inputUser == null && actionsMap != null) {
        actionUsersMap = DomainToolKit.retrieveActionUsers(dodm, actionsMap);
      }
    } catch (Exception e) {
      rptCtl.logError(this.getClass().getSimpleName()+"filter(): ", e);
      e.printStackTrace();
    }
    
    // generate the result
    if (actionsMap == null) {
      // no result
      return null;
    } else {
      // generate UserActionInfo
      Collection<Action> actions = actionsMap.values();
      Collection<UserActionInfo> actionInfs = new ArrayList<>();
      Collection<Oid> oids = new ArrayList<>();
      Object[] vals = new Object[3];
      
      // support group-by task
      // TODO: check config first?
      int viewRowSpan;
      Task prevTask = null;
      Task task;

      Map<Task, Integer> taskActionsCount = countTaskActionGroups(actions);
      Collection<DomainUser> users;
      for (Action action : actions) {
        try {
          
          // ensure that action status is set
          action.evaluateStatus(); //ensureStatusIsSet();
          
          task = action.getTask();
          
          if (prevTask == null || !prevTask.equals(task)) {
            // start a new task group
            
            // use the action group count for view-row-span
            viewRowSpan = taskActionsCount.get(task);
            
            prevTask = task;
          } else {
            // same task group
            viewRowSpan = 0;
          }
          
          // create UserActionInfo from each action and inputUser or the action's user
          if (inputUser != null) { // use input user
            vals[0] = inputUser;
          } else { // use action's user
            users = actionUsersMap.get(action);
            if (users != null) {
              // combine user names
              vals[0] = getUserNames(users);
            } else {
              vals[0] = null;
            }
          }
          vals[1] = action;
          
          vals[2] = viewRowSpan;
          
          createInfoObject(dom, UserActionInfoCls, vals, reportObj, actionInfs, oids);
          
        } catch (Exception e) {
          rptCtl.logError(this.getClass().getSimpleName()+"filter(): ", e);
          e.printStackTrace();
        }
      }

      if (!actionInfs.isEmpty()) {
        reportObj.addUserActionInfo(actionInfs);
        return oids;
      } else {
        return null;
      }
    }
  }
  
  /**
   * @effects 
   *  return a comman-separated list of names of <tt>users</tt>
   */
  private String getUserNames(Collection<DomainUser> users) {
    StringBuffer names = new StringBuffer();
    int index = 0;
    int count = users.size();
    for (DomainUser user: users) {
      names.append(user.getName());
      if (index < count-1) names.append(", ");
      index++;
    }
    
    return names.toString();
  }

  /**
   * @effects 
   *  count the {@link Actions} in <tt>actions</tt> that belong to the same {@link Task} and 
   *  add an entry for this in the result {@link Map} that is returned
   */
  private Map<Task, Integer> countTaskActionGroups(Collection<Action> actions) {
    Map<Task, Integer> groupCounts = new HashMap<>();
    
    Task task;
    Integer grpCount;
    for (Action action: actions) {
      task = action.getTask();
      grpCount = groupCounts.get(task);
      if (grpCount == null) {
        grpCount = 1;
      } else {
        grpCount++;
      }
      
      groupCounts.put(task, grpCount);
    }
    
    return groupCounts;
  }

  /**
   * @modifies actionInfs, oids
   * @effects 
   *  create a new object of <tt>objClass/tt> whose state is <tt>vals</tt>; add the object to <tt>objects</tt>
   *   and add its id to <tt>oids</tt> 
   */
  protected <T extends UserActionInfo> void createInfoObject(DOMBasic dom, Class<T> objClass, Object[] vals, 
      ReportUserActions reportObj,
      Collection<T> objects, Collection<Oid> oids) throws NotFoundException, NotPossibleException, DataSourceException {
    Tuple2<Oid,Object> objTuple = dom.createObject(objClass, vals);

    Oid oid = objTuple.getFirst();
    T obj = (T) objTuple.getSecond();
    
    obj.setReportUserActions(reportObj);
    objects.add(obj);
    oids.add(oid);    
  }

  /**
   * @effects <pre>
   *  if <tt>userLogin != null</tt>
   *    retrieve and return {@link DomainUser} whose login is <tt>userLogin</tt>
   *     
   *    throws NotFoundException if no such user is found
   *  else
   *    return null
   *    </pre>
   */
  protected DomainUser retrieveDomainUser(DODMBasic dodm, String userLogin) throws NotPossibleException, DataSourceException {
    DomainUser inputUser = null;
    if (userLogin != null) {
      // user is specified: get user-permissible Actions
      inputUser = DomainToolKit.retrieveDomainUser(dodm, userLogin);
      
      if (inputUser == null) {
        // ERROR: 
        throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, new Object[] {DomainUser.class, userLogin});
      }
    }
    
    return inputUser;
  }
  
  /**
   * @requires 
   *  inputUser != null => inputUser.roles != null
   * 
   * @effects 
   *  retrieve the {@link Action}s of the {@link Process}es that are applied in the given <tt>semester, year</tt> and for <tt>orgUnit</tt>
   *  and if <tt>inputUser != null</tt> then also that this user is allowed to perform
   *  
   *  <p>return <tt>null</tt> if no such actions exist.
   */
  protected <T extends Action> Map<Oid, T> retrieveActionsMap(DODMBasic dodm, DomainUser inputUser, OrgUnit orgUnit, Semester semester, Integer year) throws NotPossibleException, DataSourceException {
    Map<Oid, T> actionsMap = null;
    Collection<Oid> procAppIds = DomainToolKit.retrieveProcessApplicationIds(dodm, inputUser, orgUnit, semester, year);
    
    if (procAppIds != null) {
      // retrieve relevant Actions of procAppIds
      
      actionsMap = (Map<Oid, T>) DomainToolKit.retrieveAppliedActions(dodm, inputUser, procAppIds);
    }
    
    return actionsMap;
  }
  
}
