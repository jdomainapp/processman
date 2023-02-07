package vn.com.processman.modules.processexec.reports.useractions;

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
import vn.com.processman.modules.processexec.reports.useractions.model.UserActionInfo;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview
 *  Module of {@link UserActionInfo}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleUserActionInfo",
modelDesc=@ModelDesc(
    model=UserActionInfo.class
),
viewDesc=@ViewDesc(
    on=false,
    domainClassLabel="Thông tin bước làm",
    formTitle="Quản lí thông tin bước làm"
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
),
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleUserActionInfo {
//  @AttributeDesc(label="Hồ sơ tuyển sinh")
//  private String title;

  @AttributeDesc(label="STT",alignX=AlignmentX.Center)
  private int index;

  @AttributeDesc(label="Nhiệm vụ")
  private String taskInfo;
  
//  @AttributeDesc(label="Mã đầu việc"
//      ,type=JTextField.class
//      ,editable=false
//      ,ref=@Select(clazz=Action.class,attributes={Action.A_codeDef})
//      ,alignX=AlignmentX.Center
//      ,width=5, height=MCCLConstants.STANDARD_FIELD_HEIGHT)
//  private Action action;

  @AttributeDesc(label="Tên đầu việc")
  private String actionInfo;
  
  @AttributeDesc(label="Người thực hiện"
      ,alignX=AlignmentX.Center
      ,width=25, height=MCCLConstants.STANDARD_FIELD_HEIGHT)
  private String userName;

  @AttributeDesc(label="Kết quả",
      type=JTextField.class
      ,alignX=AlignmentX.Center
      ,width=5,height=MCCLConstants.STANDARD_FIELD_HEIGHT
      )
  private StatusCode actionStatus;
  
//
//  @AttributeDesc(label="Kết quả NV",alignX=AlignmentX.Center)
//  private StatusCode taskStatus;

}
