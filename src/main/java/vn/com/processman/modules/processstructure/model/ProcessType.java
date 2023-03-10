package vn.com.processman.modules.processstructure.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *  Represent different process types. 
 *  
 * @author dmle
 *
 * @version 1.2.c 
 */
public enum ProcessType {
  NotForSubject("N"),
  ForSubject("S")
  ;
  
  private String name;
  
  private ProcessType(String name) {
    this.name = name;
  }
  
  @DAttr(name="name", id=true, type=Type.String, length=5)
  public String getName() {
    return name;
  }
}
