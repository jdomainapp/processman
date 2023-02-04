package vn.com.processman.modules.processexec.reports.useractions;

import java.util.Collection;

import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processexec.reports.useractions.model.ReportUserActions;
import vn.com.processman.modules.processexec.reports.useractions.model.UserActionInfo;
import vn.com.processman.util.model.Semester;
import domainapp.basics.controller.datacontroller.SimpleDataController;
import domainapp.basics.core.View;
import domainapp.basics.model.config.ApplicationModule.ModuleType;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Region.Type;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.MetaConstants.PaperSize;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.print.PrintDesc;
import domainapp.basics.model.meta.module.print.PrintFieldDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.view.datafields.JCounterField;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.modules.exportdoc.controller.html.ObjectHtmlDocumentBuilder;
import domainapp.modules.report.controller.ParameterisedSearchReportController;
import domainapp.view.layout.TwoColumnLayoutBuilder;

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
    viewType=Type.Data,
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
