package org.sapid.checker.eclipse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CheckerActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "CheckerPlugin";
    public static final String DEFAULT_ID = CheckerActivator.PLUGIN_ID
            + ".JSPCheckerMarker";

    // The shared instance
    private static CheckerActivator plugin;

    /**
     * The constructor
     */
    public CheckerActivator() {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * @return the shared instance
     */
    public static CheckerActivator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static String getFullPath(String file) {
        URL entry = CheckerActivator.getDefault().getBundle().getEntry(file);
        String path = null;
        try {
            path = FileLocator.resolve(entry).getPath();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return path;
    }

    public static void log(String message, Throwable t) {
        IStatus status = new Status(IStatus.ERROR, getPluginId(), IStatus.OK,
                message, t);
        getDefault().getLog().log(status);
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter));
        String message = stringWriter.getBuffer().toString();
        log(message, t);
    }

    public static String getPluginId() {
        return getDefault().getBundle().getSymbolicName();
    }

}
