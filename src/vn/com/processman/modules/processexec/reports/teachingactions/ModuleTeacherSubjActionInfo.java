package vn.com.processman.modules.processexec.reports.teachingactions;

import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processexec.reports.teachingactions.model.TeacherSubjActionInfo;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
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
      ,width=5, height=MetaConstants.STANDARD_FIELD_HEIGHT)
  private SubjectAction subjectAction;

  @AttributeDesc(label="Môn học"
      ,alignX=AlignmentX.Center
      ,width=25, height=MetaConstants.STANDARD_FIELD_HEIGHT)
  private String subjectCode;

  @AttributeDesc(label="Người thực hiện"
      ,alignX=AlignmentX.Center
      ,width=25, height=MetaConstants.STANDARD_FIELD_HEIGHT)
  private String userName;
  
//  @AttributeDesc(label="Nhiệm vụ")
//  private String taskInfo;

  @AttributeDesc(label="Kết quả",
      type=JTextField.class
      ,alignX=AlignmentX.Center
      ,width=5,height=MetaConstants.STANDARD_FIELD_HEIGHT
      )
  private StatusCode status;
  
//
//  @AttributeDesc(label="Kết quả NV",alignX=AlignmentX.Center)
//  private StatusCode taskStatus;

}
