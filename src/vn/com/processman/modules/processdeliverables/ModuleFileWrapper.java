package vn.com.processman.modules.processdeliverables;

import java.io.File;

import vn.com.processman.modules.processdeliverables.model.FileType;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.view.datafields.list.JComboField;
import domainapp.core.Controller;
import domainapp.db.JFlexiDataSource;
import domainapp.view.datafields.chooser.JFileChooserField;

/**
 * @overview 
 *  Represent module for {@link FileWrapper}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleFileWrapper",
modelDesc=@ModelDesc(
    model=FileWrapper.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý tệp văn bản",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formFileWrapper.png",
    domainClassLabel="Tệp văn bản",
    viewType=Region.Type.Data,
    view=View.class,
    //layoutBuilderType=TwoColumnLayoutBuilder.class,
    parent=RegionName.Tools
    //,topContainerType=JObjectTable.class
    ,topX=0.5,    
    topY=0
    //,widthRatio=0.8f, heightRatio=0.8f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    //dataController=ObjectTableController.class,
    isDataFieldStateListener=true
),
isPrimary=true
)
public class ModuleFileWrapper {
  @AttributeDesc(label="Tệp văn bản")
  private String title;
  
  @AttributeDesc(label="Mã dữ liệu",
      alignX=AlignmentX.Center)
  private int id;

  @AttributeDesc(label="Loại",
      type=JComboField.class,
      width=200, height=25,
      isStateEventSource=true,
      alignX=AlignmentX.Center)
  private FileType type;
  
  @AttributeDesc(label="Tệp",
      type=JFileChooserField.class, 
      styleField=StyleName.DefaultBlue,
      isStateEventSource=true,
      alignX=AlignmentX.Center
      )
  private File file;
  
//  @AttributeDesc(label="Tên")
//  private String name;

  @AttributeDesc(label="Môn học [-]",
      type=JComboField.class
      ,isStateEventSource=true
      ,ref=@Select(clazz=SubjectBySemester.class,attributes={SubjectBySemester.A_displayInfo})
      //,width=10, height=25
      ,modelDesc=@ModelDesc(  // needed b/c bound attribute is not serialisable
         model=SubjectBySemester.class,
         dataSourceType=JFlexiDataSource.class
      )
      ,styleField=StyleName.DefaultTechnical
      ,alignX=AlignmentX.Center)
  private SubjectBySemester subj;
}
