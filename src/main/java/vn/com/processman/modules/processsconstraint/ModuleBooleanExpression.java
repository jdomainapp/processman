package vn.com.processman.modules.processsconstraint;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import vn.com.processman.modules.processsconstraint.controller.command.BooleanExpressionGeneratorCommand;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;

/**
 * @overview 
 *  Module of {@link BooleanExpression} 
 *  
 * @author dmle
 *
 */
@ModuleDescriptor(
name="ModuleBooleanExpression",
modelDesc=@ModelDesc(
    model=BooleanExpression.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lý biểu thức điều kiện",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formBooleanExpression.png",
    domainClassLabel="Biểu thức điều kiện",
    on=false
    /* not show
    viewType=Type.Data,
    view=View.class,
    parent=RegionName.Tools,
    topContainerType=JObjectTable.class,
    topX=0.5d,topY=0.5d
    */
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    props={
      // custom start-up command: generate BooleanExpression objects needed for application
      @PropertyDesc(name=PropertyName.controller_startup_command,
          valueIsClass=BooleanExpressionGeneratorCommand.class, valueAsString=CommonConstants.NullValue,
          valueType=Class.class)
    }  
),
isPrimary=true      
)
public class ModuleBooleanExpression {
  @AttributeDesc(label="Biểu thức điều kiện")
  private String title;
  
  @AttributeDesc(label="Mô tả", editable=false)
  private String description;
  
  @AttributeDesc(label="Kết quả" 
      //,type=JBooleanField.class
      ,alignX=AlignmentX.Center
      )
  private boolean result;
}
