package org.sapid.checker.eclipse.progress;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.sapid.checker.cx.command.Command;
import org.sapid.checker.eclipse.CheckerActivator;

/**
 * the Job kicks sdb4 command to .c or .h files.
 * @author mallowlabs
 */
public class CreateFileSDBJob extends CreateSDBJob {
	/** macro definitions */
	private String[] symbols = new String[0];

	/** include paths */
	private String[] includePaths = new String[0];

	/**
	 * Constructor.
	 * @param curDir working directory
	 * @param file .c or .h file
	 */
	public CreateFileSDBJob(String curDir, String file) {
		super(curDir, file);
	}

	/**
	 * Constructor.
	 * @param curDir working directory
	 * @param file .c or .h file
	 * @param includePaths include paths
	 * @param symbols symbols
	 */
	public CreateFileSDBJob(String curDir, String file, String[] includePaths, String[] symbols) {
		super(curDir, file);
		this.includePaths = includePaths;
		this.symbols = symbols;
	}

	@Override
	protected int kickSDB4(IProgressMonitor monitor, String sapidDestUnix,
			ProgressDialogOutput output) throws IOException {
		monitor.setTaskName("sdb4");
		String outName = new File(getCurDir()).getName();
		String cmd = "bash -c \"source " + sapidDestUnix
				+ "/lib/SetUp.sh;sdb4 -o " + outName + " "
				+ getMakefile().replace(getCurDir() + "/", "");
		for (String path : includePaths) {
			cmd += " -I" + path;
		}
		for (String symbol : symbols) {
			cmd += " -D" + symbol;
		}
		cmd += "\"";
		cmd = cmd.replaceAll("//", "/");

		int exitValue = new Command(cmd, getCurDir()).run(output);
		CheckerActivator.log(output.getStored());
		monitor.worked(1);
		return exitValue;
	}

}
