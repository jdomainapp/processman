package vn.com.processman.modules.processsconstraint.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *  Represents expression operators.
 *  
 * @author dmle
 *
 */
public enum Op {
  /** equals */
  EQ,
  /** not-equals */
  NEQ;
  
  @DAttr(name="name", id=true, type=Type.String, length=10)
  public String getName() {
    return name();
  }
}
