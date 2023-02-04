package vn.com.processman.modules.dsecurity;

import vn.com.processman.modules.dsecurity.controller.command.CreateDomainUserHierarchyCommand;
import vn.com.processman.modules.dsecurity.controller.command.UpdateDomainUserHierarchyCommand;
import domainapp.basics.core.View;
import domainapp.basics.model.config.ApplicationModule.ModuleType;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Region.Type;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.security.DomainUser;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.core.Controller;
import domainapp.modules.security.role.ModuleRoleViewer;
import domainapp.modules.security.userrole.ModuleUserRole;
import domainapp.view.layout.TwoColumnLayoutBuilder;

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
    viewType=Type.Data,
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
          ,valueAsString=MetaConstants.NullValue,valueType=Class.class),
      // custom Update object command: to update {@link UserRole} from the roles
      @PropertyDesc(name=PropertyName.controller_dataController_update,
          valueIsClass=UpdateDomainUserHierarchyCommand.class//UpdateObjectAndManyAssociatesDataControllerCommand.class
          , valueAsString=MetaConstants.NullValue,valueType=Class.class)
    }
),
type=ModuleType.System,
childModules={ModuleUserRole.class, ModuleRoleViewer.class},
isPrimary=true
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleDomainUser extends domainapp.modules.security.domainuser.normalised.ModuleDomainUser {
  // inherits
}