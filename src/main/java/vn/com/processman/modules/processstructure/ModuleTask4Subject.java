package vn.com.processman.modules.processstructure;

import java.util.Collection;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.processapplication.ModuleSubjectTask;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processstructure.controller.command.OnSubjectTaskSelectedCommand;
import vn.com.processman.modules.processstructure.controller.command.OpenAllowedSubjectTasksCommand;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;

/**
 * @overview 
 *  Represent module for {@link Task4Subject}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTask4Subject",
modelDesc=@ModelDesc(
    model=Task4Subject.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý nhiệm vụ môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formTask4Subject.png",
    domainClassLabel="Nhiệm vụ môn học"
    ,viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    //parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
    //,isDataFieldStateListener=true
)
,containmentTree=@CTree(
    root=Task4Subject.class,
    stateScope=   
      {Task.A_id, Task.A_code, Task.A_name, Task.A_description, Task.A_def, Task.A_prev, Task.A_status}
    /*,subtrees={
      @SubTree1L(
          parent=Action4Subject.class
          ,children={
            @Child(cname=SubjectAction.class, scope={"*"})
          }
      ),
      @SubTree1L(
          parent=SubjectAction.class
          ,children={
            @Child(cname=File.class, scope={File.A_name, File.A_type, File.A_url})
          }
      ),
    }*/
)
,childModules={ ModuleAction4Subject.class, ModuleSubjectTask.class }
,isPrimary=true 
)
public class ModuleTask4Subject extends ModuleTask { 
  @AttributeDesc(label="Nhiệm vụ môn học")
  private String title;
  
  /**
   * User selects a SubjectTask to initiate the command {@link OnSubjectTaskSelectedCommand} which 
   * does two things: 
   *  make the selected SubjectTask object the *active* one in the parent Task object and 
   *  initiate the evaluation of the task for the selected subject and to update subjStatus of the SubjectTask 
   */
  @AttributeDesc(label="Áp dụng cho môn học"
      //,type=DefaultPanel.class
      ,layoutBuilderType=TwoColumnLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O_C
          // prop: update activeSubjectTask of Task4Subject when user selected an entry in this
          ,props={
            // default data controller command: "Open"
            @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
            // customised Open command
            ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenAllowedSubjectTasksCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)              
            // customised OnSetCurrentObject data controller command
            ,@PropertyDesc(name=PropertyName.controller_dataController_onSetCurrentObject,valueIsClass=OnSubjectTaskSelectedCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
          }          
      )
      ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
      }
  )
  private Collection<SubjectTask> subjectTasks;
}
