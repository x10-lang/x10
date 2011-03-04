package x10dt.search.ui.typeHierarchy;
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


import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.ui.wizards.fields.DialogField;
import org.eclipse.imp.ui.wizards.fields.IListAdapter;
import org.eclipse.imp.ui.wizards.fields.ListDialogField;
import org.eclipse.imp.ui.wizards.utils.LayoutUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.ui.UISearchPlugin;


public class HistoryListAction extends Action {

	private class HistoryListDialog extends StatusDialog {

		private ListDialogField fHistoryList;
		private IStatus fHistoryStatus;
		private ITypeInfo fResult;

		private HistoryListDialog(Shell shell, IMemberInfo[] elements) {
			super(shell);
			setTitle(TypeHierarchyMessages.HistoryListDialog_title);

			String[] buttonLabels= new String[] {
				TypeHierarchyMessages.HistoryListDialog_remove_button,
			};

			IListAdapter adapter= new IListAdapter() {
				public void customButtonPressed(ListDialogField field, int index) {
					doCustomButtonPressed();
				}
				public void selectionChanged(ListDialogField field) {
					doSelectionChanged();
				}

				public void doubleClicked(ListDialogField field) {
					doDoubleClicked();
				}
			};

			
			fHistoryList= new ListDialogField(adapter, buttonLabels, ServiceFactory.getInstance().getLabelProvider(LanguageRegistry.findLanguage("X10")));
			fHistoryList.setLabelText(TypeHierarchyMessages.HistoryListDialog_label);
			fHistoryList.setElements(Arrays.asList(elements));

			ISelection sel;
			if (elements.length > 0) {
				sel= new StructuredSelection(elements[0]);
			} else {
				sel= new StructuredSelection();
			}

			fHistoryList.selectElements(sel);
		}

		/*
		 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
		 * @since 3.4
		 */
		protected boolean isResizable() {
			return true;
		}

		/*
		 * @see Dialog#createDialogArea(Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			initializeDialogUnits(parent);

			Composite composite= (Composite) super.createDialogArea(parent);

			Composite inner= new Composite(composite, SWT.NONE);
			inner.setFont(parent.getFont());

			inner.setLayoutData(new GridData(GridData.FILL_BOTH));

			LayoutUtil.doDefaultLayout(inner, new DialogField[] { fHistoryList }, true, 0, 0);
			LayoutUtil.setHeightHint(fHistoryList.getListControl(null), convertHeightInCharsToPixels(12));
			LayoutUtil.setHorizontalGrabbing(fHistoryList.getListControl(null));

			applyDialogFont(composite);
			return composite;
		}

		/**
		 * Method doCustomButtonPressed.
		 */
		private void doCustomButtonPressed() {
			fHistoryList.removeElements(fHistoryList.getSelectedElements());
		}

		private void doDoubleClicked() {
			if (fHistoryStatus.isOK()) {
				okPressed();
			}
		}


		private void doSelectionChanged() {
			IStatus status= Status.OK_STATUS;
			List selected= fHistoryList.getSelectedElements();
			if (selected.size() != 1) {
//				status.setError(""); //$NON-NLS-1$
				fResult= null;
			} else {
				fResult= (ITypeInfo) selected.get(0);
			}
			fHistoryList.enableButton(0, fHistoryList.getSize() > selected.size() && selected.size() != 0);
			fHistoryStatus= status;
			updateStatus(status);
		}

		public ITypeInfo getResult() {
			return fResult;
		}

		public ITypeInfo[] getRemaining() {
			List elems= fHistoryList.getElements();
			return (ITypeInfo[]) elems.toArray(new ITypeInfo[elems.size()]);
		}

		/*
		 * @see org.eclipse.jface.window.Window#configureShell(Shell)
		 */
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.HISTORY_LIST_DIALOG);
		}

	}

	private TypeHierarchyViewPart fView;

	public HistoryListAction(TypeHierarchyViewPart view) {
		fView= view;
		setText(TypeHierarchyMessages.HistoryListAction_label);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.HISTORY_LIST_ACTION);
	}

	/*
	 * @see IAction#run()
	 */
	public void run() {
		IMemberInfo[] historyEntries= fView.getHistoryEntries();
		HistoryListDialog dialog= new HistoryListDialog(UISearchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), historyEntries);
		if (dialog.open() == Window.OK) {
			fView.setHistoryEntries(dialog.getRemaining());
			fView.setInputElement(dialog.getResult());
		}
	}

}

