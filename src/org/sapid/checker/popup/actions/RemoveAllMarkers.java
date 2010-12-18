package org.sapid.checker.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.sapid.checker.eclipse.CheckerActivator;

public class RemoveAllMarkers implements IObjectActionDelegate {
  IFile file;

  /**
   * Constructor for Action1.
   */
  public RemoveAllMarkers() {
    super();
  }

  /**
   * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
   */
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

  /**
   * @see IActionDelegate#run(IAction)
   */
  public void run(IAction action) {
    try {
      file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      CheckerActivator.log(e);
    }
  }

  /**
   * @see IActionDelegate#selectionChanged(IAction, ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection) {
    if (selection instanceof StructuredSelection) {
      StructuredSelection ss = (StructuredSelection) selection;
      Object obj = ss.getFirstElement();
      if (obj instanceof IFile) {
        file = (IFile) obj;
      }
    }
  }
}
