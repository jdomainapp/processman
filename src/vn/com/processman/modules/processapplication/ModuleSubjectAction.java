package vn.com.processman.modules.processapplication;

import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processdeliverables.ModuleFileWrapper;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
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
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.basics.view.datafields.JTextField;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.basics.view.panels.DefaultPanel;
import domainapp.core.Controller;
import domainapp.db.JFlexiDataSource;
import domainapp.view.layout.TwoColumnLayoutBuilder;

/**
 * @overview 
 *  Represent module for {@link SubjectAction}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleSubjectAction",
modelDesc=@ModelDesc(
    model=SubjectAction.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý bước làm môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formSubjectAction.png",
    domainClassLabel="Bước làm môn học"
    ,viewType=Region.Type.Data,
    view=View.class,
    //no menu item 
    // parent=RegionName.Tools,
    //topContainerType=DefaultPanel.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class
    ,topX=0.5    
    ,topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
),
containmentTree=@CTree(
    root=SubjectAction.class
    ,subtrees={
      @SubTree1L(
          parent=SubjectAction.class,
          children={
            @Child(cname=FileWrapper.class,
                scope={FileWrapper.A_name, FileWrapper.A_type, FileWrapper.A_file}
            )
          }
      ),
    }
)
,childModules={ModuleFileWrapper.class}
,isPrimary=true 
)
public class ModuleSubjectAction {
  @AttributeDesc(label="Bước làm môn học")
  private String title;
  
  @AttributeDesc(label="Môn học",
      //type=JComboField.class
      // read-only configuration
    type=JTextField.class, editable=false
    ,ref=@Select(clazz=SubjectBySemester.class,attributes={SubjectBySemester.A_code})
    ,isStateEventSource=true
    ,width=10, height=25
    ,alignX=AlignmentX.Center, // IMPORTANT: needed to properly align the values
    modelDesc=@ModelDesc(
       model=SubjectBySemester.class,
       dataSourceType=JFlexiDataSource.class
    )
  )
  private SubjectBySemester subject;

  @AttributeDesc(label="Bước làm", type=JTextField.class, editable=false,
      ref=@Select(clazz=Action4Subject.class,attributes={Action4Subject.A_codeDef})
      ,alignX=AlignmentX.Center
      ,width=20  // text length
      ,height=25  // pixels 
  )
  private Action action;
  
  @AttributeDesc(label="Tệp kết quả", 
      type=DefaultPanel.class
      ,layoutBuilderType=TwoColumnLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O
      )
    ,props={ // v1.2
      // auto-activate this sub-container 
      @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
    }
  )
  private FileWrapper output;

  @AttributeDesc(label="Tình trạng", 
      type=JComboField.class,editable=false,
      isStateEventSource=true,
      //width=100, height=25,
      alignX=AlignmentX.Center)
  private StatusCode subjStatus;
}
