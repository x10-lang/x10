/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.core.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.ui.wizards.CapabilityConfigurationPage;
import org.eclipse.imp.ui.wizards.NewProjectWizardPageOne;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import x10dt.ui.launch.core.LaunchImages;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.dialogs.DialogsFactory;

/**
 * Responsible for creating the wizard that allow the creation of an X10 project with C++ back-end.
 * 
 * @author egeay
 */
public class X10ProjectWizard extends Wizard implements INewWizard, IExecutableExtension, IRunnableWithProgress {

  /**
   * Creates the pages of this wizard and defines the title, dialog settings and default page image descriptor.
   */
  public X10ProjectWizard() {
    setDefaultPageImageDescriptor(LaunchImages.createUnmanaged(LaunchImages.NEW_X10_PRJ_WIZBAN));
  }

  // --- IWorbenchWizard's interface methods implementation

  public void init(final IWorkbench workbench, final IStructuredSelection selection) {
    this.fWorkbench = workbench;
  }

  // --- IExecutableExtension's interface methods implementation

  public void setInitializationData(final IConfigurationElement configElement, final String propertyName, final Object data) {
    this.fConfigElement = configElement;
  }

  // --- IRunnableWithProgress' interface methods implementation

  public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
  	monitor.beginTask(null, 10);
    try {      
      this.fSecondPage.performFinish(new SubProgressMonitor(monitor, 5));
      
      final IWorkingSet[] workingSets = this.fFirstPage.getWorkingSets();
      if (workingSets.length > 0) {
        final ISourceProject newProject = this.fSecondPage.getJavaProject();
        PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newProject.getRawProject(), workingSets);
      }

      BasicNewProjectResourceWizard.updatePerspective(this.fConfigElement);
      
    } catch (Exception except) {
      // Something wrong happened, we can cancel the project creation and propagates the error.
      try {
        performCancel();
      } catch (Exception cancelExcept) {
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
      DialogsFactory.createErrorBuilder().setDetailedMessage(except)
                    .createAndOpen(getShell(), Messages.PW_ProjectCreationErrorTitle, 
                                   Messages.PW_ProjectCreationErrorMessage);
      return false;
    } catch (InterruptedException except) {
      // Operation got interrupted. We just keep the state as is without performing a deletion of the project. 
    	// A policy that could be changed.
      return false;
    }
    return true;
  }

  // --- Overridden methods
  protected NewProjectWizardPageOne getPageOne()
  {
	  return new NewProjectWizardPageOne();
  }
  
  protected CapabilityConfigurationPage getPageTwo()
  {
	  return new CapabilityConfigurationPage(LanguageRegistry.findLanguage("X10"));
  }
  
  public void addPages() {
	super.addPages();
	this.fFirstPage = getPageOne();
    this.fSecondPage = getPageTwo();

    addPage(this.fFirstPage);
    addPage(this.fSecondPage);
}

  // --- Fields

  protected IWorkbench fWorkbench;

  private IConfigurationElement fConfigElement;
  
  protected NewProjectWizardPageOne fFirstPage;

  protected CapabilityConfigurationPage fSecondPage;
  
}
