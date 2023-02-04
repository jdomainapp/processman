package vn.com.processman.modules.processsconstraint.model;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;

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
