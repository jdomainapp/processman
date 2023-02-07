package vn.com.processman.setup.gen;

import jda.modules.sccl.syntax.security.DomainSecurityDesc;
import jda.modules.sccl.syntax.security.PermSetDesc;
import jda.modules.sccl.syntax.security.RoleDesc;
import jda.modules.sccl.syntax.security.RolePermSetDesc;
import jda.modules.sccl.syntax.security.UserDesc;
import jda.modules.sccl.syntax.security.UserRolesDesc;
import jda.modules.security.role.editable.ModuleRoleManager;
import vn.com.processman.modules.dsecurity.ModuleDomainUser;
import vn.com.processman.modules.processapplication.ModuleOrgUnit;
import vn.com.processman.modules.processdeliverables.ModuleFileWrapper;
import vn.com.processman.modules.processexec.forsubject.ModuleProcessExecution4Subject;
import vn.com.processman.modules.processexec.generic.ModuleProcessExecution;
import vn.com.processman.modules.processexec.reports.subjectactions.ModuleReportSubjectActions;
import vn.com.processman.modules.processexec.reports.teachingactions.ModuleReportTeachingActions;
import vn.com.processman.modules.processexec.reports.useractions.ModuleReportUserActions;
import vn.com.processman.modules.processmanager.ModuleProcessManager;
import vn.com.processman.modules.processmanager.processapplication.ModuleProcessApplicationManager;
import vn.com.processman.modules.processmanager.processapplication.forsubject.ModuleProcess4SubjectApplicationManager;
import vn.com.processman.modules.teaching.ModuleSubject;
import vn.com.processman.modules.teaching.ModuleTeaching;
import vn.com.processman.modules.teaching.subjectbysem.ModuleSubjectBySemesterManager;
import vn.com.processman.modules.teaching.subjectbysem.ModuleSubjectBySemesterViewer;

/**
 * @overview
 *  Configures domain-specific security of the application. 
 *  This class is to be referenced by the security descriptor of the {@link SystemClass} of the application. 
 *  
 * @author dmle
 *
 * @version 1.2
 */
@DomainSecurityDesc(
    roleDescs = {
        @RoleDesc(name="CNKH", descr="Chủ nhiệm khoa")
        ,@RoleDesc(name="TRBM", descr="Trưởng bộ môn")
        ,@RoleDesc(name="GIAV", descr="Giảng viên")
        ,@RoleDesc(name="THK1", descr="Thư kí 1")
        ,@RoleDesc(name="THK2", descr="Thư kí 2")
        ,@RoleDesc(name="QLQT", descr="Quản trị quy trình")
    }      
    ,userDescs = {
        @UserDesc(name="Lê Minh Đức", login="duclm", password="1")
        ,@UserDesc(name="Đỗ Thị Phương Thảo", login="thaodtp", password="1")
        ,@UserDesc(name="Trần Văn Quân", login="quantv", password="1")
        ,@UserDesc(name="Vũ Minh Tuấn", login="tuanvm", password="1")
        ,@UserDesc(name="Hoàng Thị Kiều Hoa", login="hoahk", password="1")
        ,@UserDesc(name="Nguyễn Xuân Thắng", login="thangnx", password="1")
    }     
    ,userRoleDescs = {
        @UserRolesDesc(userLogin="duclm", roleNames={"TRBM", "QLQT"})
        ,@UserRolesDesc(userLogin="thaodtp", roleNames={"TRBM", "QLQT"})
        //,@UserRolesDesc(userLogin="quantv", roleNames={"GIAV" })
        ,@UserRolesDesc(userLogin="tuanvm", roleNames={"THK2"//, "GIAV"
                                                       })
        ,@UserRolesDesc(userLogin="hoahk", roleNames={"THK1"})
        ,@UserRolesDesc(userLogin="thangnx", roleNames={"CNKH"})
    } 
    //,appUser=""
    //,appPassword=""
    ,rolePermDescs = { 
        @RolePermSetDesc(
            roleNames={"THK1","THK2","GIAV","TRBM","CNKH"}, 
            perms={
                @PermSetDesc(resourceClasses={ModuleProcessExecution.class, 
                                           ModuleProcessExecution4Subject.class,
                                           ModuleFileWrapper.class,
                                           ModuleSubjectBySemesterViewer.class
                                            // REPORTING: TODO
//                                           ,ModuleReportMyUserActions.class
//                                           ,ModuleReportMyTeachingActions.class
                })
            }) 
        ,@RolePermSetDesc(
            roleNames={"QLQT", "CNKH"}, 
            perms={
                @PermSetDesc(resourceClasses={
                                          ModuleRoleManager.class
                                          ,ModuleSubjectBySemesterManager.class
                                           // REPORTING
                                          ,ModuleReportUserActions.class
                                          ,ModuleReportTeachingActions.class
                                          ,ModuleReportSubjectActions.class
                })
            }) 
//        ,@RolePermSetDesc(
//            roleNames={"CNKH"}, 
//            perms={
//                @PermSetDesc(resourceClasses={
//                })
//            }) 
        ,@RolePermSetDesc(
            roleNames={"QLQT"}, 
            perms={
                @PermSetDesc(resourceClasses={ModuleOrgUnit.class,
                                           ModuleProcessManager.class,
                                           //ModuleProcess4SubjectManager.class,
                                           ModuleProcessApplicationManager.class,
                                           ModuleProcess4SubjectApplicationManager.class,
                                           ModuleDomainUser.class,
                                           ModuleTeaching.class, 
                                           ModuleSubject.class
                })
            }) 
    } 
)
public class DomainSecurityClass {
  // empty
}
