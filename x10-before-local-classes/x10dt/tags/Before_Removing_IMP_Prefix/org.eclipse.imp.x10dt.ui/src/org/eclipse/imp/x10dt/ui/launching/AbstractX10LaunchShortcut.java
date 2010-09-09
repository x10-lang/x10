/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launching;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.Messages;
import org.eclipse.imp.x10dt.ui.X10DTUIPlugin;
import org.eclipse.imp.x10dt.ui.utils.LaunchUtils;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import polyglot.types.ClassType;

/**
 * Provides a common base for launch shortcuts for X10 project with the different back-ends.
 * 
 * @author egeay
 */
public abstract class AbstractX10LaunchShortcut implements ILaunchShortcut {
  
  // --- Abstract methods definition
  
  /**
   * Returns the type of configuration this shortcut is applicable to.
   * 
   * @return A non-null instance of {@link ILaunchConfigurationType}.
   */
  protected abstract ILaunchConfigurationType getConfigurationType();
  
  /**
   * Returns the project nature id of interest in case we search for all the relevant projects automatically under the
   * workspace root.
   * 
   * @return A non-null string identifying the nature.
   */
  protected abstract String getProjectNatureId();
  
  /**
   * Initializes the launch configuration attributes for the particular main type provided.
   * 
   * @param workingCopy The launch configuration working copy to consider.
   * @param type The X10 main type of interest.
   */
  protected abstract void setLaunchConfigurationAttributes(final ILaunchConfigurationWorkingCopy workingCopy,
                                                           final Pair<ClassType, IJavaElement> type);
  
  // --- Interface methods implementation

  public final void launch(final ISelection selection, final String mode) {
    if (selection instanceof IStructuredSelection) {
      final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
      final IJavaElement[] elements = new IJavaElement[structuredSelection.size()];
      final Iterator<?> it = structuredSelection.iterator();
      for (int i = 0, size = structuredSelection.size(); i < size; ++i) {
        final Object obj = it.next();
        if (obj instanceof IFile) {
          elements[i] = new X10FileElementWrapper((IFile) obj);
        } else {
          elements[i] = (IJavaElement) obj;
        }
      }
      searchAndLaunch(elements, mode);
    }
  }

  public final void launch(final IEditorPart editor, final String mode) {
    final IJavaElement javaElement = (IJavaElement) editor.getEditorInput().getAdapter(IJavaElement.class);
    if (javaElement != null) {
      searchAndLaunch(new IJavaElement[] { javaElement }, mode);
    }
  }
  
  // --- Code for descendants
  
  protected final Shell getShell() {
    final IWorkbenchWindow window = X10DTUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
    return (window == null) ? null : window.getShell();
  }
  
  // --- Private code
  
  private ILaunchConfiguration chooseConfiguration(final List<ILaunchConfiguration> configList) {
    final IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
    final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
    dialog.setElements(configList.toArray());
    dialog.setTitle(Messages.AXLS_MultipleConfDialogTitle);
    dialog.setMessage(Messages.AXLS_MultipleConfDialogMsg);
    dialog.setMultipleSelection(false);
    final int result = dialog.open();
    labelProvider.dispose();
    if (result == Window.OK) {
      return (ILaunchConfiguration) dialog.getFirstResult();
    }
    return null;    
  }
  
  @SuppressWarnings("deprecation")
  // We will need to use the new method "generateLaunchConfigurationName" once Galileo won't be supported anymore by X10DT.
  private ILaunchConfiguration createConfiguration(final Pair<ClassType, IJavaElement> type) {
    final ILaunchConfigurationType launchConfType = getConfigurationType();
    final String namePrefix = type.first.fullName().toString();
    final String confName = DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(namePrefix);
    try {
      final IProject project = type.second.getResource().getProject();
      final ILaunchConfigurationWorkingCopy workingCopy = launchConfType.newInstance(project, confName);
      setLaunchConfigurationAttributes(workingCopy, type);
      return workingCopy;
    } catch (CoreException except) {
      ErrorDialog.openError(getShell(), Messages.AXLS_ConfCreationError, 
                            NLS.bind(Messages.AXLS_ConfCreationSavingErrorMsg, launchConfType.getName()), except.getStatus());
    }
    return null;
  }
  
  private ILaunchConfiguration findLaunchConfiguration(final ILaunchConfigurationType configType, final String typeName,
                                                       final String projectName) {
    final List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
    try {
      final ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
      for (final ILaunchConfiguration config : configs) {
        if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "").equals(typeName)) { //$NON-NLS-1$
          if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "").equals(projectName)) { //$NON-NLS-1$
            candidateConfigs.add(config);
          }
        }
      }
    } catch (CoreException except) {
      X10DTUIPlugin.getInstance().getLog().log(except.getStatus());
    }
    int candidateCount = candidateConfigs.size();
    if (candidateCount == 1) {
      return (ILaunchConfiguration) candidateConfigs.get(0);
    } else if (candidateCount > 1) {
      return chooseConfiguration(candidateConfigs);
    }
    return null;
  }
  
  private void searchAndLaunch(final IJavaElement[] selection, final String mode) {
    try {
      final Pair<ClassType, IJavaElement> mainType = LaunchUtils.findMainType(selection, getProjectNatureId(), getShell());
      if (mainType != null) {
        ILaunchConfiguration config = findLaunchConfiguration(getConfigurationType(), mainType.first.fullName().toString(),
                                                              mainType.second.getJavaProject().getElementName());
        if (config == null) {
          config = createConfiguration(mainType);
        }
        if (config != null) {
          DebugUITools.launch(config, mode);
        }
      }
    } catch (InterruptedException except) {
      // Nothing to do.
    } catch (InvocationTargetException except) {
      if (except.getTargetException() instanceof CoreException) {
        ErrorDialog.openError(getShell(), Messages.AXLS_MainTypeSearchError, Messages.AXLS_MainTypeSearchErrorMsg, 
                              ((CoreException) except.getTargetException()).getStatus());
        X10DTUIPlugin.getInstance().getLog().log(((CoreException) except.getTargetException()).getStatus());
      } else {
        final IStatus status = new Status(IStatus.ERROR, X10DTUIPlugin.PLUGIN_ID, Messages.AXLS_MainTypeSearchInternalError,
                                          except.getTargetException());
        ErrorDialog.openError(getShell(), Messages.AXLS_MainTypeSearchError, Messages.AXLS_MainTypeSearchErrorMsg, status);
        X10DTUIPlugin.getInstance().getLog().log(status);
      }
    }
  }

}
