/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.perspective.actions;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PlatformUI;

/**
 * Common base for actions creating X10-related wizards.
 * 
 * @author egeay
 */
public abstract class AbstractWizardToolbarAction extends Action {

	// --- Abstract methods definition

	protected abstract INewWizard createNewWizard();

	// --- Overridden methods

	public final void run() {
		final INewWizard wizard = createNewWizard();
		if (wizard != null) {
		  wizard.init(PlatformUI.getWorkbench(), this.fSelection);
		
		  final WizardDialog dialog = new WizardDialog(this.fShell, wizard);
		  dialog.create();

		  notifyResult(dialog.open() == Window.OK);
		}
	}

	// --- Code for descendants

	protected final boolean isNotEmptyWorskpace() {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		if (workspaceRoot.getProjects().length == 0) {
			MessageDialog.openWarning(this.fShell, "New Element", "In order to create this element a project needs to be created first.");
			return false;
		}
		return true;
	}

	protected final Shell getShell() {
		return this.fShell;
	}

	protected final void setSelection(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.fSelection = (IStructuredSelection) selection;
		} else {
			this.fSelection = StructuredSelection.EMPTY;
		}
	}

	protected final void setShell(final Shell shell) {
		this.fShell = shell;
	}

	// --- Fields

	private Shell fShell;

	private IStructuredSelection fSelection;

}
