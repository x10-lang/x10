package org.eclipse.imp.x10dt.core;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.x10dt.core.preferences.PreferenceConstants;
import org.eclipse.imp.x10dt.core.preferences.PreferenceInitializer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class X10Plugin extends PluginBase {
    public static final String kPluginID= "org.eclipse.imp.x10dt.core";

    /**
     * The unique instance of this plugin class
     */
    protected static X10Plugin sPlugin;
    


    // SMS 27 Oct 2006
    // Calls to set values in X10Preferences should be obviated
    // if the SAFARI preferencees service is used
    // Calls to reference values in X10Preferences should be
    // replaced with calls to the SAFARI preferences service
    
    public static String x10CompilerPath;

    public static X10Plugin	 getInstance() {
        return sPlugin;
    }

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
	// SMS 30 Oct 2006:  Not if preferences service is used
	//IPreferenceStore prefStore= getPreferenceStore();

	Bundle x10CompilerBundle= Platform.getBundle("x10.compiler");
	URL x10CompilerURL= Platform.asLocalURL(Platform.find(x10CompilerBundle, new Path("")));

	// SMS 30 Oct 2006:  Note:  x10CompilerPath is *not* set as a preference
	x10CompilerPath= x10CompilerURL.getPath();

	// SMS 27 Oct 2006:  defs to remove
//	X10Preferences.builderEmitMessages= prefStore.getBoolean(PreferenceConstants.P_EMIT_MESSAGES);
////	X10Preferences.autoAddRuntime= prefStore.getBoolean(PreferenceConstants.P_AUTO_ADD_RUNTIME);
//	X10Preferences.x10ConfigName= prefStore.getString(PreferenceConstants.P_X10CONFIG_NAME);
//	X10Preferences.x10ConfigFile= prefStore.getString(PreferenceConstants.P_X10CONFIG_FILE);

	// SMS 30 Oct 2006:  ref to replace
	// Filling in the field here for now, but eventually want to replace references to this
	// field with calls to the preference service
	//fEmitInfoMessages= X10Preferences.builderEmitMessages;
	fEmitInfoMessages = getPreferencesService().getBooleanPreference(PreferenceConstants.P_EMIT_MESSAGES);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
	super.stop(context);
	// For some reason, X10Builder.build() gets called with an AUTO build
	// after stop() gets called, and it tries to use the plugin instance
	// to get at the log... resulting in an NPE. So don't null it out.
//	sPlugin= null;
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

    // SMS 30 Oct 2006
    // X10 actually has more preferences than the ones that are refreshed
    // here, but these may be the only ones for which fresh values were
    // a concern.  	If fEmitInfoMessages is obviated by direct references
    // to the preferences service, then it can be deleted here.  The need
    // to refresh the configuration file as is done here may remain since
    // the value is not based solely on what is in the store (the "default"
    // value computed here depends on x10CompilerPath, which is not a	
    // preference value).			
    public void refreshPrefs() {
        super.refreshPrefs();
        // SMS 27 Oct 2006:  ref to replace
        //fEmitInfoMessages= X10Preferences.builderEmitMessages;
        fEmitInfoMessages = getPreferencesService().getBooleanPreference(PreferenceConstants.P_EMIT_MESSAGES);
        // Set the "x10.configuration" System property from the corresponding preference
        // value, since polyglot.ext.x10.Configuration relies on its being set, and a
        // static initializer there throws an exception if it can't find the config file.
        // SMS 27 Oct 2006:  refs to replace
        //final String configFile= (X10Preferences.x10ConfigFile!= null ? X10Preferences.x10ConfigFile : x10CompilerPath + File.separator + "etc" + File.separator + "standard.cfg");
        final String configFile= (
        		getPreferencesService().getStringPreference(PreferenceConstants.P_X10CONFIG_FILE) != null ?
        				getPreferencesService().getStringPreference(PreferenceConstants.P_X10CONFIG_FILE) :
        				x10CompilerPath + File.separator + "etc" + File.separator + "standard.cfg");

        System.setProperty("x10.configuration", configFile);
    }
    
    
    // SMS 27 Oct 2006
    protected static PreferencesService preferencesService = 
    	getPreferencesService();
    
    public static PreferencesService getPreferencesService() {
    	if (preferencesService == null) {
    		preferencesService = new PreferencesService();
        	preferencesService.setLanguageName("x10");
           	(new PreferenceInitializer()).initializeDefaultPreferences();
    	}
    	return preferencesService;
    }
}
