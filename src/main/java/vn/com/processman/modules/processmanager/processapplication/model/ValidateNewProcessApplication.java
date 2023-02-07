package vn.com.processman.modules.processmanager.processapplication.model;

import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.warning.DomainWarning;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.mosa.model.assets.DataValidatorFunction;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.message.DomainMessageCode;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  Validate {@link ProcessApplicationManager} data as follows:
 *  <ol>
 *    <li>No two {@link ProcessApplication}s of the same {@link Process} are created for the same period 
 *  <ol>
 * @author dmle
 *
 * @version 1.2 
 */
public class ValidateNewProcessApplication extends DataValidatorFunction<ProcessApplication> {

  /**
   * @effects 
   * 
   */
  public ValidateNewProcessApplication(DODMBasic dodm, Class<ProcessApplication> domainClass) {
    super(dodm, domainClass);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.datavalidator.DataValidatorFunction#eval(java.util.Map)
   */
  @Override
  public void eval(ProcessApplication currentObj, Map<DAttr, Object> valsMap)
      throws ConstraintViolationException, DomainWarning {
    if (valsMap == null || valsMap.isEmpty()) return;
    
    String processCode = getAttributeVal(valsMap, ProcessApplicationManager.A_processCode, String.class);
    Semester semester = getAttributeVal(valsMap, ProcessApplicationManager.A_semester, Semester.class);
    Integer year = getAttributeVal(valsMap, ProcessApplicationManager.A_year, Integer.class);

    eval(processCode, semester, year);
  }

  /**
   * @effects
   *  validate <tt>processCode, semester, year</tt> to ensure that no other {@link ProcessApplication} 
   *  has been created for the same process and in the same period 
   */
  public void eval(String processCode, Semester semester, Integer year) throws ConstraintViolationException {
    DODMBasic dodm = getDodm();
    if (DomainToolKit.existProcessApplication(dodm, processCode, semester, year)) {
      throw new ConstraintViolationException(DomainMessageCode.ERROR_DUPLICATE_PROCESS_APPLICATION, new Object[] {
          processCode, semester + " " + year
      });
    }
    
  }
}
