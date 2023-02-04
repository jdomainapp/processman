package vn.com.processman.modules.processsconstraint;

import vn.com.processman.modules.processsconstraint.controller.command.BooleanExpressionGeneratorCommand;
import vn.com.processman.modules.processsconstraint.model.BooleanExpression;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.MetaConstants.AlignmentX;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.meta.module.model.ModelDesc;
import domainapp.basics.model.meta.module.view.AttributeDesc;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.core.Controller;

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
          valueIsClass=BooleanExpressionGeneratorCommand.class, valueAsString=MetaConstants.NullValue,
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
