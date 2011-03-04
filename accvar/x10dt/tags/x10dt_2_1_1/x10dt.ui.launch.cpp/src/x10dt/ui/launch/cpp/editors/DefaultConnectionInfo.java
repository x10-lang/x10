/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import org.eclipse.ptp.remotetools.environment.control.ITargetConfig;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.remotetools.environment.generichost.core.ConfigFactory;
import org.eclipse.ptp.remotetools.environment.generichost.core.TargetConfig;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;


final class DefaultConnectionInfo implements IConnectionInfo {
  
  DefaultConnectionInfo() {
    this.fControlAttr = new ConfigFactory().getAttributes();
    this.fTargetConfig = new TargetConfig(this.fControlAttr);
    this.fTargetConfig.setConnectionTimeout(5);
  }
  
  // --- Interface methods implementation
  
  public int getConnectionTimeout() {
    return this.fTargetConfig.getConnectionTimeout();
  }
  
  public void applyChangesToTargetElement() {
    // Nothing to do here.
  }
  
  public String getErrorMessage() {
    return this.fErrorMessage;
  }

  public String getHostName() {
    return this.fTargetConfig.getConnectionAddress();
  }

  public String getName() {
    return this.fName;
  }

  public String getPassphrase() {
    return this.fTargetConfig.getKeyPassphrase();
  }

  public String getPassword() {
    return this.fTargetConfig.getLoginPassword();
  }

  public int getPort() {
    return this.fTargetConfig.getConnectionPort();
  }
  
  public ControlAttributes getPTPAttributes() {
    return this.fControlAttr;
  }

  public String getPrivateKeyFile() {
    return this.fTargetConfig.getKeyPath();
  }

  public String getUserName() {
    return this.fTargetConfig.getLoginUsername();
  }
  
  public ITargetElement getTargetElement() {
    return null;
  }

  public EValidationStatus getValidationStatus() {
    return this.fValidationStatus;
  }

  public boolean isPasswordBasedAuth() {
    return this.fTargetConfig.isPasswordAuth();
  }
  
  public void setErrorMessage(final String errorMessage) {
    this.fErrorMessage = errorMessage;
  }
  
  public void setConnectionTimeout(final int timeout) {
    this.fTargetConfig.setConnectionTimeout(timeout);
  }

  public void setHostName(final String hostName) {
    this.fTargetConfig.setConnectionAddress(hostName);
  }

  public void setIsPasswordBasedFlag(final boolean isPasswordBasedAuth) {
    this.fTargetConfig.setPasswordAuth(isPasswordBasedAuth);
  }

  public void setName(final String name) {
    this.fName = name;
  }
  
  public void setPassphrase(final String passphrase) {
    this.fTargetConfig.setKeyPassphrase(passphrase);
  }

  public void setPassword(final String password) {
    this.fTargetConfig.setLoginPassword(password);
  }

  public void setPort(final int port) {
    this.fTargetConfig.setConnectionPort(port);
  }

  public void setPrivateKeyFile(final String privateKeyFile) {
    this.fTargetConfig.setKeyPath(privateKeyFile);
  }

  public void setUserName(final String userName) {
    this.fTargetConfig.setLoginUsername(userName);
  }

  public void setValidationStatus(final EValidationStatus validationStatus) {
    this.fValidationStatus = validationStatus;
    if (validationStatus != EValidationStatus.ERROR) {
      this.fErrorMessage = null;
    }
  }
  
  // --- Fields
  
  private final ITargetConfig fTargetConfig;
  
  private final ControlAttributes fControlAttr;
  
  private String fName = Constants.EMPTY_STR;
  
  private String fErrorMessage;
  
  private EValidationStatus fValidationStatus = EValidationStatus.UNKNOWN;

}
