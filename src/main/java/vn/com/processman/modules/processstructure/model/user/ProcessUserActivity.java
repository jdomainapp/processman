package vn.com.processman.modules.processstructure.model.user;

import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  Represents all activities that apply or use {@link Process} and its structure in a given 
 *  semester and year. That is, these activities must provide these two input as part of their interfaces.
 *  
 * @author dmle
 *
 * @version 3.3
 */
public interface ProcessUserActivity {
  
  /**
   * @effects 
   *  Return the semester
   */
  public Semester getSemester();
  
  /**
   * @effects 
   *  Return the year
   */
  public Integer getYear();
}
