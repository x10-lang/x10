/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.ptp.remote.remotetools.core.environment.ConfigFactory;
import org.eclipse.ptp.remote.remotetools.core.environment.PTPTargetControl;
import org.eclipse.ptp.remote.remotetools.core.environment.conf.DefaultValues;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;


final class DefaultConnectionInfo implements IConnectionInfo {
  
  // --- Interface methods implementation
  
  public void applyChangesToTargetElement() {
    // Nothing to do here.
  }
  
  public String getErrorMessage() {
    return this.fErrorMessage;
  }

  public String getHostName() {
    return this.fHostName;
  }

  public String getName() {
    return this.fName;
  }

  public String getPassphrase() {
    return this.fPassphrase;
  }

  public String getPassword() {
    return this.fPassword;
  }

  public int getPort() {
    return this.fPort;
  }
  
  public Map<String, String> getPTPAttributes() {
    final Map<String, String> attributes = new HashMap<String, String>();
    attributes.put(ConfigFactory.ATTR_LOCALHOST_SELECTION, String.valueOf(false));
    attributes.put(ConfigFactory.ATTR_LOGIN_USERNAME, this.fUserName);
    attributes.put(ConfigFactory.ATTR_LOGIN_PASSWORD, this.fPassword);
    attributes.put(ConfigFactory.ATTR_CONNECTION_PORT, String.valueOf(this.fPort));
    attributes.put(ConfigFactory.ATTR_CONNECTION_ADDRESS, this.fHostName);
    attributes.put(ConfigFactory.ATTR_KEY_PATH, this.fPrivateKeyFile);
    attributes.put(ConfigFactory.ATTR_KEY_PASSPHRASE, this.fPassphrase);
    attributes.put(ConfigFactory.ATTR_IS_PASSWORD_AUTH, String.valueOf(this.fIsPasswordBasedAuth));
    attributes.put(ConfigFactory.ATTR_CONNECTION_TIMEOUT, DefaultValues.CONNECTION_TIMEOUT);
    attributes.put(ConfigFactory.ATTR_CIPHER_TYPE, PTPTargetControl.DEFAULT_CIPHER);
    return attributes;
  }

  public String getPrivateKeyFile() {
    return this.fPrivateKeyFile;
  }

  public String getUserName() {
    return this.fUserName;
  }
  
  public ITargetElement getTargetElement() {
    return null;
  }

  public EValidationStatus getValidationStatus() {
    return this.fValidationStatus;
  }

  public boolean isPasswordBasedAuth() {
    return this.fIsPasswordBasedAuth;
  }
  
  public void setErrorMessage(final String errorMessage) {
    this.fErrorMessage = errorMessage;
  }

  public void setHostName(final String hostName) {
    this.fHostName = hostName;
  }

  public void setIsPasswordBasedFlag(final boolean isPasswordBasedAuth) {
    this.fIsPasswordBasedAuth = isPasswordBasedAuth;
  }

  public void setName(final String name) {
    this.fName = name;
  }
  
  public void setPassphrase(final String passphrase) {
    this.fPassphrase = passphrase;
  }

  public void setPassword(final String password) {
    this.fPassword = password;
  }

  public void setPort(final int port) {
    this.fPort = port;
  }

  public void setPrivateKeyFile(final String privateKeyFile) {
    this.fPrivateKeyFile = privateKeyFile;
  }

  public void setUserName(final String userName) {
    this.fUserName = userName;
  }

  public void setValidationStatus(final EValidationStatus validationStatus) {
    this.fValidationStatus = validationStatus;
    if (validationStatus != EValidationStatus.ERROR) {
      this.fErrorMessage = null;
    }
  }
  
  // --- Fields
  
  private String fName = Constants.EMPTY_STR;
  
  private String fHostName = Constants.EMPTY_STR;
  
  private int fPort = 22;
  
  private String fUserName = Constants.EMPTY_STR;
  
  private boolean fIsPasswordBasedAuth = true;
  
  private String fPassword = Constants.EMPTY_STR;
  
  private String fPrivateKeyFile = Constants.EMPTY_STR;
  
  private String fPassphrase = Constants.EMPTY_STR;
  
  private String fErrorMessage;
  
  private EValidationStatus fValidationStatus = EValidationStatus.UNKNOWN;

}
