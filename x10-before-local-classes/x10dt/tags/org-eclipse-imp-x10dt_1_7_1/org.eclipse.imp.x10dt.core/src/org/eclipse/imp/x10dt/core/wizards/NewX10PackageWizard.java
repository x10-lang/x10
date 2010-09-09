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

package org.eclipse.imp.x10dt.core.wizards;

import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.ui.wizards.NewPackageWizardPage;

import org.eclipse.jdt.internal.ui.JavaPluginImages;

public class NewX10PackageWizard extends NewElementWizard {

    private NewX10PackageWizardPage fPage;

    public NewX10PackageWizard() {
        super();
        setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWPACK);
        setDialogSettings(X10Plugin.getInstance().getDialogSettings());
        setWindowTitle(NewWizardMessages.NewPackageCreationWizard_title);
    }

    /*
     * @see Wizard#addPages
     */
    public void addPages() {
        super.addPages();
        fPage= new NewX10PackageWizardPage();
        addPage(fPage);
        fPage.init(getSelection());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        fPage.createPackage(monitor); // use the full progress monitor
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    public boolean performFinish() {
        boolean res= super.performFinish();
        if (res) {
            selectAndReveal(fPage.getModifiedResource());
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
     */
    public IJavaElement getCreatedElement() {
        return fPage.getNewPackageFragment();
    }
}
