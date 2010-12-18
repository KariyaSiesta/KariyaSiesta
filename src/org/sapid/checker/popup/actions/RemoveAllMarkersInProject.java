package org.sapid.checker.popup.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.sapid.checker.eclipse.CheckerActivator;

public class RemoveAllMarkersInProject implements IObjectActionDelegate {
    IFile file;
    IResource projectRes;
    List<IFile> targetFiles = new ArrayList<IFile>();

    /**
     * Constructor for Action1.
     */
    public RemoveAllMarkersInProject() {
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
        IResource prj = (IResource) projectRes;
        try {
            IResource resList[] = ((IContainer) prj).members();
            targetFiles = CheckAllFile.listUp(resList);

            for (IFile file : targetFiles) {
                file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
            }

        } catch (CoreException e) {
            CheckerActivator.log(e);
        }
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        if (!(selection instanceof StructuredSelection)) {
            return;
        }
        StructuredSelection ss = (StructuredSelection) selection;
        Object obj = ss.getFirstElement();
        if (obj instanceof IProject) {
            projectRes = (IProject) obj;
        } else if (obj instanceof IFolder) {
            projectRes = (IFolder) obj;
        }
    }

}
