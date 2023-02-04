package vn.com.processman.modules.processmanager.processapplication.model;

import java.util.Map;

import vn.com.processman.modules.processmanager.processapplication.forsubject.model.Process4SubjectApplicationManager;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.message.DomainMessageCode;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.warning.DomainWarning;
import domainapp.basics.model.datavalidator.DataValidatorFunction;
import domainapp.basics.model.meta.DAttr;

/**
 * @overview
 *  Validate {@link ProcessApplicationManager} and {@link Process4SubjectApplicationManager} data as follows:
 *  <ol>
 *    <li>processCode must refer to a {@link Process} that is suitable for being applied by either one of 
 *    these domain classes. 
 *  <ol>
 * @author dmle
 *
 * @version 1.2 
 */
public class ValidateProcessApplication<T> extends DataValidatorFunction<T> {

  /**
   * @effects 
   * 
   */
  public ValidateProcessApplication(DODMBasic dodm, Class<T> domainClass) {
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
  public void eval(T currentObj, Map<DAttr, Object> valsMap)
      throws ConstraintViolationException, DomainWarning {
    if (valsMap == null || valsMap.isEmpty()) return;
    
    String processCode = getAttributeVal(valsMap, ProcessApplicationManager.A_processCode, String.class);

    if (processCode != null) { // make sure that processCode was input or modified
      Class domainClass = getDomainClass();
      DODMBasic dodm = getDodm();
      
      if (domainClass.equals(ProcessApplicationManager.class)) {
        // non-subject
        if (!DomainToolKit.existProcess4NonSubject(dodm, processCode)) {
          throw new ConstraintViolationException(DomainMessageCode.ERROR_PROCESS_CODE_NOT_FOR_NON_SUBJECT, 
              new Object[] { processCode });
        }
      } else if (domainClass.equals(Process4SubjectApplicationManager.class)) { 
        // for-subject
        if (!DomainToolKit.existProcess4Subject(dodm, processCode)) {
          throw new ConstraintViolationException(DomainMessageCode.ERROR_PROCESS_CODE_NOT_FOR_SUBJECT, 
              new Object[] { processCode });
        }
      }
    }
  }
}
