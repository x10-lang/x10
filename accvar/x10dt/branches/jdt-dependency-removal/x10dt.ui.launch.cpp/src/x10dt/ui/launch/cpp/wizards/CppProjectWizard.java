/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.cpp.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.ui.wizards.CapabilityConfigurationPage;
import org.eclipse.imp.ui.wizards.NewProjectWizardPageOne;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import x10dt.ui.launch.core.wizards.X10ProjectWizard;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import x10dt.ui.launch.cpp.platform_conf.X10PlatformConfFactory;

/**
 * Responsible for creating the wizard that allow the creation of an X10 project with C++ back-end.
 * 
 * @author egeay
 */
public class CppProjectWizard extends X10ProjectWizard {

  /**
   * Creates the pages of this wizard and defines the title, dialog settings and default page image descriptor.
   */
  public CppProjectWizard() {
	  super();
    setWindowTitle(LaunchMessages.PW_WindowTitle);
    setDialogSettings(CppLaunchCore.getInstance().getDialogSettings());
    
    this.fFirstPage = new CppProjectNameDefWizardPage();
    this.fSecondPage = new CppProjectPropertiesWizardPage(fFirstPage);
  }
  
  protected NewProjectWizardPageOne getPageOne()
  {
	  return fFirstPage;
  }
  
  protected CapabilityConfigurationPage getPageTwo()
  {
	  return fSecondPage;
  }

  // --- IWorbenchWizard's interface methods implementation

  public void init(final IWorkbench workbench, final IStructuredSelection selection) {
    super.init(workbench, selection);
    this.fFirstPage.init(selection, getActivePart());
  }


  // --- IRunnableWithProgress' interface methods implementation

  public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
  	super.run(monitor);
    try {      
      createPlatformConfFile(new SubProgressMonitor(monitor, 5));
    } catch (Exception except) {
      // Something wrong happened, we can cancel the project creation and propagates the error.
      performCancel();
      throw new InvocationTargetException(except);
    }
  }

  // --- Private code
  
  private void createPlatformConfFile(final IProgressMonitor monitor) throws CoreException {
    try {
      final IFile platformConfFile = X10PlatformConfFactory.getFile(this.fSecondPage.getJavaProject().getRawProject());
      
      final IX10PlatformConf platformConf = X10PlatformConfFactory.load(platformConfFile);
      final IX10PlatformConfWorkCopy platformConfWorkCopy = platformConf.createWorkingCopy();
      platformConfWorkCopy.initializeToDefaultValues(platformConfFile.getProject());
      platformConfWorkCopy.applyChanges();

      X10PlatformConfFactory.save(platformConfFile, platformConfWorkCopy);
    } finally {
      monitor.done();
    }
  }

  private IWorkbenchPage getActivePage() {
    final IWorkbenchWindow activeWindow = this.fWorkbench.getActiveWorkbenchWindow();
    return (activeWindow == null) ? null : activeWindow.getActivePage();
  }

  private IWorkbenchPart getActivePart() {
    final IWorkbenchPage activePage = getActivePage();
    return (activePage == null) ? null : activePage.getActivePart();
  }
}
