/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10dt.search.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.ui.Messages;
import x10dt.search.ui.UISearchPlugin;
import x10dt.search.ui.dialogs.OpenTypeSelectionDialog;
import x10dt.search.ui.typeHierarchy.OpenTypeHierarchyUtil;

public class OpenTypeInHierarchyAction extends Action implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow fWindow;

	public OpenTypeInHierarchyAction() {
		super();
		setText(Messages.OpenTypeInHierarchyAction_label);
		setDescription(Messages.OpenTypeInHierarchyAction_description);
		setToolTipText(Messages.OpenTypeInHierarchyAction_tooltip);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_TYPE_IN_HIERARCHY_ACTION);
	}

	public void run() {
		Shell parent= UISearchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		OpenTypeSelectionDialog dialog= new OpenTypeSelectionDialog(parent, false,
			PlatformUI.getWorkbench().getProgressService(),
			null, 0);

		dialog.setTitle(Messages.OpenTypeInHierarchyAction_dialogTitle);
		dialog.setMessage(Messages.OpenTypeInHierarchyAction_dialogMessage);
		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;

		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			ITypeInfo type= (ITypeInfo)types[0];
			
			OpenTypeHierarchyUtil.open(new ITypeInfo[] { type }, fWindow);
		}
	}

	//---- IWorkbenchWindowActionDelegate ------------------------------------------------

	public void run(IAction action) {
		run();
	}

	public void dispose() {
		fWindow= null;
	}

	public void init(IWorkbenchWindow window) {
		fWindow= window;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
}
