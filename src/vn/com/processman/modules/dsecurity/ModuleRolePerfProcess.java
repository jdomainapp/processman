package vn.com.processman.modules.dsecurity;

import vn.com.processman.modules.dsecurity.model.RolePerfProcess;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.core.Controller;

/**
 * @overview
 *  A module used for manipulating {@link RolePerfProcess} (without view) 
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleRolePerfProcess",
modelDesc=@ModelDesc(
    model=RolePerfProcess.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Vai trò và quy trình",
    formTitle="Quản lý vai trò thực thi quy trình", 
    imageIcon="formRolePerfProcess.png"
//    ,viewType=Type.Data,
//    parent=RegionName.Tools,
//    view=View.class,
//    layoutBuilderType=TwoColumnLayoutBuilder.class,
//    topX=0.5,topY=0.0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
//    ,openPolicy=OpenPolicy.I_C
//    ,isDataFieldStateListener=true  // listens to state change event of list field
),
isPrimary=true
,isViewer=false
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleRolePerfProcess {
  //
  //  @AttributeDesc(label="Giảng viên")
  //  private String title;
}
