package vn.com.processman.modules.processexec.reports.useractions;

import vn.com.processman.modules.processexec.reports.useractions.model.UserActionInfo;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.SetUpDesc;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.view.datafields.JTextField;
import domainapp.core.Controller;
import domainapp.setup.commands.CopyResourceFilesCommand;

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
//      ,width=5, height=MetaConstants.STANDARD_FIELD_HEIGHT)
//  private Action action;

  @AttributeDesc(label="Tên đầu việc")
  private String actionInfo;
  
  @AttributeDesc(label="Người thực hiện"
      ,alignX=AlignmentX.Center
      ,width=25, height=MetaConstants.STANDARD_FIELD_HEIGHT)
  private String userName;

  @AttributeDesc(label="Kết quả",
      type=JTextField.class
      ,alignX=AlignmentX.Center
      ,width=5,height=MetaConstants.STANDARD_FIELD_HEIGHT
      )
  private StatusCode actionStatus;
  
//
//  @AttributeDesc(label="Kết quả NV",alignX=AlignmentX.Center)
//  private StatusCode taskStatus;

}
