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

  // --- Constants for Launch Configuration
  
  public static final String ATTR_SHOULD_LINK_APP = LaunchCore.PLUGIN_ID + ".ShouldLinkApp"; //$NON-NLS-1$
  
  public static final String ATTR_MAIN_TYPE_PATH = LaunchCore.PLUGIN_ID + ".MainTypePath"; //$NON-NLS-1$
  
  // --- Constants for connection info for a build
  
  public static final QualifiedName RES_MANAGER_ID = new QualifiedName(LaunchCore.PLUGIN_ID, "resource.manager"); //$NON-NLS-1$
  public static final QualifiedName WORKSPACE_DIR = new QualifiedName(LaunchCore.PLUGIN_ID, "target.workspace-dir"); //$NON-NLS-1$
  public static final QualifiedName X10_PLATFORM_CONF = new QualifiedName(LaunchCore.PLUGIN_ID, "x10.platform.conf"); //$NON-NLS-1$

  // --- Plugin ID
  
  public static final String X10_DIST_PLUGIN_ID = "x10.dist.host"; //$NON-NLS-1$
  
}
