/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.platform_conf.ETransport;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.core.utils.ProjectUtils;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;

/**
 * Utility methods for X10 Platform Configuration usage.
 * 
 * @author egeay
 */
public final class PlatformConfUtils {
  
  /**
   * Returns the appropriate according to the PTP resource manager service id and target operating system.
   * 
   * @param serviceTypeId The service id to consider.
   * @param targetOS The target operating system.
   * @return A non-null instance of {@link ETransport}.
   */
  public static ETransport getTransport(final String serviceTypeId, final ETargetOS targetOS) {
    if (PTPConstants.STANDALONE_SERVICE_PROVIDER_ID.equals(serviceTypeId)) {
      return ETransport.STANDALONE;
    } else if (PTPConstants.SOCKETS_SERVICE_PROVIDER_ID.equals(serviceTypeId)) {
      return ETransport.SOCKETS;
    } else {
      return (targetOS == ETargetOS.AIX) ? ETransport.LAPI : ETransport.MPI;
    }
  }
  
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
      return ProjectUtils.getProjectOutputDirPath(project);
    } else {
      return platformConf.getCppCompilationConf().getRemoteOutputFolder();
    }
  }
  
  // --- Private code
  
  private PlatformConfUtils() {}

}
