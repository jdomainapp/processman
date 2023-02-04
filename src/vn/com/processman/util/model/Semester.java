package vn.com.processman.util.model;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Toolkit.Season;

/**
 * @overview
 *    Represents names of the academic semesters of a year.
 *     
 * @author dmle
 */
public enum Semester {
  Spring,
  Summer,
  Fall
  ;
  
  @DAttr(name="name", id=true, type=Type.String, length=30)
  public String getName() {
    return name();
  }

  /**
   * @effects 
   *  return a {@link Semester} suitable for <tt>season</tt>
   */
  public static Semester getSemesterFor(Season season) {
    if (season.equals(Season.Spring))
      return Spring;
    else if (season.equals(Season.Summer)) {
      return Summer;
    } else 
      return Fall;
  }
}
