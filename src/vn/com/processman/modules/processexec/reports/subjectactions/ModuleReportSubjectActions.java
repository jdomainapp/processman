package vn.com.processman.modules.processexec.reports.subjectactions;

import java.util.Collection;

import vn.com.processman.modules.processexec.reports.subjectactions.model.ReportSubjectActions;
import vn.com.processman.modules.processexec.reports.subjectactions.model.SubjectActionInfo;
import vn.com.processman.modules.processexec.reports.useractions.ModuleReportUserActions;
import vn.com.processman.modules.teaching.model.Subject;
import domainapp.basics.controller.datacontroller.SimpleDataController;
import domainapp.basics.core.View;
import domainapp.basics.model.config.ApplicationModule.ModuleType;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Region.Type;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants.PaperSize;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.containment.Child;
import domainapp.basics.model.meta.module.containment.CTree;
import domainapp.basics.model.meta.module.containment.SubTree1L;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.print.PrintDesc;
import domainapp.basics.model.meta.module.print.PrintFieldDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.modules.exportdoc.controller.html.ObjectHtmlDocumentBuilder;
import domainapp.modules.report.controller.ParameterisedSearchReportController;
import domainapp.view.layout.TwoColumnLayoutBuilder;

/**
 * @overview 
 *  Module for {@link ReportSubjectActions}
 *   
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleReportSubjectActions",
modelDesc=@ModelDesc(
    model=ReportSubjectActions.class
),
viewDesc=@ViewDesc(
    formTitle="Báo cáo kết quả công việc theo môn học",
    domainClassLabel="Báo cáo kết quả công việc theo môn học",
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
isPrimary=true
,containmentTree=@CTree(
    root=ReportSubjectActions.class
    ,stateScope={ReportSubjectActions.A_orgUnit, 
      ReportSubjectActions.A_subject, 
      ReportSubjectActions.A_semester, 
      ReportSubjectActions.A_year,
      ReportSubjectActions.A_userActionInfos
    }
    ,subtrees={
      @SubTree1L(
          // specialise to use SubjectActionInfo
          parent=ReportSubjectActions.class,
          children={
            @Child(
                cname=SubjectActionInfo.class
                ,scope={"*"}
            )
          }
      ),
    }
)
,childModules={
  ModuleSubjectActionInfo.class
})
@PrintDesc(docBuilderType=ObjectHtmlDocumentBuilder.class,
//pageFormat=PageFormat.Landscape,
paperSize=PaperSize.A4,
docTemplate="ReportSubjectActions.html"
)
public class ModuleReportSubjectActions extends ModuleReportUserActions {
  @AttributeDesc(label="Báo cáo kết quả công việc theo môn học")
  private String title;

  @AttributeDesc(label="Môn học", type=JComboField.class
      ,isStateEventSource=true
      ,styleLabel=StyleName.DefaultBold
      ,ref=@Select(clazz=Subject.class,attributes={Subject.A_code})
  )
  private Subject subject;
  
  // Customise
  @AttributeDesc(label="Danh sách công việc",
      //type=DefaultPanel.class,
      editable=false,
      //layoutBuilderType=TwoColumnLayoutBuilder.class,
      modelDesc=@ModelDesc(model=SubjectActionInfo.class,indexable=true),
      controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.O_C
          )
      ,props={
        // auto-activate this sub-container 
        @PropertyDesc(name=PropertyName.view_objectForm_autoActivate,valueAsString="true",valueType=Boolean.class),
        // group-by: group the displayed objects by values of the specified attributes
        @PropertyDesc(name=PropertyName.view_objectForm_groupBy,
          valueAsString=SubjectActionInfo.A_subjectCode,valueType=String[].class),
      }      
    )  
  @PrintFieldDesc(docTemplate="SubjectActionInfo.html"
  // the attributes to appear in the export document
  //,ref=@Select(attributes={"index", "actionInfo", "subjectCode", "subjectActStatus"})
  )  
  private Collection<SubjectActionInfo> userActionInfos;
}
