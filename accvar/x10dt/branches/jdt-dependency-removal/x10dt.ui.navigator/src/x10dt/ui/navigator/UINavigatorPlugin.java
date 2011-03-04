package x10dt.ui.navigator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.runtime.ImageDescriptorRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class UINavigatorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "x10dt.ui.navigator";

	// The shared instance
	private static UINavigatorPlugin plugin;
	
	private ImageDescriptorRegistry fImageDescriptorRegistry;

	/**
	 * Id for the X10DT C++ Project Nature.
	 */
	public static final String X10_CPP_PRJ_NATURE_ID = "x10dt.ui.launch.cpp.x10nature"; //$NON-NLS-1$

	/**
	 * Id for the X10DT Java Project Nature.
	 */
	public static final String X10_PRJ_JAVA_NATURE_ID = "x10dt.ui.launch.java.x10nature"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public UINavigatorPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		if (fImageDescriptorRegistry != null)
			fImageDescriptorRegistry.dispose();

		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static UINavigatorPlugin getDefault() {
		return plugin;
	}
	
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		return getDefault().internalGetImageDescriptorRegistry();
	}

	private synchronized ImageDescriptorRegistry internalGetImageDescriptorRegistry() {
		if (fImageDescriptorRegistry == null)
			fImageDescriptorRegistry= new ImageDescriptorRegistry();
		return fImageDescriptorRegistry;
	}
	
	public static void log(Exception e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getMessage(), e));
	}
}
