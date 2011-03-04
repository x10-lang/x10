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
package x10dt.ui.launch.java.wizards;

import org.eclipse.imp.ui.wizards.CapabilityConfigurationPage;
import org.eclipse.imp.ui.wizards.NewProjectWizardPageOne;

import x10dt.ui.launch.core.wizards.X10ProjectWizard;
import x10dt.ui.launch.java.Activator;

public class JavaProjectWizard extends X10ProjectWizard {
	
	public JavaProjectWizard() {
		super();
		setDialogSettings(Activator.getDefault().getDialogSettings());
		setWindowTitle("New X10 Project (Java back-end)");
	}

	// --- Overridden methods
	protected NewProjectWizardPageOne getPageOne() {
		return new X10ProjectWizardFirstPage();
	}

	protected CapabilityConfigurationPage getPageTwo() {
		return new X10ProjectWizardSecondPage(fFirstPage);
	}
}
