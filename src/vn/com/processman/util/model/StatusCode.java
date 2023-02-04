package vn.com.processman.util.model;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;

/**
 * @overview 
 *  Represent the different status codes that are used.
 *  
 * @author dmle
 *
 */
public enum StatusCode {
  Done("K"),
  NotDone("C");
  
  private String name;
  
  private StatusCode(String name) {
    this.name=name;
  }
  
  @DAttr(name="name", id=true, type=Type.String, length=30)
  public String getName() {
    return name;
  }
}
