package vn.com.processman.modules.processexec.reports.useractions;

import java.util.Collection;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.exportdoc.controller.html.ObjectHtmlDocumentBuilder;
import jda.modules.helpviewer.model.print.PrintDesc;
import jda.modules.helpviewer.model.print.PrintFieldDesc;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.PaperSize;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.report.controller.ParameterisedSearchReportController;
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JCounterField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processexec.reports.useractions.model.ReportUserActions;
import vn.com.processman.modules.processexec.reports.useractions.model.UserActionInfo;
import vn.com.processman.util.model.Semester;

/**
 * @overview 
 *  Module for {@link ReportUserActions}
 *   
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleReportUserActions",
modelDesc=@ModelDesc(
    model=ReportUserActions.class
),
viewDesc=@ViewDesc(
    formTitle="Báo cáo kết quả công việc",
    domainClassLabel="Báo cáo kết quả công việc",
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
isPrimary=true,
childModules={
  ModuleUserActionInfo.class
})
@PrintDesc(docBuilderType=ObjectHtmlDocumentBuilder.class,
//pageFormat=PageFormat.Landscape,
paperSize=PaperSize.A4,
docTemplate="ReportUserActions.html"
)
public class ModuleReportUserActions {
  @AttributeDesc(label="Báo cáo kết quả công việc")
  private String title;
  
  @AttributeDesc(label="Đ.vị tổ chức", type=JComboField.class
      //,isStateEventSource=true
      ,styleLabel=StyleName.DefaultBold
      ,ref=@Select(clazz=OrgUnit.class,attributes={OrgUnit.A_name})
  )
  private OrgUnit orgUnit;
  
  @AttributeDesc(label="Tên truy cập NSD (vd. duclm) [-]", 
      styleLabel=StyleName.DefaultBold,
      alignX=AlignmentX.Center)
  private String userLogin;
  
  @AttributeDesc(label="Học kỳ", 
      type=JComboField.class,
      isStateEventSource=true,
      styleLabel=StyleName.DefaultBold,
      //width=100, height=25,
      alignX=AlignmentX.Center)
  private Semester semester;
  
  @AttributeDesc(label="Năm học"
      ,type=JCounterField.class
      ,isStateEventSource=true
      ,styleLabel=StyleName.DefaultBold
      ,alignX=AlignmentX.Center)
  private Integer year;
  
  @AttributeDesc(label="Danh sách công việc",
      //type=DefaultPanel.class,
      editable=false,
      //layoutBuilderType=TwoColumnLayoutBuilder.class,
      modelDesc=@ModelDesc(model=UserActionInfo.class,indexable=true),
      controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O_C
          )
      ,props={
        // auto-activate this sub-container 
        @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
        // group-by: group the displayed objects by values of the specified attributes
        @PropertyDesc(name=PropertyName.view_objectForm_groupBy,
            valueAsString=UserActionInfo.A_taskInfo,valueType=String[].class),
      }      
    )  
  @PrintFieldDesc(docTemplate="UserActionInfo.html"
  // the attributes to appear in the export document
  //,ref=@Select(attributes={"index", "actionInfo", "taskInfo", "actionStatus"})
  )  
  private Collection<UserActionInfo> userActionInfos;
}
