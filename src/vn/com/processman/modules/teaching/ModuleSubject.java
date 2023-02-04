package vn.com.processman.modules.teaching;

import vn.com.processman.modules.teaching.model.Subject;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.core.Controller;

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
    viewType=Region.Type.Data,
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
