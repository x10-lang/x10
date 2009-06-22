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

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
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
public class X10DTCorePlugin extends PluginBase {
	public static final String kPluginID= "org.eclipse.imp.x10dt.core";
	public static final String X10DT_CONSOLE_NAME = "X10DT info";
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
    protected static X10DTCorePlugin sPlugin;
    protected static MessageConsole console=null;
    
    public static String x10CompilerPath;

    public static X10DTCorePlugin	 getInstance() {
    	// mmk: Creation if not auto-started adapted from generated preferences Activator
    	if (sPlugin == null)
			new X10DTCorePlugin();
		return sPlugin;
    }

    public X10DTCorePlugin() {
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

        Bundle x10CompilerBundle= Platform.getBundle(X10_COMPILER_BUNDLE_ID);
        URL x10CompilerURL= FileLocator.toFileURL(FileLocator.find(x10CompilerBundle, new Path(""), null));

        // SMS 30 Oct 2006:  Note:  x10CompilerPath is *not* set as a preference
        x10CompilerPath= x10CompilerURL.getPath();

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
   
    @Override
    public void refreshPrefs() {
    	this.getPreferencesService().getBooleanPreference("msgs?");
    }
    
    public MessageConsole getConsole() {
    	if(console==null) {
    		console = findConsole(X10DT_CONSOLE_NAME);
    	}
    	return console;
    }
    /**
     * Write a string to the X10DT console.  Note that the user has to open the console for this to be visible.
     * @param msg
     */
    public void writeToConsole(String msg) {
    	getConsole();
		MessageConsoleStream out = console.newMessageStream();
		out.println(getTimeAndDate());
		out.println(msg);
		try {
			out.flush();
			out.close();
		} catch (IOException e1) {
			logException("Exception writing to X10DT console", e1);
		}
		showConsole();
    }

    /**
     * Get the current time and date, useful for delineating console output.
     * @return
     */
	public String getTimeAndDate() {
		Calendar cal = Calendar.getInstance();
		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String time = sdf.format(cal.getTime());
		return time;
	}
	/**
	 * Make sure the console is visible
	 */
    public void showConsole() {
		getConsole();
		final String id = IConsoleConstants.ID_CONSOLE_VIEW;

		final IWorkbench wb = PlatformUI.getWorkbench();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = win.getActivePage();
				IConsoleView view = null;

				try {
					view = (IConsoleView) page.showView(id);
				} catch (PartInitException e) {
					logException("Exception showing build console", e);
				}
				view.display(console);
			}
		});
	}



    /**
	 * Get the X10DT console
	 * 
	 * @param name
	 * @return
	 */
	public MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();

		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
