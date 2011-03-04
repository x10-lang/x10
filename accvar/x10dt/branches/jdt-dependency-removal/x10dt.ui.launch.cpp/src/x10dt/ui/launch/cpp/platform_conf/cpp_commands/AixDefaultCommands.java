/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.cpp_commands;

import org.eclipse.core.resources.IProject;

import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.ETransport;


final class AixDefaultCommands extends AbstractDefaultCPPCommands implements IDefaultCPPCommands {
  
  AixDefaultCommands(IProject project, final boolean is64Arch, final EArchitecture architecture, final ETransport transport) {
    super(project, is64Arch, architecture, transport);
  }
  
  // --- Interface methods implementation
  
  public String getArchiver() {
    final String archiver = "ar"; //$NON-NLS-1$
    if (is64Arch()) {
      return archiver + " -X64"; //$NON-NLS-1$
    } else {
      return archiver;
    }
  }

  public String getArchivingOpts() {
    return "-r"; //$NON-NLS-1$
  }

  public String getCompiler() {
    return "mpCC_r"; //$NON-NLS-1$
  }
  
  public String getCompilerOptions() {
    final String cmpOpts = String.format("-g %s -qsuppress=1540-0809:1500-029 -qrtti=all -DX10_USE_BDWGC", //$NON-NLS-1$
                                         getTransportCompilerOption());
    if (is64Arch()) {
      return addNoChecksOptions(addOptimizeOptions(cmpOpts + " -q64")); //$NON-NLS-1$
    } else {
      return addNoChecksOptions(addOptimizeOptions(cmpOpts));
    }
  }

  public String getLinker() {
    return getCompiler();
  }

  public String getLinkingLibraries() {
    return String.format("-lx10 -lgc %s -ldl -lm -lpthread", getTransportLibrary()); //$NON-NLS-1$
  }

  public String getLinkingOptions() {
    final String linkOpts = String.format("-g %s -qrtti=all -bbigtoc -bexpfull -qsuppress=1540-0809:1500-029 -DX10_USE_BDWGC", //$NON-NLS-1$
                                          getTransportCompilerOption());
    if (is64Arch()) {
      return addNoChecksOptions(addOptimizeOptions(linkOpts + " -q64")); //$NON-NLS-1$
    } else {
      return addNoChecksOptions(addOptimizeOptions(linkOpts));
    }
  }
  
}
