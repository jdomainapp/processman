package vn.com.processman.util.message;

import java.text.MessageFormat;

import domainapp.basics.util.InfoCode;

/**
 * @overview
 *  Define message codes for application
 *  
 * @author dmle
 * @version 3.2
 */
public enum DomainMessageCode implements InfoCode {

  /**
   * 0: process code (name) 
   */
  CONFIRM_APPLY_SELECTED_SUBJECTS_TO_PROCESS("Bạn có muốn áp dụng môn học đã chọn cho quy trình: {0}?"),
  /**
   * 0: user name
   * 1: semester
   * 2: year
   */
  TEACHER_NOT_TEACHING_IN_SEMESTER("Bạn ({0}) không tham gia giảng dạy trong học kì: {1} {2}"),
  /**
   * 0: user name
   * 1: semester
   * 2: year
   */  
  USER_PLAYS_NO_ROLE_IN_SEMESTER("Bạn ({0}) không tham gia vai trò nào trong học kì: {1} {2}"),
  /**
   *  0: process (code)
   *  1: process application period (e.g. semester year) 
   */
  ERROR_DUPLICATE_PROCESS_APPLICATION("Đã có một phiên bản khác của quy trình {0} được áp dụng cho {1}. Bạn cần xóa phiên bản này hoặc chọn một quy trình khác."),
  /**
   * 0: process (code)
   */
  ERROR_PROCESS_CODE_NOT_FOR_NON_SUBJECT("Mã {0} không phải của quy trình không dành cho môn học"), 
  /**
   * 0: process (code)
   */
  ERROR_PROCESS_CODE_NOT_FOR_SUBJECT("Mã {0} không phải của quy trình dành cho môn học"),
  ERROR_SUBJECT_ACTION_STATUS_CANNOT_BE_DONE("Bước làm môn học hiện tại chưa thể hoành thành vì có một số điều kiện trước hoặc/và sau chưa được thỏa mãn"),
  WARN_SUBJECT_ACTION_STATUS_SYSTEM_OVERWRITE("Lưu ý: chỉnh sửa trạng thái hoàn thành bước làm này có thể bị hệ thống tự động cập nhật lại"),
  ;
  
  private String text;

  /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
  private MessageFormat messageFormat;

  private DomainMessageCode(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }
  
  @Override
  public MessageFormat getMessageFormat() {
    if (messageFormat == null) {
      messageFormat = new MessageFormat(text);
    }
    
    return messageFormat;
  }
  
}
