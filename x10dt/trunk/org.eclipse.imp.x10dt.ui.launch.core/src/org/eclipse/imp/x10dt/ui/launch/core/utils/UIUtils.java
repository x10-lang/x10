/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;

/**
 * Utility methods for Eclipse UI operations.
 * 
 * @author egeay
 */
public final class UIUtils {
  
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
  
  /**
   * Reads an input stream within another thread for leaving the hand on the UI thread.
   * 
   * @param inputStream The input stream to read.
   * @param listener The input listener to consider.
   */
  public static void printStream(final InputStream inputStream, final IInputListener listener) {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    new Thread(new Runnable() {
      
      public void run() {
        try {
          String line;
          while ((line = reader.readLine()) != null) {
            listener.read(line);
          }
        } catch (IOException except) {
          LaunchCore.log(IStatus.ERROR, Messages.CPPB_ErrorStreamReadingError, except);
        }
      }
      
    }).start();
  }

}
