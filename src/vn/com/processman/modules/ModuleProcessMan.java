package vn.com.processman.modules;

/**
 * @overview
 * 	The main module of the application
 *  
 * @author dmle
 */
import static domainapp.basics.model.config.view.Region.RegionName.Chart;
import domainapp.basics.core.View;
import domainapp.basics.model.config.ApplicationModule.ModuleType;
import domainapp.basics.model.config.view.Region.RegionName;
import domainapp.basics.model.config.view.Region.Type;
import domainapp.basics.model.meta.module.ModuleDescriptor;
import domainapp.basics.model.meta.module.ViewDesc;
import domainapp.basics.model.meta.module.controller.ControllerDesc;
import domainapp.basics.model.util.properties.Property.PropertyName;
import domainapp.basics.model.util.properties.PropertyDesc;
import domainapp.core.Controller;

/**
 * @overview
 *  The main module of the application
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ProcessMan",
viewDesc=@ViewDesc(
    formTitle="Quản lý quy trình: ProcessMan",
    imageIcon="formMain.png",
    viewType=Type.Main, 
    children={
        RegionName.Desktop,
        RegionName.MenuBar,
        RegionName.ToolBar,
        RegionName.StatusBar
    },
    view=View.class,
    excludeComponents={
      // general actions
      //Export, Print, 
      Chart,
      // object-related actions
      //Add // experimental
    },
    topX=0,
    topY=0,
    widthRatio=1f,
    heightRatio=1f
    ,props={
      @PropertyDesc(name=PropertyName.view_toolBar_buttonIconDisplay,
          valueAsString="true",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_toolBar_buttonTextDisplay,
          valueAsString="false",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_searchToolBar_buttonIconDisplay,
          valueAsString="true",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_searchToolBar_buttonTextDisplay,
          valueAsString="false",valueType=Boolean.class)
          /* use these for object form actions
      @PropertyDesc(name=PropertyName.view_objectForm_actions_buttonIconDisplay,
          valueAsString="true",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_objectForm_actions_buttonTextDisplay,
          valueAsString="false",valueType=Boolean.class),
          */
       ////// Keyboard shot-cuts
          /*
           * Key Stroke String Format (ref: KeyStroke.getKeyStroke(String))
           *  
                <modifiers>* (<typedID> | <pressedReleasedID>)
            
                modifiers := shift | control | ctrl | meta | alt | altGraph
                typedID := typed <typedKey>
                typedKey := string of length 1 giving Unicode character.
                pressedReleasedID := (pressed | released) key
                key := KeyEvent key code name, i.e. the name following "VK_".
             
            
                  If typed, pressed or released is not specified, pressed is assumed. Here are some examples:
            
                 "INSERT" => getKeyStroke(KeyEvent.VK_INSERT, 0);
                 "control DELETE" => getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK);
                 "alt shift X" => getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK);
                 "alt shift released X" => getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, true);
                 "typed a" => getKeyStroke('a');
  
           */
//      ,@PropertyDesc(name=PropertyName.view_shotcuts_desktop_Exit, 
//        valueAsString="ctrl Q", valueType=String.class)
//      ,@PropertyDesc(name=PropertyName.view_shotcuts_desktop_Logout, 
//        valueAsString="ctrl alt L", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Open, 
          valueAsString="ctrl O", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_New, 
        valueAsString="ctrl N", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Delete, 
        valueAsString="ctrl D", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Update, 
        valueAsString="ctrl U", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_CopyObject, 
        valueAsString="ctrl C", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_First, 
        valueAsString="HOME", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Previous, 
        valueAsString="PAGE_UP", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Next, 
        valueAsString="PAGE_DOWN", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Last, 
        valueAsString="END", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Refresh, 
        valueAsString="ctrl F5", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Reload, 
        valueAsString="ctrl R", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Export, 
        valueAsString="ctrl E", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Print, 
        valueAsString="ctrl P", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_tool_Chart, 
        valueAsString="ctrl T", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_action_Create, 
        valueAsString="ctrl S", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_action_Reset, 
        valueAsString="ESCAPE", valueType=String.class)
      ,@PropertyDesc(name=PropertyName.view_shotcuts_action_Cancel, 
        valueAsString="ctrl ESCAPE", valueType=String.class)
     }
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
),
type=ModuleType.DomainMain
)
public class ModuleProcessMan {

}
