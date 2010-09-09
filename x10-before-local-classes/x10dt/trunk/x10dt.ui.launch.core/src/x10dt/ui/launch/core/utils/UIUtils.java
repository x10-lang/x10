/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;


import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.Messages;

/**
 * Utility methods for Eclipse UI operations.
 * 
 * @author egeay
 */
public final class UIUtils {
  
  /**
   * Returns access to a pre-existing X10 console or creates a new one if none exists.
   * 
   * @return A non-null console instance.
   */
  public static MessageConsole findOrCreateX10Console() {
    MessageConsole x10Console = null;
    final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
    for (final IConsole console : consoleManager.getConsoles()) {
      if (Messages.CPPB_ConsoleName.equals(console.getName())) {
        x10Console = (MessageConsole) console;
      }
    }
    if (x10Console == null) {
      x10Console = new MessageConsole(Messages.CPPB_ConsoleName, null);
      consoleManager.addConsoles(new IConsole[] { x10Console });
    }
    return x10Console;
  }
  
  /**
   * Brings up the X10 console view to the end-user.
   */
  public static void showX10Console() {
    final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
    for (final IConsole console : consoleManager.getConsoles()) {
      if (Messages.CPPB_ConsoleName.equals(console.getName())) {
        consoleManager.showConsoleView(console);
        break;
      }
    }
  }
  
  /**
   * Shows the error log view by calling {@link #showView(String)} with the appropriate id.
   * 
   * @throws PartInitException Occurs if a workbench part could not be initialized properly.
   */
  public static void showErrorLogView() throws PartInitException {
    showView(ERROR_LOG_VIEW_ID);
  }
  
  /**
   * Shows the problems view by calling {@link #showView(String)} with the appropriate id.
   * 
   * @throws PartInitException Occurs if a workbench part could not be initialized properly.
   */
  public static void showProblemsView() throws PartInitException {
    showView(PROBLEMS_VIEW_ID);
  }
  
  /**
   * Shows a view from a non-UI thread.
   * 
   * @param viewId The id of the view to show.
   * @throws PartInitException Occurs if a workbench part could not be initialized properly.
   */
  public static void showView(final String viewId) throws PartInitException {
    final IWorkbench workbench = LaunchCore.getInstance().getWorkbench();
    final PartInitException[] exception = new PartInitException[1];
    workbench.getDisplay().asyncExec(new Runnable() {
      
      public void run() {
        try {
          workbench.getActiveWorkbenchWindow().getActivePage().showView(viewId);
        } catch (PartInitException except) {
          exception[0] = except;
        }
      }
      
    });
    if (exception[0] != null) {
      throw exception[0];
    }
  }
  
  // --- Private code
  
  private UIUtils() {}
  
  // --- Fields
  
  private static final String ERROR_LOG_VIEW_ID = "org.eclipse.pde.runtime.LogView"; //$NON-NLS-1$
  
  private static final String PROBLEMS_VIEW_ID = "org.eclipse.ui.views.ProblemView"; //$NON-NLS-1$

}
