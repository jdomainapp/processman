package vn.com.processman.modules.processexec.reports.teachingactions.model;

import java.util.Collection;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processexec.reports.useractions.model.ReportUserActions;
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
 *  A sub-type of {@link ReportUserActions} that provide extra information for teacher-typed users. 
 *  
 * @author dmle
 *
 * @version 1.2
 */
@DClass(serialisable=false)
public class ReportTeachingActions extends ReportUserActions {

  /** shadows the super-type's output attribute to specialise for {@link TeacherSubjActionInfo}
   */
  @Output(filter=ReportTeachingActionsFilter.class, outputClass=TeacherSubjActionInfo.class)
  @DAttr(name=A_userActionInfos,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=TeacherSubjActionInfo.class),auto=true)
  @DAssoc(ascName="reportTeacherActions-and-teacherActionInfo",ascType=AssocType.One2Many,
    endType=AssocEndType.One,role="reportTeacherActions",
    associate=@Associate(type=TeacherSubjActionInfo.class,cardMin=0,cardMax=MetaConstants.CARD_MORE))
  private Collection<TeacherSubjActionInfo> userActionInfos;
  
  public ReportTeachingActions(OrgUnit orgUnit, String userLogin,
      Semester semester, Integer year) {
    super(orgUnit, userLogin, semester, year);
    // TODO Auto-generated constructor stub
  }

}
