package vn.com.processman.modules.processexec.forsubject;

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
          valueIsClass=MouseClickOnTaskAndActionHelperCommand.class, valueAsString=CommonConstants.NullValue,
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
