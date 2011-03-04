package org.eclipse.imp.x10dt.analysis;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class X10AnalysisPlugin extends Plugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.imp.x10dt.analysis";

	// The shared instance
	private static X10AnalysisPlugin sInstance;
	
    private static ILog sLog;

	/**
	 * The constructor
	 */
	public X10AnalysisPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		sInstance = this;
        try {
            new X10TypeFactGenerator(); // to trigger static initializers in that class...
        } catch (Exception e) {
            // do nothing
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		sInstance = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static X10AnalysisPlugin getInstance() {
		return sInstance;
	}

	public static void log(Exception e) {
        getInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getMessage(), e));
    }

    public static void log(String msg, Exception e) {
        getInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, msg, e));
    }

    public static void log(String msg) {
        getInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, msg, null));
    }
}
