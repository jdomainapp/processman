package vn.com.processman.modules.processstructure;

import java.util.Collection;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
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
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;
import vn.com.processman.modules.dsecurity.ModuleDomainRole;
import vn.com.processman.modules.dsecurity.ModuleRolePerfProcess;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processstructure.controller.command.OpenAllowedTasksCommand;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.ProcessType;
import vn.com.processman.modules.processstructure.model.Task;

/**
 * @overview 
 *  Represent module for {@link Process}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcess",
modelDesc=@ModelDesc(
    model=Process.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý quy trình",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcess.png",
    domainClassLabel="Quy trình",
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
)
,childModules={
  ModuleTask.class, ModuleTask4Subject.class, ModuleDomainRole.class, ModuleRolePerfProcess.class, 
}
,containmentTree=@CTree(
    root=Process.class,
    stateScope=   
    /**The attributes needed to display essential information about the state of a {@link Process} object*/
    {Process.A_id, Process.A_code, Process.A_name, Process.A_type, Process.A_description, Process.A_def}
)
,isPrimary=true 
)
public class ModuleProcess {
  @AttributeDesc(label="Quy trình")
  private String title;

  @AttributeDesc(label="Mã dữ liệu",
      alignX=AlignmentX.Center)
  private int id;
  
  @AttributeDesc(label="Mã nghiệp vụ",
    alignX=AlignmentX.Center)
  private String code;
  
  @AttributeDesc(label="Tên")
  private String name;

  @AttributeDesc(label="Loại",  
      type=JComboField.class,
      //width=100, height=25,
      isStateEventSource=true,
      alignX=AlignmentX.Center)
  private ProcessType type;
  
  @AttributeDesc(label="Mô tả")
  private String description;

  @AttributeDesc(label="Quy trình gốc", type=JComboField.class, 
      ref=@Select(clazz=Process.class,attributes={Process.A_code})
      ,isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private Process def;
  
  @AttributeDesc(label="Tạo bởi", type=JComboField.class, 
      ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
      ,isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private OrgUnit createBy;
  
  /// END: essential attributes 
  @AttributeDesc(label="Nhiệm vụ",
      type=DefaultPanel.class
      ,styleLabel=StyleName.Heading4DarkYellow
      ,styleField=StyleName.DefaultOnYellow
      ,layoutBuilderType=TwoColumnLayoutBuilder.class //TabLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.L_C
          ,props={  // v3.3: Open with Tasks that user is allowed to perform
              // default data controller command: "Open"
              @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
              // customised Open command
              ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenAllowedTasksCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
          }          
      )
  )
  private Collection<Task> tasks;
  
  @AttributeDesc(label="Mã nghiệp vụ",
      alignX=AlignmentX.Center)
  private String codeDef;
  
  @AttributeDesc(label="Tên",
      alignX=AlignmentX.Center)
  private String nameDef;
  
  @AttributeDesc(label="Mô tả",
      alignX=AlignmentX.Center)
  private String descriptionDef;
}
