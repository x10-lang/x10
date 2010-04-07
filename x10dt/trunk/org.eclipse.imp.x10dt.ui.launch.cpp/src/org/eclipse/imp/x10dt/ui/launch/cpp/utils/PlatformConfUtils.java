/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;

/**
 * Utility methods for X10 Platform Configuration usage.
 * 
 * @author egeay
 */
public final class PlatformConfUtils {
  
  /**
   * Returns the workspace directory (where generated files will be created and compiled) for a given project independently 
   * if the connection type, either local or remote.
   * 
   * @param platformConf The X10 platform configuration to consider.
   * @param project The project to consider.
   * @return A non-null non-empty string identifying a directory.
   * @throws CoreException Occurs if the project output folder does not exist or we can't obtain its path appropriately.
   */
  public static String getWorkspaceDir(final IX10PlatformConf platformConf, final IProject project) throws CoreException {
    if (platformConf.getConnectionConf().isLocal()) {
      return JavaProjectUtils.getProjectOutputDirPath(project);
    } else {
      return platformConf.getCppCompilationConf().getRemoteOutputFolder();
    }
  }
  
  // --- Private code
  
  private PlatformConfUtils() {}

}
