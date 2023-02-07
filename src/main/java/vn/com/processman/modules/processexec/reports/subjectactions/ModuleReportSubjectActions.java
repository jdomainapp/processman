package vn.com.processman.modules.processexec.reports.subjectactions;

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
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.processexec.reports.subjectactions.model.ReportSubjectActions;
import vn.com.processman.modules.processexec.reports.subjectactions.model.SubjectActionInfo;
import vn.com.processman.modules.processexec.reports.useractions.ModuleReportUserActions;
import vn.com.processman.modules.teaching.model.Subject;

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
