package vn.com.processman.modules.processdeliverables.model;

import vn.com.processman.modules.processapplication.model.SubjectAction;
import vn.com.processman.modules.processstructure.model.Action;
import vn.com.processman.modules.teaching.subjectbysem.model.SubjectBySemester;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Tuple;
import domainapp.basics.util.io.ToolkitIO;

/**
 * @effects 
 *  Represents a file object.
 *  
 * @author dmle
 */
public class FileWrapper {

  public static final String A_name = "name";
  public static final String A_type = "type";
  //public static final String A_url = "url";
  public static final String A_file = "file";
  public static final String A_subj = "subj";
  
  /**
   * non-id, essential attributes needed for the non-id constructor to create objects
   */
  public static final String[] A_EssentialCreateNews = {A_name, A_type, A_file};

  @DAttr(name="id", type=Type.Integer, id=true, auto=true, mutable=false, optional=false)
  private int id;
  private static int idCounter = 0;

  /**derived from {@link #file} */
  @DAttr(name=A_name, type=Type.String, auto=true, mutable=false,length=255)
  private String name;

  @DAttr(name=A_type, type=Type.Domain, optional=false, defaultValueFunction=true)
  private FileType type;

//  /**derived from {@link #file} */
//  @DomainConstraint(name=A_url, type=Type.String, auto=true, mutable=false, length=255)
//  private String url;

  @DAttr(name=A_file, type=Type.File, length=30)
  private java.io.File file;
  
  /** the {@link SubjectBySemester} the deliverable of which is this*/
  @DAttr(name=A_subj, type=Type.Domain)
  private SubjectBySemester subj;
  
  /** the {@link Action} the output of which is this*/
  @DAssoc(ascName = Action.Assoc_ActionAndFile, role = "file",
      ascType = AssocType.One2One, endType = AssocEndType.One, 
      associate = @Associate(type=Action.class, cardMin=1, cardMax=1, determinant=true))  
  @DAttr(name="action", type=Type.Domain, serialisable=false)
  private Action action;

  /** the {@link SubjectAction} the output of which is this*/
  @DAssoc(ascName = SubjectAction.Assoc_SubjectActionAndFile, role = "file",
      ascType = AssocType.One2One, endType = AssocEndType.One, 
      associate = @Associate(type=SubjectAction.class, cardMin=1, cardMax=1, determinant=true))  
  @DAttr(name="subjectAction", type=Type.Domain, serialisable=false)
  private SubjectAction subjectAction;
  
  /**
   * Constructor: from data source
   * 
   * @modifies file
   * 
   * @effects 
   *  rename <tt>file</tt> to have <tt>name</tt>
   *  initialise <tt>this</tt> using the arguments
   */
  public FileWrapper(Integer id, String name, FileType type, java.io.File file, SubjectBySemester subj) throws NotPossibleException {
    this.id = nextID(id);
    this.type = type;
    this.subj = subj;
    
    this.name = name;
    //this.url = url;

    // rename this.file to the standard file name (if not already)
    if (file != null)
      file = ToolkitIO.renameFile(file, name);
    
    this.file = file;
  }

  /**
   * Base constructor used for object forms
   * @requires file != null
   */
  public FileWrapper(FileType type, java.io.File file, SubjectBySemester subj) {
    this.id = nextID(null);
    this.file = file;
    this.type = type;
    this.subj = subj;
    
    if (file != null)
      this.name = file.getName();
    //this.url = file.getPath();
    
  }
  
//  // constructor: from object form (for a subject)
//  public File(
//      //String name, 
//      FileType type, 
//      //String url, 
//      java.io.File file, SubjectBySemester subj) {
//    this(null, file.getName(), type, file.getPath(), file, subj);
//  }
  
  // constructor: from object form (for non-subject)
  public FileWrapper(
      //String name, 
      FileType type, 
      //String url, 
      java.io.File file) {
    this(type, file, null);
  }

  public String getName() {
    return name;
  }

//  public void setName(String name) {
//    this.name = name;
//  }

  public FileType getType() {
    return type;
  }

  public void setType(FileType type) {
    this.type = type;
  }

  @DOpt(type=DOpt.Type.DefaultValueFunction)
  @AttrRef(value=A_type)
  public static FileType getDefaultFileType() {
    return FileType.Action_Output;
  }
  
//  public String getUrl() {
//    return url;
//  }

//  public void setUrl(String url) {
//    this.url = url;
//  }


  public java.io.File getFile() {
    return file;
  }

  public void setFile(java.io.File file) {
    this.file = file;
    
    // update derived attributes
    if (file != null) {
      this.name = file.getName();
      //this.url = file.getPath();
    } else {
      this.name = null;
      //this.url = null;
    }
  }
  
  public SubjectBySemester getSubj() {
    return subj;
  }

  public void setSubj(SubjectBySemester subj) {
    this.subj = subj;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public SubjectAction getSubjectAction() {
    return subjectAction;
  }

  public void setSubjectAction(SubjectAction subjectAction) {
    this.subjectAction = subjectAction;
  }

  public int getId() {
    return id;
  }
  
  // util methods
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter) {
        idCounter = num;
      }
      return currID;
    }
  }

  /**
   * @requires minVal != null /\ maxVal != null
   * @effects update the auto-generated value of attribute <tt>attrib</tt>,
   *          specified for <tt>derivingValue</tt>, using
   *          <tt>minVal, maxVal</tt>
   */
  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(DAttr attrib,
      Tuple derivingValue, Object minVal, Object maxVal)
      throws ConstraintViolationException {
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)
          idCounter = maxIdVal;
      }
      // TODO add support for other attributes here
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FileWrapper other = (FileWrapper) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "File (" + id + ", " + name + ", " + type + ")";
  }
}
