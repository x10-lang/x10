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
package org.eclipse.imp.x10dt.core.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.x10dt.core.builder.X10ProjectNature;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * A simple action to enable the X10 builder for the currently-selected project.
 * @see IWorkbenchWindowActionDelegate
 */
public class BuilderEnable implements IWorkbenchWindowActionDelegate {
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
	    } else if (first instanceof IJavaProject) {
		fProject= ((IJavaProject) first).getProject();
	    }
	}
    }

    public void dispose() {}

    public void init(IWorkbenchWindow window) {}
}