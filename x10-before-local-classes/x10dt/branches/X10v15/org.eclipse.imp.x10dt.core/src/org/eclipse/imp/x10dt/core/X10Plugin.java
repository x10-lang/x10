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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferenceCache;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class X10Plugin extends PluginBase {
    public static final String kPluginID= "org.eclipse.imp.x10dt.core";
    public static final String kLanguageName = "X10";

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
    	// mmk: Creation if not auto-starated adapted from generated preferences Activator
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

        Bundle x10CompilerBundle= Platform.getBundle("x10.compiler");
        URL x10CompilerURL= FileLocator.toFileURL(FileLocator.find(x10CompilerBundle, new Path(""), null));

        // SMS 30 Oct 2006:  Note:  x10CompilerPath is *not* set as a preference
        x10CompilerPath= x10CompilerURL.getPath();

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
}
