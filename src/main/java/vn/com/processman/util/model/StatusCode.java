package vn.com.processman.util.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;

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
