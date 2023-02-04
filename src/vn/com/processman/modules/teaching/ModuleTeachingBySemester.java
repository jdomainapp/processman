package vn.com.processman.modules.teaching;

import vn.com.processman.modules.teaching.model.TeachingBySemester;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.core.Controller;

/**
 * @overview
 *  A base module used for manipulating {@link TeachingBySemester} (without view) 
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTeachingBySemester",
modelDesc=@ModelDesc(
    model=TeachingBySemester.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Giảng dạy theo học kỳ",
    formTitle="Quản lý giảng dạy theo học kỳ", 
    imageIcon="formTeachingBySemester.png"
//    ,viewType=Type.Data,
//    parent=RegionName.Tools,
//    view=View.class,
//    layoutBuilderType=TwoColumnLayoutBuilder.class,
//    topX=0.5,topY=0.0//widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
),
isPrimary=true
,isViewer=false
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleTeachingBySemester {
  //
  //  @AttributeDesc(label="Giảng dạy theo học kỳ")
  //  private String title;
}