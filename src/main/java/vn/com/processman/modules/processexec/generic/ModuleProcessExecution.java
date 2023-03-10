package vn.com.processman.modules.processexec.generic;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.view.RegionName;
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
import vn.com.processman.modules.processexec.base.ModuleProcessExecutionBase;
import vn.com.processman.modules.processexec.base.model.ProcessExecution;
import vn.com.processman.modules.processexec.generic.controller.command.MouseClickOnTaskHelperCommand;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;

/**
 * @overview 
 *  A sub-type of the generic module {@link ModuleProcessExecutionBase} for executing   
 *  all kinds of {@link Task}s and {@link Action}s which may or may not be associated to a particular subject.
 *  
 *  <p>The key difference between this module and {@link ModuleProcessExecutionBase} lies in that while {@link ModuleProcessExecutionBase} 
 *  has common view for both {@link Task} and {@link Task4Subject}, 
 *  this module has a specialised view for each of these, which makes it easier for user to operate by embedding 
 *  the corresponding sub-views for {@link Action} and {@link Action4Subject}. 
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcessExecution",
modelDesc=@ModelDesc(
    model=ProcessExecution.class
),
viewDesc=@ViewDesc(
    formTitle="Qu???n l?? th???c hi???n quy tr??nh",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessExecution.png",
    domainClassLabel="Th???c hi???n quy tr??nh",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TabLayoutBuilder.class, //TabLayoutBuilder.class,
    parent=RegionName.Tools
    ,topX=0.5,    
    topY=0
    ,widthRatio=0.5f, heightRatio=0.8f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
    ,props={
      // custom helper command to handle mouse-click actions on referenced objects of this module and of the descendant modules
      @PropertyDesc(name=PropertyName.controller_dataController_helperMouseClickOnReferencedObject,
          valueIsClass=MouseClickOnTaskHelperCommand.class, valueAsString=CommonConstants.NullValue,
          valueType=Class.class)
    }  
)
,containmentTree=@CTree(
    root=ProcessExecution.class
    ,subtrees={
      @SubTree1L(
          parent=ProcessExecution.class,
          children={
            @Child(cname=Process.class,
                //scope={Process.A_codeDef, Process.A_nameDef, Process.A_tasks}
                // extended scope definition 
                scope={},
                scopeDef=".ScopeDefProcess"
            )
          }
      ),
      @SubTree1L(
          parent=Process.class,
          children={
            @Child(cname=Task.class,
                //scope={Task.A_self, Task.A_codeDef, Task.A_nameDef, Task.A_process, Task.A_status}
                // extended scope definition 
                scope={},
                scopeDef=".ScopeDefTask"                
            )
          }
      ),
    }
),childModules={
  ModuleProcess.class 
 },
isPrimary=true
)
public class ModuleProcessExecution extends ModuleProcessExecutionBase {
  @AttributeDesc(label="Th???c hi???n quy tr??nh")
  private String title;
}
