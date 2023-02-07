package vn.com.processman.modules.processexec.generic;

import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.Child;
import jda.modules.mccl.syntax.containment.SubTree1L;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processstructure.ModuleAction;
import vn.com.processman.modules.processstructure.ModuleTask;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Task;

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
    viewType=RegionType.Data,
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
