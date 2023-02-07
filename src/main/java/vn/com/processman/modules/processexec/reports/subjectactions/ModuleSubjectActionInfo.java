package vn.com.processman.modules.processexec.reports.subjectactions;

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
import vn.com.processman.modules.processexec.reports.subjectactions.model.SubjectActionInfo;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview
 *  Module of {@link SubjectActionInfo}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleSubjectActionInfo",
modelDesc=@ModelDesc(
    model=SubjectActionInfo.class
),
viewDesc=@ViewDesc(
    on=false,
    domainClassLabel="Thông tin bước làm theo môn học",
    formTitle="Quản lí thông tin bước làm theo môn học"
    // no view
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
),
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleSubjectActionInfo {
//  @AttributeDesc(label="Hồ sơ tuyển sinh")
//  private String title;

  @AttributeDesc(label="STT",alignX=AlignmentX.Center)
  private int index;

  @AttributeDesc(label="Môn học"
      ,alignX=AlignmentX.Center
      ,width=25, height=MCCLConstants.STANDARD_FIELD_HEIGHT)
  private String subjectCode;

  @AttributeDesc(label="Tên đầu việc")
  private String actionInfo;

  @AttributeDesc(label="Kết quả",
      type=JTextField.class
      ,alignX=AlignmentX.Center
      ,width=5,height=MCCLConstants.STANDARD_FIELD_HEIGHT
      )
  private StatusCode status;

}
