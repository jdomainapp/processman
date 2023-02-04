package vn.com.processman.modules.processstructure;

import java.util.Collection;

import vn.com.processman.modules.processstructure.controller.command.OnCreateActionCommand;
import vn.com.processman.modules.processstructure.controller.command.OnDeleteActionCommand;
import vn.com.processman.modules.processstructure.controller.command.OnUpdateActionCommand;
import vn.com.processman.modules.processstructure.controller.command.OpenAllowedActionsCommand;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
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
import domainapp.basics.view.datafields.JTextField;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.basics.view.panels.DefaultPanel;
import domainapp.core.Controller;
import domainapp.view.layout.TwoColumnLayoutBuilder;

/**
 * @overview 
 *  Represent module for {@link Task}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTask",
modelDesc=@ModelDesc(
    model=Task.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý nhiệm vụ",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formTask.png",
    domainClassLabel="Nhiệm vụ",
    viewType=Region.Type.Data,
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
)
,containmentTree=@CTree(
    root=Task.class,
    stateScope=   
    /**The attributes needed to display essential information about the state of a {@link Task} object*/
    {Task.A_id, Task.A_code, Task.A_name, Task.A_description, Task.A_def, Task.A_prev, Task.A_status}
)
, childModules = {
  ModuleAction.class
}
,isPrimary=true
)
public class ModuleTask {
  @AttributeDesc(label="Nhiệm vụ")
  private String title;

  @AttributeDesc(label="Mã dữ liệu",
      alignX=AlignmentX.Center)
  private int id;

  @AttributeDesc(label="Mã dữ liệu", type=JTextField.class 
      ,ref=@Select(clazz=Task.class,attributes={Task.A_id})
//      ,modelDesc=@ModelDesc(
//          model=Task.class,
//          dataSourceType=JFlexiDataSource.class
//      )
      , alignX=AlignmentX.Center
  )
  private Task self;

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
  
  @AttributeDesc(label="Mô tả")
  private String description;

  @AttributeDesc(label="Mô tả",
      alignX=AlignmentX.Center)
  private String descriptionDef;

  @AttributeDesc(label="Tuần bắt đầu",
      alignX=AlignmentX.Center)
  private Integer startWeek;

  @AttributeDesc(label="Tuần kết thúc",
      alignX=AlignmentX.Center)
  private Integer endWeek;

  @AttributeDesc(label="Quy trình", type=JComboField.class, 
      ref=@Select(clazz=Process.class,attributes={Process.A_code}))
  private Process process;
  
  @AttributeDesc(label="Nhiệm vụ gốc", type=JComboField.class, 
      ref=@Select(clazz=Task.class,attributes={Task.A_code})
      ,isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private Task def;
  
  @AttributeDesc(label="Nhiệm vụ liền trước", type=JComboField.class, 
      ref=@Select(clazz=Task.class,attributes={Task.A_code})
      ,isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private Task prev;
  
  @AttributeDesc(label="Tình trạng", editable=false, 
      type=JComboField.class,
      //width=100, height=25,
      isStateEventSource=true,
      alignX=AlignmentX.Center)
  private StatusCode status;
  
  /// END: essential attributes 
  
  @AttributeDesc(label="Bước làm"
      /* use this configuration for detailed view */
      , type=DefaultPanel.class
      ,styleLabel=StyleName.Heading4DarkYellow
      ,styleField=StyleName.DefaultOnLightGray
      ,layoutBuilderType=TwoColumnLayoutBuilder.class //TabLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.L_C
          // v3.3: customise post-update to update link for Role-Process if role is changed
          // and Open with Actions that user is allowed to perform
          ,props={ 
            // default data controller command: "Open"
            @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
            // customised Open command
            ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenAllowedActionsCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue)              
            ,@PropertyDesc(name=PropertyName.controller_dataController_onCreateObject,valueIsClass=OnCreateActionCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue) 
            ,@PropertyDesc(name=PropertyName.controller_dataController_onUpdateObject,valueIsClass=OnUpdateActionCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue) 
            ,@PropertyDesc(name=PropertyName.controller_dataController_onDeleteObject,valueIsClass=OnDeleteActionCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue) 
          }
      )
      /*use this configuration for brief view (a view through which user can click on an action and view it on a separate viewer)
      , type=JObjectTable.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O
      )*/
  )
  private Collection<Action> actions;
  
}
