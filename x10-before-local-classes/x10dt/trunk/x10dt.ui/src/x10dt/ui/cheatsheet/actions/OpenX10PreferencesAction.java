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
package x10dt.ui.cheatsheet.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

public class OpenX10PreferencesAction extends Action implements ICheatSheetAction {
    public OpenX10PreferencesAction() {
	this("Open the X10 Preferences page");
    }

    public OpenX10PreferencesAction(String text) {
	super(text, null);
    }

    public void run(String[] params, ICheatSheetManager manager) {
	Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	PreferenceManager prefMgr= PlatformUI.getWorkbench().getPreferenceManager();
	PreferenceDialog prefsDialog= new PreferenceDialog(shell, prefMgr);

	prefsDialog.setSelectedNode("x10dt.core.preferences.X10PreferencePage");
	prefsDialog.open();
    }
}
