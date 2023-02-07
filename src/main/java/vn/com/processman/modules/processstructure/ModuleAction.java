package vn.com.processman.modules.processstructure;

import java.util.Collection;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;
import jda.mosa.view.assets.tables.JObjectTable;
import vn.com.processman.modules.dsecurity.model.Role;
import vn.com.processman.modules.processdeliverables.ModuleFileWrapper;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processsconstraint.ModuleBooleanExpression;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import vn.com.processman.modules.processstructure.controller.command.OpenActionPostConditionsCommand;
import vn.com.processman.modules.processstructure.controller.command.OpenActionPreConditionsCommand;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.model.StatusCode;

/**
 * @overview 
 *  Represent module for {@link Action}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleAction",
modelDesc=@ModelDesc(
    model=Action.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý bước làm",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formAction.png",
    domainClassLabel="Bước làm",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    // no menu item
    //parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy = OpenPolicy.I_C,
    isDataFieldStateListener=true
    // uncomment these if role is editable on this view
//    // v3.3: customise post-update to update link for Role-Process if role is changed
//    ,props={ 
//      @PropertyDesc(name=PropertyName.controller_dataController_onCreateObject,valueIsClass=OnCreateActionCommand.class,valueType=Class.class,valueAsString=MCCLConstants.NullValue) 
//      ,@PropertyDesc(name=PropertyName.controller_dataController_onUpdateObject,valueIsClass=OnUpdateActionCommand.class,valueType=Class.class,valueAsString=MCCLConstants.NullValue) 
//      ,@PropertyDesc(name=PropertyName.controller_dataController_onDeleteObject,valueIsClass=OnDeleteActionCommand.class,valueType=Class.class,valueAsString=MCCLConstants.NullValue) 
//    }
)
,containmentTree=@CTree(
    root=Action.class,
    stateScope=   
    {Action.A_id, Action.A_code, Action.A_name, 
      //Action.A_preConds,
      Action.A_description, Action.A_def, Action.A_prev, Action.A_task, 
      //Action.A_output, 
      //Action.A_postConds,
      Action.A_status}
//    ,subtrees={
//      @SubTree1L(
//          parent=Action.class
//          ,children={
//            @Child(cname=File.class, scope={File.A_name, File.A_type, File.A_url})
//          }
//      )
//    }
)
, childModules= {
  ModuleBooleanExpression.class,
  ModuleFileWrapper.class,
}
,isPrimary=true 
)
public class ModuleAction {
  @AttributeDesc(label="Bước làm")
  private String title;

  @AttributeDesc(label="Mã dữ liệu",
      alignX=AlignmentX.Center)
  private int id;

  @AttributeDesc(label="Mã dữ liệu", type=JTextField.class 
      ,ref=@Select(clazz=Action.class,attributes={Action.A_id})
      , alignX=AlignmentX.Center
  )
  private Action self;

  @AttributeDesc(label="Mã nghiệp vụ",
      alignX=AlignmentX.Center)
  private String code;

  @AttributeDesc(label="Mã nghiệp vụ",
      alignX=AlignmentX.Center)
  private String codeDef;
  
  @AttributeDesc(label="Tên")
  private String name;

  @AttributeDesc(label="Tên",
      alignX=AlignmentX.Center)
  private String nameDef;
  
  @AttributeDesc(label="Điều kiện trước",
      type=JObjectTable.class, editable=false
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.L_A
          /**customise Open action to retrieve pre-conditions from the definitive Action (if needed).
           * this is required because of the way conditions are created in memory and added directly to Action. 
           * */
          ,props={
          // default data controller command: "Open"
          @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
          // customised Open command
          ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenActionPreConditionsCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
          }          
      )
//    ,props={
//      // auto-activate this sub-container 
//      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
//    }
  )
  private Collection<BooleanExpression> preConds;
  
  @AttributeDesc(label="Mô tả")
  private String description;

  @AttributeDesc(label="Mô tả",
      alignX=AlignmentX.Center)
  private String descriptionDef;

  @AttributeDesc(label="Bước gốc", type=JComboField.class, 
      ref=@Select(clazz=Action.class,attributes={Action.A_code})
      ,isStateEventSource=true
      )
  private Action def;

  @AttributeDesc(label="Bước liền trước", type=JComboField.class, 
      ref=@Select(clazz=Action.class,attributes={Action.A_code})
      ,isStateEventSource=true
      )
  private Action prev;
  
  @AttributeDesc(label="Nhiệm vụ", type=JComboField.class, 
      ref=@Select(clazz=Task.class,attributes={Task.A_code}))
  private Task task;

  @AttributeDesc(label="Tệp kết quả", 
      /* use this configuration for embedded object form */
      type=DefaultPanel.class
      ,layoutBuilderType=TwoColumnLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O
      )
//    ,props={
//      // auto-activate this sub-container 
//      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
//    }
      /*
       use this configuration for simple view
      type=JTextField.class, editable=false, 
      ref=@Select(clazz=FileWrapper.class, attributes={FileWrapper.A_name})
       */
  )
  private FileWrapper output;

  @AttributeDesc(label="Điều kiện sau",
      type=JObjectTable.class, editable=false
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.L_A
          /**customise Open action to retrieve post-conditions from the definitive Action (if needed).
           * this is required because of the way conditions are created in memory and added directly to Action. 
           * */
          ,props={
          // default data controller command: "Open"
          @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
          // customised Open command
          ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenActionPostConditionsCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
          }                    
      )
//    ,props={
//      // auto-activate this sub-container 
//      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
//    }
  )
  private Collection<BooleanExpression> postConds;

  @AttributeDesc(label="Tình trạng", 
      type=JComboField.class// editable=false
      //width=100, height=25,
      ,isStateEventSource=true
      /*use this configuration for simple display:
      type=JTextField.class */
      ,alignX=AlignmentX.Center)
  private StatusCode status;

  @AttributeDesc(label="Vai trò", 
      type=JComboField.class
      //width=100, height=25,
      ,isStateEventSource=true
      /*use this configuration for simple display:
      type=JTextField.class */
      ,alignX=AlignmentX.Center)
  private Role role;
  
}
