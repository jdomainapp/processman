package vn.com.processman.test;

import java.util.ArrayList;
import java.util.Collection;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpGen;
import jda.test.dodm.DODMEnhancedTester;
import vn.com.processman.setup.gen.SystemClass;

/**
 * @overview
 *  The main test driver application
 * 
 * @author dmle
 *
 */
public class TestProcessMan extends DODMEnhancedTester {
  
  //private SetUp su;
  private SetUpGen su;
  
  private static SystemDesc sysDesc;
  private static final Class SystemClass = SystemClass.class;
  
  static {
    // system descriptor
    sysDesc = SetUpGen.getSystemDesc(SystemClass);
    
    // insert system class into system property. This is needed to run su
    System.setProperty(PropertyName.setup_systemClass.getSysPropName(), SystemClass.getName());
  }
  
  @Override
  protected boolean isEmbedded() {
    return false;
  }

  @Override
  protected Configuration initClientServerConfiguration(String appName,
      String dataSourceName) {
    // use the same configuration as the setup
    SetUpBasic su = getDefaultSetUp();
    
    su.createApplicationConfiguration();
    
    return su.getConfig();
  }

  public String getDataSourceName() {
    String dsUrl = sysDesc.dsDesc().dsUrl();
    String dsName = dsUrl.substring(dsUrl.lastIndexOf("/")+1); 
    return dsName; //ProcessManSetUp.DataSourceName;
  }
  
//  @Override
//  public void initData() {
//    defaultInitData();
//  }
  
  public void initClasses() throws NotPossibleException {
    method("initClasses()");

    getDefaultSetUp();
    
    Collection<Class> modelClasses = su.getDomainModelClasses(); 
    // v3.3: filter from this classes that are in the app_config, app_security schemas
    if (modelClasses != null) {
      Collection<Class> domainModelClasses = new ArrayList<>();
      for (Class c : modelClasses) {
        if (!DSMBasic.isClassInSchema(c, DCSLConstants.CONFIG_SCHEMA, DCSLConstants.SECURITY_SCHEMA)) {
          domainModelClasses.add(c);
        }
      }
      
      domainClasses = domainModelClasses.toArray(new Class[domainModelClasses.size()]);
    }
  }
  
  public SetUpBasic getDefaultSetUp() {
    if (su == null)
      su = new SetUpGen(); 
      //su = new ProcessManSetUp();
    
    return su;
  }
  
  public SetUpBasic getClientServerSetUp() {
    return getDefaultSetUp();
  }
  
  public void registerConfigurationSchema() throws Exception {
    super.registerConfigurationSchema(getDefaultSetUp());
  }

  public void registerDomainSchema() throws Exception {
    super.registerDomainSchema(getDefaultSetUp());
  }
}
