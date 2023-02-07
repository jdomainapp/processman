package vn.com.processman.test.processstructure.browser;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dodm.DODMBasic;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.dodm.DODMTesterWithBrowse.BrowsingStep;
import vn.com.processman.test.TestProcessMan;

public class BrowserMasterTest extends TestProcessMan {
  
  protected static void init() throws Exception {
    instance.registerClasses();
  }
  
  /**
   * @requires 
   *  {@link #init()}
   */
  public <T> void  browseFirstToLast(Class<T> c) throws Exception {
    System.out.printf("%s.browseFirstToLast(%s)%n",this.getClass().getSimpleName(),c.getSimpleName());
    
    DODMBasic schema = instance.getDODM();
    
    try {
      DODMTesterWithBrowse.browseFirstToLast(schema, c, false);
    } catch (NotFoundException e) {
      e.printStackTrace();  // print error to console before throwing
      throw e;
    }
  }
  
  /**
   * @requires 
   *  {@link #init()}
   */
  public <T> void browseSomeRandomly(Class<T> c) throws Exception { 
    System.out.printf("%s.browseSomeRandomly(%s)%n",this.getClass().getSimpleName(),c.getSimpleName());
    
    DODMBasic schema = instance.getDODM();
    
    DODMTesterWithBrowse.browseRandom(schema, c, false);
  }
  
  /**
   * @requires 
   *  {@link #init()}
   */
  public <T> void browseValidation(Class<T> c) throws Exception { 
    System.out.printf("%s.browseValidation(%s)%n",this.getClass().getSimpleName(),c.getSimpleName());
    
      
    DODMBasic schema = instance.getDODM();
    
    BrowsingStep[] steps = {
        BrowsingStep.FIRST, 
        BrowsingStep.LAST,
        BrowsingStep.PREV
    };
    
    DODMTesterWithBrowse.browse(schema, c, steps, false);
  }
  
  /**
   * @requires 
   *  {@link #init()}
   */
  public <T> void browseValidation2(Class<T> c) throws Exception { 
    System.out.printf("%s.browseValidation2(%s)%n",this.getClass().getSimpleName(),c.getSimpleName());
    
      
    DODMBasic schema = instance.getDODM();
    
    BrowsingStep[] steps = {
        BrowsingStep.FIRST, 
        BrowsingStep.LAST,
    };
    
    DODMTesterWithBrowse<T> browser = DODMTesterWithBrowse.browse(schema, c, steps, false);
    
    printf("Browser buffer count: %d%n", browser.getObjectBufferSize());
    
    // browse first to last
    //browser.browseFirstToLast();
    browser.browseLastToFirst();

    printf("Browser buffer count: %d%n", browser.getObjectBufferSize());

    steps = new BrowsingStep[] {
      BrowsingStep.PREV, 
      BrowsingStep.NEXT,
      BrowsingStep.LAST,
      BrowsingStep.PREV, 
    };
    
    browser.browse(steps, null);
    
    printf("Browser buffer count: %d%n", browser.getObjectBufferSize());
  }
}
