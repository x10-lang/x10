/**
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial Implementation
 *
 */
package x10dt.ui.launch.rms.core.environment;

import org.eclipse.ptp.remotetools.environment.control.ITargetConfig;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;

/**
 * Implementation of {@link ITargetConfig} for a generic host.
 * 
 * @author egeay
 */
public final class TargetConfig implements ITargetConfig {

  /**
   * Creates the current target configuration instance from the attributes transmitted.
   * 
   * @param attrs The attributes to use.
   */
  public TargetConfig(final ControlAttributes attrs) {
    this.fAttrs = attrs;
  }
  
  // --- Interface methods implementation

  public String getCipherType() {
    return this.fAttrs.getString(ConfigFactory.ATTR_CIPHER_TYPE);
  }

  public String getConnectionAddress() {
    return this.fAttrs.getString(ConfigFactory.ATTR_CONNECTION_ADDRESS);
  }

  public int getConnectionPort() {
    return this.fAttrs.getInt(ConfigFactory.ATTR_CONNECTION_PORT);
  }

  public int getConnectionTimeout() {
    return this.fAttrs.getInt(ConfigFactory.ATTR_CONNECTION_TIMEOUT);
  }

  public String getKeyPassphrase() {
    return this.fAttrs.getString(ConfigFactory.ATTR_KEY_PASSPHRASE);
  }

  public String getKeyPath() {
    return this.fAttrs.getString(ConfigFactory.ATTR_KEY_PATH);
  }

  public String getLoginPassword() {
    return this.fAttrs.getString(ConfigFactory.ATTR_LOGIN_PASSWORD);
  }

  public String getLoginUsername() {
    return this.fAttrs.getString(ConfigFactory.ATTR_LOGIN_USERNAME);
  }

  public boolean isPasswordAuth() {
    return this.fAttrs.getBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH);
  }

  public void setCipherType(final String cipherType) {
    this.fAttrs.setString(ConfigFactory.ATTR_CIPHER_TYPE, cipherType);
  }

  public void setConnectionAddress(final String connectionAddress) {
    this.fAttrs.setString(ConfigFactory.ATTR_CONNECTION_ADDRESS, connectionAddress);
  }

  public void setConnectionPort(final int connectionPort) {
    this.fAttrs.setInt(ConfigFactory.ATTR_CONNECTION_PORT, connectionPort);
  }

  public void setConnectionTimeout(final int connectionTimeout) {
    this.fAttrs.setInt(ConfigFactory.ATTR_CONNECTION_TIMEOUT, connectionTimeout);
  }

  public void setKeyPassphrase(final String keyPassphrase) {
    this.fAttrs.setString(ConfigFactory.ATTR_KEY_PASSPHRASE, keyPassphrase);
  }

  public void setKeyPath(final String keyPath) {
    this.fAttrs.setString(ConfigFactory.ATTR_KEY_PATH, keyPath);
  }

  public void setLoginPassword(final String password) {
    this.fAttrs.setString(ConfigFactory.ATTR_LOGIN_PASSWORD, password);
  }

  public void setLoginUsername(final String username) {
    this.fAttrs.setString(ConfigFactory.ATTR_LOGIN_USERNAME, username);

  }
  
  public void setPasswordAuth(final boolean isPasswordAuth) {
    this.fAttrs.setBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH, isPasswordAuth);
  }

  // --- Fields

  private final ControlAttributes fAttrs;
}
