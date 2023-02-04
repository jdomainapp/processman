package vn.com.processman.modules.processexec.base;

import vn.com.processman.modules.processdeliverables.ModuleFileWrapper;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processsconstraint.ModuleBooleanExpression;
import vn.com.processman.modules.processstructure.ModuleAction;
import vn.com.processman.modules.processstructure.model.Action;
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
import domainapp.core.Controller;
import domainapp.view.layout.TwoColumnLayoutBuilder;

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
    viewType=Region.Type.Data,
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
