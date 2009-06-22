package org.eclipse.imp.x10dt.ui.cheatsheet.actions;

import org.eclipse.imp.x10dt.core.wizards.X10ProjectWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

public class NewX10ProjectAction extends Action implements ICheatSheetAction {
    public NewX10ProjectAction() {
	this("Create a new X10 project");
    }

    public NewX10ProjectAction(String text) {
	super(text, null);
    }

    public void run(String[] params, ICheatSheetManager manager) {
	X10ProjectWizard newProjWizard= new X10ProjectWizard();
	Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	WizardDialog wizDialog= new WizardDialog(shell, newProjWizard);

	wizDialog.open();
    }
}
