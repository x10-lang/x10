/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.java.launching;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.imp.utils.Pair;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.core.elements.IResourceManager;

import polyglot.types.ClassType;
import x10dt.core.X10DTCorePlugin;
import x10dt.ui.launch.java.Activator;
import x10dt.ui.launch.java.Messages;
import x10dt.ui.launch.rms.core.launch_configuration.LaunchConfigConstants;
import x10dt.ui.launching.AbstractX10LaunchShortcut;

/**
 * Implements the launch shortcut for Java back-end.
 * 
 * @author egeay
 */
public final class X10JavaLaunchShortcut extends AbstractX10LaunchShortcut implements ILaunchShortcut {

  // --- Interface methods implementation

  protected ILaunchConfigurationType getConfigurationType() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(LAUNCH_CONF_TYPE);
  }
  
  protected String getProjectNatureId() {
    return X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID;
  }
  
  protected void setLaunchConfigurationAttributes(final ILaunchConfigurationWorkingCopy workingCopy,
                                                  final Pair<ClassType, IJavaElement> type) {
    workingCopy.setAttribute(ATTR_PROJECT_NAME, type.second.getJavaProject().getElementName());
    workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, type.first.fullName().toString());
    
    workingCopy.setAttribute(LaunchConfigConstants.ATTR_NUM_PLACES, 1);
    workingCopy.setAttribute(LaunchConfigConstants.ATTR_USE_HOSTFILE, false);
    workingCopy.setAttribute(LaunchConfigConstants.ATTR_HOSTLIST, Arrays.asList("localhost")); //$NON-NLS-1$
    
    workingCopy.setAttribute(MultiVMAttrConstants.ATTR_IS_LOCAL, true);
    try {
      final IResourceManager resourceManager = new ResourceManagerHelper(workingCopy.getName()).createResourceManager();
      workingCopy.setAttribute(IPTPLaunchConfigurationConstants.ATTR_RESOURCE_MANAGER_UNIQUENAME, 
                               resourceManager.getUniqueName());
    } catch (CoreException except) {
      Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
                                                     Messages.XJLS_RMCreationError, except));
    }
  }
  
  // --- Fields
  
  private static final String LAUNCH_CONF_TYPE = "x10dt.ui.launch.java.launching.X10LaunchConfigurationType"; //$NON-NLS-1$
  
}
