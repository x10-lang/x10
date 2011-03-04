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

import org.eclipse.imp.x10dt.refactoring.StripMineLoopRefactoring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class StripMineLoopInputPage extends AnnotationRefactoringInputWizardPage implements Listener {
    private Text fText;

    public StripMineLoopInputPage(String name) {
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

        addAnnotationRadioButtons(result);

        final Label numTimesLabel= new Label(result, SWT.NONE);
        numTimesLabel.setText("Strip size:");
        final Text numTimesText= new Text(result, SWT.SINGLE);

        numTimesText.setText("4");
        getStripMineLoopRefactoring().setStripFactor(4);

        numTimesText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String newText= numTimesText.getText();
                if (newText.length() > 0) {
                    try {
                        int factor= Integer.parseInt(newText);
                        if (factor < 1) {
                            StripMineLoopInputPage.this.setErrorMessage("Strip size must be a positive integral number");
                            StripMineLoopInputPage.this.setPageComplete(false);
                        } else {
                            getStripMineLoopRefactoring().setStripFactor(factor);
                            StripMineLoopInputPage.this.setErrorMessage("");
                            StripMineLoopInputPage.this.setPageComplete(true);
                        }
                    } catch (NumberFormatException ex) {
                        StripMineLoopInputPage.this.setErrorMessage("Strip size must be a positive integral number");
                        StripMineLoopInputPage.this.setPageComplete(false);
                    }
                } else {
                    StripMineLoopInputPage.this.setErrorMessage("Strip size must be a positive integral number");
                    StripMineLoopInputPage.this.setPageComplete(false);
                }
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

    private StripMineLoopRefactoring getStripMineLoopRefactoring() {
        return (StripMineLoopRefactoring) getRefactoring();
    }
}
