/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;

import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;

/**
 * Responsible for checking remote connection, C++ compilation/linking commands and Communication Interface.
 * 
 * <p>One can get implementation(s) of that interface via {@link PlatformCheckerFactory}.
 * 
 * @author egeay
 */
public interface IX10PlatformChecker {
  
  /**
   * Adds a validation listener to the checker in order to get some notifications about the validation results.
   * 
   * @param listener The listener to add.
   */
  public void addValidationListener(final IX10PlatformValidationListener listener);
    
  /**
   * Removes all the currently registered listeners for the checker.
   */
  public void removeAllValidationListeners();
  
  /**
   * Removes the transmitted listener from the list of currently registered listeners.
   * 
   * @param listener The listener to remove.
   */
  public void removeValidationListener(final IX10PlatformValidationListener listener);
  
  /**
   * Validates the Communication Interface by trying to execute a discovery command on the target machine.
   * 
   * @param platformConf The platform configuration to use for validation.
   * @param monitor The monitor to use for reporting progress and/or cancel the operation.
   */
  public void validateCommunicationInterface(final IX10PlatformConf platformConf, final IProgressMonitor monitor);
  
  /**
   * Validates the C++ compilation and linking of a simple HelloWorld program either locally or remotely depending of the
   * connection type.
   * 
   * @param platformConf The platform configuration to use for validation.
   * @param monitor The monitor to use for reporting progress and/or cancel the operation.
   */
  public void validateCppCompilationConf(final IX10PlatformConfWorkCopy platformConf,  final IProgressMonitor monitor);
  
  /**
   * Validates the connection to a remote machine via the PTP target element transmitted.
   * 
   * @param targetElement The PTP target element to consider.
   * @param monitor The monitor to use for reporting progress and/or cancel the operation.
   */
  public void validateRemoteConnectionConf(final ITargetElement targetElement, final IProgressMonitor monitor);

}
