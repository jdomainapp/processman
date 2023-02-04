package vn.com.processman.modules.processexec.generic;

import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processstructure.ModuleAction;
import vn.com.processman.modules.processstructure.ModuleTask;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Task;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.Child;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.containment.SubTree1L;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.core.Controller;
import domainapp.view.layout.TabLayoutBuilder;

/**
 * @overview 
 *  Represent execution module for {@link Task} which embeds the {@link Action} module into it.
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTaskExec",
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
                scopeDef=".ScopeDefActionEmbeddedExec"
              )
          }
      ),
      @SubTree1L(
          parent=Action.class
          ,children={
            @Child(cname=FileWrapper.class, 
                //scope={File.A_name, File.A_type, File.A_url}
                scope={},
                scopeDef=".ScopeDefFileExec"
            )
          }
      )
    }
)
, childModules = {
  ModuleAction.class
}
,isPrimary=true
)
public class ModuleTaskExec extends ModuleTask {
  @AttributeDesc(label="Nhiệm vụ")
  private String title;
}
