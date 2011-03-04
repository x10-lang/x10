/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.cpp_commands;

import x10.Configuration;
import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.ETransport;


abstract class AbstractDefaultCPPCommands implements IDefaultCPPCommands {
  
  protected AbstractDefaultCPPCommands(final boolean is64Arch, final EArchitecture architecture, final ETransport transport) {
    this.fIs64Arch = is64Arch;
    this.fArchitecture = architecture;
    this.fTransport = transport;
  }
  
  // --- Code for descendants
  
  protected final String addNoChecksOptions(final String command) {
    if (Configuration.NO_CHECKS) {
      return command + " -DNO_CHECKS"; //$NON-NLS-1$
    } else {
      return command;
    }
  }
  
  protected final String addOptimizeOptions(final String command) {
    if (Configuration.OPTIMIZE) {
      return command + " -O2 -DNDEBUG -DNO_PLACE_CHECKS -finline-functions"; //$NON-NLS-1$
    } else {
      return command;
    }
  }
  
  protected final EArchitecture getArchitecture() {
    return this.fArchitecture;
  }
  
  protected String getTransportCompilerOption() {
    switch (this.fTransport) {
      case LAPI:
        return "-DTRANSPORT=lapi"; //$NON-NLS-1$
      case MPI:
        return "-DTRANSPORT=sockets"; //$NON-NLS-1$
      case SOCKETS:
      case STANDALONE:
        return ""; //$NON-NLS-1$
      default:
        throw new AssertionError();
    }
  }
  
  protected String getTransportLibrary() {
    switch (this.fTransport) {
      case LAPI:
        return "-lupcrts_lapi"; //$NON-NLS-1$
      case MPI:
        return "-lx10rt_pgas_sockets"; //$NON-NLS-1$
      case SOCKETS:
        return "-lx10rt_sockets"; //$NON-NLS-1$
      case STANDALONE:
        return "-lx10rt_standalone"; //$NON-NLS-1$
      default:
        throw new AssertionError();
    }
  }
  
  protected final boolean is64Arch() {
    return this.fIs64Arch;
  }
  
  protected final boolean supportsStreamingSIMDExtensions() {
    return this.fArchitecture == EArchitecture.x86;
  }
  
  // --- Fields
  
  private final boolean fIs64Arch;
  
  private final EArchitecture fArchitecture;
  
  private final ETransport fTransport;
  
  
  protected static final String M64BIT_OPTION = " -m64"; //$NON-NLS-1$
  
  protected static final String STREAMING_SIMD_EXTENSIONS = " -msse2 -mfpmath=sse"; //$NON-NLS-1$

}
