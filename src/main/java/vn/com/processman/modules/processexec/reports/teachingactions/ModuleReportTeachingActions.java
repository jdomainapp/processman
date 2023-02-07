package vn.com.processman.modules.processexec.reports.teachingactions;

import java.util.Collection;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.exportdoc.controller.html.ObjectHtmlDocumentBuilder;
import jda.modules.helpviewer.model.print.PrintDesc;
import jda.modules.helpviewer.model.print.PrintFieldDesc;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants.PaperSize;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.Child;
import jda.modules.mccl.syntax.containment.SubTree1L;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.report.controller.ParameterisedSearchReportController;
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.processexec.reports.teachingactions.model.ReportTeachingActions;
import vn.com.processman.modules.processexec.reports.teachingactions.model.TeacherSubjActionInfo;
import vn.com.processman.modules.processexec.reports.useractions.ModuleReportUserActions;

/**
 * @overview 
 *  Module for {@link ReportTeachingActions}
 *   
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleReportTeachingActions",
modelDesc=@ModelDesc(
    model=ReportTeachingActions.class
),
viewDesc=@ViewDesc(
    formTitle="Báo cáo kết quả công việc giảng dạy",
    domainClassLabel="Báo cáo kết quả công việc giảng dạy",
    imageIcon="formReport.png",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    parent=RegionName.ToolReport,
    topX=0.5, topY=0.05D
    ,props={ 
      // auto-export: to automatically display the exported report to user when result becomes available 
      @PropertyDesc(name=PropertyName.view_objectForm_autoExport,valueAsString="true",valueType=Boolean.class)
    }
),
controllerDesc=@ControllerDesc(
    controller=ParameterisedSearchReportController.class,
    dataController=SimpleDataController.class,
    isDataFieldStateListener=true
    ),
type=ModuleType.DomainReport,
isViewer=false,
isPrimary=true
,containmentTree=@CTree(
    root=ReportTeachingActions.class
    ,stateScope={"*"}
    ,subtrees={
      @SubTree1L(
          // specialise to use TeacherSubjActionInfo
          parent=ReportTeachingActions.class,
          children={
            @Child(
                cname=TeacherSubjActionInfo.class
                ,scope={"*"}
            )
          }
      ),
    }
)
,childModules={
  ModuleTeacherSubjActionInfo.class
})
@PrintDesc(docBuilderType=ObjectHtmlDocumentBuilder.class,
//pageFormat=PageFormat.Landscape,
paperSize=PaperSize.A4,
docTemplate="ReportTeachingActions.html"
)
public class ModuleReportTeachingActions extends ModuleReportUserActions {
  @AttributeDesc(label="Báo cáo kết quả công việc giảng dạy")
  private String title;

// inherited  
//  @AttributeDesc(label="Đ.vị tổ chức", type=JComboField.class
//      //,isStateEventSource=true
//      ,styleLabel=StyleName.DefaultBold
//      ,ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
//  )
//  private OrgUnit orgUnit;
//  
//  @AttributeDesc(label="Tên truy cập NSD (vd. duclm) [-]", 
//      styleLabel=StyleName.DefaultBold,
//      alignX=AlignmentX.Center)
//  private String userLogin;
//  
//  @AttributeDesc(label="Học kỳ", 
//      type=JComboField.class,
//      isStateEventSource=true,
//      styleLabel=StyleName.DefaultBold,
//      //width=100, height=25,
//      alignX=AlignmentX.Center)
//  private Semester semester;
//  
//  @AttributeDesc(label="Năm học"
//      ,type=JCounterField.class
//      ,isStateEventSource=true
//      ,styleLabel=StyleName.DefaultBold
//      ,alignX=AlignmentX.Center)
//  private Integer year;
  
  // Customise
  @AttributeDesc(label="Danh sách công việc",
      //type=DefaultPanel.class,
      editable=false,
      //layoutBuilderType=TwoColumnLayoutBuilder.class,
      modelDesc=@ModelDesc(model=TeacherSubjActionInfo.class,indexable=true),
      controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O_C
          )
      ,props={
        // auto-activate this sub-container 
        @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
        // group-by: group the displayed objects by values of the specified attributes
        @PropertyDesc(name=PropertyName.view_objectForm_groupBy,
          valueAsString=TeacherSubjActionInfo.A_actionInfo + "," + TeacherSubjActionInfo.A_userName,valueType=String[].class),
      }      
    )  
  @PrintFieldDesc(docTemplate="TeacherSubjActionInfo.html"
  // the attributes to appear in the export document
  //,ref=@Select(attributes={"index", "actionInfo", "subjectCode", "subjectActStatus"})
  )  
  private Collection<TeacherSubjActionInfo> userActionInfos;
}
