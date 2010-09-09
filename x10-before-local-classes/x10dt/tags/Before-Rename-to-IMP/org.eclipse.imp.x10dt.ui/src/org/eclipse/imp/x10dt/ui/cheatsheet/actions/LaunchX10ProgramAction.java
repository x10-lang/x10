package x10.uide.cheatsheet.actions;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import x10.uide.launching.X10LaunchShortcut;

public class LaunchX10ProgramAction extends Action implements ICheatSheetAction {
    public LaunchX10ProgramAction() {
	this("Launch an X10 program");
    }

    public LaunchX10ProgramAction(String text) {
	super(text, null);
    }

    public void run(String[] params, ICheatSheetManager manager) {
	X10LaunchShortcut x10ls= new X10LaunchShortcut();
	IEditorPart editorPart= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

	x10ls.launch(editorPart, ILaunchManager.RUN_MODE);
    }
}
