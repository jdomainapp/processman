package vn.com.processman.modules.processmanager.processapplication.forsubject;

import java.util.Collection;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.Child;
import jda.modules.mccl.syntax.containment.SubTree1L;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.helper.objectbrowser.SingularIdPooledObjectBrowser;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.list.JListField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processmanager.processapplication.ModuleProcessApplicationManager;
import vn.com.processman.modules.processmanager.processapplication.controller.command.OpenOnNewAppliedProcessCommand;
import vn.com.processman.modules.processmanager.processapplication.controller.command.OpenProcessApplicationsCommand;
import vn.com.processman.modules.processmanager.processapplication.forsubject.controller.command.CreateNewProcess4SubjectCommand;
import vn.com.processman.modules.processmanager.processapplication.forsubject.controller.command.UpdateProcess4SubjectApplicationCommand;
import vn.com.processman.modules.processmanager.processapplication.forsubject.model.Process4SubjectApplicationManager;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.teaching.model.Subject;

/**
 * @overview 
 *  A sub-type module that is designed for managing {@link ProcessApplication}s for {@link Subject}-related {@link Process}es.
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleProcess4SubjectApplicationManager",
modelDesc=@ModelDesc(
    model=Process4SubjectApplicationManager.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý áp dụng quy trình gắn môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formProcess4SubjectApplicationManager.png",
    domainClassLabel="Áp dụng quy trình gắn môn học"
    ,viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class, //TabLayoutBuilder.class,
    parent=RegionName.Tools
    ,topX=0.5,    
    topY=0
    ,widthRatio=0.5f, heightRatio=0.8f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
    ,props={
        @PropertyDesc(name=PropertyName.controller_dataController_update,valueIsClass=UpdateProcess4SubjectApplicationCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
    }          
)
,containmentTree=@CTree(
    root=Process4SubjectApplicationManager.class
    ,subtrees={
      @SubTree1L(
          parent=Process4SubjectApplicationManager.class,
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
            @Child(cname=Task4Subject.class,  // a sub-type of Task
                scope={},
                scopeDef=".ScopeDefTask4SubjectApplication"
              ),
          }
      ),
      @SubTree1L(
          parent=Task4Subject.class,
          children={
            @Child(
              cname=Action4Subject.class    // a sub-type of Action
              ,scope={}
              ,scopeDef=".ScopeDefAction4SubjectApplication"
            )
          }
      ),
      @SubTree1L(
        parent=Task4Subject.class,
        children={
        @Child(
          cname=SubjectTask.class   
          ,scope={}
          ,scopeDef=".ScopeDefSubjectTask"
          )
        }
      ),
      @SubTree1L(
        parent=Action4Subject.class,
        children={
        @Child(cname=SubjectAction.class
            ,scope={}
            ,scopeDef=".ScopeDefSubjectAction"
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
public class ModuleProcess4SubjectApplicationManager extends ModuleProcessApplicationManager {
  @AttributeDesc(label="Áp dụng quy trình gắn môn học")
  private String title;

  // override parent's field to customise the label
  @AttributeDesc(label="Mã đầy đủ quy trình<br> <b><u>dành</u></b> cho môn học <br> cần áp dụng (mẫu: dt-YZ)")
  private String processCode;

  @AttributeDesc(label="Chọn môn học"
      ,modelDesc=@ModelDesc(model=Subject.class)
      ,ref=@Select(clazz=Subject.class,attributes={Subject.A_code})
      ,type=JListField.class
      ,isStateEventSource=true
      ,width=100,height=5
  )
  private Collection<Subject> subjects;
  
  /** 'override' the super-type's attribute to use specialised data controller commands  
   * for Subject-related processes */
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
              ,@PropertyDesc(name=PropertyName.controller_dataController_open,valueIsClass=OpenProcessApplicationsCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
              ,@PropertyDesc(name=PropertyName.controller_dataController_new,valueIsClass=CreateNewProcess4SubjectCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
              ,@PropertyDesc(name=PropertyName.controller_dataController_openOnNew,valueIsClass=OpenOnNewAppliedProcessCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue) // v1.2
          }          
      )
    ,props={
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
    }
  )
  private Collection<Process> processes;
}
