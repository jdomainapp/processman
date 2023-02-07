package vn.com.processman.modules.processexec.reports.subjectactions.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processexec.reports.teachingactions.model.TeacherSubjActionInfo;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.teaching.model.Subject;

/**
 * @overview
 *  A sub-type of {@link TeacherSubjActionInfo} that provides extra information relating to the {@link Subject}s that are
 *  involved in the action. 
 *  
 * @author dmle
 *
 * @version  1.2
 */
@DClass(serialisable=false)
public class SubjectActionInfo extends TeacherSubjActionInfo {
  
  /** the number of rows that value of attribute {@link #subjectCode} will span */
  @DAttr(name="subjectViewRowSpan", type=DAttr.Type.Integer, mutable=false)  
  private int subjectViewRowSpan;
  
  /**
   * @effects 
   * 
   */
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public SubjectActionInfo(Action4Subject action, SubjectAction subjectAction, Integer subjectViewRowSpan) {
    super(
        //user,
        null,
        action, subjectAction, 
        //actViewRowSpan,
        0,
        //userViewRowSpan
        0
        );
    
    this.subjectViewRowSpan = subjectViewRowSpan;
    
    //generateOutput();
  }
  
  private void generateOutput() {
    // nothing new
  }

  public int getSubjectViewRowSpan() {
    return subjectViewRowSpan;
  }


  /**
   * This method is used by embedded scripts of the export document 
   */
  public boolean isSubjectGroupStart() {
    return subjectViewRowSpan > 0;
  }
  
  // IMPORTANT: needed to refresh the derived attributes of this when the referenced objects have been changed
  @DOpt(type=DOpt.Type.ObjectStateRefresher)
  public void refresh() {
    super.refresh();
    
    generateOutput();
  }
}
