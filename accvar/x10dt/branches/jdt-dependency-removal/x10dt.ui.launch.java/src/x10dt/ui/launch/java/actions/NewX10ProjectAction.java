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
package x10dt.ui.launch.java.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import x10dt.ui.launch.java.wizards.JavaProjectWizard;

public class NewX10ProjectAction extends Action implements ICheatSheetAction {
    public NewX10ProjectAction() {
	this("Create a new X10 project");
    }

    public NewX10ProjectAction(String text) {
	super(text, null);
    }

    public void run(String[] params, ICheatSheetManager manager) {
	JavaProjectWizard newProjWizard= new JavaProjectWizard();
	Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	WizardDialog wizDialog= new WizardDialog(shell, newProjWizard);

	wizDialog.open();
    }
}
