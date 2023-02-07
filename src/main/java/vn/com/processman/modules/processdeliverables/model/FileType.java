package vn.com.processman.modules.processdeliverables.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  Represent different file types
 *  
 * @author dmle
 */
public enum FileType {
  FeeList,
  InternalMarkTable,
  FinalMarkTable,
  Template_009,
  Action_Output
  ;
  
//  @DomainConstraint(name="name", id=true, type=Type.String, length=30)
//  private String name;
  @DAttr(name="name", id=true, type=Type.String, length=30)
  public String getName() {
    return name();
  }
}
