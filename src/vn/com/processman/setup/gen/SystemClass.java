/**
 * 
 */
package vn.com.processman.setup.gen;


import vn.com.processman.modules.ModuleProcessMan;
import vn.com.processman.modules.dsecurity.ModuleDomainRole;
import vn.com.processman.modules.dsecurity.ModuleDomainUser;
import vn.com.processman.modules.dsecurity.ModuleRolePerfProcess;
import vn.com.processman.modules.dsecurity.ModuleTeacherBasic;
import vn.com.processman.modules.processapplication.ModuleOrgUnit;
import vn.com.processman.modules.processapplication.ModuleProcessApplication;
import vn.com.processman.modules.processapplication.ModuleSubjectAction;
import vn.com.processman.modules.processapplication.ModuleSubjectTask;
import vn.com.processman.modules.processdeliverables.ModuleFileWrapper;
import vn.com.processman.modules.processexec.forsubject.ModuleProcessExecution4Subject;
import vn.com.processman.modules.processexec.generic.ModuleProcessExecution;
import vn.com.processman.modules.processexec.generic.ModuleTask4SubjectExec;
import vn.com.processman.modules.processexec.generic.ModuleTaskExec;
import vn.com.processman.modules.processexec.reports.subjectactions.ModuleReportSubjectActions;
import vn.com.processman.modules.processexec.reports.subjectactions.ModuleSubjectActionInfo;
import vn.com.processman.modules.processexec.reports.teachingactions.ModuleReportTeachingActions;
import vn.com.processman.modules.processexec.reports.teachingactions.ModuleTeacherSubjActionInfo;
import vn.com.processman.modules.processexec.reports.useractions.ModuleReportUserActions;
import vn.com.processman.modules.processexec.reports.useractions.ModuleUserActionInfo;
import vn.com.processman.modules.processmanager.ModuleProcessManager;
import vn.com.processman.modules.processmanager.processapplication.ModuleProcessApplicationManager;
import vn.com.processman.modules.processmanager.processapplication.forsubject.ModuleProcess4SubjectApplicationManager;
import vn.com.processman.modules.processsconstraint.ModuleBooleanExpression;
import vn.com.processman.modules.processstructure.ModuleAction;
import vn.com.processman.modules.processstructure.ModuleAction4Subject;
import vn.com.processman.modules.processstructure.ModuleProcess;
import vn.com.processman.modules.processstructure.ModuleTask;
import vn.com.processman.modules.processstructure.ModuleTask4Subject;
import vn.com.processman.modules.teaching.ModuleSubject;
import vn.com.processman.modules.teaching.ModuleTeaching;
import vn.com.processman.modules.teaching.ModuleTeachingBySemester;
import vn.com.processman.modules.teaching.subjectbysem.ModuleSubjectBySemesterManager;
import vn.com.processman.modules.teaching.subjectbysem.ModuleSubjectBySemesterViewer;
import vn.com.processman.setup.data.dsecurity.RolePerfProcesses;
import vn.com.processman.setup.data.processapplication.OrgUnits;
import vn.com.processman.setup.data.processdeliverables.FileWrappers;
import vn.com.processman.setup.data.processdeliverables.Subjects;
import vn.com.processman.setup.data.processstructure.Action4Subjects;
import vn.com.processman.setup.data.processstructure.Actions;
import vn.com.processman.setup.data.processstructure.Processes;
import vn.com.processman.setup.data.processstructure.Task4Subjects;
import domainapp.basics.model.config.Configuration.Language;
import domainapp.basics.model.config.dodm.OsmConfig.ConnectionType;
import domainapp.core.dodm.dom.DOM;
import domainapp.core.dodm.dsm.DSM;
import domainapp.core.dodm.osm.postgresql.PostgreSQLOSM;
import domainapp.model.meta.app.DSDesc;
import domainapp.model.meta.app.OrgDesc;
import domainapp.model.meta.app.SecurityDesc;
import domainapp.model.meta.app.SysSetUpDesc;
import domainapp.model.meta.app.SystemDesc;
import domainapp.modules.exportdoc.ModuleExportDocument;
import domainapp.modules.exportdoc.htmlpage.ModuleHtmlPage;
import domainapp.modules.exportdoc.page.ModulePage;
import domainapp.modules.security.role.ModuleRoleViewer;
import domainapp.modules.security.role.editable.ModuleRoleManager;
import domainapp.modules.security.userrole.ModuleUserRole;
import domainapp.setup.SetUpConfig;

/**
 * @overview 
 *  The system class of the ProcessMan application.
 * @author dmle
 * @version 1.2
 */
@SystemDesc(
    appName="ProcessMan",
    splashScreenLogo="applogo.png",
    language=Language.Vietnamese,
    orgDesc=@OrgDesc(
        name="Khoa CNTT, Trường Đại học Hà nội",
        address="Km9 Đường Nguyễn Trãi, Quận Thanh Xuân", 
        logo="aboutlogo.png", 
        url="http://fit.hanu.edu.vn"
    ), 
    dsDesc=@DSDesc(
        type="postgresql", 
        dsUrl="//localhost:5432/processman", 
        user="admin",
        password="password",
        dsmType=DSM.class,
        domType=DOM.class,
        osmType=PostgreSQLOSM.class,
        connType=ConnectionType.Client
    ), 
    modules={         
      // application main module
      ModuleProcessMan.class

      // process structure
      ,ModuleProcess.class,
      ModuleTask.class, ModuleTask4Subject.class,
      ModuleAction.class, ModuleAction4Subject.class

      // process manager
      ,ModuleProcessManager.class
      , ModuleRolePerfProcess.class
      
      // process application
      , ModuleProcessApplicationManager.class
      , ModuleProcess4SubjectApplicationManager.class
      
      // process execution
      ,ModuleProcessExecution.class
        ,ModuleTaskExec.class, ModuleTask4SubjectExec.class
      ,ModuleProcessExecution4Subject.class
        
      // process constraints
      ,ModuleBooleanExpression.class 
        
      // process deliverables
      ,ModuleFileWrapper.class 
      
      // process applications
      ,ModuleOrgUnit.class, ModuleProcessApplication.class,
      ModuleSubjectTask.class, ModuleSubjectAction.class

      // manage domain security
      ,ModuleDomainUser.class
      ,ModuleRoleViewer.class, ModuleRoleManager.class
      ,ModuleUserRole.class
      ,ModuleTeacherBasic.class
      // domain security
      ,ModuleDomainRole.class
      
      // manage teaching
      ,ModuleSubject.class, 
      ModuleSubjectBySemesterManager.class, ModuleSubjectBySemesterViewer.class 
      ,ModuleTeachingBySemester.class
      ,ModuleTeaching.class
      
      /////// REPORTS ///////////////
      ,ModuleUserActionInfo.class
      ,ModuleReportUserActions.class
      
      ,ModuleTeacherSubjActionInfo.class
      ,ModuleReportTeachingActions.class
      
      ,ModuleSubjectActionInfo.class
      ,ModuleReportSubjectActions.class
    },
    sysModules={ 
      //ModuleSplashScreen.class,
      ModulePage.class,ModuleHtmlPage.class,ModuleExportDocument.class,
    }, 
    setUpDesc=@SysSetUpDesc(
      setUpConfigType=SetUpConfig.class
    )
    ,dataFileLoaders={
      // USE THESE ONLY WHEN security is NOT ENABLED
      //user and role
//      Roles.class,
//      DomainUsers.class,
//      UserRoles.class,
      
      // organisation 
      OrgUnits.class,
      
      //
      Processes.class,
      //Tasks.class,
      Task4Subjects.class, 
      Actions.class,
      Action4Subjects.class, 
      //Action4SubjectsWoutRoles.class,
      //
      Subjects.class 
      //,SubjectBySemesters.class 
      //,FileWrappers.class 
      //,SubjectTasks.class, SubjectActions.class
      //
      //,ProcessApplications.class
      ,RolePerfProcesses.class
    }
    ,securityDesc=@SecurityDesc(
      isEnabled=true // true: to initialise security schema
      ,domainSecurityDesc= DomainSecurityClass.class
    )
)
public class SystemClass {
  // empty
}