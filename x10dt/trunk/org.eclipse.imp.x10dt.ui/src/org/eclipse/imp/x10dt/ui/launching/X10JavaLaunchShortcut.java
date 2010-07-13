/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launching;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.io.File;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.builder.X10ProjectNature;
import org.eclipse.jdt.core.IJavaElement;

import polyglot.types.ClassType;

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
    return X10ProjectNature.k_natureID;
  }
  
  protected void setLaunchConfigurationAttributes(final ILaunchConfigurationWorkingCopy workingCopy,
                                                  final Pair<ClassType, IJavaElement> type) {
    workingCopy.setAttribute(ATTR_PROJECT_NAME, type.second.getJavaProject().getElementName());
    workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, type.first.fullName().toString());
    
    String commonPath= X10DTCorePlugin.x10CompilerPath;
    final String runtimePath = commonPath.substring(0, commonPath.lastIndexOf(File.separatorChar)+1) + 
                               X10DTCorePlugin.X10_RUNTIME_BUNDLE_ID + File.separator + "classes";
    workingCopy.setAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, runtimePath);
  }
  
  // --- Fields
  
  private static final String LAUNCH_CONF_TYPE = "org.eclipse.imp.x10dt.ui.launching.X10LaunchConfigurationType"; //$NON-NLS-1$
  
}
