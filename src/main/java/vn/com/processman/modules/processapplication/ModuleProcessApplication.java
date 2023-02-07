package vn.com.processman.modules.processapplication;

import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.assets.datafields.JCounterField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.util.model.Semester;

/**
 * @overview 
 *  Represent module for {@link ProcessApplication}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcessApplication",
modelDesc=@ModelDesc(
    model=ProcessApplication.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý áp dụng quy trình",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessApplication.png",
    domainClassLabel="Áp dụng quy trình"
    // no view
//    viewType=Region.Type.Data,
//    view=View.class,
//    parent=RegionName.Tools,
//    topContainerType=JObjectTable.class,
//    topX=0.5,    
//    topY=0.5
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true,
    openPolicy=OpenPolicy.O_C
),
childModules={
 ModuleProcess.class 
}
,isPrimary=true 
)
public class ModuleProcessApplication {
//  @AttributeDesc()
//  private String title;
  
  @AttributeDesc(label="Đ.vị tổ chức", type=JComboField.class
      ,isStateEventSource=true
      ,ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
  )
  private OrgUnit orgUnit;

//  @AttributeDesc(label="Quy trình", 
//      type=JComboField.class
//      ,ref=@Select(clazz=Process.class,attributes={Process.A_code})
//      ,isStateEventSource=true
//      //,width=100, height=25,
//      ,alignX=AlignmentX.Center
//  )
//  private Process process;
  
  @AttributeDesc(label="Học kỳ", 
      type=JComboField.class,
      isStateEventSource=true,
      //width=100, height=25,
      alignX=AlignmentX.Center)
  private Semester semester;
  
  @AttributeDesc(label="Năm học"
      ,type=JCounterField.class
      ,isStateEventSource=true
      ,alignX=AlignmentX.Center)
  private Integer year;
  
  // END: essential attributes
  
  @AttributeDesc(label="Quy trình", 
      /* use this configuration for combo-box
        type=JComboField.class
      ,ref=@Select(clazz=Process.class,attributes={Process.A_code})
      ,isStateEventSource=true
      //,width=100, height=25,
      ,alignX=AlignmentX.Center
      */
      /* use this configuration for nested form 
       */
      type=DefaultPanel.class
      ,layoutBuilderType=TabLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(openPolicy=OpenPolicy.O_C)
  )
  private Process process;

}
