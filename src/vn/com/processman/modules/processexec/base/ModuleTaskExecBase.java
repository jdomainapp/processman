package vn.com.processman.modules.processexec.base;

import vn.com.processman.modules.processexec.base.controller.command.MouseClickOnTaskAndActionHelperCommand;
import vn.com.processman.modules.processstructure.ModuleAction;
import vn.com.processman.modules.processstructure.ModuleTask;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Task;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.Child;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.containment.SubTree1L;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.core.Controller;
import domainapp.view.layout.TabLayoutBuilder;

/**
 * @overview 
 *  Represent module for {@link Task}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTaskExecBase",
modelDesc=@ModelDesc(
    model=Task.class,
    editable=false
),
viewDesc=@ViewDesc(
    formTitle="Thực hiện nhiệm vụ",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formTask.png",
    domainClassLabel="Nhiệm vụ",
    viewType=Region.Type.Data,
    view=View.class,
    layoutBuilderType=TabLayoutBuilder.class,
    // no menu item:
    // parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
    ,props={
    // custom helper command to handle mouse-click actions on referenced objects of this module and of the descendant modules
    @PropertyDesc(name=PropertyName.controller_dataController_helperMouseClickOnReferencedObject,
        valueIsClass=MouseClickOnTaskAndActionHelperCommand.class, valueAsString=MetaConstants.NullValue,
        valueType=Class.class)
    }
)
,containmentTree=@CTree(
    root=Task.class
    ,stateScope= {Task.A_id, Task.A_codeDef, Task.A_nameDef, 
      Task.A_descriptionDef, Task.A_status, Task.A_actions}   
    ,subtrees={
      @SubTree1L(
          parent=Task.class,
          children={
            @Child(cname=Action.class,
                //scope={Action.A_self, Action.A_codeDef, Action.A_nameDef }
                // extended scope def
                scope={},
                scopeDef=".ScopeDefActionExec"
              )
          }
      ),
    }
)
, childModules = {
  ModuleAction.class
}
,isPrimary=true
)
public class ModuleTaskExecBase extends ModuleTask {
  @AttributeDesc(label="Nhiệm vụ")
  private String title;
  
//  @AttributeDesc(label="Mã dữ liệu",
//      alignX=AlignmentX.Center)
//  private int id;
//
//  @AttributeDesc(label="Mã nghiệp vụ",
//      alignX=AlignmentX.Center)
//  private String codeDef;
//  
//  @AttributeDesc(label="Tên",
//      alignX=AlignmentX.Center)
//  private String nameDef;
//
//  @AttributeDesc(label="Mô tả",
//      alignX=AlignmentX.Center)
//  private String descriptionDef;
//  
//  @AttributeDesc(label="Tình trạng", 
//      type=JComboField.class,editable=false,
//      //width=100, height=25,
//      isStateEventSource=true,
//      alignX=AlignmentX.Center)
//  private StatusCode status;
//  
//  /// END: essential attributes 
//  
//  @AttributeDesc(label="Các bước làm"
//      /* use this configuration for detailed view 
//      , type=DefaultPanel.class
//      ,layoutBuilderType=TwoColumnLayoutBuilder.class
//      ,controllerDesc=@ControllerDesc(
//          openPolicy=OpenPolicy.L_C
//      )*/
//      , type=JObjectTable.class
//      ,controllerDesc=@ControllerDesc(
//          openPolicy=OpenPolicy.O
//      )
//  )
//  private Collection<Action> actions;
  
}
