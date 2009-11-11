/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.dialogs;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.swt.widgets.Shell;

/**
 * Factory methods to create some useful dialogs for X10.
 * 
 * @author egeay
 */
public final class DialogsFactory {
  
  /**
   * Defines a dialog allowing to start a list of resource managers in Stopped state.
   * 
   * @param parentShell The parent shell to use.
   * @param rmList The list of stopped resource managers to take care of.
   * @return The dialog return code.
   */
  public static int openResourceManagerStartDialog(final Shell parentShell, final Collection<IResourceManager> rmList) {
    final ResourceManagerStartDialog dialog = new ResourceManagerStartDialog(parentShell, rmList);
    dialog.setHelpAvailable(false);
    return dialog.open();
  }

  /**
   * Defines an error dialog for an X10 platform configuration that failed the validation step.
   * 
   * @param parentShell The parent shell to use.
   * @param dialogTitle The dialog title.
   * @param errorMessage The main error message.
   * @param status The status associated with the error. May be <b>null</b>.
   * @param codeText The text of the code to be validated. May be <b>null</b>.
   * @return The dialog return code.
   */
  public static int openValidationErrorDialog(final Shell parentShell, final String dialogTitle, final String errorMessage, 
                                              final IStatus status, final String codeText) {
    final Dialog dialog = new ValidationErrorDialog(parentShell, dialogTitle, errorMessage, status, codeText);
    return dialog.open();
  }

}
