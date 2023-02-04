package vn.com.processman.modules.processexec.reports.subjectactions.model;

import java.util.Collection;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processexec.reports.teachingactions.model.ReportTeachingActions;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.util.model.Semester;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.modules.report.model.meta.Output;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;

/**
 * @overview
 *  A sub-type of {@link ReportTeachingActions} that represents a report about {@link Subject} and their {@link SubjectAction}s. 
 *  
 * @author dmle
 *
 * @version 1.2
 */
@DClass(serialisable=false)
public class ReportSubjectActions extends ReportTeachingActions {

  public static final String A_subject = "subject";
  public static final String A_subjectInfo = "subjectInfo";

  @DAttr(name=A_subject,type=Type.Domain)
  private Subject subject;

  /**derived from {@link #subject}*/
  @DAttr(name=A_subjectInfo, type=DAttr.Type.String, length=35, mutable=false)
  private String subjectInfo;
  
  /** shadows the super-type's output attribute to specialise for {@link SubjectActionInfo}
   */
  @Output(filter=ReportSubjectActionsFilter.class, outputClass=SubjectActionInfo.class)
  @DAttr(name=A_userActionInfos,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=SubjectActionInfo.class),auto=true)
  @DAssoc(ascName="reportSubjectActions-and-subjectActionInfo",ascType=AssocType.One2Many,
    endType=AssocEndType.One,role="reportSubjectActions",
    associate=@Associate(type=SubjectActionInfo.class,cardMin=0,cardMax=MetaConstants.CARD_MORE))
  private Collection<SubjectActionInfo> userActionInfos;
  
  public ReportSubjectActions(OrgUnit orgUnit,
      //omitted: String userLogin,
      Semester semester, Integer year
      ,Subject subject
      ) {
    super(orgUnit,
        //userLogin,
        null,
        semester, year);
    
    this.subject = subject;
    
    updateSubjectInfo(subject);
  }

  private void updateSubjectInfo(Subject subject) {
    if (subject != null) {
      subjectInfo = subject.getName() + " (" + subject.getCode() + ")";
    } else {
      subjectInfo = null;
    }
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
    
    updateSubjectInfo(subject);
  }

  public String getSubjectInfo() {
    return subjectInfo;
  }
}
