/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.wizards.CppProjectWizard;
import org.eclipse.imp.x10dt.ui.perspective.actions.AbstractWizardToolbarAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Defines the toolbar action to activate the wizard for creating an X10 Project with C++ back-end.
 * 
 * @author egeay
 */
public final class OpenX10ProjectCppBCWizardToolbarAction extends AbstractWizardToolbarAction 
                                                          implements IWorkbenchWindowActionDelegate {

	// --- IWorkbenchWindowActionDelegate's Interface methods implementation

	public void dispose() {
	}

	public void init(final IWorkbenchWindow window) {
		setShell(window.getShell());
	}

	public void run(final IAction action) {
		super.run();
	}

	public void selectionChanged(final IAction action, final ISelection selection) {
		setSelection(selection);
	}

	// --- Abstract methods implementation

	protected INewWizard createNewWizard() {
	  boolean noResourceManager = true;
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        noResourceManager = false;
      }
    }
    if (noResourceManager) {
      ErrorDialog.openError(getShell(), LaunchMessages.OXPCWTA_ErrorDialogTitle, LaunchMessages.OXPCWTA_ErrorDialogMsg, 
                            new Status(IStatus.INFO, CppLaunchCore.PLUGIN_ID, LaunchMessages.OXPCWTA_ErrorDialogStatusMsg));
      return null;
    } else {
      return new CppProjectWizard();
    }
	}

}
