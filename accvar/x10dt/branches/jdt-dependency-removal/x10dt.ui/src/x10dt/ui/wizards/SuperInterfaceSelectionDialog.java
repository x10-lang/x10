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
package x10dt.ui.wizards;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.actions.StatusInfo;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.ui.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.ui.dialogs.OpenTypeSelectionDialog;
import x10dt.search.ui.typeHierarchy.SearchUtils;
import x10dt.search.ui.typeHierarchy.TypeNameMatch;
import x10dt.ui.utils.WizardUtils;


/**
 * A type selection dialog providing means to open interface(s).
 */
public class SuperInterfaceSelectionDialog extends OpenTypeSelectionDialog {

	private static final int ADD_ID= IDialogConstants.CLIENT_ID + 1;

	private NewTypeWizardPage fTypeWizardPage;
	private List fOldContent;

	/**
	 * Creates new instance of SuperInterfaceSelectionDialog
	 *
	 * @param parent
	 *            shell to parent the dialog on
	 * @param context
	 *            context used to execute long-running operations associated
	 *            with this dialog
	 * @param page
	 *            page that opened this dialog
	 * @param p
	 *            the java project which will be considered when searching for
	 *            interfaces
	 */
	public SuperInterfaceSelectionDialog(Shell parent, IRunnableContext context, NewTypeWizardPage page, ISourceProject p) {
		super(parent, true, context, createSearchScope(p), 1);
		fTypeWizardPage= page;
		// to restore the content of the dialog field if the dialog is canceled
		fOldContent= fTypeWizardPage.getSuperInterfaces();
		setStatusLineAboveButtons(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SelectionDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ADD_ID, NewWizardMessages.SuperInterfaceSelectionDialog_addButton_label, true);
		super.createButtonsForButtonBar(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		return WizardUtils.getDialogSettingsSection("DialogBounds_SuperInterfaceSelectionDialog"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#updateButtonsEnableState(org.eclipse.core.runtime.IStatus)
	 */
	protected void updateButtonsEnableState(IStatus status) {
		super.updateButtonsEnableState(status);
		Button addButton= getButton(ADD_ID);
		if (addButton != null && !addButton.isDisposed())
			addButton.setEnabled(!status.matches(IStatus.ERROR));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
	 */
	protected void handleShellCloseEvent() {
		super.handleShellCloseEvent();
		// Handle the closing of the shell by selecting the close icon
		fTypeWizardPage.setSuperInterfaces(fOldContent, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	protected void cancelPressed() {
		fTypeWizardPage.setSuperInterfaces(fOldContent, true);
		super.cancelPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == ADD_ID) {
			addSelectedInterfaces();
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#okPressed()
	 */
	protected void okPressed() {
		addSelectedInterfaces();
		super.okPressed();
	}

	/*
	 * Adds selected interfaces to the list.
	 */
	private void addSelectedInterfaces() {
		StructuredSelection selection= getSelectedItems();
		if (selection == null)
			return;
		for (Iterator iter= selection.iterator(); iter.hasNext();) {
			Object obj= iter.next();
			if (obj instanceof TypeNameMatch) {
				accessedHistoryItem(obj);
				TypeNameMatch type= (TypeNameMatch) obj;
				String qualifiedName= getNameWithTypeParameters(type.getType());
				String message;

				if (fTypeWizardPage.addSuperInterface(qualifiedName)) {
					message= MessageFormat.format(NewWizardMessages.SuperInterfaceSelectionDialog_interfaceadded_info, qualifiedName);
				} else {
					message= MessageFormat.format(NewWizardMessages.SuperInterfaceSelectionDialog_interfacealreadyadded_info, qualifiedName);
				}
				updateStatus(new StatusInfo(IStatus.INFO, message));
			}
		}
	}

	/*
	 * Creates a searching scope including only one project.
	 */
	private static IX10SearchScope createSearchScope(ISourceProject p) {
		return SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, p.getRawProject());
	}

	/*(non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#handleDoubleClick()
	 */
	protected void handleDoubleClick() {
		buttonPressed(ADD_ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#handleSelected(org.eclipse.jface.viewers.StructuredSelection)
	 */
	protected void handleSelected(StructuredSelection selection) {
		super.handleSelected(selection);

		if (selection.size() == 0 && fTypeWizardPage.getSuperInterfaces().size() > fOldContent.size()) {
			// overrides updateStatus() from handleSelected() if
			// list of super interfaces was modified
			// the <code>super.handleSelected(selection)</code> has to be
			// called, because superclass implementation of this class updates
			// state of the table.

			updateStatus(Status.OK_STATUS);

			getButton(ADD_ID).setEnabled(false);
		} else {
			// if selection isn't empty, the add button should be enabled in
			// exactly the same scenarios as the OK button
			getButton(ADD_ID).setEnabled(getButton(OK).isEnabled());
		}
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog2#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.SUPER_INTERFACE_SELECTION_DIALOG);
	}

	public static String getNameWithTypeParameters(ITypeInfo type) {
		String superName= SearchUtils.getFullyQualifiedName(type, '.');
//		if (!JavaModelUtil.is50OrHigher(type.getJavaProject())) {
//			return superName;
//		}
//		try {
//			ITypeParameter[] typeParameters= type.getTypeParameters();
//			if (typeParameters.length > 0) {
//				StringBuffer buf= new StringBuffer(superName);
//				buf.append('<');
//				for (int k= 0; k < typeParameters.length; k++) {
//					if (k != 0) {
//						buf.append(',').append(' ');
//					}
//					buf.append(typeParameters[k].getElementName());
//				}
//				buf.append('>');
//				return buf.toString();
//			}
//		} catch (ModelException e) {
//			// ignore
//		}
		return superName;

	}

}
