package com.ibm.watson.safari.x10.wizards;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import com.ibm.watson.safari.x10.X10Plugin;
import com.ibm.watson.safari.x10.builder.X10ProjectNature;

/**
 * This is the "New X10 Class" wizard. Its role is to create a new class in the given package
 * in the given project. The wizard creates one source file with the extension "x10" and
 * expanded with the default class template.
 */
public class NewX10ClassWizard extends NewElementWizard implements INewWizard {
    private NewX10ClassPage fPage;

    public NewX10ClassWizard() {
	super();
	setNeedsProgressMonitor(true);
    }

    /**
     * Adding the fPage to the wizard.
     */
    public void addPages() {
	fPage= new NewX10ClassPage(getSelection());
	addPage(fPage);
	fPage.init(getSelection());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
	fPage.createType(monitor); // use the full progress monitor
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    public boolean performFinish() {
	warnAboutTypeCommentDeprecation();
	boolean res= super.performFinish();
	if (res) {
	    IResource resource= fPage.getModifiedResource();
	    if (resource != null) {
		if (getWorkbench() == null)
		    init(PlatformUI.getWorkbench(), null);
		selectAndReveal(resource);
		openResource((IFile) resource);
	    }
	}
	return res;
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
	super.init(workbench, selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
     */
    public IJavaElement getCreatedElement() {
	return fPage.getCreatedType();
    }
}
