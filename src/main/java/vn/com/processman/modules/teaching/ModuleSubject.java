package vn.com.processman.modules.teaching;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import vn.com.processman.modules.teaching.model.Subject;

/**
 * @overview 
 *  Represent module for {@link Subject}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleSubject",
modelDesc=@ModelDesc(
    model=Subject.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formSubject.png",
    domainClassLabel="Môn học",
    viewType=RegionType.Data,
    view=View.class,
    parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.O_A
),
isPrimary=true
//,childModules={ ModuleSubjectBySemester.class }
)
public class ModuleSubject {
  @AttributeDesc(label="Môn học")
  private String title;
  
//  @AttributeDesc(label="Mã dữ liệu")
//  private String id;
  
  @AttributeDesc(label="Mã môn học",
      width=10, // text field length
      height=25, 
      alignX=AlignmentX.Center
      )
  private String code;
  
  @AttributeDesc(label="Tên", 
      width=30, // text field length 
      height=25, 
      alignX=AlignmentX.Center)
  private String name;
}
