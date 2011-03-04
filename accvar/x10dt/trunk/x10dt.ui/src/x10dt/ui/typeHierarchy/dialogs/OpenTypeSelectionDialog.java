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
package x10dt.ui.typeHierarchy.dialogs;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Shell;

import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.TypeSelectionExtension;

/**
 * A type selection dialog used for opening types.
 */
public class OpenTypeSelectionDialog extends FilteredTypesSelectionDialog {

	private static final String DIALOG_SETTINGS= "x10dt.ui.dialogs.OpenTypeSelectionDialog"; //$NON-NLS-1$

	public OpenTypeSelectionDialog(Shell parent, boolean multi, IRunnableContext context, IX10SearchScope scope, int elementKinds) {
		this(parent, multi, context, scope, elementKinds, null);
	}

	public OpenTypeSelectionDialog(Shell parent, boolean multi, IRunnableContext context, IX10SearchScope scope, int elementKinds, TypeSelectionExtension extension) {
		super(parent, multi, context, scope, elementKinds, extension);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.OPEN_TYPE_DIALOG);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog#getDialogSettings()
	 */
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings= X10DTUIPlugin.getInstance().getDialogSettings().getSection(DIALOG_SETTINGS);

		if (settings == null) {
			settings= X10DTUIPlugin.getInstance().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}

		return settings;
	}
}
