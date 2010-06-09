/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;


import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;

/**
 * Utility methods for Eclipse UI operations.
 * 
 * @author egeay
 */
public final class UIUtils {
  
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
