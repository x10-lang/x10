/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.cpp_commands;

/**
 * Factory methods to create different implementations of {@link IDefaultCPPCommands}.
 * 
 * @author egeay
 */
public final class DefaultCPPCommandsFactory {
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for AIX.
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createAixCommands(final boolean is64Arch) {
    return new AixDefaultCommands(is64Arch);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for Cygwin.
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createCygwinCommands(final boolean is64Arch) {
    return new CygwinDefaultCommands(is64Arch);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for Linux.
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createLinuxCommands(final boolean is64Arch) {
    return new LinuxDefaultCommands(is64Arch);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for Mac OS X.
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createMacCommands(final boolean is64Arch) {
    return new MacDefaultCommands(is64Arch);
  }
  
  /**
   * Creates the implementation of {@link IDefaultCPPCommands} for general Unix system.
   * 
   * @param is64Arch The flag indicating if we should consider commands for 64-bit architecture or not.
   * @return A non-null implementation of {@link IDefaultCPPCommands}.
   */
  public static IDefaultCPPCommands createUnkownUnixCommands(final boolean is64Arch) {
    return new UnknownUnixDefaultCommands(is64Arch);
  }
  
  // --- Private code
  
  private DefaultCPPCommandsFactory() {}

}
