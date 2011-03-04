/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.cpp_commands;

import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.ETransport;


final class UnknownUnixDefaultCommands extends AbstractDefaultCPPCommands implements IDefaultCPPCommands {
  
  UnknownUnixDefaultCommands(final boolean is64Arch, final EArchitecture architecture, final ETransport transport) {
    super(is64Arch, architecture, transport);
  }
  
  // --- Interface methods implementation
  
  public String getArchiver() {
    return "ar"; //$NON-NLS-1$
  }

  public String getArchivingOpts() {
    return "r"; //$NON-NLS-1$
  }

  public String getCompiler() {
    return "g++"; //$NON-NLS-1$
  }

  public String getCompilerOptions() {
    final String cmpOpts = String.format("-g %s -Wno-long-long -Wno-unused-parameter -pthread", //$NON-NLS-1$
                                         getTransportCompilerOption());
    if (is64Arch()) {
      return addNoChecksOptions(addOptimizeOptions(cmpOpts + M64BIT_OPTION));
    } else {
      return addNoChecksOptions(addOptimizeOptions(cmpOpts));
    }
  }

  public String getLinker() {
    return getCompiler();
  }

  public String getLinkingLibraries() {
    return String.format("-lx10 %s -lm -lpthread -Wl,--rpath -Wl,${X10-DIST}/lib", getTransportLibrary()); //$NON-NLS-1$
  }

  public String getLinkingOptions() {
    final String linkOpts = String.format("-g %s -Wno-long-long -Wno-unused-parameter", getTransportCompilerOption()); //$NON-NLS-1$
    if (is64Arch()) {
      return addNoChecksOptions(addOptimizeOptions(linkOpts + M64BIT_OPTION));
    } else {
      return addNoChecksOptions(addOptimizeOptions(linkOpts));
    }
  }

}
