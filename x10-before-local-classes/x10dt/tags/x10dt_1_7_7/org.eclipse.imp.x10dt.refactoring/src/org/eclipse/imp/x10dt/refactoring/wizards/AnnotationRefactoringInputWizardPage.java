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

import org.eclipse.imp.x10dt.refactoring.AnnotationRefactoringBase;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * An input page with a helper method to set up a radio button group to select whether
 * to perform the transformation directly or simply add a transformation annotation.
 */
public abstract class AnnotationRefactoringInputWizardPage extends UserInputWizardPage {
    /**
     * @param name
     */
    public AnnotationRefactoringInputWizardPage(String name) {
        super(name);
    }

    protected void addAnnotationRadioButtons(Composite parent) {
        Group radioGroup= new Group(parent, SWT.NONE);
        radioGroup.setLayout(new FillLayout());
        radioGroup.setText("Implement refactoring...");

        GridData gd= new GridData();
        gd.horizontalSpan= 4;
        radioGroup.setLayoutData(gd);

        Button annotationRB= new Button(radioGroup, SWT.RADIO);
        annotationRB.setText("By adding annotation");

        Button transformRB= new Button(radioGroup, SWT.RADIO);
        transformRB.setText("By rewriting source");

        annotationRB.setSelection(true);
        transformRB.setSelection(false);
        getAnnotationRefactoring().setByAnnotation(true);
        annotationRB.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) { }
            public void widgetSelected(SelectionEvent e) {
                getAnnotationRefactoring().setByAnnotation(true);
            }
        });
        transformRB.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) { }
            public void widgetSelected(SelectionEvent e) {
                getAnnotationRefactoring().setByAnnotation(false);
            }
        });
    }

    private AnnotationRefactoringBase getAnnotationRefactoring() {
        return (AnnotationRefactoringBase) getRefactoring();
    }

    public boolean getByAnnotation() {
        return getAnnotationRefactoring().getByAnnotation();
    }
}
