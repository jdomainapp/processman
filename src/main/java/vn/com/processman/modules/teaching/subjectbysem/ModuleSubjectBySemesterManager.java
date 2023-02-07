package vn.com.processman.modules.teaching.subjectbysem;

import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JCounterField;
import jda.mosa.view.assets.datafields.list.JComboField;
import vn.com.processman.modules.teaching.model.Subject;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import vn.com.processman.util.model.Semester;

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
    viewType=RegionType.Data,
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
