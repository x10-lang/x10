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
package x10dt.core;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.imp.preferences.IPreferencesService;
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
public class X10DTCorePlugin extends PluginBase implements IPreferenceChangeListener {
  
    public static final String kPluginID= "x10dt.core";
    public static final String X10DT_CONSOLE_NAME = "X10DT info";
    public static final String kLanguageName = "X10";
   
    /**
     * The unique instance of this plugin class
     */
    protected static X10DTCorePlugin sPlugin;
    protected static MessageConsole console=null;

    /**
     * Id for the X10DT C++ Project Nature.
     */
    public static final String X10_CPP_PRJ_NATURE_ID = "x10dt.ui.launch.cpp.x10nature"; //$NON-NLS-1$

    /**
     * Id for the X10DT Java Project Nature.
     */
    public static final String X10_PRJ_JAVA_NATURE_ID = "x10dt.ui.launch.java.x10nature"; //$NON-NLS-1$

    public X10DTCorePlugin() {
      super();
      sPlugin= this;
    }
    
    // --- Public services
    
    public static X10DTCorePlugin	 getInstance() {
    	// mmk: Creation if not auto-started adapted from generated preferences Activator
    	if (sPlugin == null)
			new X10DTCorePlugin();
    	return sPlugin;
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
          if (win == null) {
            return; // Workbench is presumably shutting down... do nothing - there's no window to put the console in anyway
          }
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
    
    // --- IPropertyChangeListener's interface methods implementation
    
    public void preferenceChange(final PreferenceChangeEvent event) {
//    updateX10ConfigurationFromPreferences();
      final WorkspaceJob job = new WorkspaceJob("Clean workspace...") {
        
        public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
          ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
          return Status.OK_STATUS;
        }
        
      };
      job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
      job.setPriority(Job.BUILD);
      job.schedule();
    }

    // --- PluginBase's abstract methods implementation
    
    public String getLanguageID() {
      return kLanguageName;
    }

    public String getID() {
      return kPluginID;
    }
    
    // --- Overridden methods

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
      super.start(context);
      
//      updateX10ConfigurationFromPreferences();

      X10DTCorePlugin.getInstance().getPreferencesService().getPreferences(IPreferencesService.INSTANCE_LEVEL)
                     .addPreferenceChangeListener(this);
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
      X10DTCorePlugin.getInstance().getPreferencesService().getPreferences(IPreferencesService.INSTANCE_LEVEL)
                     .removePreferenceChangeListener(this);
    }
    
    @Override
    public void refreshPrefs() {
      this.getPreferencesService().getBooleanPreference("msgs?");
    }
    
    // --- Private code
    
//    private void updateX10ConfigurationFromPreferences() {
//      final IPreferencesService prefService = getPreferencesService();
//      // Compiler prefs update
//      Configuration.DEBUG = true;
//      final String additionalOptions = prefService.getStringPreference(X10Constants.P_ADDITIONALCOMPILEROPTIONS);
//      if ((additionalOptions != null) && (additionalOptions.length() > 0)) {
//        // First initialize to default values.
//        Configuration.CHECK_INVARIANTS = false;
//        Configuration.ONLY_TYPE_CHECKING = false;
//        Configuration.NO_CHECKS = false;
//        Configuration.FLATTEN_EXPRESSIONS = false;
//        Configuration.WALA = false;
//        Configuration.FINISH_ASYNCS = false;
//        for (final String opt : additionalOptions.split("\\s")) { ////$NON-NLS-1$
//          try {
//            Configuration.parseArgument(opt);
//          } catch (OptionError except) {
//            logException(NLS.bind("Could not recognize or set option ''{0}''.", opt), except);
//          } catch (ConfigurationError except) {
//            logException(NLS.bind("Could not initialize option ''{0}''.", opt), except);
//          }
//        }
//      }
//      // Optimization prefs update
//      Configuration.STATIC_CALLS = prefService.getBooleanPreference(X10Constants.P_STATICCALLS);
//      Configuration.VERBOSE_CALLS = prefService.getBooleanPreference(X10Constants.P_VERBOSECALLS);
//      Configuration.OPTIMIZE = prefService.getBooleanPreference(X10Constants.P_OPTIMIZE);
//    }

}
