package org.eclipse.imp.x10dt.ui.cheatsheet.actions;

import org.eclipse.imp.x10dt.core.wizards.NewX10ClassWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

public class NewX10TypeAction extends Action implements ICheatSheetAction {
    public NewX10TypeAction() {
	this("Create a new X10 class");
    }

    public NewX10TypeAction(String text) {
	super(text, null);
    }

    public void run(String[] params, ICheatSheetManager manager) {
	NewX10ClassWizard newClassWizard= new NewX10ClassWizard();
	Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	WizardDialog wizDialog= new WizardDialog(shell, newClassWizard);

	wizDialog.open();
    }
}
