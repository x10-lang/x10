/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.wizards;

import static org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchImages.NEW_X10_PRJ_WIZBAN;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ErrorUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchImages;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * Responsible for creating the wizard that allow the creation of an X10 project with C++ back-end.
 * 
 * @author egeay
 */
public class CppProjectWizard extends Wizard implements INewWizard, IExecutableExtension, IRunnableWithProgress {

  /**
   * Creates the pages of this wizard and defines the title, dialog settings and default page image descriptor.
   */
  public CppProjectWizard() {
    this.fFirstPage = new CppProjectNameDefWizardPage();
    this.fSecondPage = new CppProjectX10PlatformWizardPage(this.fFirstPage);
    this.fThirdPage = new CppProjectPropertiesWizardPage(this.fFirstPage);

    addPage(this.fFirstPage);
    addPage(this.fSecondPage);
    addPage(this.fThirdPage);

    setWindowTitle(LaunchMessages.PW_WindowTitle);
    setDialogSettings(CppLaunchCore.getInstance().getDialogSettings());
    setDefaultPageImageDescriptor(CppLaunchImages.createUnmanaged(NEW_X10_PRJ_WIZBAN));
  }

  // --- IWorbenchWizard's interface methods implementation

  public void init(final IWorkbench workbench, final IStructuredSelection selection) {
    this.fWorkbench = workbench;
    this.fFirstPage.init(selection, getActivePart());
  }

  // --- IExecutableExtension's interface methods implementation

  public void setInitializationData(final IConfigurationElement configElement, final String propertyName, final Object data) {
    this.fConfigElement = configElement;
  }

  // --- IRunnableWithProgress' interface methods implementation

  public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    try {      
      // First, we create the project and potentially the hello class.
      this.fThirdPage.performFinish(monitor);
      
      // Second, we potentially persist the connection info of the remote target to use.
      this.fSecondPage.attachConnectionParameters(this.fThirdPage.getJavaProject().getProject());

      // Third, we potentially add it to the working set if necessary.
      final IWorkingSet[] workingSets = this.fFirstPage.getWorkingSets();
      if (workingSets.length > 0) {
        final IJavaProject newProject = this.fThirdPage.getJavaProject();
        PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newProject, workingSets);
      }

      // Fourth, we update the perspective if necessary.
      BasicNewProjectResourceWizard.updatePerspective(this.fConfigElement);

      // Sixth and finally, we deal with the created file, if it exists. We update the
      // package explorer.
      if (this.fThirdPage.getCreatedFile() != null) {
        showFileInExplorer(this.fThirdPage.getCreatedFile());
      }
    } catch (CoreException except) {
      // Something wrong happened, we can cancel the project creation and propagates the error.
      try {
        this.fThirdPage.performCancel();
      } catch (CoreException cancelExcept) {
        throw new InvocationTargetException(cancelExcept);
      }
      throw new InvocationTargetException(except);
    }
  }

  // --- Wizard's abstract methods implementation

  public boolean performFinish() {
    try {
      this.fWorkbench.getProgressService().runInUI(PlatformUI.getWorkbench().getProgressService(), this,
                                                   ResourcesPlugin.getWorkspace().getRoot());
    } catch (InvocationTargetException except) {
      ErrorUtils.dialogWithLog(getShell(), LaunchMessages.PW_ProjectCreationErrorTitle, 
                               LaunchMessages.PW_ProjectCreationErrorMessage, except);
      return false;
    } catch (InterruptedException except) {
      // Operation got interrupted. We just keep the state as is without performing a deletion
      // of the project. A policy that could be changed.
      return false;
    }
    return true;
  }

  // --- Overridden methods
  
  public boolean canFinish() {
    return this.fSecondPage.isPageComplete();
  }

  public boolean performCancel() {
    try {
      this.fThirdPage.performCancel();
    } catch (CoreException except) {
      ErrorUtils.dialogWithLog(getShell(), LaunchMessages.PW_PrjCancelationErrorTitle, 
                               LaunchMessages.PW_PrjCancelationErrorMsg, except.getStatus());
    }
    return true;
  }

  // --- Private code

  private IWorkbenchPage getActivePage() {
    final IWorkbenchWindow activeWindow = this.fWorkbench.getActiveWorkbenchWindow();
    return (activeWindow == null) ? null : activeWindow.getActivePage();
  }

  private IWorkbenchPart getActivePart() {
    final IWorkbenchPage activePage = getActivePage();
    return (activePage == null) ? null : activePage.getActivePart();
  }

  private void showFileInExplorer(final IFile createdFile) {
    final IWorkbenchPage activePage = getActivePage();
    if (activePage != null) {
      final IPackagesViewPart pkgPart = (IPackagesViewPart) activePage.findView(JavaUI.ID_PACKAGES);
      if (pkgPart != null) {
        pkgPart.selectAndReveal(createdFile);
      }
    }
  }

  // --- Fields

  private final CppProjectNameDefWizardPage fFirstPage;
  
  private final CppProjectX10PlatformWizardPage fSecondPage;

  private final CppProjectPropertiesWizardPage fThirdPage;

  private IWorkbench fWorkbench;

  private IConfigurationElement fConfigElement;

}
