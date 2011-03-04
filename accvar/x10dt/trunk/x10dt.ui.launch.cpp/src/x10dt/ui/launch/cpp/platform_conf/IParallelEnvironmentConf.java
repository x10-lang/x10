/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * The communication interface configuration parameters for IBM Parallel Environment.
 * 
 * @author egeay
 */
public interface IParallelEnvironmentConf extends IIBMCommunicationInterfaceConf {

  /**
   * Returns the debugging level of the proxy.
   * 
   * @return A non-null instance.
   */
  public ECIDebugLevel getDebuggingLevel();
   
  /**
   * Indicates if PE should allow applications to run after proxy shutdown.
   * 
   * @return True if it should allow, false otherwise.
   */
  public boolean shouldRunMiniProxy();
  
  /**
   * Indicates if LoadLeveler is used to manage access to system resources.
   * 
   * @return True if it should use LoadLeveler, false otherwise.
   */
  public boolean shouldUseLoadLeveler();
  
}
