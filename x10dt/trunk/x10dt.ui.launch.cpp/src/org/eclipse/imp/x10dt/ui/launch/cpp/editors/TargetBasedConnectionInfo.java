/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.ptp.remote.remotetools.core.environment.ConfigFactory;
import org.eclipse.ptp.remotetools.environment.control.ITargetStatus;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;


final class TargetBasedConnectionInfo implements IConnectionInfo {
  
  TargetBasedConnectionInfo(final ITargetElement targetElement) {
    this.fTargetElement = targetElement;
    this.fAttributes = targetElement.getAttributes();
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
  
  public String getErrorMessage() {
    return this.fErrorMessage;
  }

  public String getHostName() {
    return this.fAttributes.get(ConfigFactory.ATTR_CONNECTION_ADDRESS);
  }

  public String getName() {
    return this.fTargetElement.getName();
  }

  public String getPassphrase() {
    return this.fAttributes.get(ConfigFactory.ATTR_KEY_PASSPHRASE);
  }

  public String getPassword() {
    return this.fAttributes.get(ConfigFactory.ATTR_LOGIN_PASSWORD);
  }

  public int getPort() {
    return Integer.parseInt(this.fAttributes.get(ConfigFactory.ATTR_CONNECTION_PORT));
  }
  
  public Map<String, String> getPTPAttributes() {
    return this.fAttributes;
  }

  public String getPrivateKeyFile() {
    return this.fAttributes.get(ConfigFactory.ATTR_KEY_PATH);
  }
  
  public String getUserName() {
    return this.fAttributes.get(ConfigFactory.ATTR_LOGIN_USERNAME);
  }

  public ITargetElement getTargetElement() {
    return this.fTargetElement;
  }
  
  public EValidationStatus getValidationStatus() {
    return this.fValidationStatus;
  }

  public boolean isPasswordBasedAuth() {
    return Boolean.parseBoolean(this.fAttributes.get(ConfigFactory.ATTR_IS_PASSWORD_AUTH));
  }
  
  public void setErrorMessage(final String errorMessage) {
    this.fErrorMessage = errorMessage;
  }

  public void setHostName(final String hostName) {
    this.fAttributes.put(ConfigFactory.ATTR_CONNECTION_ADDRESS, hostName);
    this.fIsDirty = true;
  }

  public void setIsPasswordBasedFlag(final boolean isPasswordBasedAuth) {
    this.fAttributes.put(ConfigFactory.ATTR_IS_PASSWORD_AUTH, String.valueOf(isPasswordBasedAuth));
    this.fIsDirty = true;
  }

  public void setName(final String name) {
    this.fTargetElement.setName(name);
    this.fIsDirty = true;
  }
  
  public void setPassphrase(final String passphrase) {
    this.fAttributes.put(ConfigFactory.ATTR_KEY_PASSPHRASE, passphrase);
    this.fIsDirty = true;
  }

  public void setPassword(final String password) {
    this.fAttributes.put(ConfigFactory.ATTR_LOGIN_PASSWORD, password);
    this.fIsDirty = true;
  }

  public void setPort(final int port) {
    this.fAttributes.put(ConfigFactory.ATTR_CONNECTION_PORT, String.valueOf(port));
    this.fIsDirty = true;
  }

  public void setPrivateKeyFile(final String privateKeyFile) {
    this.fAttributes.put(ConfigFactory.ATTR_KEY_PATH, privateKeyFile);
    this.fIsDirty = true;
  }
  
  public void setUserName(final String userName) {
    this.fAttributes.put(ConfigFactory.ATTR_LOGIN_USERNAME, userName);
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
  
  private final Map<String, String> fAttributes;
  
  private EValidationStatus fValidationStatus = EValidationStatus.UNKNOWN;
  
  private String fErrorMessage;
  
  private boolean fIsDirty;

}
