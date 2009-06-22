package com.ibm.watson.safari.x10;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.uide.runtime.IPluginLog;
import org.eclipse.uide.runtime.UIDEPluginBase;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class X10Plugin extends UIDEPluginBase {
    public static final String kPluginID= "com.ibm.watson.safari.x10";

    public X10Plugin() {
	sPlugin= this;
    }

    public String getID() {
	return kPluginID;
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
	return AbstractUIPlugin.imageDescriptorFromPlugin(kPluginID, path);
    }
}
