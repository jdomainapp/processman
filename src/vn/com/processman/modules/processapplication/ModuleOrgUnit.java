package vn.com.processman.modules.processapplication;

import java.util.Collection;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Process;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.basics.view.panels.DefaultPanel;
import domainapp.core.Controller;
import domainapp.view.layout.TabLayoutBuilder;
import domainapp.view.layout.TwoColumnLayoutBuilder;

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
    viewType=Region.Type.Data,
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
