package vn.com.processman.modules.processexec.forsubject;

import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processexec.base.ModuleProcessExecutionBase;
import vn.com.processman.modules.processexec.base.controller.command.MouseClickOnTaskAndActionHelperCommand;
import vn.com.processman.modules.processexec.forsubject.model.ProcessExecution4Subject;
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
 *  A sub-type of {@link ModuleProcessExecutionBase} that is specifically for   
 *  {@link Task}s and {@link Action}s that are associated to a particular subject.
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcessExecution4Subject",
modelDesc=@ModelDesc(
    model=ProcessExecution4Subject.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý thực hiện quy trình gắn môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessExecution4Subject.png",
    domainClassLabel="Thực hiện quy trình gắn môn học",
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
          valueIsClass=MouseClickOnTaskAndActionHelperCommand.class, valueAsString=MetaConstants.NullValue,
          valueType=Class.class)
    }  
)
,containmentTree=@CTree(
    root=ProcessExecution4Subject.class
    ,subtrees={
      @SubTree1L(
          parent=ProcessExecution4Subject.class,
          children={
            @Child(
                cname=Process.class
                //extended scope definition: name of a constant object in the model class of this module
                ,scope={}
                ,scopeDef=".ScopeDefProcessExec"
            )
          }
      ),
      @SubTree1L(
          parent=Process.class,
          children={           
            @Child(cname=Task4Subject.class,  // a sub-type of Task
                //extended scope definition: name of a constant object in the model class of this module
                scope={},
                scopeDef=".ScopeDefTask4SubjectExec"
              ),
          }
      ),
      @SubTree1L(
          parent=Task4Subject.class,
          children={
            @Child(
              cname=Action4Subject.class    // a sub-type of Action
              //extended scope definition: name of a constant object in the model class of this module
              ,scope={}
              ,scopeDef=".ScopeDefAction4SubjectExec"
            )
          }
      ),
      @SubTree1L(
          parent=Task4Subject.class,
          children={
            @Child(
              cname=SubjectTask.class   
              //,scope= {SubjectTask.A_subject, SubjectTask.A_subjStatus}
              //extended scope definition: name of a constant object in the model class of this module
              ,scope={}
              ,scopeDef=".ScopeDefSubjectTaskExec"
              )
          }
      ),
      @SubTree1L(
          parent=Action4Subject.class,
          children={
            @Child(cname=SubjectAction.class
                //,scope={"*"}
                //extended scope definition: name of a constant object in the model class of this module
                ,scope={}
                ,scopeDef=".ScopeDefSubjectActionExec"
            )
          }
      ),
      @SubTree1L(
          parent=SubjectAction.class,
          children={
            @Child(cname=FileWrapper.class,
                //scope={File.A_name, File.A_type, File.A_url}
                scope={},
                scopeDef=".ScopeDefFileExec"
            )
          }
      ),
    }
),childModules={
  ModuleProcess.class 
 },
isPrimary=true
)
public class ModuleProcessExecution4Subject extends ModuleProcessExecutionBase {
  @AttributeDesc(label="Thực hiện quy trình gắn môn học")
  private String title;
}
