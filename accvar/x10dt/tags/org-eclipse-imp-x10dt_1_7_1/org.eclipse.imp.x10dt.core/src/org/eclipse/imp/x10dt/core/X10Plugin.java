/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.core;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * <p>Logging methods, for reference, and what they look like in Error log
 * <br>writeErrorMessage(String) -  "red X" icon
 * <br>writeInfoMessage(String) -  "blue i" icon
 * <br>logException(String, Exception) - Red X over text icon (stack trace avail)
 * <p>Even more flexibility via:
 * <br>getLog().logStatus(new Status(...));
 * 
 */
public class X10Plugin extends PluginBase {
    public static final String kPluginID= "org.eclipse.imp.x10dt.core";
    public static final String kLanguageName = "X10";
    
    /** Plugin id of version of X10 runtime used for this X10DT */
    public static final String X10_RUNTIME_BUNDLE_ID="x10.runtime.17";      //PORT1.7 provide constant here for runtime
    /** Plugin id of version of X10 compiler used for this X10DT */
    public static final String X10_COMPILER_BUNDLE_ID ="x10.compiler.p3";   //PORT1.7 provide constant here for compiler
    public static final String X10_COMMON_BUNDLE_ID="x10.common.17";  //PORT1.7 added
    public static final String X10_CONSTRAINTS_BUNDLE_ID="x10.constraints"; //PORT1.7 added
    /**
     * The unique instance of this plugin class
     */
    protected static X10Plugin sPlugin;
    


    // SMS 27 Oct 2006
    // Calls to set values in X10Preferences should be obviated
    // if the SAFARI preferences service is used
    // Calls to reference values in X10Preferences should be
    // replaced with calls to the SAFARI preferences service
    
    public static String x10CompilerPath;

    public static X10Plugin	 getInstance() {
    	// mmk: Creation if not auto-started adapted from generated preferences Activator
    	if (sPlugin == null)
			new X10Plugin();
		return sPlugin;
    }

    public X10Plugin() {
    	super();
    	sPlugin= this;
    }

    @Override
    public String getLanguageID() {
        return kLanguageName;
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

        Bundle x10CompilerBundle= Platform.getBundle(X10_COMPILER_BUNDLE_ID);
        URL x10CompilerURL= FileLocator.toFileURL(FileLocator.find(x10CompilerBundle, new Path(""), null));

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
	// BRT fEmitInfoMessages = getPreferencesService().getBooleanPreference(PreferenceConstants.P_EMIT_MESSAGES);
	// BRT consider putting this in an X10DT pref page.  Probably want several flavors.
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

//  mmk 5/20/2008: replaced prefs code with generated prefs code (below)
//    // SMS 30 Oct 2006
//    // X10 actually has more preferences than the ones that are refreshed
//    // here, but these may be the only ones for which fresh values were
//    // a concern.  	If fEmitInfoMessages is obviated by direct references
//    // to the preferences service, then it can be deleted here.  The need
//    // to refresh the configuration file as is done here may remain since
//    // the value is not based solely on what is in the store (the "default"
//    // value computed here depends on x10CompilerPath, which is not a	
//    // preference value).			
//    public void refreshPrefs() {
//        super.refreshPrefs();
//        // SMS 27 Oct 2006:  ref to replace
//        //fEmitInfoMessages= X10Preferences.builderEmitMessages;
//        fEmitInfoMessages = getPreferencesService().getBooleanPreference(PreferenceConstants.P_EMIT_MESSAGES);
//        // Set the "x10.configuration" System property from the corresponding preference
//        // value, since polyglot.ext.x10.Configuration relies on its being set, and a
//        // static initializer there throws an exception if it can't find the config file.
//        // SMS 27 Oct 2006:  refs to replace
//        //final String configFile= (X10Preferences.x10ConfigFile!= null ? X10Preferences.x10ConfigFile : x10CompilerPath + File.separator + "etc" + File.separator + "standard.cfg");
//        final String configFile= (
//        		getPreferencesService().getStringPreference(PreferenceConstants.P_X10CONFIG_FILE) != null ?
//        				getPreferencesService().getStringPreference(PreferenceConstants.P_X10CONFIG_FILE) :
//        				x10CompilerPath + File.separator + "etc" + File.separator + "standard.cfg");
//
//        System.setProperty("x10.configuration", configFile);
//    }
//    
    @Override
    public void refreshPrefs() {
    	System.out.println("refreshPrefs");
    	this.getPreferencesService().getBooleanPreference("msgs?");
    }
}
