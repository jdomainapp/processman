package vn.com.processman.modules.processexec.reports.teachingactions.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.report.model.Report;
import jda.modules.security.def.DomainUser;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processexec.reports.useractions.model.ReportUserActionsFilter;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  A filter that generates the output for {@link ReportTeachingActions} by retrieving the relevant data from the data source 
 *  to produce the {@link TeacherSubjActionInfo} objects the output.
 *  
 * @author dmle
 *
 * @version 1.2 
 */
public class ReportTeachingActionsFilter extends ReportUserActionsFilter {

  private static final Class<TeacherSubjActionInfo> TeachSubjActionInfoCls = TeacherSubjActionInfo.class;

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
    ReportTeachingActions reportObj = (ReportTeachingActions) report;
    final String userLogin = reportObj.getUserLogin();
    final OrgUnit orgUnit = reportObj.getOrgUnit();
    final Semester semester = reportObj.getSemester();
    final Integer year = reportObj.getYear();
    
    // query the data source for the relevant Actions and then use them to generate 
    // UserActionInfo objects
    
    Map<Oid,Action4Subject> actionsMap = null; 
    DomainUser inputUser = null; 
    //Map<Action4Subject,Collection<Teacher>> actionUsersMap = null;
    
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

//      // (3) if no user is specified then find all the users responsible for performing each action    
//      if (inputUser == null && actionsMap != null) {
//        //retrieve multiple users for this type of action (multiple teachers teaching in the same semester)
//        actionUsersMap = DomainToolKit.retrieveAction4SubjectTeachers(dodm, actionsMap);
//      }
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
      Collection<Action4Subject> actions = actionsMap.values();
      Collection<TeacherSubjActionInfo> actionInfs = new ArrayList<>();
      Collection<Oid> oids = new ArrayList<>();
      Collection<SubjectAction> subjActs;
      Object[] vals = new Object[5];
      SubjectAction formerActiveSubjAct;
      
      // support two separate groupings: by action and by user
      int actViewRowSpan;  // action view row span (for subject-action grouping)
      Integer userViewRowSpan; // user view-row-span (for user grouping) 
      int subjActIndex;
      DomainUser user, prevUser = null;
      Map<SubjectAction, DomainUser> subjActUsers = null; 
      Map<DomainUser, Integer> subjActByUserCounts = null;
      
      for (Action4Subject action : actions) {
        try {
          // ensure action is loaded with subject actions (for inputUser if it is specified, or for all users if it is not)
          subjActs = DomainToolKit.retrieveSubjectActionsOf(dodm, action, inputUser);
          
          // ensure action's status is set
          action.ensureStatusIsSet();
          
          // reset variables
          if (prevUser != null) prevUser = null;
          userViewRowSpan = 0;
          
          if (subjActs != null) {
            /*
            // evaluate subject-action statuses and then evaluate status of action 
            formerActiveSubjAct = action.getActiveSubjectAction();
            
            for (SubjectAction subjAct : subjActs) {
              // ensure subjAct status is set
              SubjectAction.evaluateSubjStatus(subjAct);
            }
            // reset active subject action
            action.setActiveSubjectAction(formerActiveSubjAct);
            
            action.evaluateStatus();  // must be done after evaluating each SubjectAction
            */
            
            // create info objects from action and each SubjectAction in subjActs
            
            subjActIndex = 0;
            
            // group users of subjActs for use to set userViewRowSpan (below)
            if (subjActUsers == null) {
              subjActUsers = new HashMap<>(); subjActByUserCounts = new HashMap<>();
            } else {
              subjActUsers.clear(); subjActByUserCounts.clear();
            }
            
            if (inputUser == null) {
              DomainToolKit.retrieveUsersForSubjectActions(dodm, subjActs, subjActUsers);
              if (!subjActUsers.isEmpty())
                countSubjectActionsByUser(subjActUsers, subjActByUserCounts);
            }
            
            for (SubjectAction subjAct : subjActs) {
              // create info using (user, action, subjAct)

              // only set actViewRowSpan for first subjAct of action
              if (subjActIndex == 0) {
                actViewRowSpan = subjActs.size();
                
                if (inputUser == null) {
                  // add 1 to actViewRowSpan (the aggregated entry at the end of the group (below))
                  actViewRowSpan++;
                }
              } else {
                actViewRowSpan = 0;
              }
              
              if (inputUser != null) { // use input user
                user = inputUser;
                userViewRowSpan = actViewRowSpan;
              } else { // use action's user
                user = subjActUsers.get(subjAct);
                if (prevUser == null || !prevUser.equals(user)) {
                  // new user group
                  userViewRowSpan = subjActByUserCounts.get(user);
                  prevUser = user;
                } else {
                  // existing user group
                  userViewRowSpan = 0;
                }
                
                // a rare case: for those subjActs that do not have an associated user
                if (userViewRowSpan == null) userViewRowSpan = 1;
              }

              vals[0] = user;
              vals[1] = action;
              vals[2] = subjAct;   
              vals[3] = actViewRowSpan;
              vals[4] = userViewRowSpan;
              
              createInfoObject(dom, TeachSubjActionInfoCls, vals, reportObj, actionInfs, oids);
              
              subjActIndex++;
            } // end: for subjActs
            
            if (inputUser == null) {
              // create an aggregated entry for action's overall status
              vals[0] = null;
              vals[1] = action;
              vals[2] = null;   
              vals[3] = 0;  // no view span (spanned by actionInfo row)
              vals[4] = 0;  // no user span (merged into the adjacent cell)
              createInfoObject(dom, TeachSubjActionInfoCls, vals, reportObj, actionInfs, oids);
            }
          } else {
            // no subject actions: create info using (user, action)
            if (inputUser != null) { // use input user
              user = inputUser;
            } else { // use action's user
              user = DomainToolKit.retrieveActionUser(dodm, action);
            }
            
            if (user != null) 
              userViewRowSpan = 1;
            else  // no user
              userViewRowSpan = 0;
            
            vals[0] = user;
            vals[1] = action;
            vals[2] = null;   
            vals[3] = 1; // actViewRowSpan
            vals[4] = userViewRowSpan;

            createInfoObject(dom, TeachSubjActionInfoCls, vals, reportObj, actionInfs, oids);
          }
          
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
   * @requires
   *  subjActUsers != null
   *  
   * @modifies subjActByUserCounts
   * @effects 
   *  count number of {@link SubjectAction} of each {@link DomainUser} in <tt>subjActUsers</tt> and 
   *  add an entry to <tt>subjActByUserCounts</tt> to reflect that.
   */
  private void countSubjectActionsByUser(
      Map<SubjectAction, DomainUser> subjActUsers,
      Map<DomainUser, Integer> subjActByUserCounts) {
    Integer count;
    SubjectAction sa, sa1;
    DomainUser user, user1;
    Collection<Entry<SubjectAction, DomainUser>> set = subjActUsers.entrySet();
    for (Entry<SubjectAction, DomainUser> e: set) {
      sa = e.getKey();
      user = e.getValue();
      
      count = subjActByUserCounts.get(user);
      
      if (count == null) { // not yet counted
        count = 1;
        for (Entry<SubjectAction, DomainUser> e1: set) {
          sa1 = e1.getKey();
          user1 = e1.getValue();
          if (!sa.equals(sa1) && user.equals(user1)) {
            // update count
            count++;
          }
        }
        
        subjActByUserCounts.put(user, count);
      }
    }
  }

  /**
   * @requires 
   *  inputUser != null => inputUser.roles != null
   *  
   * @effects 
   *  retrieve the {@link Action4Subject}s of the subject-related {@link Process}es that are applied in the given <tt>semester, year</tt> for <tt>orgUnit</tt>
   *  and if <tt>inputUser != null</tt> then also that this user is allowed to perform
   *  
   *  <p>return <tt>null</tt> if no such actions exist.
   */
  @Override
  protected <T extends Action> Map<Oid, T> retrieveActionsMap(DODMBasic dodm, DomainUser inputUser, OrgUnit orgUnit, Semester semester, Integer year) throws NotPossibleException, DataSourceException {
    Map<Oid, T> actionsMap = null;
    
    // there is no need to retrieve process-application-ids first because we can query Action4Subject directly
    // using semester and year
    //Collection<Oid> procAppIds = DomainToolKit.retrieveProcessApplicationIds(dodm, inputUser, orgUnit, semester, year);
    
    actionsMap = (Map<Oid, T>) DomainToolKit.retrieveAppliedAction4Subjects(dodm, inputUser, orgUnit, semester, year);
    
    return actionsMap;
  }
}
