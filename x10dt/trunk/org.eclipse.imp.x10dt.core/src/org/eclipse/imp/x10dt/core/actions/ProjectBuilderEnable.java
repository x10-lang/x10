package org.eclipse.imp.x10dt.core.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.x10dt.core.builder.X10ProjectNature;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ProjectBuilderEnable implements IObjectActionDelegate {
    private IProject fProject;

    public ProjectBuilderEnable() {
	super();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
	new X10ProjectNature().addToProject(fProject);
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
	if (selection instanceof IStructuredSelection) {
	    IStructuredSelection ss= (IStructuredSelection) selection;
	    Object first= ss.getFirstElement();

	    if (first instanceof IProject) {
		fProject= (IProject) first;
	    } else if (first instanceof IJavaProject) {
		fProject= ((IJavaProject) first).getProject();
	    }
	}
    }
}
