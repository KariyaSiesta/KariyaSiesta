package org.sapid.checker.eclipse.cdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;

/**
 * Utility class to get CDT project configuration.
 * @author mallowlabs
 * @since v1.1
 */
public class CResourceConfigUtil {

	/** This is utility class. */
	private CResourceConfigUtil() {
	}

	/**
	 * get include paths of project.
	 * @param prj a C project (has .cproject file)
	 * @return path list has absolute paths or project relative paths
	 */
	public static String[] getIncludePaths(IProject prj) {
		List<String> list = new ArrayList<String>();
		ITool[] tools = getToolsFromDefaultToolChain(prj);
		for (ITool tool : tools) {
			IOption[] options = tool.getOptions();
			for (IOption option : options) {
				try {
					if (option.getValueType() != IOption.INCLUDE_PATH) {
						continue;
					}
					String[] paths = option.getIncludePaths();
					if (option.isExtensionElement()) {
						continue;
					}
					for (String path : paths) {
						if (path.startsWith("\"${workspace_loc:")) {
							// workspace path
							String prjPrefix = prj.getFullPath().toString()
									+ "/";
							path = path.replace("\"${workspace_loc:", "")
									.replace("}\"", "").replace(prjPrefix, "");
							list.add(path);
						} else if (path.startsWith("\"${")) {
							// variable
							// do nothing
						} else {
							// absolute path
							list.add(path);
						}
					}
				} catch (BuildException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	private static ITool[] getToolsFromDefaultToolChain(IProject prj) {
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(prj);
		IConfiguration config = info.getDefaultConfiguration();
		IToolChain chain = config.getToolChain();
		ITool[] tools = chain.getTools();
		return tools;
	}

	/**
	 * get macro definitions of project.
	 * @param prj a C project (has .cproject file)
	 * @return macro list (format: MACRO=VALUE or MACRO)
	 */
	public static String[] getSymbols(IProject prj) {
		List<String> list = new ArrayList<String>();
		ITool[] tools = getToolsFromDefaultToolChain(prj);
		for (ITool tool : tools) {
			IOption[] options = tool.getOptions();
			for (IOption option : options) {
				try {
					if (option.getValueType() != IOption.PREPROCESSOR_SYMBOLS) {
						continue;
					}
					String[] symbols = option.getDefinedSymbols();
					for (String symbol : symbols) {
						if (symbol.startsWith("\"") && symbol.contains("${")) {
							continue; // skip variable
						}
						list.add(symbol);
					}
				} catch (BuildException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

}
