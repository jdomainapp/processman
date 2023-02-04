package vn.com.processman.modules.processstructure;

import java.util.Collection;

import vn.com.processman.modules.processapplication.ModuleSubjectTask;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processstructure.controller.command.OnSubjectTaskSelectedCommand;
import vn.com.processman.modules.processstructure.controller.command.OpenAllowedSubjectTasksCommand;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.LAName;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.core.Controller;
import domainapp.view.layout.TwoColumnLayoutBuilder;

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
    ,viewType=Region.Type.Data,
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
            ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenAllowedSubjectTasksCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue)              
            // customised OnSetCurrentObject data controller command
            ,@PropertyDesc(name=PropertyName.controller_dataController_onSetCurrentObject,valueIsClass=OnSubjectTaskSelectedCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue)
          }          
      )
      ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
      }
  )
  private Collection<SubjectTask> subjectTasks;
}
