package vn.com.processman.modules.dsecurity;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.role.ModuleRoleViewer;
import jda.modules.security.userrole.ModuleUserRole;
import jda.mosa.controller.Controller;
import vn.com.processman.modules.dsecurity.model.Role;
import vn.com.processman.modules.processstructure.model.Action;

/**
 * @overview
 *  A module for managing the assignment of {@link Role}s to {@link Action}s.
 *  
 * @author dmle
 * @version 3.3
 */
@ModuleDescriptor(
name="ModuleDomainRole",
modelDesc=@ModelDesc(
    model=Role.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Gán Vai trò & Bước làm",
    formTitle="Quản lý Gán Vai trò và Bước làm", 
    imageIcon="frmRole.png"
    /* no view
    ,viewType=Type.Data,
    parent=RegionName.Tools,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    view=View.class,
    topX=0.5,topY=0.5
    */
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true,
    openPolicy=OpenPolicy.O_A
    ),
isPrimary=true,
childModules={ModuleUserRole.class, ModuleRoleViewer.class}
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleDomainRole {
//  @AttributeDesc(label="Gán Vai trò & Bước làm")
//  private String title;
//
//  @AttributeDesc(label="Tên vai trò",editable=false,
//      alignX=AlignmentX.Center)
//  private String name;
//
//  @AttributeDesc(label="Mô tả",editable=false)
//  private String description;
//  
//  @AttributeDesc(label="Các bước làm <br>được thực hiện"
//      ,type=JListField.class
//      ,modelDesc=@ModelDesc(model=Action.class)
//      ,ref=@Select(clazz=Action.class,attributes={Action.A_name})
//      ,isStateEventSource=true
//      ,width=300,height=10
//  )
//  private Collection<Action> actions;

}