package vn.com.processman.modules.teaching.subjectbysem;

import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.Semester;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.view.datafields.JCounterField;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.core.Controller;

/**
 * @overview 
 *  Represent manager module for {@link SubjectBySemester}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleSubjectBySemesterManager",
modelDesc=@ModelDesc(
    model=SubjectBySemester.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý môn học theo học kỳ",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formSubjectBySemester.png",
    domainClassLabel="Môn học theo học kỳ",
    viewType=Region.Type.Data,
    view=View.class,
    parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
),
isPrimary=true,
isViewer=false
)
public class ModuleSubjectBySemesterManager {
  @AttributeDesc(label="Môn học theo học kỳ")
  private String title;
  
//  @AttributeDesc(label="Mã dữ liệu")
//  private String id;
  
  @AttributeDesc(label="Môn học", type=JComboField.class, 
      ref=@Select(clazz=Subject.class,attributes={Subject.A_code})
  ,isStateEventSource=true
  ,alignX=AlignmentX.Center
  )
  private Subject subject;
  
  @AttributeDesc(label="Học kỳ"
      , type=JComboField.class
      ,isStateEventSource=true
      ,alignX=AlignmentX.Center)
  private Semester semester;

  @AttributeDesc(label="Năm"
      ,type=JCounterField.class
      ,isStateEventSource=true
      ,alignX=AlignmentX.Center)
  private int year;  
    
  @AttributeDesc(label="Được cho thi?"
      ,isStateEventSource=true
      ,alignX=AlignmentX.Center)
  private boolean approvedForExam;
}
