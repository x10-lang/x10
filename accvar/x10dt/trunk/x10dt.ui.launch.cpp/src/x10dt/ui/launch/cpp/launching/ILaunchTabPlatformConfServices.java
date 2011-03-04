/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.launching;

import org.eclipse.debug.core.ILaunchConfiguration;

import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;

/**
 * Responsible mostly for providing services about the platform configuration and services for the C++ Application tab. 
 * 
 * @author egeay
 */
public interface ILaunchTabPlatformConfServices {
  
  /**
   * Notifies when an X10 platform configuration is available once a project gets properly defined.
   * 
   * @param platformConf The X10 platform configuration available for the given launch configuration.
   */
  public void platformConfSelected(final IX10PlatformConf platformConf);
  
  /**
   * Transfers the launch configuration for the given tab that implements this interface with the launch configuration
   * transmitted.
   * 
   * @param configuration The launch configuration to assign to the current tab.
   */
  public void setLaunchConfiguration(final ILaunchConfiguration configuration);

}
