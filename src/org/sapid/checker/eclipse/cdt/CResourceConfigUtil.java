package org.sapid.checker.eclipse.cdt;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IFileInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * Utility class to get CDT resource configuration.
 * @author mallowlabs
 * @since v1.1
 */
public class CResourceConfigUtil {

	/** This is utility class. */
	private CResourceConfigUtil() {
	}

	/**
	 * get include paths of resource.
	 * @param resource a C resource (project has .cproject file)
	 * @return path list has absolute paths or project relative paths
	 */
	public static String[] getIncludePaths(IResource resource) {
		Set<String> list = new LinkedHashSet<String>();
		IResource tmp = resource;
		while (tmp.getType() != IResource.ROOT) {
			ITool[] tools = getToolsFromDefaultToolChain(tmp);
			for (ITool tool : tools) {
				if (!(tool.getId().contains("c.compiler"))) {
					continue;
				}
				IOption[] options = tool.getOptions();
				for (IOption option : options) {
					try {
						if (option.getValueType() != IOption.INCLUDE_PATH) {
							continue;
						}
						String[] paths = option.getIncludePaths();
						for (String path : paths) {
							if (path.startsWith("\"${workspace_loc:")) {
								// workspace path
								String prjPrefix = resource.getProject()
										.getFullPath().toString()
										+ "/";
								path = path.replace("\"${workspace_loc:", "")
										.replace("}\"", "")
										.replace(prjPrefix, "");
								list.add(path);
							} else if (path.startsWith("\"${")) {
								// skip variable
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
			tmp = tmp.getParent();
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * get macro definitions of the resource.
	 * @param resource a C resource (has .cproject file)
	 * @return macro list (format: MACRO=VALUE or MACRO)
	 */
	public static String[] getSymbols(IResource resource) {
		Set<String> list = new LinkedHashSet<String>();
		IResource tmp = resource;
		while (tmp.getType() != IResource.ROOT) {
			ITool[] tools = getToolsFromDefaultToolChain(tmp);
			for (ITool tool : tools) {
				if (!(tool.getId().contains("c.compiler"))) {
					continue;
				}
				IOption[] options = tool.getOptions();
				for (IOption option : options) {
					try {
						if (option.getValueType() != IOption.PREPROCESSOR_SYMBOLS) {
							continue;
						}
						String[] symbols = option.getDefinedSymbols();
						for (String symbol : symbols) {
							if (symbol.startsWith("\"")
									&& symbol.contains("${")) {
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
			tmp = tmp.getParent();
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	private static ITool[] getToolsFromDefaultToolChain(IResource resource) {
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(resource
				.getProject());
		IConfiguration config = info.getDefaultConfiguration();
		// project
		if (resource.getType() == IResource.PROJECT) {
			return config.getToolChain().getTools();
		}
		IPath prjRelativePath = resource.getProjectRelativePath();
		IResourceInfo ri = config.getResourceInfo(prjRelativePath, false);
		// file
		if (ri instanceof IFileInfo) {
			return ((IFileInfo) ri).getToolsToInvoke();
		}
		// others
		return ri.getTools();
	}

	/**
	 * if "Tool Chain Editor" -&gt; "Exclude resource from build" is checked return true.
	 * @param file *.c or *.h file
	 * @return if the file is an excluded file return true, else false;
	 */
	public static boolean isExcludeResource(IFile file) {
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(file
				.getProject());
		IConfiguration config = info.getDefaultConfiguration();
		IResourceInfo ri = config.getResourceInfo(
				file.getProjectRelativePath(), false);
		return ri.isExcluded();
	}

}
