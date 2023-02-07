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
import jda.mosa.view.assets.panels.DefaultPanel;
import vn.com.processman.modules.processapplication.ModuleSubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processsconstraint.ModuleBooleanExpression;
import vn.com.processman.modules.processstructure.controller.command.OnSubjectActionSelectedCommand;
import vn.com.processman.modules.processstructure.controller.command.OpenAllowedSubjectActionsCommand;
import vn.com.processman.modules.processstructure.model.Action4Subject;

/**
 * @overview 
 *  Represent module for {@link Action4Subject}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleAction4Subject",
modelDesc=@ModelDesc(
    model=Action4Subject.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý bước làm môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formAction4Subject.png",
    domainClassLabel="Bước làm môn học",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    //parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
    // uncomment these if role is editable on this view
//  // v3.3: customise post-update to update link for Role-Process if role is changed
//  ,props={ 
//    @PropertyDesc(name=PropertyName.controller_dataController_onCreateObject,valueIsClass=OnCreateActionCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue) 
//    ,@PropertyDesc(name=PropertyName.controller_dataController_onUpdateObject,valueIsClass=OnUpdateActionCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue) 
//    ,@PropertyDesc(name=PropertyName.controller_dataController_onDeleteObject,valueIsClass=OnDeleteActionCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue) 
//  }    
)
,containmentTree=@CTree(
    root=Action4Subject.class,
    stateScope=   
    {Action4Subject.A_id, Action4Subject.A_code, Action4Subject.A_name, 
      //Action.A_preConds,
      Action4Subject.A_description, Action4Subject.A_def, Action4Subject.A_prev, Action4Subject.A_task,
      //Action4Subject.A_subjectActions,
      //Action.A_postConds,
      Action4Subject.A_status}
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
,
childModules={
  ModuleBooleanExpression.class,
  ModuleSubjectAction.class
},
isPrimary=true 
)
public class ModuleAction4Subject extends ModuleAction {
  @AttributeDesc(label="Bước làm môn học")
  private String title;

  /**
   * User selects a SubjectAction to initiate the command {@link OnSubjectActionSelectedCommand} which 
   * does two things: 
   *  make the selected SubjectAction object the *active* one in the parent Action object and 
   *  initiate the evaluation of the action for the selected subject and to update subjStatus of the SubjectAction 
   */
  @AttributeDesc(label="Áp dụng cho môn học",
      type=DefaultPanel.class
      ,layoutBuilderType=TwoColumnLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O_C
          // prop: update activeSubjectAction of Action4Subject when user selected an entry in this
          ,props={
              // default data controller command: "Open"
              @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
              // customised Open command
              ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenAllowedSubjectActionsCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)              
            // customised OnSetCurrentObject data controller command
            ,@PropertyDesc(name=PropertyName.controller_dataController_onSetCurrentObject,valueIsClass=OnSubjectActionSelectedCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
          }          
      )
      ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
      }
  )
  private Collection<SubjectAction> subjectActions;
}
