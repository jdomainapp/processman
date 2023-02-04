package vn.com.processman.modules.processexec.base;

import vn.com.processman.modules.processexec.base.controller.command.MouseClickOnTaskAndActionHelperCommand;
import vn.com.processman.modules.processstructure.ModuleTask4Subject;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Task4Subject;
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
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.core.Controller;
import domainapp.view.layout.TabLayoutBuilder;

/**
 * @overview 
 *  Represent module for {@link Task4Subject}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTask4SubjectExecBase",
modelDesc=@ModelDesc(
    model=Task4Subject.class,
    editable=false
),
viewDesc=@ViewDesc(
    formTitle="Thực hiện nhiệm vụ môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formTask4Subject.png",
    domainClassLabel="Nhiệm vụ môn học"
    ,viewType=Region.Type.Data,
    view=View.class,
    layoutBuilderType=TabLayoutBuilder.class,
    // no menu item:
    // parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
    ,openPolicy=OpenPolicy.I_C
    ,isDataFieldStateListener=true        
    ,props={
    // custom helper command to handle mouse-click actions on referenced objects of this module and of the descendant modules
    @PropertyDesc(name=PropertyName.controller_dataController_helperMouseClickOnReferencedObject,
        valueIsClass=MouseClickOnTaskAndActionHelperCommand.class, valueAsString=MetaConstants.NullValue,
        valueType=Class.class)
    }
)
,containmentTree=@CTree(
    root=Task4Subject.class
    ,stateScope= {Task4Subject.A_id, Task4Subject.A_codeDef, Task4Subject.A_nameDef, 
      Task4Subject.A_descriptionDef, Task4Subject.A_status, Task4Subject.A_actions, 
      Task4Subject.A_subjectTasks
      }
    ,subtrees={
      @SubTree1L(
          parent=Task4Subject.class,
          children={
            @Child(cname=Action.class,
                //scope={Action.A_self, Action.A_codeDef, Action.A_nameDef}
                // extended scope def
                scope={},
                scopeDef=".ScopeDefActionExec"                
            )
          }
      ),
    }
)
,isPrimary=true 
)
public class ModuleTask4SubjectExecBase extends ModuleTask4Subject {
//  @AttributeDesc(label="Nhiệm vụ môn học")
//  private String title;
}
