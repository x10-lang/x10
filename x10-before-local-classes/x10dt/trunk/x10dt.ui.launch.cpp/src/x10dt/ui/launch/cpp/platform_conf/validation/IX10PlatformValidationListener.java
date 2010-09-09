/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.validation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;

/**
 * Listener that gets notified of validation events performed by {@link IX10PlatformChecker}.
 * 
 * @author egeay
 */
public interface IX10PlatformValidationListener {
	
	/**
	 * Event indicating that the Communication Interface has been validated.
	 */
	public void platformCommunicationInterfaceValidated();

	/**
	 * Event indicating that the Communication Interface validation has failed.
	 * 
	 * @param message The message providing information about the failure.
	 */
	public void platformCommunicationInterfaceValidationFailure(final String message);
  
  /**
   * Event indicating that the C++ compilation/linking has been successfully performed on the target machine. Note that it
   * implies also that the remote connection (if any are needed) has been validated.
   */
  public void platformCppCompilationValidated();
  
  /**
   * Event indicating that the C++ compilation/linking validation has failed.
   * 
   * @param message The message providing information about the failure.
   */
  public void platformCppCompilationValidationFailure(final String message);
  
  /**
   * Event indicating that unfortunately an error occurred during the validation process.
   * 
   * @param exception The exception that got raised.
   */
  public void platformCppCompilationValidationError(final Exception exception);
  
  /**
   * Event indicating that the validation of the connection to the remote machine failed.
   * 
   * @param exception The exception associated with the failure.
   */
  public void remoteConnectionFailure(final Exception exception);
  
  /**
   * Event indicating that the remote connection status has been invalidated.
   */
  public void remoteConnectionUnknownStatus();
  
  /**
   * Event indicating that the validation of the connection to the remote machine succeeded.
   * 
   * @param targetElement The PTP target element used for the validation.
   */
  public void remoteConnectionValidated(final ITargetElement targetElement);
  
  /**
   * Event indicating that we could not create the service provider for the underlying PTP resource manager.
   * 
   * @param exception The exception associated with the failure.
   */
  public void serviceProviderFailure(final CoreException exception);

}
