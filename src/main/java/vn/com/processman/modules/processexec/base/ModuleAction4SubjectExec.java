package vn.com.processman.modules.processexec.base;

import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.Child;
import jda.modules.mccl.syntax.containment.SubTree1L;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import vn.com.processman.modules.processapplication.ModuleSubjectAction;
import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processdeliverables.model.FileWrapper;
import vn.com.processman.modules.processsconstraint.ModuleBooleanExpression;
import vn.com.processman.modules.processstructure.ModuleAction4Subject;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.processstructure.model.Action4Subject;

/**
 * @overview 
 *  Represent a module for <b>performing</b> {@link Action4Subject}
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleAction4SubjectExec",
modelDesc=@ModelDesc(
    model=Action4Subject.class,
    editable=false
),
viewDesc=@ViewDesc(
    formTitle="Thực hiện bước làm môn học",
    //default: formTitleIcon="formTitleIcon.png",
    imageIcon="formAction4Subject.png",
    domainClassLabel="Bước làm môn học",
    viewType=RegionType.Data,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    //no menu item: to be invoked from code only
    // parent=RegionName.Tools,
    topX=0.5,    
    topY=0,
    widthRatio=-1f,
    heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C,
    isDataFieldStateListener=true
)
,containmentTree=@CTree(
    root=Action4Subject.class
    ,stateScope=   
    /**The attributes needed to display essential information about the state of an object*/
    {Action.A_id, Action.A_codeDef, Action.A_nameDef, 
      Action.A_preConds,
      Action.A_descriptionDef,
      Action.A_postConds,
      Action.A_status,
      Action4Subject.A_subjectActions  
    }
    ,subtrees={
      @SubTree1L(
          parent=Action4Subject.class
          ,children={
            @Child(cname=SubjectAction.class, 
                scope={},
                scopeDef=".ScopeDefSubjectActionExec"
            )
          }
      ),
      @SubTree1L(
          parent=SubjectAction.class
          ,children={
            @Child(cname=FileWrapper.class, 
                //scope={File.A_name, File.A_type, File.A_url})
                // extended scope def
                scope={},
                scopeDef=".ScopeDefFileExec"
            )
          }
      ),
    }
)
,
childModules={
  ModuleBooleanExpression.class,
  ModuleSubjectAction.class
},
isPrimary=true 
)
public class ModuleAction4SubjectExec extends ModuleAction4Subject {
//  @AttributeDesc(label="Bước làm môn học")
//  private String title;

}
