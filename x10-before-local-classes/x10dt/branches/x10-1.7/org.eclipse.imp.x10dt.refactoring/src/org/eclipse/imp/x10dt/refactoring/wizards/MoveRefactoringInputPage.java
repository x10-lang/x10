/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package org.eclipse.imp.x10dt.refactoring.wizards;

import org.eclipse.imp.x10dt.refactoring.MoveRefactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MoveRefactoringInputPage extends UserInputWizardPage {
    public MoveRefactoringInputPage(String name) {
        super(name);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite result = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();

        layout.numColumns = 4;
        result.setLayout(layout);

        final Label targetLbl= new Label(result, SWT.NONE);
        targetLbl.setText("Destination:");
        final Text targetText= new Text(result, SWT.SINGLE);
        final Button browsePB= new Button(result, SWT.PUSH);
        browsePB.setText("Browse...");
        browsePB.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) { }
            public void widgetSelected(SelectionEvent e) {
                // TODO Bring up dialog to select the destination type for the move
            }
        });

        setControl(result);
    }

    private MoveRefactoring getMoveRefactoring() {
        return (MoveRefactoring) getRefactoring();
    }
}
