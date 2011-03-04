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

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.NewProjectAction;
import org.eclipse.ui.dialogs.SelectionDialog;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.Messages;
import x10dt.ui.typeHierarchy.SearchUtils;
import x10dt.ui.typeHierarchy.dialogs.OpenTypeSelectionDialog;


public class OpenTypeAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	public OpenTypeAction() {
		super();
		setText(Messages.OpenTypeAction_label);
		setDescription(Messages.OpenTypeAction_description);
		setToolTipText(Messages.OpenTypeAction_tooltip);
		setImageDescriptor(X10DTUIPlugin.OPENTYPE_DESC);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_TYPE_ACTION);
	}

	public void run() {
		runWithEvent(null);
	}
	
	public void runWithEvent(Event e) {
		Shell parent= X10DTUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
		if (! doCreateProjectFirstOnEmptyWorkspace(parent)) {
			return;
		}

		SelectionDialog dialog= new OpenTypeSelectionDialog(parent, true, PlatformUI.getWorkbench().getProgressService(), null, 0);
		dialog.setTitle(Messages.OpenTypeAction_dialogTitle);
		dialog.setMessage(Messages.OpenTypeAction_dialogMessage);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;

		Object[] types= dialog.getResult();
		if (types == null || types.length == 0)
			return;

		if (types.length == 1) {
			try {
				SearchUtils.openEditor((ITypeInfo)types[0]);
			} catch (CoreException x) {
				//ExceptionHandler.handle(x, Messages.OpenTypeAction_errorTitle, Messages.OpenTypeAction_errorMessage);
				X10DTUIPlugin.log(x);
			}
			return;
		}

		final IWorkbenchPage workbenchPage= X10DTUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (workbenchPage == null) {
			IStatus status= new Status(IStatus.ERROR, X10DTUIPlugin.PLUGIN_ID, Messages.OpenTypeAction_no_active_WorkbenchPage);
			//ExceptionHandler.handle(status, Messages.OpenTypeAction_errorTitle, Messages.OpenTypeAction_errorMessage);
			return;
		}

		MultiStatus multiStatus= new MultiStatus(X10DTUIPlugin.PLUGIN_ID, 10001, Messages.OpenTypeAction_multiStatusMessage, null);

		for (int i= 0; i < types.length; i++) {
			ITypeInfo type= (ITypeInfo)types[i];
			try {
				SearchUtils.openEditor(type);
			} catch (CoreException x) {
				multiStatus.merge(x.getStatus());
			}
		}

		if (!multiStatus.isOK())
		{
			//ExceptionHandler.handle(multiStatus, Messages.OpenTypeAction_errorTitle, Messages.OpenTypeAction_errorMessage);
		}
	}

	/**
	 * Opens the new project dialog if the workspace is empty.
	 * @param parent the parent shell
	 * @return returns <code>true</code> when a project has been created, or <code>false</code> when the
	 * new project has been canceled.
	 */
	protected boolean doCreateProjectFirstOnEmptyWorkspace(Shell parent) {
		IWorkspaceRoot workspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
		if (workspaceRoot.getProjects().length == 0) {
			String title= Messages.OpenTypeAction_dialogTitle;
			String message= Messages.OpenTypeAction_createProjectFirst;
			if (MessageDialog.openQuestion(parent, title, message)) {
				new NewProjectAction().run();
				return workspaceRoot.getProjects().length != 0;
			}
			return false;
		}
		return true;
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
