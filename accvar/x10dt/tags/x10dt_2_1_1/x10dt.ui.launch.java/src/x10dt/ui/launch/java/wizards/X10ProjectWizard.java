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
/*
 * Created on Feb 6, 2006
 */
package x10dt.ui.launch.java.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.java.hosted.wizards.NewProjectWizardSecondPage;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class X10ProjectWizard extends Wizard implements INewWizard, IExecutableExtension {
  private X10ProjectWizardFirstPage fFirstPage;

  private NewProjectWizardSecondPage fSecondPage;
  
  private IConfigurationElement fConfigElement;

  public X10ProjectWizard() {
    super();
    // setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWJPRJ);
    // setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
    setWindowTitle("New X10 Project (Java back-end)");
  }
  
  public void setInitializationData(
			final IConfigurationElement configElement,
			final String propertyName, final Object data) {
		this.fConfigElement = configElement;
	}

  public boolean performFinish() {
    IWorkspaceRunnable op = new IWorkspaceRunnable() {
      public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
        try {
          finishPage(monitor);
        } catch (InterruptedException e) {
          throw new OperationCanceledException(e.getMessage());
        }
      }
    };
    try {
      ISchedulingRule rule = null;
      Job job = Job.getJobManager().currentJob();
      if (job != null)
        rule = job.getRule();
      IRunnableWithProgress runnable = null;
      if (rule != null)
        runnable = new WorkbenchRunnableAdapter(op, rule, true);
      else
        runnable = new WorkbenchRunnableAdapter(op, getSchedulingRule());
      getContainer().run(canRunForked(), true, runnable);
      BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
    } catch (InvocationTargetException e) {
      handleFinishException(getShell(), e);
      return false;
    } catch (InterruptedException e) {
      return false;
    }
    return true;
  }

  /**
   * Returns the scheduling rule for creating the element.
   */
  protected ISchedulingRule getSchedulingRule() {
    return ResourcesPlugin.getWorkspace().getRoot(); // look all by default
  }

  protected boolean canRunForked() {
    return true;
  }

  protected void handleFinishException(Shell shell, InvocationTargetException e) {
    String title = NewWizardMessages.NewElementWizard_op_error_title;
    String message = NewWizardMessages.NewElementWizard_op_error_message;
    ExceptionHandler.handle(e, shell, title, message);
  }

  /*
   * @see Wizard#addPages
   */
  public void addPages() {
    super.addPages();
    fFirstPage = new X10ProjectWizardFirstPage();
    addPage(fFirstPage);
    fSecondPage = new X10ProjectWizardSecondPage(fFirstPage);
    addPage(fSecondPage);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
   */
  protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
    fSecondPage.performFinish(monitor); // use the full progress monitor
  }

  public void init(IWorkbench workbench, IStructuredSelection selection) {
  }

  public void selectAndReveal(IFile newFile) {
    BasicNewResourceWizard.selectAndReveal(newFile, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
  }
}
