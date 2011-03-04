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


final class LinuxDefaultCommands extends AbstractDefaultCPPCommands implements IDefaultCPPCommands {
  
  LinuxDefaultCommands(IProject project, final boolean is64Arch, final EArchitecture architecture, final ETransport transport) {
    super(project, is64Arch, architecture, transport);
  }
  
  // --- Interface methods implementation
  
  public String getArchiver() {
    return "ar"; //$NON-NLS-1$
  }

  public String getArchivingOpts() {
    return "cq"; //$NON-NLS-1$
  }
  
  public String getCompiler() {
    return (getTransport() == ETransport.MPI) ? "mpicxx" : "g++"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  public String getCompilerOptions() {
    String cmpOpts = String.format("-g %s -Wno-long-long -Wno-unused-parameter -pthread -DX10_USE_BDWGC", //$NON-NLS-1$
                                   getTransportCompilerOption());
    if (is64Arch()) {
      cmpOpts += M64BIT_OPTION;
    }
    if (supportsStreamingSIMDExtensions()) {
      cmpOpts += STREAMING_SIMD_EXTENSIONS;
    }
    return addNoChecksOptions(addOptimizeOptions(cmpOpts));
  }

  public String getLinker() {
    return getCompiler();
  }

  public String getLinkingLibraries() {
    return String.format("-lx10 -lgc %s -ldl -lm -lpthread -Wl,--rpath -Wl,${X10-DIST}/lib -Wl,--rpath -Wl,${X10-DIST}/stdlib/lib -Wl,-export-dynamic -lrt", //$NON-NLS-1$
                         getTransportLibrary());
  }

  public String getLinkingOptions() {
    String linkOpts = String.format("-g %s -Wno-long-long -Wno-unused-parameter -pthread -DX10_USE_BDWGC", //$NON-NLS-1$
                                    getTransportCompilerOption());
    if (is64Arch()) {
      linkOpts += M64BIT_OPTION;
    }
    if (supportsStreamingSIMDExtensions()) {
      linkOpts += STREAMING_SIMD_EXTENSIONS;
    }
    return addNoChecksOptions(addOptimizeOptions(linkOpts));
  }

}
