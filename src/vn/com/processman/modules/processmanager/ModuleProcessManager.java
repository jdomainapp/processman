package vn.com.processman.modules.processmanager;

import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
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
 *  A module that is designed for managing {@link Process}, {@link ProcessApplication}, 
 *  {@link Task}s and {@link Action}s in general.
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcessManager",
modelDesc=@ModelDesc(
    model=Process.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý quy trình",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessManager.png",
    domainClassLabel="Quy trình",
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
)
,containmentTree=@CTree(
    root=Process.class
    //,stateScope={OrgUnit.A_id, OrgUnit.A_name, OrgUnit.A_processes}
//    ,subtrees={
//      @SubTree1L(
//          parent=OrgUnit.class,
//          children={
//            @Child(
//                cname=Process.class
//                /**{@link Process#A_EssentialCreateNews}*/
//                ,scope={
//                    Process.A_code, Process.A_name, Process.A_description, Process.A_createBy, Process.A_tasks
//                }
//            )
//          }
//      ),
    /**{@link Process#A_EssentialCreateNews}*/
    ,stateScope={
        Process.A_code, Process.A_name, Process.A_type, Process.A_description, Process.A_createBy, Process.A_tasks
    }
    ,subtrees={
      @SubTree1L(
          parent=Process.class,
          children={           
            @Child(cname=Task.class, 
              /**{@link Task#A_EssentialCreateNews}*/
              scope={
              Task.A_code, Task.A_name, Task.A_description, 
              Task.A_startWeek, Task.A_endWeek,
              Task.A_process,
              Task.A_prev, 
              Task.A_actions}
              ),
          }
      ),
      @SubTree1L(
          parent=Task.class,
          children={
            @Child(
              cname=Action.class    
              /**{@link Action#A_EssentialCreateNews}*/
              ,scope={
                  Action.A_code, Action.A_name, 
                  Action.A_description, Action.A_prev, 
                  Action.A_task, Action.A_role
                }
            )
          }
      )
    }
),childModules={
  ModuleProcess.class 
 },
isPrimary=true,
isViewer=false
)
public class ModuleProcessManager extends ModuleProcess {
  @AttributeDesc(label="Quy trình")
  private String title;
}
