package vn.com.processman.modules.dsecurity;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import vn.com.processman.modules.dsecurity.model.Teacher;

/**
 * @overview
 *  A base module used for manipulating {@link Teacher} (without view) 
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTeacher",
modelDesc=@ModelDesc(
    model=Teacher.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Giảng viên",
    formTitle="Quản lý giảng viên", 
    imageIcon="formTeacher.png"
//    ,viewType=Type.Data,
//    parent=RegionName.Tools,
//    view=View.class,
//    layoutBuilderType=TwoColumnLayoutBuilder.class,
//    topX=0.5,topY=0.0//widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
//    ,openPolicy=OpenPolicy.I_C
//    ,isDataFieldStateListener=true  // listens to state change event of list field
//    ,props={
//      // custom Create object command: to create {@link UserRole} from the roles
//      @PropertyDesc(name=PropertyName.controller_dataController_create,
//          valueIsClass=CreateObjectAndManyAssociatesDataControllerCommand.class, 
//          valueAsString=MetaConstants.NullValue,valueType=Class.class),
//      // custom Update object command: to update {@link UserRole} from the roles
//      @PropertyDesc(name=PropertyName.controller_dataController_update,
//          valueIsClass=UpdateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=MetaConstants.NullValue,
//          valueType=Class.class)
//    }
),
isPrimary=true
,isViewer=false
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleTeacherBasic extends ModuleDomainUser {
  //
//  @AttributeDesc(label="Giảng viên")
//  private String title;
}
