package vn.com.processman.modules.processmanager.processapplication;

import java.util.Collection;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processmanager.processapplication.controller.command.CreateNewProcessApplicationCommand;
import vn.com.processman.modules.processmanager.processapplication.controller.command.OpenOnNewAppliedProcessCommand;
import vn.com.processman.modules.processmanager.processapplication.controller.command.OpenProcessApplicationsCommand;
import vn.com.processman.modules.processmanager.processapplication.model.ProcessApplicationManager;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task;
import vn.com.processman.util.model.Semester;
import domainapp.basics.controller.helper.objectbrowser.SingularIdPooledObjectBrowser;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.Child;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.containment.SubTree1L;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.LAName;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.basics.view.datafields.JCounterField;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.basics.view.panels.DefaultPanel;
import domainapp.core.Controller;
import domainapp.view.layout.TabLayoutBuilder;
import domainapp.view.layout.TwoColumnLayoutBuilder;

/**
 * @overview 
 *  A generic module that is designed for managing {@link ProcessApplication}s.
 *  
 *  <p>Sub-types of this are derived from this for managing application of specialised types of {@link Process}es.
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcessApplicationManager",
modelDesc=@ModelDesc(
    model=ProcessApplicationManager.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý áp dụng quy trình",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcessApplicationManager.png",
    domainClassLabel="Áp dụng quy trình"
    ,viewType=Region.Type.Data,
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
    root=ProcessApplicationManager.class
    ,subtrees={
      @SubTree1L(
          parent=ProcessApplicationManager.class,
          children={
            @Child(
                cname=Process.class
                ,scope={Process.A_id, Process.A_codeDef, Process.A_nameDef, Process.A_descriptionDef, Process.A_tasks}
            )
          }
      ),
      @SubTree1L(
          parent=Process.class,
          children={           
            @Child(cname=Task.class,  // a sub-type of Task
                scope={},
                scopeDef=".ScopeDefTaskApplication"
              ),
          }
      ),
      @SubTree1L(
          parent=Task.class,
          children={
            @Child(
              cname=Action.class    // a sub-type of Action
              ,scope={}
              ,scopeDef=".ScopeDefActionApplication"
            )
          }
      )
    }
) // end containment tree
,childModules={
  ModuleProcess.class 
 },
isPrimary=true
)
public class ModuleProcessApplicationManager {
  @AttributeDesc(label="Áp dụng quy trình")
  private String title;
  
//  @AttributeDesc(label="Mã dữ liệu",
//      alignX=AlignmentX.Center)
//  private int id;

  @AttributeDesc(label="Áp dụng cho"
      ,type=JComboField.class
      ,ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
      ,isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private OrgUnit orgUnit;
  
  @AttributeDesc(label="Mã đầy đủ quy trình<br> <b><u>không dành</u></b> cho môn học <br> cần áp dụng (mẫu: dt-XY)")
  private String processCode;
  
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
  
  @AttributeDesc(label="Quy trình",
      type=DefaultPanel.class
      ,styleLabel=StyleName.Heading4DarkYellow
      ,styleField=StyleName.DefaultOnLightYellow
      ,layoutBuilderType=TwoColumnLayoutBuilder.class //TabLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O_C
          // v1.2: single-id browser is needed for use with open-on-new command (below) to allow this sub-form
          // to avoiding openning all Process(es) when creating a new derived Process (this is because  
          // the association Process-ProcessApplication has updateLink=false)
          ,objectBrowser=SingularIdPooledObjectBrowser.class          
          ,props={
              @PropertyDesc(name=PropertyName.controller_dataControllerCommand,valueAsString="Open",valueType=LAName.class)
              ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenProcessApplicationsCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue)
              ,@PropertyDesc(name=PropertyName.controller_dataController_new,valueIsClass=CreateNewProcessApplicationCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue)
              ,@PropertyDesc(name=PropertyName.controller_dataController_openOnNew,valueIsClass=OpenOnNewAppliedProcessCommand.class,valueType=Class.class,valueAsString=MetaConstants.NullValue) // v1.2
          }          
      )
    ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
    }
  )
  private Collection<Process> processes;
}
