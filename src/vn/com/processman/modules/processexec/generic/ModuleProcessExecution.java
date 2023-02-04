package vn.com.processman.modules.processexec.generic;

import vn.com.processman.modules.processexec.base.ModuleProcessExecutionBase;
import vn.com.processman.modules.processexec.base.model.ProcessExecution;
import vn.com.processman.modules.processexec.generic.controller.command.MouseClickOnTaskHelperCommand;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
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
    formTitle="Quản lý thực hiện quy trình",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessExecution.png",
    domainClassLabel="Thực hiện quy trình",
    viewType=Region.Type.Data,
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
          valueIsClass=MouseClickOnTaskHelperCommand.class, valueAsString=MetaConstants.NullValue,
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
  @AttributeDesc(label="Thực hiện quy trình")
  private String title;
}
