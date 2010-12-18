/*
 * All Rights Reserved.
 * Sourcecode is licensed under Mozilla Public License 1.1
 */
package org.sapid.checker.eclipse.properties;

import java.io.File;

import org.eclipse.jface.preference.PathEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Field editor for a list of Jar and Zip files.
 * 
 * @author Ralf Schandl
 */
public class AbsolutePathEditor extends PathEditor {

  /** Default extensions. */
  private String[]                     extensions = { "*.xml" };

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
  protected AbsolutePathEditor() {
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
  public AbsolutePathEditor(String name, String labelText, String dirChooserLabelText, Composite parent) {
    super(name, labelText, dirChooserLabelText, parent);

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

    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell shell = workbench.getActiveWorkbenchWindow().getShell();
    FileDialog openDialog = new FileDialog(shell, SWT.OPEN);
    openDialog.setFilterExtensions(extensions);
    return openDialog.open();
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
      if ("".equals(files[i].trim()))
        continue;
      if (!(new File(files[i]).exists())) {
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

}