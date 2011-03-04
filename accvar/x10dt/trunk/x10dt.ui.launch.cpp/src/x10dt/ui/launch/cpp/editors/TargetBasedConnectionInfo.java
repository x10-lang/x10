/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ptp.remotetools.environment.control.ITargetConfig;
import org.eclipse.ptp.remotetools.environment.control.ITargetStatus;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.rms.core.environment.TargetConfig;


final class TargetBasedConnectionInfo implements IConnectionInfo {
  
  TargetBasedConnectionInfo(final ITargetElement targetElement) {
    this.fTargetElement = targetElement;
    this.fAttributes = targetElement.getAttributes();
    this.fTargetConfig = new TargetConfig(this.fAttributes);
  }
  
  // --- Interface methods implementation
  
  public void applyChangesToTargetElement() throws CoreException {
    if (this.fIsDirty) {
      if (this.fTargetElement.getControl().query() != ITargetStatus.STOPPED) {
        this.fTargetElement.getControl().kill(new NullProgressMonitor());
      }
      this.fTargetElement.setAttributes(this.fAttributes);
      this.fTargetElement.getControl().updateConfiguration();
    }
  }
  
  public int getConnectionTimeout() {
    return this.fTargetConfig.getConnectionTimeout();
  }
  
  public String getErrorMessage() {
    return this.fErrorMessage;
  }

  public String getHostName() {
    return this.fTargetConfig.getConnectionAddress();
  }

  public String getName() {
    return this.fTargetElement.getName();
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
    return this.fAttributes;
  }

  public String getPrivateKeyFile() {
    return this.fTargetConfig.getKeyPath();
  }
  
  public String getUserName() {
    return this.fTargetConfig.getLoginUsername();
  }

  public ITargetElement getTargetElement() {
    return this.fTargetElement;
  }
  
  public EValidationStatus getValidationStatus() {
    return this.fValidationStatus;
  }

  public boolean isPasswordBasedAuth() {
    return this.fTargetConfig.isPasswordAuth();
  }
  
  public void setConnectionTimeout(final int timeout) {
    this.fTargetConfig.setConnectionTimeout(timeout);
    this.fIsDirty = true;
  }
  
  public void setErrorMessage(final String errorMessage) {
    this.fErrorMessage = errorMessage;
  }

  public void setHostName(final String hostName) {
    this.fTargetConfig.setConnectionAddress(hostName);
    this.fIsDirty = true;
  }

  public void setIsPasswordBasedFlag(final boolean isPasswordBasedAuth) {
    this.fTargetConfig.setPasswordAuth(isPasswordBasedAuth);
    this.fIsDirty = true;
  }

  public void setName(final String name) {
    this.fTargetElement.setName(name);
    this.fIsDirty = true;
  }
  
  public void setPassphrase(final String passphrase) {
    this.fTargetConfig.setKeyPassphrase(passphrase);
    this.fIsDirty = true;
  }

  public void setPassword(final String password) {
    this.fTargetConfig.setLoginPassword(password);
    this.fIsDirty = true;
  }

  public void setPort(final int port) {
    this.fTargetConfig.setConnectionPort(port);
    this.fIsDirty = true;
  }

  public void setPrivateKeyFile(final String privateKeyFile) {
    this.fTargetConfig.setKeyPath(privateKeyFile);
    this.fIsDirty = true;
  }
  
  public void setUserName(final String userName) {
    this.fTargetConfig.setLoginUsername(userName);
    this.fIsDirty = true;
  }

  public void setValidationStatus(final EValidationStatus validationStatus) {
    this.fValidationStatus = validationStatus;
    if (validationStatus != EValidationStatus.ERROR) {
      this.fErrorMessage = null;
    }
  }
  
  // --- Fields
  
  private final ITargetElement fTargetElement;
  
  private final ControlAttributes fAttributes;
  
  private final ITargetConfig fTargetConfig;
  
  private EValidationStatus fValidationStatus = EValidationStatus.UNKNOWN;
  
  private String fErrorMessage;
  
  private boolean fIsDirty;

}
