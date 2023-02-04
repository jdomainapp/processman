package vn.com.processman.modules.teaching;

import java.util.Collection;

import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.modules.teaching.subjectbysem.ModuleSubjectBySemesterManager;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import domainapp.basics.core.View;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Region.Type;
import domainapp.basics.model.config.view.Style.StyleName;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc.OpenPolicy;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.view.datafields.list.JListField;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.controller.datacontroller.command.manyAssoc.CreateObjectAndManyAssociatesDataControllerCommand;
import domainapp.controller.datacontroller.command.manyAssoc.UpdateObjectAndManyAssociatesDataControllerCommand;
import domainapp.core.Controller;
import domainapp.db.JFlexiDataSource;
import domainapp.view.layout.TwoColumnLayoutBuilder;

/**
 * @overview
 *  A module used for manipulating teachings of {@link Teacher}s (i.e. the subjects 
 *  that they will be teaching).
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleTeaching",
modelDesc=@ModelDesc(
    model=Teacher.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Giảng dạy của giảng viên",
    formTitle="Quản lý giảng dạy", 
    imageIcon="formTeaching.png"
    ,viewType=Type.Data,
    parent=RegionName.Tools,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    topX=0.5,topY=0.0//widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
    ,openPolicy=OpenPolicy.I_C
    ,isDataFieldStateListener=true  // listens to state change event of list field
    ,props={
      // many-many assoc commands
      @PropertyDesc(name=PropertyName.controller_dataController_create,
          valueIsClass=CreateObjectAndManyAssociatesDataControllerCommand.class, 
          valueAsString=MetaConstants.NullValue,valueType=Class.class),
      @PropertyDesc(name=PropertyName.controller_dataController_update,
          valueIsClass=UpdateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=MetaConstants.NullValue,
          valueType=Class.class)
    }
),
isPrimary=true
,isViewer=true
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
,childModules={ ModuleTeachingBySemester.class, ModuleSubjectBySemesterManager.class }
)
public class ModuleTeaching {
  @AttributeDesc(label="Giảng dạy của giảng viên")
  private String title;
  
  @AttributeDesc(label="Họ và tên",alignX=AlignmentX.Center, editable=false)
  private String name;

  @AttributeDesc(label="Tên đăng nhập",alignX=AlignmentX.Center, editable=false)
  private String login;
  
  // teachings
  @AttributeDesc(label="Môn học (theo học kỳ)",
      type=JListField.class
      ,styleField=StyleName.DefaultTechnical
      ,ref=@Select(clazz=SubjectBySemester.class,attributes={SubjectBySemester.A_displayInfo})
      ,modelDesc=@ModelDesc(model=SubjectBySemester.class,
          dataSourceType=JFlexiDataSource.class)
      ,isStateEventSource=true
      ,width=300,height=5
  )
  private Collection<SubjectBySemester> teachingSubjs;
}