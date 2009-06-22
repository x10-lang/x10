package com.ibm.watson.safari.x10;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ibm.watson.safari.x10.preferences.X10Preferences;

/**
 * The main plugin class to be used in the desktop.
 */
public class X10Plugin extends AbstractUIPlugin {
    public static final String kPluginID= "com.ibm.watson.safari.x10";

//    private static boolean sWriteInfoMsgs= false;
    private static ILog sLog= null;

    /**
     * The unique instance of this plugin class
     */
    private static X10Plugin sPlugin;

    public X10Plugin() {
	sPlugin= this;
    }

    public static X10Plugin getInstance() {
	return sPlugin;
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
	sPlugin= null;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path
     *                the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return AbstractUIPlugin.imageDescriptorFromPlugin("com.ibm.watson.safari.x10", path);
    }

    public static void maybeWriteInfoMsg(String msg, String componentID) {
	if (!X10Preferences.builderEmitMessages)
	    return;

	Status status= new Status(Status.INFO, componentID, 0, msg, null);

	if (sLog == null)
	    sLog= sPlugin.getLog();

	sLog.log(status);
    }

    public static void writeErrorMsg(String msg, String componentID) {
	Status status= new Status(Status.ERROR, componentID, 0, msg, null);

	if (sLog == null)
	    sLog= sPlugin.getLog();

	sLog.log(status);
    }

    public static void refreshPrefs() {
	// TODO Auto-generated method stub
    }
}
