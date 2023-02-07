package vn.com.processman.modules.processexec.base;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
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
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import vn.com.processman.modules.processexec.base.controller.command.MouseClickOnTaskAndActionHelperCommand;
import vn.com.processman.modules.processstructure.ModuleTask4Subject;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Task4Subject;

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
    ,viewType=RegionType.Data,
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
        valueIsClass=MouseClickOnTaskAndActionHelperCommand.class, valueAsString=CommonConstants.NullValue,
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
