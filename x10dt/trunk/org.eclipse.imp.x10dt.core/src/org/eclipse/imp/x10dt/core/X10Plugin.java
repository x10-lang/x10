package com.ibm.watson.safari.x10;

import java.io.File;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.uide.runtime.UIDEPluginBase;
import org.osgi.framework.BundleContext;
import com.ibm.watson.safari.x10.preferences.PreferenceConstants;
import com.ibm.watson.safari.x10.preferences.X10Preferences;

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

	// Initialize the X10Preferences fields with the preference store data.
	IPreferenceStore prefStore= getPreferenceStore();

	X10Preferences.builderEmitMessages= prefStore.getBoolean(PreferenceConstants.P_EMIT_MESSAGES);
	X10Preferences.autoAddRuntime= prefStore.getBoolean(PreferenceConstants.P_AUTO_ADD_RUNTIME);
	X10Preferences.x10CommonPath= prefStore.getString(PreferenceConstants.P_X10COMMON_PATH);
	X10Preferences.x10ConfigFile= prefStore.getString(PreferenceConstants.P_X10CONFIG_FILE);
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
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return AbstractUIPlugin.imageDescriptorFromPlugin(kPluginID, path);
    }

    public void refreshPrefs() {
        super.refreshPrefs();
        // Set the "x10.configuration" System property from the corresponding preference
        // value, since polyglot.ext.x10.Configuration relies on its being set, and a
        // static initializer there throws an exception if it can't find the config file.
        final String configFile= (X10Preferences.x10ConfigFile!= null ? X10Preferences.x10ConfigFile : X10Preferences.x10CommonPath + File.separator + "etc" + File.separator + "standard.cfg");
	System.setProperty("x10.configuration", configFile);
    }
}
