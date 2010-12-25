package org.sapid.checker.eclipse.progress;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.sapid.checker.cx.command.Command;
import org.sapid.checker.cx.command.CommandOutput;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;
import org.eclipse.core.runtime.Status;

/***
 * SDB生成をバックグラウンドで行なうためのジョブ
 *
 * @author mzp
 */
public class CreateSDBJob extends Job {
	private String curDir;

	private String makefile = "Makefile";

	/**
	 * constructor
	 * @param curDir
	 * @param makefile
	 */
	public CreateSDBJob(String curDir, String makefile) {
		super("Create SDB");
		this.curDir = curDir;
		if (makefile != null) {
			this.makefile = makefile;
		}
	}

	/**
	 * @return the curDir
	 */
	public String getCurDir() {
		return curDir;
	}

	/**
	 * @param curDir the curDir to set
	 */
	public void setCurDir(String curDir) {
		this.curDir = curDir;
	}

	/**
	 * @return the makefile
	 */
	public String getMakefile() {
		return makefile;
	}

	/**
	 * @param makefile the makefile to set
	 */
	public void setMakefile(String makefile) {
		this.makefile = makefile;
	}

	protected IStatus report(int status,String message){
		return new Status(status, CheckerActivator.PLUGIN_ID, message);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final int TASK_AMOUNT = 3;
		monitor.beginTask("Chcker", TASK_AMOUNT);

		String sapidDest = CheckerActivator.getDefault().getPreferenceStore()
				.getString("SAPID_DEST");
		if (!new File(sapidDest).exists()) {
			return this.report(IStatus.ERROR,Messages.getString("CreateSDBProgress.0"));
		}
		try {
			String sapidDestUnix = sapidDest;
			if (System.getProperty("os.name").contains("Windows")) {
				// cygpath
				monitor.setTaskName("cygpath");
				sapidDestUnix = kickCygPath(monitor, sapidDest);
			} else {
				monitor.setTaskName("path");
			}
			monitor.worked(1);

			if (monitor.isCanceled()) {
				return this.report(IStatus.CANCEL, Messages.getString("CheckWithProgress.CANCELD"));
			}

			// sdb4
			ProgressDialogOutput output = new ProgressDialogOutput(monitor);
			int exitValue = kickSDB4(monitor, sapidDestUnix, output);
			if (exitValue != 0) {
				return this.report(IStatus.ERROR, output.getStored());
			}
			output.setStored("");
			if (monitor.isCanceled()) {
				return this.report(IStatus.CANCEL, Messages.getString("CheckWithProgress.CANCELD"));
			}
			// spdMkCXModel
			exitValue = kickSpdMkCXModel(monitor, sapidDestUnix, output);
			if (exitValue != 0) {
				return this.report(IStatus.ERROR, output.getStored());
			}
		} catch (IOException e) {
			return this.report(IStatus.ERROR, e.toString());
		}

		monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * Windows パスの SAPID_DEST から Unix パスを取得するために CygPath を kick する
	 * 
	 * @param monitor
	 * @param sapidDest
	 * @return
	 * @throws IOException
	 */
	protected String kickCygPath(IProgressMonitor monitor, String sapidDest)
			throws IOException {
		ProgressDialogOutput output = new ProgressDialogOutput(monitor);
		String cmd = "cygpath -u \"" + sapidDest + "\"";
		new Command(cmd, curDir).run(output);
		String sapidDestUnix = output.getStored().trim();
		return sapidDestUnix;
	}

	/**
	 * SDB4 を Kick する
	 * 
	 * @param monitor
	 * @param sapidDestUnix
	 * @param output
	 * @return
	 * @throws IOException
	 */
	protected int kickSDB4(IProgressMonitor monitor, String sapidDestUnix,
			ProgressDialogOutput output) throws IOException {
		monitor.setTaskName("sdb4");
		String cmd = "bash -c \"source " + sapidDestUnix
				+ "/lib/SetUp.sh;make -B ";
		// cmd += "-o " + new File(curDir).getName() + " ";
		cmd = cmd.replaceAll("//", "/");
		if ("Makefile".equalsIgnoreCase(makefile)) {
			cmd += "-f " + makefile;
		}
		cmd += " CC=sdb4\"";
		int exitValue = new Command(cmd, curDir).run(output);
		CheckerActivator.log(output.getStored());
		monitor.worked(1);
		return exitValue;
	}

	/**
	 * spdMkCXModel を Kick する
	 * 
	 * @param monitor
	 * @param sapidDestUnix
	 * @param output
	 * @return
	 * @throws IOException
	 */
	protected int kickSpdMkCXModel(IProgressMonitor monitor,
			String sapidDestUnix, ProgressDialogOutput output)
			throws IOException {
		int exitValue;
		monitor.setTaskName("spdMkCXModel");
		String sdb_spec = org.sapid.checker.cx.Messages.getString("SDB_SPEC");
		// SDB\SPEC => SDB/SPEC
		sdb_spec = sdb_spec.replaceAll("\\\\", Matcher.quoteReplacement("/"));
		String cmd = "bash -c \"source " + sapidDestUnix
				+ "/lib/SetUp.sh;mkFlowView;spdMkCXModel -f -t -v -sdbD "
				+ sdb_spec + "\"";
		exitValue = new Command(cmd, curDir).run(output);
		CheckerActivator.log(output.getStored());
		monitor.worked(1);
		return exitValue;
	}

	/**
	 * Progress Monitor に状況を表示する CommandOutput
	 * 
	 * @author Toshinori OSUKA
	 */
	public class ProgressDialogOutput implements CommandOutput {
		IProgressMonitor monitor;
		String stored = "";

		public ProgressDialogOutput(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public String hook(String buffer) {
			monitor.setTaskName(buffer);
			// CheckerActivator.log(buffer);
			stored += buffer + "\n";
			return buffer;
		}

		public String getStored() {
			return stored;
		}

		public void setStored(String stored) {
			this.stored = stored;
		}
	}
}
