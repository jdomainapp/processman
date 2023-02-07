package vn.com.processman.modules.processapplication;

import java.util.Collection;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.view.RegionName;
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
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Process;

/**
 * @overview 
 *  Represent module for {@link OrgUnit}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleOrgUnit",
modelDesc=@ModelDesc(
    model=OrgUnit.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý đơn vị tổ chức",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formOrgUnit.png",
    domainClassLabel="Đơn vị tổ chức",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    parent=RegionName.Tools,
    topX=0.5,    
    topY=0
    //,widthRatio=0.8f, heightRatio=0.8f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
)
,containmentTree=@CTree(
    root=Process.class,
    stateScope=   
    /**The attributes needed to display essential information about the state of an object*/
    {OrgUnit.A_id, OrgUnit.A_name}
),childModules={
  ModuleProcessApplication.class, 
  ModuleProcess.class
 },
isPrimary=true
)
public class ModuleOrgUnit {
  @AttributeDesc(label="Đơn vị tổ chức")
  private String title;
  
  @AttributeDesc(label="Mã dữ liệu",
      alignX=AlignmentX.Center)
  private int id;

  @AttributeDesc(label="Tên đơn vị")
  private String name;

//  @AttributeDesc(label="Tên đơn vị", type=JComboField.class 
//      ,ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
//      , alignX=AlignmentX.Center
//  )
//  private OrgUnit self;
  
  // process applications
  @AttributeDesc(label="Các quy trình đang áp dụng tại đ.vị",
      type=DefaultPanel.class,
      layoutBuilderType=TabLayoutBuilder.class,
      controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.I_C
      )
  )
  private Collection<ProcessApplication> processApplications;
  
  // process creations
  @AttributeDesc(label="Quy trình",
      type=DefaultPanel.class
      ,styleLabel=StyleName.Heading4DarkYellow
      ,styleField=StyleName.DefaultOnLightYellow
      ,layoutBuilderType=TwoColumnLayoutBuilder.class //TabLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.I_C
      )
    ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
    }
  )
  private Collection<Process> processes;
}
