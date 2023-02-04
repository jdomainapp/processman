package vn.com.processman.modules.processexec.reports.subjectactions.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processexec.reports.teachingactions.model.ReportTeachingActionsFilter;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.model.Semester;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.modules.report.model.Report;

/**
 * @overview
 *  A filter that generates the output for {@link ReportSubjectActions} by retrieving the relevant data from the data source 
 *  to produce the {@link SubjectActionInfo} objects as the output.
 *  
 * @author dmle
 *
 * @version 1.2 
 */
public class ReportSubjectActionsFilter extends ReportTeachingActionsFilter {

  private static final Class<SubjectActionInfo> SubjActionInfoCls = SubjectActionInfo.class;

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
    ReportSubjectActions reportObj = (ReportSubjectActions) report;
    final Subject subject = reportObj.getSubject();
    final OrgUnit orgUnit = reportObj.getOrgUnit();
    final Semester semester = reportObj.getSemester();
    final Integer year = reportObj.getYear();
    
    // query the data source for the relevant SubjectActions and then use them to generate 
    // SubjectActionInfo objects
    
    Map<Oid,SubjectAction> subjActionsMap = null; 
    
    try {
      // (1) retrieve SubjectActions
      subjActionsMap = DomainToolKit.retrieveSubjectActionsBySubject(dodm, subject, orgUnit, semester, year);
    } catch (Exception e) {
      rptCtl.logError(this.getClass().getSimpleName()+".filter(): ", e);
      e.printStackTrace();
    }
    
    // generate the result
    if (subjActionsMap == null) {
      // no result
      return null;
    } else {
      // generate UserActionInfo
      Collection<SubjectAction> subjActions = subjActionsMap.values();
      Collection<SubjectActionInfo> actionInfs = new ArrayList<>();
      Collection<Oid> oids = new ArrayList<>();
      Object[] vals = new Object[3];
      
      // support two separate groupings: by subject and by action
      Integer subjectViewRowSpan; // subject view-row-span (for subject grouping) 
      Map<SubjectBySemester, Integer> subjActCounts = countSubjectActsBySubject(subjActions); 
      SubjectBySemester prevSubj = null, subj;
      Action4Subject action;
      
      for (SubjectAction subjAct : subjActions) {
        try {
          // ensure subjAct status is set
          // SubjectAction.evaluateSubjStatus(subjAct);

          subj = subjAct.getSubject();
          action = subjAct.getAction();
          
          // ensure action's status is set
          action.ensureStatusIsSet();
          
          // create info objects from subjAct using (user=null, action=subjAct.action, subjAct)
          
          // only set subjectViewRowSpan for first subjAct of the subject
          if (prevSubj == null || !prevSubj.equals(subj)) {
            subjectViewRowSpan = subjActCounts.get(subj);
            prevSubj = subj;
          } else {
            subjectViewRowSpan = 0;
          }
          
          vals[0] = action;
          vals[1] = subjAct;   
          vals[2] = subjectViewRowSpan; 
          
          createInfoObject(dom, SubjActionInfoCls, vals, reportObj, actionInfs, oids);
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
   *  count number of {@link SubjectAction} of each {@link SubjectBySemester} in <tt>subjActions</tt> and 
   *  add an entry to a {@link Map} to reflect that.
   *  
   *  Return this map.
   */
  private Map<SubjectBySemester, Integer> countSubjectActsBySubject(Collection<SubjectAction> subjActions) {
    Integer count;
    SubjectBySemester subj, subj1;
    Map<SubjectBySemester, Integer> countMap = new HashMap<>();
    for (SubjectAction sa: subjActions) {
      subj = sa.getSubject();
      count = countMap.get(subj);
      
      if (count == null) { // not yet counted
        count = 1;
        for (SubjectAction sa1: subjActions) {
          subj1 = sa1.getSubject();
          if (!sa1.equals(sa) && subj.equals(subj1)) {
            // update count
            count++;
          }
        }
        
        countMap.put(subj, count);
      }
    }
    
    return countMap;
  }

}
