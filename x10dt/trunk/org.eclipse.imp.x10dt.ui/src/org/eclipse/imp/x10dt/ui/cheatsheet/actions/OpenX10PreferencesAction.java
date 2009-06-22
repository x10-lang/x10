package org.eclipse.imp.x10dt.ui.cheatsheet.actions;

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

	prefsDialog.setSelectedNode("com.ibm.watson.safari.x10.preferences.X10PreferencePage");
	prefsDialog.open();
    }
}
