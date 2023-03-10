package vn.com.processman.modules.processexec.base;

import java.util.Collection;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
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
import jda.mosa.view.assets.datafields.JCounterField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processexec.base.controller.command.MouseClickOnTaskAndActionHelperCommand;
import vn.com.processman.modules.processexec.base.controller.command.OpenProcessesForExecutionCommand;
import vn.com.processman.modules.processexec.base.model.ProcessExecution;
import vn.com.processman.modules.processstructure.ModuleAction;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.ModuleTask;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.model.Semester;

/**
 * @overview 
 *  Represent a <b>generic</b> module for {@link ProcessExecution}. A general execution supports all  
 *  kinds of {@link Task}s and {@link Action}s which may or may not be associated to a particular subject.
 *  
 *  <p>Each type of {@link Task} or {@link Action} needs a module definition for its execution 
 *  which simply extends {@link ModuleTask} or {@link ModuleAction}. 
 *  Two examples of these are given in the package, 
 *  namely {@link ModuleTaskExecBase} and {@link ModuleTask4SubjectExecBase} which extends {@link ModuleTask}, 
 *  and {@link ModuleActionExec} and {@link ModuleAction4SubjectExec} which extends {@link ModuleAction}.   
 *    
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcessExecutionBase",
modelDesc=@ModelDesc(
    model=ProcessExecution.class
),
viewDesc=@ViewDesc(
    formTitle="Qu???n l?? th???c hi???n quy tr??nh t???ng qu??t",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessExecution.png",
    domainClassLabel="Th???c hi???n quy tr??nh t???ng qu??t",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TabLayoutBuilder.class, //TabLayoutBuilder.class,
    parent=RegionName.Tools
    ,topX=0.5,    
    topY=0
    ,widthRatio=0.9f, heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
    ,props={
      // custom helper command to handle mouse-click actions on referenced objects of this module and of the descendant modules
      @PropertyDesc(name=PropertyName.controller_dataController_helperMouseClickOnReferencedObject,
          valueIsClass=MouseClickOnTaskAndActionHelperCommand.class, valueAsString=CommonConstants.NullValue,
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
public class ModuleProcessExecutionBase {
  @AttributeDesc(label="Th???c hi???n quy tr??nh t???ng qu??t")
  private String title;
  
//  @AttributeDesc(label="M?? d??? li???u",
//      alignX=AlignmentX.Center)
//  private int id;

  @AttributeDesc(label="??.v??? ??p d???ng"
      ,type=JComboField.class
      ,ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
      ,isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private OrgUnit orgUnit;
  
  @AttributeDesc(label="T??n quy tr??nh <br>c???n th???c hi???n <br>(v.d thi%)")
  private String processName;
  
  @AttributeDesc(label="H???c k???", 
      type=JComboField.class,
      isStateEventSource=true,
      //width=100, height=25,
      alignX=AlignmentX.Center)
  private Semester semester;
  
  @AttributeDesc(label="N??m h???c"
      ,type=JCounterField.class
      ,isStateEventSource=true
      ,alignX=AlignmentX.Center)
  private Integer year;
  
  @AttributeDesc(label="Quy tr??nh",
      type=DefaultPanel.class
      ,styleLabel=StyleName.Heading4DarkYellow
      ,styleField=StyleName.DefaultOnLightYellow
      ,layoutBuilderType=TwoColumnLayoutBuilder.class //TabLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O_C
          ,props={
              // default data controller command: "Open"
              @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
              // customised Open command
              ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenProcessesForExecutionCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
          }          
      )
    ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
    }
  )
  private Collection<Process> processes;
}
