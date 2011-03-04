/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/

package x10dt.core.builder.migration;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

final class ProjectMigrationDialog extends Dialog {
	private final Set<IProject> fMigrateProjects;
	private final Set<IProject> fBrokenProjects;
	private boolean fDontAskAgain;

	ProjectMigrationDialog(Shell parentShell, Set<IProject> brokenProjects, Set<IProject> migrateProjects) {
		super(parentShell);
		this.fMigrateProjects = migrateProjects;
		this.fBrokenProjects = brokenProjects;
	}

	public boolean getDontAskAgain() {
		return fDontAskAgain;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(350, 380);
		newShell.setText(Messages.ProjectMigrationDialog_title);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, Messages.ProjectMigrationDialog_proceedButtonLabel, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		GridLayout topLayout = new GridLayout(1, false);
		area.setLayout(topLayout);

		Text descriptionText = new Text(area, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		descriptionText.setText(Messages.ProjectMigrationDialog_explanationText);
		descriptionText.setBackground(parent.getBackground());
		descriptionText.setLayoutData(new GridData(340, 45));

		Label topLabel = new Label(area, SWT.NONE);
		topLabel.setText(Messages.ProjectMigrationDialog_projectListLabel);

		Composite tableButtons = new Composite(area, SWT.NONE);
		tableButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final CheckboxTableViewer cbTableViewer = CheckboxTableViewer.newCheckList(tableButtons, SWT.BORDER | SWT.V_SCROLL);

		cbTableViewer.setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			public void dispose() {}
			public Object[] getElements(Object inputElement) {
				return fBrokenProjects.toArray();
			}
		});
		cbTableViewer.setLabelProvider(new ITableLabelProvider() {
			public void addListener(ILabelProviderListener listener) {}

			public void removeListener(ILabelProviderListener listener) {}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void dispose() {}

			public String getColumnText(Object element, int columnIndex) {
				return ((IProject) element).getName();
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		cbTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				IProject p = (IProject) event.getElement();
				if (event.getChecked()) {
					fMigrateProjects.add(p);
				} else {
					fMigrateProjects.remove(p);
				}
			}
		});
		cbTableViewer.setCheckStateProvider(new ICheckStateProvider() {
			public boolean isGrayed(Object element) { return false; }
			public boolean isChecked(Object element) {
				return fMigrateProjects.contains(element);
			}
		});
		cbTableViewer.setInput(new Object()); // a dummy input just to trigger the initial update

		Composite selectButtons = new Composite(tableButtons, SWT.NONE);
		GridLayout selectButtonsLayout = new GridLayout(1, true);
		selectButtons.setLayout(selectButtonsLayout);

		Button selectAllButton = new Button(selectButtons, SWT.PUSH);
		selectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		selectAllButton.setText(Messages.ProjectMigrationDialog_selectAllButtonTitle);
		selectAllButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fMigrateProjects.addAll(fBrokenProjects);
				cbTableViewer.refresh();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button deselectAllButton = new Button(selectButtons, SWT.PUSH);
		deselectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		deselectAllButton.setText(Messages.ProjectMigrationDialog_deselectAllButtonTitle);
		deselectAllButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fMigrateProjects.clear();
				cbTableViewer.refresh();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		GridLayout tableButtonsLayout = new GridLayout(2, false);
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.heightHint= 100; // if you don't set this, the vertical scrollbar will never appear!
		cbTableViewer.getTable().setLayoutData(gridData);

		selectButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		tableButtons.setLayout(tableButtonsLayout);

		final Button dontAskAgainCB = new Button(area, SWT.CHECK);

		dontAskAgainCB.setText(Messages.ProjectMigrationDialog_dontAskCheckboxTitle);

		Text dontAskExplanationText= new Text(area, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		dontAskExplanationText.setBackground(parent.getBackground());
		dontAskExplanationText.setLayoutData(new GridData(340, 65));
		dontAskExplanationText.setText(Messages.ProjectMigrationDialog_dontAskExplanationText1
				+ Messages.ProjectMigrationDialog_dontAskExplanationText2
				+ Messages.ProjectMigrationDialog_dontAskExplanationText3);

		dontAskAgainCB.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fDontAskAgain = dontAskAgainCB.getSelection();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		return area;
	}
}
