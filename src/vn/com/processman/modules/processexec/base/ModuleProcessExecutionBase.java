package vn.com.processman.modules.processexec.base;

import java.util.Collection;

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
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.Child;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.containment.SubTree1L;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.LAName;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.basics.view.datafields.JCounterField;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.basics.view.panels.DefaultPanel;
import domainapp.core.Controller;
import domainapp.view.layout.TabLayoutBuilder;
import domainapp.view.layout.TwoColumnLayoutBuilder;

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
    formTitle="Quản lý thực hiện quy trình tổng quát",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessExecution.png",
    domainClassLabel="Thực hiện quy trình tổng quát",
    viewType=Region.Type.Data,
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
          valueIsClass=MouseClickOnTaskAndActionHelperCommand.class, valueAsString=MetaConstants.NullValue,
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
  @AttributeDesc(label="Thực hiện quy trình tổng quát")
  private String title;
  
//  @AttributeDesc(label="Mã dữ liệu",
//      alignX=AlignmentX.Center)
//  private int id;

  @AttributeDesc(label="Đ.vị áp dụng"
      ,type=JComboField.class
      ,ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
      ,isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private OrgUnit orgUnit;
  
  @AttributeDesc(label="Tên quy trình <br>cần thực hiện <br>(v.d thi%)")
  private String processName;
  
  @AttributeDesc(label="Học kỳ", 
      type=JComboField.class,
      isStateEventSource=true,
      //width=100, height=25,
      alignX=AlignmentX.Center)
  private Semester semester;
  
  @AttributeDesc(label="Năm học"
      ,type=JCounterField.class
      ,isStateEventSource=true
      ,alignX=AlignmentX.Center)
  private Integer year;
  
  @AttributeDesc(label="Quy trình",
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
              ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenProcessesForExecutionCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue)
          }          
      )
    ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
    }
  )
  private Collection<Process> processes;
}
