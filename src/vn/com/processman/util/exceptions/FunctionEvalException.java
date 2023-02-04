package vn.com.processman.util.exceptions;

import java.text.MessageFormat;

import domainapp.basics.exceptions.ApplicationRuntimeException;
import domainapp.basics.util.InfoCode;

public class FunctionEvalException extends ApplicationRuntimeException {

  public static enum Code implements InfoCode {
    
    ;

    private String text;
    
    private Code(String text) {
      this.text = text;
    }
    
    @Override
    public String getText() {
      return text;
    }

    /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
    private MessageFormat messageFormat;
    
    @Override
    public MessageFormat getMessageFormat() {
      if (messageFormat == null) {
        messageFormat = new MessageFormat(text);
      }
      
      return messageFormat;
    }
  };  // end Code
  
  public FunctionEvalException(InfoCode errCode, Object[] args) {
    super(errCode, args);
  }

  public FunctionEvalException(InfoCode errCode, Throwable e, Object... args) {
    super(errCode, e, args);
  }
  
}
