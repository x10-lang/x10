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

import org.eclipse.imp.x10dt.refactoring.LoopUnrollRefactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class LoopUnrollInputPage extends UserInputWizardPage implements Listener {
    private Text fText;

    public LoopUnrollInputPage(String name) {
        super(name);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite result = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();

        layout.numColumns = 1;
        result.setLayout(layout);

        final Label numTimesLabel= new Label(result, SWT.NONE);
        numTimesLabel.setText("# of times to unroll:");
        final Text numTimesText= new Text(result, SWT.SINGLE);

        numTimesText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String newText= numTimesText.getText();
                if (newText.length() > 0) {
                    try {
                        int factor= Integer.parseInt(newText);
                        // Apparently, in the literature, unrolling "twice" gives an unroll factor of 3,
                        // i.e., a loop w/ 3 copies of the body.
                        getLoopUnrollRefactoring().setUnrollFactor(factor+1);
                    } catch (NumberFormatException ex) {
                        LoopUnrollInputPage.this.setErrorMessage("Unroll count must be an integral number");
                    }
                }
            }
        });

        Group rbGroup= new Group(result, SWT.SHADOW_ETCHED_IN);
        rbGroup.setText("Handling loops with non-integral # of unrollings:");
        final Button addAssertRB= new Button(rbGroup, SWT.RADIO);
        final Button genTailLoopRB= new Button(rbGroup, SWT.RADIO);
        GridLayout rbgLayout= new GridLayout();
        rbgLayout.numColumns= 1;
        rbGroup.setLayout(rbgLayout);

        addAssertRB.setText("Add assertion that loop bounds is multiple of unroll factor");
        genTailLoopRB.setText("Generate additional loop to handle iterations beyond unroll boundary");

        addAssertRB.setSelection(false);
        genTailLoopRB.setSelection(true);
        addAssertRB.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) { }

            public void widgetSelected(SelectionEvent e) {
                genTailLoopRB.setSelection(!addAssertRB.getSelection());
                getLoopUnrollRefactoring().setAddAssertion();
            }
        });
        genTailLoopRB.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) { }

            public void widgetSelected(SelectionEvent e) {
                addAssertRB.setSelection(!genTailLoopRB.getSelection());
                getLoopUnrollRefactoring().setGenerateTailLoop();
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
    }

    private LoopUnrollRefactoring getLoopUnrollRefactoring() {
        return (LoopUnrollRefactoring) getRefactoring();
    }
}
