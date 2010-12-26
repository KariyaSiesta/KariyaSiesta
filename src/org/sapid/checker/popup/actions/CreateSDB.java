/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.popup.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.sapid.checker.eclipse.cdt.CResourceConfigUtil;
import org.sapid.checker.eclipse.progress.CreateSDBJob;

/**
 * 「SDB を作成」のアクションデリゲータ<br>
 * Makefile の右クリックメニューと紐付けされる
 * @author Toshinori OUSKA
 */
public class CreateSDB implements IObjectActionDelegate {
	private IResource selectedItem = null;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		List<IFile> files = new ArrayList<IFile>();
		String projectRealPath = selectedItem.getProject().getLocation()
				.toFile().getAbsolutePath();
		if (selectedItem instanceof IFile) {
			files.add((IFile) selectedItem);
		} else if (selectedItem instanceof IContainer) { // folder or project
			IContainer container = (IContainer) selectedItem;
			try {
				files.addAll(findCandHFilesRecursive(container));
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Job job = new CreateSDBJob(projectRealPath,
				(IFile[]) files.toArray(new IFile[files.size()]));
		job.schedule();
	}

	/**
	 * ruby の Array#join 風
	 * @param list
	 * @param sp
	 * @return
	 */
	@SuppressWarnings("unused")
	private String joinArray(List<?> list, String sp) {
		StringBuffer buffer = new StringBuffer();
		for (Iterator<?> itr = list.iterator(); itr.hasNext();) {
			buffer.append(itr.next().toString());
			buffer.append(sp);
		}
		return buffer.toString();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object obj = ss.getFirstElement();
			if (obj instanceof IFile) {
				selectedItem = (IFile) obj;
			} else if (obj instanceof IProject) {
				selectedItem = (IProject) obj;
			} else if (obj instanceof ITranslationUnit) {
				selectedItem = ((ITranslationUnit) obj).getResource();
			}
		}
	}

	/**
	 * gather all *.c and *.h files.
	 * @param container project or folder
	 * @return all *.c and *.h files (excludes: not build target)
	 * @throws CoreException
	 */
	private List<IFile> findCandHFilesRecursive(IContainer container)
			throws CoreException {
		List<IFile> files = new ArrayList<IFile>();
		IResource[] resources = container.members();
		for (IResource resource : resources) {
			if (resource instanceof IFile) {
				String ext = resource.getFileExtension();
				if (!("c".equalsIgnoreCase(ext))
						&& !("h".equalsIgnoreCase(ext))) {
					continue;
				}
				if (CResourceConfigUtil.isExcludeResource((IFile)resource)) {
					continue;
				}
				files.add((IFile) resource);
			} else if (resource instanceof IContainer) {
				files.addAll(findCandHFilesRecursive((IContainer) resource));
			}
		}
		return files;
	}

}
