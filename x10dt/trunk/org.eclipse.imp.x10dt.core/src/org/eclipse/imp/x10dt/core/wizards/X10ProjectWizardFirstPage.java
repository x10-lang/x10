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
