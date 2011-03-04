/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.dialogs;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * Builder for creating an error dialog with different customization scenarios.
 * 
 * <p>This builder will allow the creation of the dialog even from a non-UI thread.
 * 
 * @author egeay
 */
public interface IErrorDialogBuilder {
  
  /**
   * Creates, opens the error dialog with the parameters provided and returns the return status flag.
   * 
   * @param shell The shell to use for the dialog creation.
   * @param dialogTitle The dialog title.
   * @param topMessage The message at the top of the dialog.
   * @return The return code of the dialog. Either {@link Window#OK} or {@link Window#CANCEL}.
   */
  public int createAndOpen(final Shell shell, final String dialogTitle, final String topMessage);
  
  /**
   * Creates, opens the error dialog with the parameters provided and returns the return status flag.
   * 
   * @param shellProvider The shell provider to use for the dialog creation.
   * @param dialogTitle The dialog title.
   * @param topMessage The message at the top of the dialog.
   * @return The return code of the dialog. Either {@link Window#OK} or {@link Window#CANCEL}.
   */
  public int createAndOpen(final IShellProvider shellProvider, final String dialogTitle, final String topMessage);
      
  /**
   * Defines the detailed message from the string provided.
   * 
   * <p>This is an <b>optional</b> call.
   * 
   * @param detailedMessage The detailed message to use.
   * @return The current builder.
   */
  public IErrorDialogBuilder setDetailedMessage(final String detailedMessage);
  
  /**
   * Defines the detailed message as the exception stack trace for the exception transmitted.
   * 
   * <p>This is an <b>optional</b> call.
   * 
   * @param exception The exception to consider.
   * @return The current builder.
   */
  public IErrorDialogBuilder setDetailedMessage(final Throwable exception);
  
  /**
   * Defines the detailed message from the operation status provided.
   * 
   * <p>This is an <b>optional</b> call.
   * 
   * @param status The operation status to consider.
   * @return The current builder.
   */
  public IErrorDialogBuilder setDetailedMessage(final IStatus status);
  
  /**
   * Defines the detailed message as either the operation status embedded within {@link CoreException} or the exception
   * stack trace of the wrapped target exception.
   * 
   * @param exception The target exception to consider.
   * @return The current builder.
   */
  public IErrorDialogBuilder setDetailedMessage(final InvocationTargetException exception);

  /**
   * Defines the image to use near the top message.
   * 
   * <p>This is an <b>optional</b> call. By default the SWT error icon will be used.
   * 
   * @param image The image to use
   * @return The current builder.
   */
  public IErrorDialogBuilder setImage(final Image image);
  
  /**
   * Defines the flag to log or not the messages within Eclipse Error Log.
   * 
   * <p>This is an <b>optional</b> call. By default messages will be logged within Eclipse Error Log.
   * 
   * @param shouldLogError True if one wants to log, false otherwise.
   * @return The current builder.
   */
  public IErrorDialogBuilder setShouldLogFlag(final boolean shouldLogError);

}
