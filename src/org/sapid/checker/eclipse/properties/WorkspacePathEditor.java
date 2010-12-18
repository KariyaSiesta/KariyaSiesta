/*
 * All Rights Reserved.
 * Sourcecode is licensed under Mozilla Public License 1.1
 */
package org.sapid.checker.eclipse.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;

/**
 * Field editor for a list of Jar and Zip files.
 * 
 * @author Ralf Schandl
 */
public class WorkspacePathEditor extends PathEditor {

  /**
   * Default selection valiadator for a ElementTreeSelectionDialog. Requires that a file
   * is selected.
   */
  private class DefaultSelectionValidator implements ISelectionStatusValidator {
    /**
     * {@inheritDoc}
     * 
     * @see ISelectionStatusValidator#validate(java.lang.Object[])
     */
    public IStatus validate(Object[] selection) {
      if (selection.length == 0) {
        return new Status(IStatus.ERROR, "Validator", IStatus.ERROR, "A file must be selected", null);
      }
      if (!(selection[0] instanceof IFile)) {
        return new Status(IStatus.ERROR, "Validator", IStatus.ERROR, "A file must be selected", null);
      }
      return new Status(IStatus.OK, "Validator", IStatus.OK, "", null);
    }
  }

  /**
   * Filters files from the ElementTreeSelectionDialog if they don't match any of the
   * given extensions.
   */
  private class ExtensionViewerFilter extends ViewerFilter {

    /** Holds the allowed file extension for the files to select. */
    private String[] fAllowedFileExtensions;

    /** Should we search in subdirs? */
    private boolean  fSearchRecursiv;

    /** List of files that should be excluded by the filter. */
    private List<IFile>     fExcludeList;

    /**
     * Constructor.
     * 
     * @param ext
     *          Array with allowed extensions
     * @param filesToExclude
     *          Array of files to exclude.
     * @param searchRecursive
     *          If true: also search in subdirs.
     */
    public ExtensionViewerFilter(String[] ext, IFile[] filesToExclude, boolean searchRecursive) {
      fAllowedFileExtensions = ext;
      fSearchRecursiv = searchRecursive;
      if (filesToExclude != null) {
        fExcludeList = Arrays.asList(filesToExclude);

      }

    }

    /**
     * {@inheritDoc}
     * 
     * @see ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (element instanceof IFile) {
        if (fExcludeList != null && fExcludeList.contains(element)) {
          return false;
        }

        return checkFileExtensions((IFile) element);
      } else if (element instanceof IContainer) { // IProject, IFolder
        if (!fSearchRecursiv) {
          return true;
        }
        try {
          IResource[] resources = ((IContainer) element).members();
          for (int i = 0; i < resources.length; i++) {
            // recursive! Only show containers that contain an archive
            if (select(viewer, parentElement, resources[i])) {
              return true;
            }
          }
        } catch (CoreException e) {
          CheckerActivator.log("CoreException while getting members for a IContainer", e);
        }
      }
      return false;
    }

    /**
     * Checks if the file has a allowed extension.
     * 
     * @param file
     *          The IFile object.
     * @return True or False.
     */
    private boolean checkFileExtensions(IFile file) {
      String ext = file.getFileExtension();

      for (int i = 0; i < fAllowedFileExtensions.length; i++) {
        if (fAllowedFileExtensions[i].equalsIgnoreCase(ext)) {
          return true;
        }
      }
      return false;
    }
  }

  /** Default extensions. */
  private String[]                     extensions = { "xml", "XML" };

  /** The parent composite for the viewer. */
  private Composite                    parentShell;

  /** The current project. */
  private IProject                     project;

  /**
   * The list widget; <code>null</code> if none (before creation or after disposal).
   */
  private org.eclipse.swt.widgets.List listWidget;

  /** Is this editor vaild? */
  private boolean                      valid      = true;

  /** Store the old value to fire property change events. */
  private String                       oldValue;

  /**
   * This constructor is not needed.
   */
  protected WorkspacePathEditor() {
    super();
  }

  /**
   * @param name
   *          Name of the field to edit.
   * @param labelText
   *          Label for the field in the property page.
   * @param dirChooserLabelText
   *          Lable for the chooser window.
   * @param parent
   *          The parent composite.
   * @param aProject
   *          The current project (not used currently).
   */
  public WorkspacePathEditor(String name, String labelText, String dirChooserLabelText, Composite parent,
      IProject aProject) {
    super(name, labelText, dirChooserLabelText, parent);
    parentShell = parent;
    project = aProject;

    listWidget = getListControl(parent);

    /*
     * The Listeditor does not fire a change event, if its value was changed, so we have
     * to implement this here. The only chance (or: the only working way I found) is to
     * add a listener to a paint event. Whenever the number of entries in the list changes
     * we call valueChanged, which will fire the change event if needed (and it is
     * needed!). If a repaint is done for any other reason, we just ignore it.
     * 
     */
    listWidget.addPaintListener(new PaintListener() {
      /** Number of entries in the list widget of the ListEditor. */
      private int listEntries = 0;

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
       */
      public void paintControl(PaintEvent e) {
        if (listWidget.getItemCount() != listEntries) {
          listEntries = listWidget.getItemCount();
          valueChanged();
        }
      }
    });
  }

  /**
   * Overwritten her, so we can initialize the field oldValue (needed to detect vlue
   * changes).
   * 
   * @see org.eclipse.jface.preference.FieldEditor#doLoad().
   */
  protected void doLoad() {
    super.doLoad();
    oldValue = getValue();
  }

  /**
   * Overwritten her, so we can triger a value change event.
   * 
   * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault().
   */
  protected void doLoadDefault() {
    super.doLoadDefault();
    valueChanged();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.jface.preference.ListEditor#getNewInputObject()
   */
  protected String getNewInputObject() {

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

    // Create list of already selected files
    String[] usedEntries = getListControl(parentShell).getItems();
    ArrayList<IResource> usedFiles = new ArrayList<IResource>(usedEntries.length);
    for (int i = 0; i < usedEntries.length; i++) {
      IResource resource = root.findMember(usedEntries[i]);
      if (resource instanceof IFile) {
        usedFiles.add(resource);
      }
    }

    ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
        new WorkbenchContentProvider());
    dialog.setTitle(Messages.getString("WorkspacePathEditor.0"));
    dialog.setMessage(Messages.getString("WorkspacePathEditor.1"));
    dialog.setAllowMultiple(false);
    dialog.setInput(root);
    dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
    dialog.addFilter(new ExtensionViewerFilter(extensions, (IFile[]) usedFiles.toArray(new IFile[usedFiles.size()]),
        true));
    dialog.setValidator(new DefaultSelectionValidator());

    if (dialog.open() != Window.OK) {
      return null;
    }

    Object[] elements = dialog.getResult();
    if(elements.length>0) {
        // TODO: 複数対応部分
	    IFile res = (IFile) elements[0];
	    return res.getFullPath().toString();
    } else {
    	return null;
    }

  }

  /**
   * Overwritten here to use a fix separator to make the settings portable between
   * different OSes. Choosed the char '|' as it is (afaik) no valid file name char in the
   * OSes I know.
   * 
   * {@inheritDoc}
   */
  protected String createList(String[] items) {
    return PropertyParser.unparsePath(items);
  }

  /**
   * Overwritten here to use a fix separator to make the settings portable between
   * different OSes. Choosed the char '|' as it is (afaik) no valid file name char in the
   * OSes I know.
   * 
   * {@inheritDoc}
   */
  protected String[] parseString(String stringList) {
    return PropertyParser.parsePath(stringList);
  }

  /**
   * @return the current validation state.
   */
  public boolean isValid() {
    refreshValidState();
    return valid;
  }

  /**
   * Informs this field editor's listener, if it has one, about a change to the value (<code>VALUE</code>
   * property) provided that the old and new values are different.
   */
  protected void valueChanged() {
    setPresentsDefaultValue(false);
    boolean oldState = valid;
    refreshValidState();

    if (valid != oldState) {
      fireStateChanged(IS_VALID, oldState, valid);
    }

    String newValue = getValue();
    if (!newValue.equals(oldValue)) {
      fireValueChanged(VALUE, oldValue, newValue);
      oldValue = getValue();
    }
  }

  /**
   * @return The current entries of the ListEditor as a concatinated String.
   */
  private String getValue() {
    return createList(listWidget.getItems());
  }

  /**
   * @see org.eclipse.jface.preference.FieldEditor#refreshValidState().
   */
  protected void refreshValidState() {
    valid = true;
    StringBuffer invalidFiles = new StringBuffer();

    String[] files = listWidget.getItems();

    for (int i = 0; i < files.length; i++) {
      if (!checkFileExists(files[i])) {
        valid = false;
        invalidFiles.append("\n");
        invalidFiles.append(files[i]);
      }
    }
    if (!valid) {
      StringBuffer msg = new StringBuffer("List contains invalid files:");
      msg.append(invalidFiles);
      listWidget.setToolTipText(msg.toString());
    } else {
      listWidget.setToolTipText(null);
    }
  }

  /**
   * Checks if the given pathname existsa for the current project.
   * 
   * @param relativePath
   *          Relative pathname to check for existance.
   * @return True if the file exists.
   */
  private boolean checkFileExists(String relativePath) {

    if (!"".equals(relativePath)) {
      IResource resource = project.getWorkspace().getRoot().findMember(relativePath);
      if (resource == null || !resource.exists()) {
        return false;
      }
    }

    return true;
  }
}