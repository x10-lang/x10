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
package x10dt.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

/**
 * This is the "New X10 Class" wizard. Its role is to create a new class in the given package in the given project. The wizard
 * creates one source file with the extension "x10" and expanded with the default class template.
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
    fPage = new NewX10ClassPage(getSelection());
    addPage(fPage);
    fPage.init(getSelection());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
   */
  protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
    fPage.createType(monitor); // use the full progress monitor
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.wizard.IWizard#performFinish()
   */
  public boolean performFinish() {
    warnAboutTypeCommentDeprecation();
    boolean res = super.performFinish();
    if (res) {
      IResource resource = fPage.getModifiedResource();
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

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
   */
  public IJavaElement getCreatedElement() {
    return fPage.getCreatedType();
  }
}
