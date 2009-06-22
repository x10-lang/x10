package com.ibm.watson.safari.x10.cheatsheets.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

public class LaunchX10ProgramAction extends Action implements ICheatSheetAction {
    public LaunchX10ProgramAction() {
	this("Launch an X10 program");
    }

    public LaunchX10ProgramAction(String text) {
	super(text, null);
    }

    public void run(String[] params, ICheatSheetManager manager) {
	Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	MessageDialog.openInformation(shell, "Launch X10 Program", "Fix Me! I should do something!");
    }
}
