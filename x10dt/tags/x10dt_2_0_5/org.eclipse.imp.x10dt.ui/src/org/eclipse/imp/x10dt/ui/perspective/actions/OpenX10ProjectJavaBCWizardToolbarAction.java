/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.perspective.actions;

import org.eclipse.imp.x10dt.core.wizards.X10ProjectWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public final class OpenX10ProjectJavaBCWizardToolbarAction extends AbstractWizardToolbarAction 
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
		return new X10ProjectWizard();
	}

}
