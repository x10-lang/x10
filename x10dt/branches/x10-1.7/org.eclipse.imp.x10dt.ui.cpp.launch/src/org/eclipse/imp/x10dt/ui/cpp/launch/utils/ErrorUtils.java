/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.utils;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility methods for error handling.
 * 
 * @author egeay
 */
public final class ErrorUtils {

  /**
   * Logs a message with current plugin id, and creates also an error dialog with the parameters provided.
   * 
   * <p>If the wrapped exception within {@link InvocationTargetException} is a {@link CoreException} then the creation occurs
   * with the status related to. Otherwise, we create a status with the parameters provided.
   * 
   * @param shell shell The shell to use to create the dialog box.
   * @param title The title of the error dialog box.
   * @param message The message for the dialog box.
   * @param except The exception to log and display in the dialog box.
   */
  public static void dialogWithLog(final Shell shell, final String title, final String message,
                                   final InvocationTargetException except) {
    final Throwable throwable = except.getTargetException();
    if (throwable instanceof CoreException) {
      ErrorUtils.dialogWithLog(shell, title, message, ((CoreException) throwable).getStatus());
    } else {
      ErrorUtils.dialogWithLog(shell, title, IStatus.ERROR, message, except.getTargetException());
    }
  }

  /**
   * Logs the status with current plugin id, and also creates an error dialog from it.
   * 
   * @param shell shell The shell to use to create the dialog box.
   * @param title The title of the error dialog box.
   * @param dialogMessage The message of the error dialog box.
   * @param status The status message to log and display in the dialog box.
   */
  public static void dialogWithLog(final Shell shell, final String title, final String dialogMessage, final IStatus status) {
    LaunchCore.log(status);
    ErrorDialog.openError(shell, title, dialogMessage, status);
  }

  /**
   * Logs a message with current plugin id, and creates an error dialog with a different message and settings provided.
   * 
   * <p>Equivalent to {@link #dialogWithLog(Shell, String, String, int, String, Throwable)} with no dialog message and
   * no exception.
   * 
   * @param shell shell The shell to use to create the dialog box.
   * @param title The title of the error dialog box.
   * @param severity The severity of the message to log.
   * @param statusMessage The message to log.
   */
  public static void dialogWithLog(final Shell shell, final String title, final int severity, final String statusMessage) {
    dialogWithLog(shell, title, null /* dialogMessage */, severity, statusMessage, null /* exception */);
  }

  /**
   * Logs a message with current plugin id, and creates an error dialog with a different message and settings provided.
   * 
   * <p>Equivalent to {@link #dialogWithLog(Shell, String, String, int, String, Throwable)} with no dialog message.
   * 
   * @param shell shell The shell to use to create the dialog box.
   * @param title The title of the error dialog box.
   * @param severity The severity of the message to log.
   * @param statusMessage The message to log.
   * @param exception The exception to log.
   */
  public static void dialogWithLog(final Shell shell, final String title, final int severity, final String statusMessage,
                                   final Throwable exception) {
    dialogWithLog(shell, title, null /* dialogMessage */, severity, statusMessage, exception);
  }

  /**
   * Logs a message with current plugin id, and creates an error dialog with a different message and settings provided.
   * 
   * <p>Equivalent to {@link #dialogWithLog(Shell, String, String, int, String, Throwable)} with no exception to log.
   * 
   * @param shell The shell to use to create the dialog box.
   * @param title The title of the error dialog box.
   * @param dialogMessage The message of the error dialog box.
   * @param severity The severity of the message to log.
   * @param statusMessage The message to log.
   */
  public static void dialogWithLog(final Shell shell, final String title, final String dialogMessage, final int severity,
                                   final String statusMessage) {
    dialogWithLog(shell, title, dialogMessage, severity, statusMessage, null /* exception */);
  }

  /**
   * Logs a message with current plugin id, and creates an error dialog with a different message and settings provided.
   * 
   * @param shell The shell to use to create the dialog box.
   * @param title The title of the error dialog box.
   * @param dialogMessage The message of the error dialog box.
   * @param severity The severity of the message to log.
   * @param statusMessage The message to log.
   * @param exception The exception to log.
   */
  public static void dialogWithLog(final Shell shell, final String title, final String dialogMessage, final int severity,
                                   final String statusMessage, final Throwable exception) {
    assert statusMessage != null;
    final IStatus status = new Status(severity, LaunchCore.PLUGIN_ID, statusMessage, exception);
    LaunchCore.log(status);
    ErrorDialog.openError(shell, title, dialogMessage, status);
  }

}
