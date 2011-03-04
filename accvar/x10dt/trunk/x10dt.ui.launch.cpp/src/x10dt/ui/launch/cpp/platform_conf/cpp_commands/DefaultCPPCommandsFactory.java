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

/**
 * Factory methods to create different implementations of {@link IDefaultCPPCommands}.
 * 
 * @author egeay
 */
public final class DefaultCPPCommandsFactory {
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for AIX.
 * @param project 
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @param architecture The architecture of the machine to consider.
   * @param transport The current transport to consider.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createAixCommands(IProject project, final boolean is64Arch, final EArchitecture architecture,
                                                      final ETransport transport) {
    return new AixDefaultCommands(project, is64Arch, architecture, transport);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for Cygwin.
 * @param project 
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @param architecture The architecture of the machine to consider.
   * @param transport The current transport to consider.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createCygwinCommands(IProject project, final boolean is64Arch, final EArchitecture architecture,
                                                         final ETransport transport) {
    return new CygwinDefaultCommands(project, is64Arch, architecture, transport);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for Linux.
 * @param project 
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @param architecture The architecture of the machine to consider.
   * @param transport The current transport to consider.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createLinuxCommands(IProject project, final boolean is64Arch, final EArchitecture architecture,
                                                        final ETransport transport) {
    return new LinuxDefaultCommands(project, is64Arch, architecture, transport);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for Mac OS X.
 * @param project 
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @param architecture The architecture of the machine to consider.
   * @param transport The current transport to consider.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createMacCommands(IProject project, final boolean is64Arch, final EArchitecture architecture,
                                                      final ETransport transport) {
    return new MacDefaultCommands(project, is64Arch, architecture, transport);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for general Unix system.
 * @param project 
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @param architecture The architecture of the machine to consider.
   * @param transport The current transport to consider.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createUnkownUnixCommands(IProject project, final boolean is64Arch, final EArchitecture architecture,
                                                             final ETransport transport) {
    return new UnknownUnixDefaultCommands(project, is64Arch, architecture, transport);
  }
  
  // --- Private code
  
  private DefaultCPPCommandsFactory() {}

}
