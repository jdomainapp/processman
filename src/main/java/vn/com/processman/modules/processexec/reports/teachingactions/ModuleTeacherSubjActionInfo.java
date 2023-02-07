package vn.com.processman.modules.processexec.reports.teachingactions;

import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.assets.datafields.JTextField;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processexec.reports.teachingactions.model.TeacherSubjActionInfo;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview
 *  Module of {@link TeacherSubjActionInfo}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTeacherSubjActionInfo",
modelDesc=@ModelDesc(
    model=TeacherSubjActionInfo.class
),
viewDesc=@ViewDesc(
    on=false,
    domainClassLabel="Thông tin bước làm giảng dạy",
    formTitle="Quản lí thông tin bước làm giảng dạy"
    // no view
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
),
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleTeacherSubjActionInfo {
//  @AttributeDesc(label="Hồ sơ tuyển sinh")
//  private String title;

  @AttributeDesc(label="STT",alignX=AlignmentX.Center)
  private int index;

  @AttributeDesc(label="Tên đầu việc")
  private String actionInfo;

  @AttributeDesc(label="Mã việc-môn"
      ,type=JTextField.class
      ,editable=false
      ,alignX=AlignmentX.Center
      ,width=5, height=MCCLConstants.STANDARD_FIELD_HEIGHT)
  private SubjectAction subjectAction;

  @AttributeDesc(label="Môn học"
      ,alignX=AlignmentX.Center
      ,width=25, height=MCCLConstants.STANDARD_FIELD_HEIGHT)
  private String subjectCode;

  @AttributeDesc(label="Người thực hiện"
      ,alignX=AlignmentX.Center
      ,width=25, height=MCCLConstants.STANDARD_FIELD_HEIGHT)
  private String userName;
  
//  @AttributeDesc(label="Nhiệm vụ")
//  private String taskInfo;

  @AttributeDesc(label="Kết quả",
      type=JTextField.class
      ,alignX=AlignmentX.Center
      ,width=5,height=MCCLConstants.STANDARD_FIELD_HEIGHT
      )
  private StatusCode status;
  
//
//  @AttributeDesc(label="Kết quả NV",alignX=AlignmentX.Center)
//  private StatusCode taskStatus;

}
