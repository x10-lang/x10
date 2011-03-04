/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * The common communication interface parameters for IBM Parallel Environment and LoadLeveler.
 * 
 * @author egeay
 */
public interface IIBMCommunicationInterfaceConf extends ICommunicationInterfaceConf {
  
  /**
   * Returns the path to the alternate library.
   * 
   * @return A non-null string.
   */
  public String getAlternateLibraryPath();
  
  /**
   * The mode that defines to run multi-cluster or not.
   * 
   * @return A non-null instance defining the mode.
   */
  public EClusterMode getClusterMode();
  
  /**
   * Returns the interval in time that the proxy will poll for job status. 
   * 
   * @return A negative number if it's not defined otherwise a natural number.
   */
  public int getJobPolling();
  
  /**
   * Returns the minimum interval that the proxy will poll to query node status.
   * 
   * @return A negative number if it's not defined otherwise a natural number.
   */
  public int getNodePollingMin();
  
  /**
   * Returns the maximum interval that the proxy will poll to query node status.
   * 
   * @return A negative number if it's not defined otherwise a natural number.
   */
  public int getNodePollingMax();
  
  /**
   * Returns the path to the proxy.
   * 
   * @return A non-null string.
   */
  public String getProxyServerPath();
  
  /**
   * Indicates if yes or no the proxy will be launched manually or not.
   * 
   * @return True if it is launched manually, false it is launched automatically.
   */
  public boolean shouldLaunchProxyManually();
  
  /**
   * Returns the policy to stop at the proxy startup for debugging.
   * 
   * <p>If you are debugging the proxy, you can force the proxy into a spin loop in the main entry point allowing a 
   * debugger to be attached. Once a debugger has been attached then breakpoints can be assigned and the loop canceled 
   * by setting the "debug_loop" variable to 0 in the debugger and continuing execution.
   * 
   * @return True to stop at the main proxy entry, false otherwise.
   */
  public boolean shouldSuspendProxyAtStartup();

}
