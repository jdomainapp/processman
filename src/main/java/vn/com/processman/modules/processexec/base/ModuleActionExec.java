package vn.com.processman.modules.processexec.base;

import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.Child;
import jda.modules.mccl.syntax.containment.SubTree1L;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.processdeliverables.ModuleFileWrapper;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processsconstraint.ModuleBooleanExpression;
import vn.com.processman.modules.processstructure.ModuleAction;
import vn.com.processman.modules.processstructure.model.Action;

/**
 * @overview 
 *  Represent module for <b>performing</b> {@link Action}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleActionExec",
modelDesc=@ModelDesc(
    model=Action.class,
    editable=false
),
viewDesc=@ViewDesc(
    formTitle="Thực hiện bước làm",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formAction.png",
    domainClassLabel="Bước làm",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    //no menu item: to be invoked from code only
    // parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy = OpenPolicy.I_C,
    isDataFieldStateListener=true
),containmentTree=@CTree(
    root=Action.class
    ,stateScope=   
    /**The attributes needed to display essential information about the state of an object*/
    {Action.A_id, Action.A_codeDef, Action.A_nameDef, 
      Action.A_preConds,
      Action.A_descriptionDef,  
      Action.A_output, 
      Action.A_postConds,
      Action.A_status}
    ,subtrees={
      @SubTree1L(
          parent=Action.class
          ,children={
            @Child(cname=FileWrapper.class, 
                scope={},
                scopeDef=".ScopeDefFileExec"
            )
          }
      )
    }
)
, childModules= {
  ModuleBooleanExpression.class,
  ModuleFileWrapper.class,
}
,isPrimary=false
,isViewer=false
)
public class ModuleActionExec extends ModuleAction {
//  @AttributeDesc(label="Bước làm")
//  private String title;
}
