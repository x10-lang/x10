/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp;

import org.eclipse.core.runtime.QualifiedName;

/**
 * Miscellaneous constants used within this plug-in.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public final class Constants {

  // --- Constants for Launch Configuration
  
  public static final String ATTR_SHOULD_LINK_APP = CppLaunchCore.PLUGIN_ID + ".ShouldLinkApp"; //$NON-NLS-1$
  
  // --- Constants for connection info for a build
  
  public static final QualifiedName RES_MANAGER_ID = new QualifiedName(CppLaunchCore.PLUGIN_ID, "resource.manager"); //$NON-NLS-1$
  public static final QualifiedName WORKSPACE_DIR = new QualifiedName(CppLaunchCore.PLUGIN_ID, "target.workspace-dir"); //$NON-NLS-1$

  // --- Constants for C++ Builder preference page
  
  public static final String P_CPP_BUILDER_COMPILE_CMD = "compileCppCommand"; //$NON-NLS-1$
  public static final String P_CPP_BUILDER_ARCHIVE_CMD = "archiveCppCommand"; //$NON-NLS-1$
  public static final String P_CPP_BUILDER_LINK_CMD = "linkCppCommand"; //$NON-NLS-1$
  public static final String P_CPP_BUILDER_X10_DIST_LOC = "x10DistLoc"; //$NON-NLS-1$
  public static final String P_CPP_BUILDER_PGAS_LOC = "pgasLoc"; //$NON-NLS-1$

}
