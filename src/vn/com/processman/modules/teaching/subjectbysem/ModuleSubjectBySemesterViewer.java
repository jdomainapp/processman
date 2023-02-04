package vn.com.processman.modules.teaching.subjectbysem;

import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.core.Controller;

/**
 * @overview 
 *  Represent viewer module for {@link SubjectBySemester}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleSubjectBySemesterViewer",
modelDesc=@ModelDesc(
    model=SubjectBySemester.class,
    // no editing allowed
    editable=false
),
viewDesc=@ViewDesc(
    formTitle="Môn học theo học kỳ",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formSubjectBySemester.png",
    domainClassLabel="Môn học theo học kỳ",
    viewType=Region.Type.Data,
    view=View.class,
    //no menu item:
    // parent=RegionName.Tools,
    topX=0.5,    
    topY=0
),
containmentTree=@CTree(
    root=SubjectBySemester.class
    ,stateScope={SubjectBySemester.A_subject, SubjectBySemester.A_semester, SubjectBySemester.A_year}
)
,controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
),
isPrimary=true
,isViewer=true
)
public class ModuleSubjectBySemesterViewer extends ModuleSubjectBySemesterManager {
//  @AttributeDesc(label="Môn học theo học kỳ")
//  private String title;
//  
////  @AttributeDesc(label="Mã dữ liệu")
////  private String id;
//  
//  @AttributeDesc(label="Môn học", type=JComboField.class, 
//      ref=@Select(clazz=Subject.class,attributes={Subject.A_code})
//  ,isStateEventSource=true
//  ,alignX=AlignmentX.Center
//  )
//  private Subject subject;
//  
//  @AttributeDesc(label="Học kỳ"
//      , type=JComboField.class
//      ,isStateEventSource=true
//      ,alignX=AlignmentX.Center)
//  private Semester semester;
//
//  @AttributeDesc(label="Năm"
//      ,type=JCounterField.class
//      ,isStateEventSource=true
//      ,alignX=AlignmentX.Center)
//  private int year;  
//    
//  @AttributeDesc(label="Được cho thi?"
//      ,isStateEventSource=true
//      ,alignX=AlignmentX.Center)
//  private boolean approvedForExam;
}
