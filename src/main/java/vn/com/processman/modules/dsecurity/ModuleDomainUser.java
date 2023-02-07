package vn.com.processman.modules.dsecurity;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.def.DomainUser;
import jda.modules.security.role.ModuleRoleViewer;
import jda.modules.security.userrole.ModuleUserRole;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.dsecurity.controller.command.CreateDomainUserHierarchyCommand;
import vn.com.processman.modules.dsecurity.controller.command.UpdateDomainUserHierarchyCommand;

/**
 * @overview
 *  Extends {@link domainapp.modules.security.domainuser.ModuleDomainUser} to support creation of 
 *  specific sub-types. 
 *  
 * @author dmle
 *
 */
@ModuleDescriptor(
name="ModuleDomainUser",
modelDesc=@ModelDesc(
    model=DomainUser.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Người dùng",
    formTitle="Quản lý người dùng", 
    imageIcon="frmDomainUser.png",
    viewType=RegionType.Data,
    parent=RegionName.Tools,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    topX=0.5,topY=0.0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
    ,openPolicy=OpenPolicy.I_C
    ,isDataFieldStateListener=true  // listens to state change event of list field
    ,props={
      // custom Create object command: to create {@link UserRole} from the roles
      @PropertyDesc(name=PropertyName.controller_dataController_create,
          valueIsClass=CreateDomainUserHierarchyCommand.class//CreateObjectAndManyAssociatesDataControllerCommand.class 
          ,valueAsString=CommonConstants.NullValue,valueType=Class.class),
      // custom Update object command: to update {@link UserRole} from the roles
      @PropertyDesc(name=PropertyName.controller_dataController_update,
          valueIsClass=UpdateDomainUserHierarchyCommand.class//UpdateObjectAndManyAssociatesDataControllerCommand.class
          , valueAsString=CommonConstants.NullValue,valueType=Class.class)
    }
),
type=ModuleType.System,
childModules={ModuleUserRole.class, ModuleRoleViewer.class},
isPrimary=true
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleDomainUser extends jda.modules.security.domainuser.normalised.ModuleDomainUser {
  // inherits
}