package com.ibm.wala.cast.x10;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class X10Plugin extends Plugin {

	//The shared instance.
	private static X10Plugin plugin;
	
	/**
	 * The constructor.
	 */
	public X10Plugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static X10Plugin getDefault() {
		return plugin;
	}

}
