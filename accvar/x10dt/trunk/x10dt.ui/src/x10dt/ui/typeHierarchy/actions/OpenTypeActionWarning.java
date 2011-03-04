/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10dt.ui.typeHierarchy.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.Messages;


public class OpenTypeActionWarning extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	public OpenTypeActionWarning() {
		super();
	}

	public void run() {
		runWithEvent(null);
	}
	
	public void runWithEvent(Event e) {
		Shell parent= X10DTUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.openWarning(parent, Messages.TypeSelectionDialog_keybindingWarning_title, Messages.TypeSelectionDialog_keybindingWarning_message);
	}


	// ---- IWorkbenchWindowActionDelegate
	// ------------------------------------------------

	public void run(IAction action) {
		run();
	}

	public void dispose() {
		// do nothing.
	}

	public void init(IWorkbenchWindow window) {
		// do nothing.
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}

	// ---- IActionDelegate2
	// ------------------------------------------------

	public void runWithEvent(IAction action, Event event) {
		runWithEvent(event);
	}

	public void init(IAction action) {
		// do nothing.
	}
}
