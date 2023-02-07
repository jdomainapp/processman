package vn.com.processman.modules.processexec.reports.teachingactions.model;

import java.util.Collection;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.report.Output;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processexec.reports.useractions.model.ReportUserActions;
import vn.com.processman.util.model.Semester;

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
    associate=@Associate(type=TeacherSubjActionInfo.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private Collection<TeacherSubjActionInfo> userActionInfos;
  
  public ReportTeachingActions(OrgUnit orgUnit, String userLogin,
      Semester semester, Integer year) {
    super(orgUnit, userLogin, semester, year);
    // TODO Auto-generated constructor stub
  }

}
