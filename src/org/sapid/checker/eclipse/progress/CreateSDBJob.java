package org.sapid.checker.eclipse.progress;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.sapid.checker.cx.command.Command;
import org.sapid.checker.cx.command.CommandOutput;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;
import org.sapid.checker.eclipse.cdt.CResourceConfigUtil;

/***
 * SDB生成をバックグラウンドで行なうためのジョブ
 * @author mzp, mallowlabs
 */
public class CreateSDBJob extends Job {
	/** working directory. */
	private String curDir;
	/** target resource. */
	private IFile[] files;

	/**
	 * constructor.
	 * @param curDir working directory
	 * @param files target files (*.c or *.h)
	 */
	public CreateSDBJob(String curDir, IFile[] files) {
		super("Create SDB");
		this.curDir = curDir;
		this.files = files;
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

	protected IStatus report(int status, String message) {
		return new Status(status, CheckerActivator.PLUGIN_ID, message);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final int TASK_AMOUNT = 2;
		monitor.beginTask("Chcker", TASK_AMOUNT + files.length);

		String sapidDest = CheckerActivator.getDefault().getPreferenceStore()
				.getString("SAPID_DEST");
		if (!new File(sapidDest).exists()) {
			return this.report(IStatus.ERROR,
					Messages.getString("CreateSDBProgress.0"));
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
				return this.report(IStatus.CANCEL,
						Messages.getString("CheckWithProgress.CANCELD"));
			}

			int exitValue = 0;
			ProgressDialogOutput output = new ProgressDialogOutput(monitor);
			for (IFile file : files) {
				// sdb4
				exitValue = kickSDB4(monitor, sapidDestUnix, file, output);
				if (exitValue != 0) {
					return this.report(IStatus.ERROR, output.getStored());
				}
				output.setStored("");
				if (monitor.isCanceled()) {
					return this.report(IStatus.CANCEL,
							Messages.getString("CheckWithProgress.CANCELD"));
				}
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
	 * @param monitor
	 * @param sapidDestUnix
	 * @param output
	 * @return
	 * @throws IOException
	 */
	protected int kickSDB4(IProgressMonitor monitor, String sapidDestUnix,
			IFile file, ProgressDialogOutput output) throws IOException {
		monitor.setTaskName("sdb4");

		String[] includePaths = CResourceConfigUtil.getIncludePaths(file);
		String[] symbols = CResourceConfigUtil.getSymbols(file);

		// bash -c "source /usr/local/Sapid/lib/SetUp.sh;
		// sdb4 -o cxc-test test2.c -Iinclude -I/file/local/include -DMAMACRO"
		StringBuffer buff = new StringBuffer();
		buff.append("bash -c \"source ").append(sapidDestUnix)
				.append("/lib/SetUp.sh;");
		buff.append("sdb4 -o ").append(new File(curDir).getName()).append(" ");
		buff.append(file.getProjectRelativePath().toString());

		for (String path : includePaths) {
			buff.append(" -I").append(path);
		}
		for (String symbol : symbols) {
			buff.append(" -D").append(symbol);
		}
		buff.append("\"");
		String cmd = buff.toString().replaceAll("//", "/");
		int exitValue = new Command(cmd, curDir).run(output);
		CheckerActivator.log(output.getStored());
		monitor.worked(1);
		return exitValue;
	}

	/**
	 * spdMkCXModel を Kick する
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
