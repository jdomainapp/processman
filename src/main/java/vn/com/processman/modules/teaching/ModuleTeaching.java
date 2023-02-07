package vn.com.processman.modules.teaching;

import java.util.Collection;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.ds.viewable.JFlexiDataSource;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.CreateObjectAndManyAssociatesDataControllerCommand;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.UpdateObjectAndManyAssociatesDataControllerCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.list.JListField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.dsecurity.model.Teacher;
import vn.com.processman.modules.teaching.subjectbysem.ModuleSubjectBySemesterManager;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;

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
    ,viewType=RegionType.Data,
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
          valueAsString=CommonConstants.NullValue,valueType=Class.class),
      @PropertyDesc(name=PropertyName.controller_dataController_update,
          valueIsClass=UpdateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=CommonConstants.NullValue,
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