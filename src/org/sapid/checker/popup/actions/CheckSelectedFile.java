package org.sapid.checker.popup.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;
import org.sapid.checker.eclipse.progress.CheckWithProgress;
import org.sapid.checker.eclipse.properties.PropertyStore;

public class CheckSelectedFile implements IObjectActionDelegate {
    IFile file;

    /**
     * Constructor for Action1.
     */
    public CheckSelectedFile() {
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

        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        Shell shell = window.getShell();

        // プログレスバーを使う
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
        CheckWithProgress searchThread = new CheckWithProgress();
        searchThread.setFile((IFile) file);

        try {
            // プロジェクトの設定を取得
            IFile ruleXML = PropertyStore.getProjectSettingAsFile(file
                    .getProject());
            searchThread.setRuleXML(ruleXML.getRawLocation().toOSString());
        } catch (IOException e) {
            CheckerActivator.log(e);
            return;
        }
        try {
            dialog.run(true, true, searchThread);
        } catch (InvocationTargetException e) {
            CheckerActivator.log(e);
        } catch (InterruptedException e) {
            if (!e.getMessage().equals(
                    Messages.getString("CheckWithProgress.CANCELD"))) {
                MessageDialog.openInformation(Display.getCurrent()
                        .getActiveShell(), "Checker Error", e.getMessage());
            }
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
			} else if (obj instanceof ITranslationUnit) {
				file = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(((ITranslationUnit) obj).getPath());
            }
        }
    }

}
