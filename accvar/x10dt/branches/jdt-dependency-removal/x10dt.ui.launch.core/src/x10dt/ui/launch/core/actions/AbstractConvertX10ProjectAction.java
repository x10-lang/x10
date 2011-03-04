/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.smapifier.builder.SmapiProjectNature;
import org.eclipse.imp.utils.ExtensionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.dialogs.DialogsFactory;

/**
 * Base class for back-end conversion action on some selected X10 project(s).
 * 
 * @author egeay
 */
public abstract class AbstractConvertX10ProjectAction implements IObjectActionDelegate {
  
  // --- Abstract methods definition
  
  protected abstract String getTargetBackEndType();
  
  // --- Interface methods implementation
  
  public final void run(final IAction action) {
    if (this.fCurSelection instanceof IStructuredSelection) {
      for (final Iterator<?> it = ((IStructuredSelection) this.fCurSelection).iterator(); it.hasNext();) {
        final IProject project = ((ISourceProject) it.next()).getRawProject();
        
        final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
          
          public void run(final IProgressMonitor monitor) throws CoreException {
            project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
          }
          
        };
        final ISchedulingRule rule = ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
        try {
          ResourcesPlugin.getWorkspace().run(runnable, rule, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
        } catch (CoreException except) {
          LaunchCore.log(except.getStatus());
        }
        
        try {
          final IProjectDescription newDescr = project.getDescription();
          
          final IBackEndX10ProjectConverter converter = createProjectConverter();
          converter.preProjectSetup(this.fShellProvider, project);
        
          final String[] natureIds = new String[] { 
            converter.getProjectNatureId(), SmapiProjectNature.k_natureID
          };
          newDescr.setNatureIds(natureIds);

          project.setDescription(newDescr, new NullProgressMonitor());
          
          converter.postProjectSetup(this.fShellProvider, project);
        } catch (CoreException except) {
          DialogsFactory.createErrorBuilder().setDetailedMessage(except.getStatus())
                        .createAndOpen(this.fShellProvider, Messages.ACXPA_ConversionFailureTitle, 
                                       NLS.bind(Messages.ACXPA_ConversionFailureMessage, project.getName()));
        } catch (ExtensionException except) {
          DialogsFactory.createErrorBuilder().setDetailedMessage(except)
                        .createAndOpen(this.fShellProvider, Messages.ACXPA_ConversionFailureTitle, 
                         NLS.bind(Messages.ACXPA_ExtensionPointErrorMsg, project.getName()));
        }
      }
    }
  }

  public final void selectionChanged(final IAction action, final ISelection selection) {
    this.fCurSelection = selection;
  }

  public final void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
    this.fShellProvider = targetPart.getSite();
  }
  
  // --- Private code
  
  private IBackEndX10ProjectConverter createProjectConverter() throws ExtensionException {
    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final String targetBackEndType = getTargetBackEndType();
    IConfigurationElement configurationElement = null;
    for (final IConfigurationElement curConfElement : registry.getConfigurationElementsFor(PROJECT_CONVERTER_EXTENSION_ID)) {
      if (targetBackEndType.equals(curConfElement.getAttribute(BACK_END_TYPE_ATTR))) {
        if (configurationElement != null) {
          throw new ExtensionException(NLS.bind(Messages.ACXPA_FoundMultipleExtPoints,
                                                PROJECT_CONVERTER_EXTENSION_ID, targetBackEndType));
        }
        configurationElement = curConfElement;
      }
    }
    if (configurationElement == null) {
      throw new ExtensionException(NLS.bind(Messages.ACXPA_NoExtensionFound, 
                                            targetBackEndType, PROJECT_CONVERTER_EXTENSION_ID));
    }
    try {
      return (IBackEndX10ProjectConverter) configurationElement.createExecutableExtension(CLASS_ATTR);
    } catch (CoreException except) {
      final String className = configurationElement.getAttribute(CLASS_ATTR);
      throw new ExtensionException(NLS.bind(Messages.ACXPA_ClassCreationError, 
                                            new Object[] { className, PROJECT_CONVERTER_EXTENSION_ID, targetBackEndType}), 
                                            except);
    }
  }
  
  // --- Fields
  
  private ISelection fCurSelection;

  private IShellProvider fShellProvider;
  
  
  private static final String PROJECT_CONVERTER_EXTENSION_ID =  LaunchCore.PLUGIN_ID + ".x10ProjectConvert"; //$NON-NLS-1$
  
  private static final String BACK_END_TYPE_ATTR = "backEndType"; //$NON-NLS-1$
  
  private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
 
}
