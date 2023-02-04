package vn.com.processman.modules.processapplication;

import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.view.datafields.JTextField;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.core.Controller;
import domainapp.db.JFlexiDataSource;

/**
 * @overview 
 *  Represent module for {@link SubjectTask}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleSubjectTask",
modelDesc=@ModelDesc(
    model=SubjectTask.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý nhiệm vụ môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formSubjectTask.png",
    domainClassLabel="Nhiệm vụ môn học"
    // no view
//    viewType=Region.Type.Data,
//    view=View.class,
//    parent=RegionName.Tools,
//    topContainerType=JObjectTable.class,
//    topX=0.5,    
//    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
   // openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
),
isPrimary=true 
)
public class ModuleSubjectTask {
  @AttributeDesc(label="Nhiệm vụ môn học")
  private String title;
  
  @AttributeDesc(label="Mã dữ liệu")
  private String id;
  
  @AttributeDesc(label="Môn học", // gốc
      type=JComboField.class
      ,ref=@Select(clazz=Subject.class,attributes={Subject.A_code})
      ,isStateEventSource=true
      //,width=100, height=25
      ,alignX=AlignmentX.Center
  )
  private Subject baseSubject;

  @AttributeDesc(label="Môn học", // theo học kì
//      type=JComboField.class
     // read-only configuration
      type=JTextField.class, editable=false
      ,ref=@Select(clazz=SubjectBySemester.class,attributes={SubjectBySemester.A_code})
      ,modelDesc=@ModelDesc(
          model=SubjectBySemester.class,
          dataSourceType=JFlexiDataSource.class
       )
      ,isStateEventSource=true
      ,width=10, height=25
      ,alignX=AlignmentX.Center
  )
  private SubjectBySemester subject;
  
  @AttributeDesc(label="Nhiệm vụ", type=JTextField.class, editable=false,
      ref=@Select(clazz=Task4Subject.class,attributes={Task4Subject.A_codeDef})
      ,width=20  // text length
      ,height=25  // pixels 
      )
  private Task4Subject task;
  
  @AttributeDesc(label="Tình trạng", 
      type=JComboField.class,editable=false,
      isStateEventSource=true,
      //width=100, height=25,
      alignX=AlignmentX.Center)
  private StatusCode subjStatus;  
}
