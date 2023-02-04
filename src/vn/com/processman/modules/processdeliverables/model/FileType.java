package vn.com.processman.modules.processdeliverables.model;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;

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
