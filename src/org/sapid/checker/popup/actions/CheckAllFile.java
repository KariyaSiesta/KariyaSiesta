package org.sapid.checker.popup.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.ProgressJob;
import org.sapid.checker.eclipse.progress.CheckAllWithProgress;
import org.sapid.checker.eclipse.properties.PropertyStore;

public class CheckAllFile implements IObjectActionDelegate {
    private IResource projectRes;
    private List<IFile> targetFiles = null;
    private final static String TARGET_EXTENSION_C = "c";
    private final static String TARGET_EXTENSION_H = "h";

    /**
     * Constructor for Action1.
     */
    public CheckAllFile() {
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
        IResource project = projectRes;
        try {
            targetFiles = listUp(((IContainer) project).members());
            CheckAllWithProgress searchThread = new CheckAllWithProgress();
            searchThread.setFile(targetFiles);
            searchThread.setProjectName(project.getName());

            // プロジェクトの設定を取得
            IProject prj;
            if (project instanceof IProject) {
                prj = (IProject) project;
            } else {
                prj = project.getProject();
            }
            IFile ruleXML = PropertyStore.getProjectSettingAsFile(prj);
            searchThread.setRuleXML(ruleXML.getRawLocation().toOSString());

            Job job = new ProgressJob("Check All Files",searchThread);
            job.setUser(true);
            job.schedule();
        }catch (Exception e) {
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
            if (obj instanceof IProject) {
                projectRes = (IProject) obj;
            } else if (obj instanceof IFolder) {
                projectRes = (IFolder) obj;
            }
        }
    }

    static public List<IFile> listUp(IResource resList[]) {
        List<IFile> list = new ArrayList<IFile>();
        for (int i = 0; i < resList.length; i++) {
            try {
                IResource res = resList[i];
                if (resList[i] == null) {
                    continue;
                } else if (res instanceof IFolder) {
                    if (!res.getName().startsWith(".")) {
                        list.addAll(listUp(((IFolder) res).members()));
                    }
                } else if (res instanceof IFile) {
                    String ext = ((IFile) res).getFileExtension();
                    if (TARGET_EXTENSION_C.equalsIgnoreCase(ext)
                            || TARGET_EXTENSION_H.equalsIgnoreCase(ext)) {
                        list.add((IFile) res);
                    }
                }
            } catch (Exception e) {
                CheckerActivator.log(e);
            }
        }
        return list;
    }
}
