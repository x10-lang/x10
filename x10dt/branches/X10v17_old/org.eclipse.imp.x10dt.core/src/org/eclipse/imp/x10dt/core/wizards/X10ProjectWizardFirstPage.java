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

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.core.wizards;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.imp.wizards.NewProjectWizardFirstPage;
import org.eclipse.imp.wizards.fields.LayoutUtil;
import org.eclipse.imp.wizards.fields.SelectionButtonDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class X10ProjectWizardFirstPage extends NewProjectWizardFirstPage {
    private GenerateGroup fGenGroup;

    public X10ProjectWizardFirstPage() {
        super("X10 Project");
        setPageComplete(false);
        setTitle("New X10 Project");
        setDescription("Creates a new X10 project");
        fInitialName= ""; //$NON-NLS-1$
    }

    /**
     * Request a project layout.
     */
    public final class GenerateGroup implements Observer, SelectionListener {
        private final SelectionButtonDialogField fNoSrcRadio, fHelloX10Radio;

        private final Group fGroup;

        public GenerateGroup(Composite composite) {
            fGroup= new Group(composite, SWT.NONE);
            fGroup.setFont(composite.getFont());
            fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            fGroup.setLayout(initGridLayout(new GridLayout(3, false), true));
            //fGroup.setText(NewWizardMessages.JavaProjectWizardFirstPage_LayoutGroup_title);   // <= 3.3
            //fGroup.setText(NewWizardMessages.NewJavaProjectWizardPageOne_LayoutGroup_title);  // >= 3.4
            fGroup.setText("Sample source");

            fNoSrcRadio= new SelectionButtonDialogField(SWT.RADIO);
            //fStdRadio.setLabelText(NewWizardMessages.JavaProjectWizardFirstPage_LayoutGroup_option_oneFolder);        // <= 3.3
            //fStdRadio.setLabelText(NewWizardMessages.NewJavaProjectWizardPageOne_LayoutGroup_option_oneFolder);       // >= 3.4
            fNoSrcRadio.setLabelText("Create &no sample source code");

            fHelloX10Radio= new SelectionButtonDialogField(SWT.RADIO);
            //fSrcBinRadio.setLabelText(NewWizardMessages.JavaProjectWizardFirstPage_LayoutGroup_option_separateFolders);       // <= 3.3
            //fSrcBinRadio.setLabelText(NewWizardMessages.NewJavaProjectWizardPageOne_LayoutGroup_option_separateFolders);      // >= 3.4
            fHelloX10Radio.setLabelText("&Create a sample 'Hello World' X10 application");
            
            fNoSrcRadio.doFillIntoGrid(fGroup, 3);
            LayoutUtil.setHorizontalGrabbing(fNoSrcRadio.getSelectionButton(null));

            fHelloX10Radio.doFillIntoGrid(fGroup, 2);

            boolean genSrc= true;
            fNoSrcRadio.setSelection(!genSrc);
            fHelloX10Radio.setSelection(genSrc);
        }

        public void update(Observable o, Object arg) {
            final boolean detect= false; // fDetectGroup.mustDetect();
            fNoSrcRadio.setEnabled(!detect);
            fHelloX10Radio.setEnabled(!detect);
            fGroup.setEnabled(!detect);
        }

        public boolean isGenHello() {
            return fHelloX10Radio.isSelected();
        }

        public boolean isNoGen() {
            return fNoSrcRadio.isSelected();
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            widgetDefaultSelected(e);
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
//          String id= NewJavaProjectPreferencePage.ID;
//          PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, null).open();
//          fDetectGroup.handleComplianceChange();
//          fJREGroup.handlePossibleComplianceChange();
        }
    }

    @Override
    public void createControl(Composite parent) {
        Composite local= new Composite(parent, SWT.NONE);
        local.setFont(parent.getFont());
        local.setLayout(initGridLayout(new GridLayout(1, false), true));
        local.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        super.createControl(local);

        final Composite composite= new Composite(local, SWT.NULL);
        composite.setFont(parent.getFont());
        composite.setLayout(initGridLayout(new GridLayout(1, false), true));
        composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        fGenGroup= new GenerateGroup(composite);
        setControl(local);
    }

    public boolean isNoGen() {
        return fGenGroup.isNoGen();
    }

    public boolean isGenHello() {
        return fGenGroup.isGenHello();
    }

    public String getJRECompliance() {
        return "1.5"; // RMF 7/25/2006 - Always use 1.5 compliance; the X10
                        // runtime requires it
    }
}
