package vn.com.processman.modules.processstructure.controller.command;

import java.util.Collection;
import java.util.Map;

import vn.com.processman.modules.processapplication.model.SubjectTask;
import vn.com.processman.modules.processstructure.model.Task4Subject;
import vn.com.processman.modules.processstructure.model.user.ProcessUserActivity;
import vn.com.processman.util.DomainToolKit;
import vn.com.processman.util.model.Semester;
import domainapp.basics.controller.datacontroller.command.DataControllerCommand;
import domainapp.basics.controller.util.MessageCode;
import domainapp.basics.core.ControllerBasic;
import domainapp.basics.core.ControllerBasic.DataController;
import domainapp.basics.core.dodm.DODMBasic;
import domainapp.basics.core.dodm.dom.DOMBasic;
import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.model.Oid;
import domainapp.basics.model.query.FlexiQuery;
import domainapp.basics.model.security.DomainUser;

/**
 * @overview
 *  If security is enabled and user is a regular user then only open {@link SubjectTask}s that user is allowed to perform, otherwise open all {@link SubjectTask}s 
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class OpenAllowedSubjectTasksCommand<C> extends DataControllerCommand {

  // the query used to retrieve objects under security restriction
  private static FlexiQuery query;
  
  /**
   * @effects 
   * 
   */
  public OpenAllowedSubjectTasksCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    /*
     if security is enabled and user is a process-user
       if user is a Teacher that is teaching some subject(s) in the semester
         retrieve SubjectTasks for the subjects that the user is currently teaching, as follows:
         
         let theSemester, theYear be the input semester and year (of the user module (which is a ProcessUserActivity))
         let userSubjSems = SubjectBySemesters that user is teaching in theSemester and theYear, as follows:
           select SubjectBySemester.id
           where  SubjectBySemester.semester = semester and SubjectBySemester.year = year and 
                  TeachingBySemester join SubjectBySemester and 
                  TeachingBySemester.teacher = currentUser
           
         then retrieve the relevant SubjectTasks as follows: 
           select SubjectTask.id
           where SubjectTask.task = parentObj and 
                 SubjectTask join SubjectBySemester and 
                 SubjectBySemester.id in userSubjSems

       else
         retrieve all SubjectTasks 
     else
       retrieve all SubjectTasks
     */
    DataController dctl = getDataController();
    DODMBasic dodm = dctl.getDodm();
    DSMBasic dsm = dodm.getDsm();
    DOMBasic dom = dodm.getDom();

    boolean retrieveConstrainedSubjTasks = false;

    DomainUser user;
    if (isLoggedIn()) {
      user = getCurrentUser();
      Task4Subject parentObj = (Task4Subject) dctl.getParentObject();

      if (DomainToolKit.isProcessUserStrictly(user)) {
        // user is a process-user
        
        Map<Oid,SubjectTask> result = null;
        // ensure that object metadata are fully loaded
        // NOTE: this must be done before openning result objects (below)
        if (!dctl.isOpenMetadata()) {
          dctl.openMetadata();
        }
        
        if (DomainToolKit.isTeacherUser(user)) {
          // if user is a Teacher
          ProcessUserActivity procAct = (ProcessUserActivity) getUserModuleCurrentObject();
          Semester semester = procAct.getSemester();
          Integer year = procAct.getYear();
          
          Collection<Oid> userSubjSems = DomainToolKit.retrieveUserSubjectSemIds(dodm, user, semester, year);

          retrieveConstrainedSubjTasks = true;

          if (userSubjSems != null) {
            // user teaches in the semester
            
            result = DomainToolKit.retrieveSubjectTasksBySubjectSemesters(dodm, parentObj, userSubjSems);
            
            if (result != null) {
              dctl.openObjects(result.values(), true);
            } else {
              // clear existing result (if any)
              ControllerBasic parentCtl = dctl.getParent().getCreator();
              String parentObjName = parentCtl.getDomainClassLabel() + " (" + parentObj.getCode() + ")";
              String functionTitle = parentCtl.getModuleTitle();
              parentCtl.displayMessageFromCode(MessageCode.NO_OBJECTS_FOUND_FOR_FUNCTION, dctl,
                  new Object[] {parentObjName, functionTitle}
                  );
            }
          } else {
            // should not happen (due to a check performed by the Process opener command)
          }
        }
      }     
    }
    
    if (!retrieveConstrainedSubjTasks) {
      // open normally
      dctl.open();
    }
  }
}
