/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package x10dt.refactoring.wizards;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import x10dt.refactoring.RenameRefactoring;

public class RenameInputPage extends UserInputWizardPage {
    private Text fNameText;
    private final RenameRefactoring fRefactoring;

    public RenameInputPage(String name, RenameRefactoring refactoring) {
        super(name);
        fRefactoring= refactoring;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite result= new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout= new GridLayout();
        layout.numColumns= 2;
        result.setLayout(layout);

        final Label nameLabel= new Label(result, SWT.NONE);
        nameLabel.setText("New name:");

        fNameText= new Text(result, SWT.NONE);
        fNameText.setText(fRefactoring.getCurName());
        fNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                fRefactoring.setNewName(((Text) e.widget).getText());
            }
        });
        // final Button deleteButton= new Button(result, SWT.CHECK);
        //
        // deleteButton.setText("Delete declarations after inlining");
        // deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //
        // deleteButton.addSelectionListener(new SelectionListener() {
        // public void widgetSelected(SelectionEvent e) {
        // // Set a parameter on the refactoring, e.g. getRenameRefactoring().setDoDelete(deleteButton.getSelection());
        // }
        // public void widgetDefaultSelected(SelectionEvent e) { }
        // });
    }

    public String getNewName() {
        return fNameText.getText();
    }

    private RenameRefactoring getRenameRefactoring() {
        return (RenameRefactoring) getRefactoring();
    }
}
