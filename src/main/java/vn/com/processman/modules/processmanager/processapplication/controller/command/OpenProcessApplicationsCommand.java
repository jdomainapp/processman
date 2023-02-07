/**
 * @overview
 *
 * @author dmle
 */
package vn.com.processman.modules.processmanager.processapplication.controller.command;

import java.util.ArrayList;
import java.util.Collection;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.FlexiQuery;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.model.Oid;
import vn.com.processman.modules.processapplication.model.OrgUnit;
import vn.com.processman.modules.processapplication.model.ProcessApplication;
import vn.com.processman.modules.processmanager.processapplication.model.ProcessApplicationManager;
import vn.com.processman.modules.processstructure.model.Process;
import vn.com.processman.util.model.Semester;

/**
 * @overview
 *  A {@link DataControllerCommand} that is used to retrieve a {@link ProcessApplication} <tt>n</tt> from a given process code
 *  and other details provided in a {@link ProcessApplicationManager} <tt>m</tt> and display the {@link Process} 
 *  referenced in <tt>n</tt> on the view 
 *  
 * @author dmle
 */
public class OpenProcessApplicationsCommand<C> extends DataControllerCommand {

  private FlexiQuery query;
  private FlexiQuery nestedQuery;

  public OpenProcessApplicationsCommand(DataController<C> dctl) {
    super(dctl);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {

    DataController<Process> dctl = getDataController();
    
    ProcessApplicationManager m = (ProcessApplicationManager) dctl.getParent().getCurrentObject();
    final OrgUnit orgUnit = m.getOrgUnit();
    final String processCode = m.getProcessCode();
    final Semester sem = m.getSemester();
    final int year = m.getYear();

    DODMBasic dodm = dctl.getDodm();
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();

    // find ProcessApplication    
    if (query == null) {
      query = new FlexiQuery(dsm, ProcessApplication.class);
      
      // join exp: ProcessApplication and Process
      query.addJoinExpression(
          new Class[] {ProcessApplication.class, Process.class},  
          new String[] {Process.Assoc_ProcessAndProcessApplication}
          );
      
      // nested query: select Process.id where Process.code =processCode
      nestedQuery = new FlexiQuery(dsm, Process.class);
      nestedQuery.addDomainAttribute(Process.A_id);

      // exp: Process.def in (select Process.id where Process.code=processCode)
      query.addConstraintExpression(Process.class, Process.A_def, Op.IN, nestedQuery);
    }

    Expression exp1, exp2, exp3;
    // exps: ProcessApplication.orgUnit=orgUnit, ProcessApplication.semester=sem, ProcessApplication.year=year
    exp1 = query.addConstraintExpression(ProcessApplication.A_orgUnit, Op.EQ, orgUnit);
    exp2 = query.addConstraintExpression(ProcessApplication.A_semester, Op.EQ, sem);
    exp3 = query.addConstraintExpression(ProcessApplication.A_year, Op.EQ, year);
    
    // update nested query
    String attrib = Process.A_code;
    Op op = Op.EQ;
    nestedQuery.addConstraintExpression(attrib, op, processCode);

    // 2. execute query to retrieve data 
    Collection<Oid> oids = null;
    
    try {
      oids = dom.retrieveObjectOids(ProcessApplication.class, query);
    } catch (Exception e) {
      throw e;
    } finally {
      query.remove(exp1); query.remove(exp2); query.remove(exp3);
      nestedQuery.removeAll();
    }

    // 3. display result
    if (oids != null) {
      // retrieve the objects
      final DAttr refAttrib = dsm.getDomainConstraint(ProcessApplication.class, ProcessApplication.A_process);
      Collection<Oid> procIds = new ArrayList();

      Oid procId;
      for (Oid oid : oids) {
        try {
          // load the ids
  
          // classes
          procId = dom.retrieveAssociateOid(ProcessApplication.class, oid, refAttrib,
              Process.class);
  
          if (procId != null) {
            procIds.add(procId);
          }
        } catch (Exception e) {
          // dctl.displayError(this.getClass().getSimpleName()+"filter(): ", e);
          e.printStackTrace();
        }
      }
      
      // IMPORTANT: needs to open metadata before doing this (because otherwise dctl is in the Opened state
      // and subsequent attempt to initialise its metadata (e.g to create new object) will be ignored)
      if (!dctl.isOpenMetadata())
        dctl.openMetadata();
      
      // open result on the data container
      dctl.open(procIds);
    } else {
      ControllerBasic parentCtl = dctl.getParent().getCreator();
      String parentObjName = parentCtl.getDomainClassLabel() + " (" + m.getId() + ")";
      String functionTitle = parentCtl.getModuleTitle();
      parentCtl.displayMessageFromCode(MessageCode.NO_OBJECTS_FOUND_FOR_FUNCTION, dctl,
          new Object[] {parentObjName, functionTitle}
          );
    }  
  }
}
