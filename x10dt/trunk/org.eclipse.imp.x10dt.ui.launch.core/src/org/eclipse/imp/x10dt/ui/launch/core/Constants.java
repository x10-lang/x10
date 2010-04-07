/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core;

import org.eclipse.core.runtime.QualifiedName;

/**
 * Miscellaneous constants used within this plug-in.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public final class Constants {
  
  public static final String EMPTY_STR = ""; //$NON-NLS-1$

  // --- Constants for Launch Configuration
  
  public static final String ATTR_SHOULD_LINK_APP = LaunchCore.PLUGIN_ID + ".ShouldLinkApp"; //$NON-NLS-1$
  
  public static final String ATTR_X10_MAIN_CLASS = LaunchCore.PLUGIN_ID + ".X10MainClass"; //$NON-NLS-1$
  
  public static final String ATTR_MAIN_CPP_FILE_PATH = LaunchCore.PLUGIN_ID + ".MainCppFilePath"; //$NON-NLS-1$
  
  // --- Constant for X10 builder
  
  public static final QualifiedName EXEC_PATH = new QualifiedName(LaunchCore.PLUGIN_ID, "exec.path"); //$NON-NLS-1$
  
  // --- Plugin ID
  
  public static final String X10_DIST_PLUGIN_ID = "x10.dist.host"; //$NON-NLS-1$
  
}
