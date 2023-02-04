package vn.com.processman.modules.processapplication.model;

import java.util.Map;

import vn.com.processman.util.message.DomainMessageCode;
import vn.com.processman.util.model.StatusCode;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.warning.DomainWarning;
import domainapp.basics.model.datavalidator.DataValidatorFunction;
import domainapp.basics.model.meta.DAttr;

/**
 * @overview
 *  Validate {@link SubjectAction} as follows:
 *  <ol>
 *    <li>value of {@link SubjectAction#subjStatus} of current object can only be set to {@link StatusCode#Done} if both pre- and post-conditions 
 *    of {@link SubjectAction#action} are satisified.
 *  </ol>
 * @author dmle
 *
 * @version 1.2
 */
public class ValidateSubjectAction extends DataValidatorFunction<SubjectAction> {

  public ValidateSubjectAction(DODMBasic dodm, Class domainClass) {
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
  public void eval(SubjectAction currentObj, Map<DAttr, Object> valsMap) throws ConstraintViolationException, DomainWarning {
    if (valsMap == null || valsMap.isEmpty() || currentObj == null) return;
    
    StatusCode subjStatus = getAttributeVal(valsMap, SubjectAction.A_subjStatus, StatusCode.class);

    if (subjStatus != null) { // subjStatus was input or modified
      // validate subjStatus
      
      // check if currentObj can be done
      boolean canBeDone = SubjectAction.evaluateSubjStatusSimulator(currentObj);
      if (subjStatus.equals(StatusCode.Done) && !canBeDone) {
        // invalid: throw exception
        throw new ConstraintViolationException(DomainMessageCode.ERROR_SUBJECT_ACTION_STATUS_CANNOT_BE_DONE);
      } 
      /* use this when SubjectAction.subjStatus is evaluated automatically to display a warning about this 
      else if (subjStatus.equals(StatusCode.NotDone) && canBeDone) {
        // this is strictly not wrong (user try to unset subject action status even though it can be done)
        // display a warning that this change may not be permanent b/c the system can automatically set it back 
        // StatusCode.Done
        throw new DomainWarning(DomainMessageCode.WARN_SUBJECT_ACTION_STATUS_SYSTEM_OVERWRITE);
      }
      */
    }
  }

}
