/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.dialogs;

import java.util.Collection;

import org.eclipse.jface.window.Window;
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
    return new ResourceManagerStartDialog(parentShell, rmList).open();
  }
  
  /**
   * Defines a dialog allowing to start a list of resource managers, with a particular dialog text message, and to return
   * a unique one of interest for the end-user.
   * 
   * @param parentShell The parent shell to use for the dialog.
   * @param rmList The list of resource managers of interest.
   * @param dialogText The dialog text.
   * @return The resource manager started and selected by the end-user or <b>null</b> if one canceled the operation.
   */
  public static IResourceManager selectResourceManagerStartDialog(final Shell parentShell, 
                                                                  final Collection<IResourceManager> rmList,
                                                                  final String dialogText) {
    final CustomizableRMStartDialog dialog = new CustomizableRMStartDialog(parentShell, rmList, dialogText);
    return (dialog.open() == Window.OK) ? dialog.getSelectedResourceManager() : null;
  }

}
