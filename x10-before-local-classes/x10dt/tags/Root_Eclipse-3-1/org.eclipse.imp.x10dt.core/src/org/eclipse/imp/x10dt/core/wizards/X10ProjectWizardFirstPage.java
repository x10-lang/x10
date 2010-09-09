/*
 * Created on Feb 6, 2006
 */
package org.eclipse.imp.x10dt.core.wizards;

import org.eclipse.imp.wizards.NewProjectWizardFirstPage;

public class X10ProjectWizardFirstPage extends NewProjectWizardFirstPage {
    public X10ProjectWizardFirstPage() {
	super("X10 Project");
	setPageComplete(false);
	setTitle("New X10 Project");
	setDescription("Creates a new X10 project");
	fInitialName= ""; //$NON-NLS-1$
    }

    public String getJRECompliance() {
        return "1.5"; // RMF 7/25/2006 - Always use 1.5 compliance; the X10 runtime requires it
    }
}
