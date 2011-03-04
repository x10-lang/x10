/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;


interface IConnectionInfo {
  
  void applyChangesToTargetElement() throws CoreException;
  
  boolean isPasswordBasedAuth();
  
  int getConnectionTimeout();
  
  String getErrorMessage();
  
  String getHostName();
  
  String getName();
  
  String getPassphrase();
  
  String getPassword();
 
  int getPort();
  
  ControlAttributes getPTPAttributes();
  
  String getPrivateKeyFile();
  
  String getUserName();
  
  ITargetElement getTargetElement();
  
  EValidationStatus getValidationStatus();
  
  void setErrorMessage(final String errorMessage);
  
  void setConnectionTimeout(final int timeout);
  
  void setHostName(final String hostName);
  
  void setIsPasswordBasedFlag(final boolean isPasswordBasedAuth);
  
  void setName(final String name);
  
  void setPassphrase(final String passphrase);
  
  void setPassword(final String password);
  
  void setPort(final int port);
  
  void setPrivateKeyFile(final String privateKeyFile);
  
  void setUserName(final String userName);
  
  void setValidationStatus(final EValidationStatus validationStatus);

}
