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

import org.eclipse.imp.x10dt.refactoring.LoopFlatParallelizationRefactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class LoopFlatParallelizationInputPage extends UserInputWizardPage implements Listener {
    private Text fText;

    public LoopFlatParallelizationInputPage(String name) {
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

        final Button verboseCB= new Button(result, SWT.CHECK);
        verboseCB.setText("Verbose");
        verboseCB.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) { }
            public void widgetSelected(SelectionEvent e) {
                boolean state= verboseCB.getSelection();
                LoopFlatParallelizationInputPage.this.getLoopFlatParallelizationRefactoring().setVerbose(state);
            }
        });

        setControl(result);
    }

    public void handleEvent(Event e) {
        Widget source = e.widget;
        String fTextResult = "";
        if (source == fText) {
            fTextResult = fText.getText();
            if (fTextResult == null)
                fTextResult = "";
        }
//      getLoopFlatParallelizationRefactoring().setPlace(fTextResult);
    }

    private LoopFlatParallelizationRefactoring getLoopFlatParallelizationRefactoring() {
        return (LoopFlatParallelizationRefactoring) getRefactoring();
    }
}
