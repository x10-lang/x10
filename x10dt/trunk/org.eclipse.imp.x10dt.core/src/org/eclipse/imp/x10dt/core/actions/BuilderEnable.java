package com.ibm.watson.safari.x10.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.ibm.watson.safari.x10.X10ProjectNature;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown in the UI. When the user tries to use the
 * action, this delegate will be created and execution will be delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class BuilderEnable implements IWorkbenchWindowActionDelegate {
    private IWorkbenchWindow window;
    private IProject fProject;

    public BuilderEnable() {}

    /**
     * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench UI.
     * @see IWorkbenchWindowActionDelegate#run
     */
    public void run(IAction action) {
	new X10ProjectNature().addToProject(fProject);
    }

    /**
     * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but this can only happen after the
     * delegate has been created.
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection) {
	if (selection instanceof IStructuredSelection) {
	    IStructuredSelection ss= (IStructuredSelection) selection;
	    Object first= ss.getFirstElement();

	    if (first instanceof IProject) {
		fProject= (IProject) first;
	    }
	}
    }

    /**
     * We can use this method to dispose of any system resources we previously allocated.
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {}

    /**
     * We will cache window object in order to be able to provide parent shell for the message dialog.
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
	this.window= window;
    }
}