package org.sapid.checker.eclipse;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;

/***
 * IRunnableWithProgressをJobに変換するクラス
 * 
 * @author mzp
 */
public class ProgressJob extends Job {
	private IRunnableWithProgress progress;
	
	public ProgressJob(String name, IRunnableWithProgress progress){
		super(name);
		this.progress = progress;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try{
			this.progress.run(monitor);
			return Status.OK_STATUS;
		}catch(InvocationTargetException e){
			return new Status(IStatus.ERROR, CheckerActivator.PLUGIN_ID, e.toString());
		}catch(InterruptedException e){
			return new Status(IStatus.ERROR, CheckerActivator.PLUGIN_ID, e.toString());
		}
	}
}
