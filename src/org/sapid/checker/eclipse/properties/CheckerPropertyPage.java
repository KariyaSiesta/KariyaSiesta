package org.sapid.checker.eclipse.properties;

import java.io.IOException;

import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;

/**
 * 強引にFieldEditorPreferencePageをPropertyPageとして使うためのクラス
 * 
 * @author keigoi
 * 
 */
public class CheckerPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

  /**
   * The element.
   */
  private IAdaptable element;

  /**
   * @return the element associated with this property page
   */
  public IAdaptable getElement() {
    return element;
  }

  /** @return project for this page */
  private IProject getProject() {
    return (IProject) getElement().getAdapter(IProject.class);
  }

  /**
   * Sets the element that owns properties shown on this page.
   * 
   * @param element
   *          the element
   */
  public void setElement(IAdaptable element) {
    this.element = element;
    PropertyStore prefs = getPropertyStore();
    prefs.setProject(getProject());
    try {
      prefs.load();
    } catch (IOException ex) {
      CheckerActivator.log(Messages.getString("JSPCheckerPropertyPage.PROP_FAILLOAD"), ex); //$NON-NLS-1$
    }
  }

  /**
   * Constructor for SamplePropertyPage.
   */
  public CheckerPropertyPage() {
    super(FieldEditorPreferencePage.GRID);
  }

  @Override
  protected void createFieldEditors() {
    addField(new WorkspacePathEditor(PropertyStore.ATTR_WORKSPACE_XMLS, Messages.getString("JSPCheckerPropertyPage.XPATH_RULEXML_WS"), //$NON-NLS-1$
        Messages.getString("JSPCheckerPropertyPage.SELECT_XPATHRULEXML"), getFieldEditorParent(), getProject())); //$NON-NLS-1$
    addField(new AbsolutePathEditor(PropertyStore.ATTR_ABSOLUTE_XMLS, Messages.getString("JSPCheckerPropertyPage.XPATH_RULEXML_FS"), Messages.getString("JSPCheckerPropertyPage.SELECT_RULEXML"), //$NON-NLS-1$ //$NON-NLS-2$
        getFieldEditorParent()));
    addField(new ModuleEditor(PropertyStore.ATTR_OTHER_MODULES, Messages.getString("JSPCheckerPropertyPage.JAVA_RULEMODULE"), getFieldEditorParent())); //$NON-NLS-1$
  }

  @Override
  public boolean performOk() {
    // バックアップ
    PropertyStore backup = new PropertyStore();
    backup.setProject(getProject());
    initializePropertyStore(backup);

    boolean isOk = super.performOk();

    if (isOk) {
      try {
        getPropertyStore().save();
      } catch (IOException e) {
        MessageDialog.openInformation(this.getShell(), Messages.getString("JSPCheckerPropertyPage.JSPCHECKERPLUGIN"), Messages.getString("JSPCheckerPropertyPage.PROP_FAILSAVE")); //$NON-NLS-1$ //$NON-NLS-2$
        setPropertyStore(backup);
        return false;
      }
    } else {
      setPropertyStore(backup);
      return false;
    }

    return true;
  }

  public PropertyStore getPropertyStore() {
    PropertyStore store = (PropertyStore) getPreferenceStore();
    if (store == null) {
      store = new PropertyStore();
      setPropertyStore(store);
    }
    return store;
  }

  public void setPropertyStore(PropertyStore store) {
    setPreferenceStore(store);
  }

  @Override
  protected void initialize() {
    initializePropertyStore(getPropertyStore());
    super.initialize();
  }

  private void initializePropertyStore(PropertyStore store) {

  }
}