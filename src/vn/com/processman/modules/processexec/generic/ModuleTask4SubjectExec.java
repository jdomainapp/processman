package vn.com.processman.modules.processexec.generic;

import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processstructure.ModuleTask4Subject;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.Child;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.containment.SubTree1L;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.core.Controller;
import domainapp.view.layout.TabLayoutBuilder;

/**
 * @overview 
 *  Represent execution module for {@link Task4Subject} which embeds the {@link Action4Subject} module into it.
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTask4SubjectExec",
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
            @Child(
              cname=Action4Subject.class    // a sub-type of Action
              //extended scope definition: name of a constant object in the model class of this module
              ,scope={}
              ,scopeDef=".ScopeDefAction4SubjectEmbeddedExec"
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
                scope={}, 
                scopeDef = ".ScopeDefFileExec"
            )
          }
      ),
    }
)
,isPrimary=true 
)
public class ModuleTask4SubjectExec extends ModuleTask4Subject {
  @AttributeDesc(label="Nhiệm vụ môn học")
  private String title;
}
