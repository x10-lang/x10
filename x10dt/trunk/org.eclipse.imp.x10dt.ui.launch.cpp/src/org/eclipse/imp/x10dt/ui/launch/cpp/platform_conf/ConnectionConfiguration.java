/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CodingUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.remotetools.core.environment.ConfigFactory;
import org.eclipse.ptp.remote.remotetools.core.environment.PTPTargetControl;
import org.eclipse.ptp.remote.remotetools.core.environment.conf.DefaultValues;
import org.eclipse.ptp.remotetools.environment.control.ITargetStatus;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;


final class ConnectionConfiguration implements IConnectionConf {
  
  // --- Interface methods implementation
  
  public Map<String, String> getAttributes() {
    final Map<String, String> attributes = new HashMap<String, String>();
    attributes.put(ConfigFactory.ATTR_LOCALHOST_SELECTION, String.valueOf(this.fIsLocal));
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
  
  public String getConnectionName() {
    return (this.fConnectionName == null) ? Constants.EMPTY_STR : this.fConnectionName;
  }

  public String getHostName() {
    return (this.fHostName == null) ? Constants.EMPTY_STR : this.fHostName;
  }
  
  public String getPassphrase() {
    return (this.fPassphrase == null) ? Constants.EMPTY_STR : this.fPassphrase;
  }
  
  public String getPassword() {
    return (this.fPassword == null) ? Constants.EMPTY_STR : this.fPassword;
  }
  
  public int getPort() {
    return this.fPort;
  }
  
  public String getPrivateKeyFile() {
    return (this.fPrivateKeyFile == null) ? Constants.EMPTY_STR : this.fPrivateKeyFile;
  }
  
  public String getUserName() {
    return (this.fUserName == null) ? Constants.EMPTY_STR : this.fUserName;
  }
  
  public boolean hasSameConnectionInfo(final IRemoteConnection remoteConnection) {
    return hasSameConnectionInfo(remoteConnection.getAttributes());
  }
  
  public boolean hasSameConnectionInfo(final ITargetElement targetElement) {
    return hasSameConnectionInfo(targetElement.getAttributes());
  }
  
  public boolean isLocal() {
    return this.fIsLocal;
  }
  
  public boolean isPasswordBasedAuthentication() {
    return this.fIsPasswordBasedAuth;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final ConnectionConfiguration rhsObj = (ConnectionConfiguration) rhs;
    if (this.fIsLocal == rhsObj.fIsLocal) {
      if (this.fIsLocal) {
        return true;
      } else {
        if (Arrays.equals(new Object[] { this.fConnectionName, this.fHostName, this.fUserName }, 
                          new Object[] { rhsObj.fConnectionName, rhsObj.fHostName, rhsObj.fUserName }) && 
            (getPort() == rhsObj.getPort())) {
          if (this.fIsPasswordBasedAuth == rhsObj.fIsPasswordBasedAuth) {
            if (this.fIsPasswordBasedAuth) {
              return CodingUtils.equals(this.fPassword, rhsObj.fPassword);
            } else {
              return CodingUtils.equals(this.fPrivateKeyFile, rhsObj.fPrivateKeyFile) &&
                     CodingUtils.equals(this.fPassphrase, rhsObj.fPassphrase);
            }
          } else {
            return false;
          }
        } else {
          return false;
        }
      }
    } else {
      return false;
    }
  }
  
  public int hashCode() {
    return CodingUtils.generateHashCode(34, this.fIsLocal, this.fConnectionName, this.fHostName, this.fPort, this.fUserName, 
                                        this.fIsPasswordBasedAuth, this.fPassword, this.fPrivateKeyFile, this.fPassphrase);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Is local: ").append(this.fIsLocal) //$NON-NLS-1$
      .append("\nConnection name: ").append(this.fConnectionName) //$NON-NLS-1$
      .append("\nHost name: ").append(this.fHostName) //$NON-NLS-1$
      .append("\nPort: ").append(this.fPort) //$NON-NLS-1$
      .append("\nUser name: ").append(this.fUserName) //$NON-NLS-1$
      .append("\nIs password authentication: ").append(this.fIsPasswordBasedAuth) //$NON-NLS-1$
      .append("\nPassword: ").append(this.fPassword) //$NON-NLS-1$
      .append("\nPrivate key file: ").append(this.fPrivateKeyFile) //$NON-NLS-1$
      .append("\nPassphrase: ").append(this.fPassphrase); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Private code
  
  ConnectionConfiguration() {}
  
  ConnectionConfiguration(final ConnectionConfiguration original) {
    this.fIsLocal = original.fIsLocal;
    this.fConnectionName = original.fConnectionName;
    this.fHostName = original.fHostName;
    this.fPort = original.fPort;
    this.fUserName = original.fUserName;
    this.fIsPasswordBasedAuth = original.fIsPasswordBasedAuth;
    this.fPassword = original.fPassword;
    this.fPrivateKeyFile = original.fPrivateKeyFile;
    this.fPassphrase = original.fPassphrase;
  }
  
  void applyChanges(final ConnectionConfiguration source) {
    this.fIsLocal = source.fIsLocal;
    this.fConnectionName = source.fConnectionName;
    this.fHostName = source.fHostName;
    this.fPort = source.fPort;
    this.fUserName = source.fUserName;
    this.fIsPasswordBasedAuth = source.fIsPasswordBasedAuth;
    this.fPassword = source.fPassword;
    this.fPrivateKeyFile = source.fPrivateKeyFile;
    this.fPassphrase = source.fPassphrase;
  }
  
  void initTargetElement() {
    if (! this.fIsLocal && (this.fHostName != null)) {
      final Collection<ITargetElement> founds = new ArrayList<ITargetElement>();
      for (final ITargetElement targetElement : PTPConfUtils.getTargetElements()) {
        if (this.fHostName.equals(targetElement.getAttributes().get(ConfigFactory.ATTR_CONNECTION_ADDRESS))) {
          founds.add(targetElement);
        }
      }
      if (founds.size() == 1) {
        initValuesFromTargetElement(founds.iterator().next());
      } else if (founds.size() > 1) {
        for (final ITargetElement targetElement : founds) {
          try {
            if (targetElement.getControl().query() == ITargetStatus.RESUMED) {
              initValuesFromTargetElement(targetElement);
              break;
            } else {
              targetElement.getControl().create(new NullProgressMonitor());
              initValuesFromTargetElement(targetElement);
              break;
            }
          } catch (CoreException except) {
            // Let's forget and move to the next one.
          }
        }
      }
    }
  }
  
  void initValuesFromTargetElement(final ITargetElement targetElement) {
    final Map<String, String> attributes = targetElement.getAttributes();
    this.fIsLocal = false;
    this.fConnectionName = targetElement.getName();
    this.fHostName = attributes.get(ConfigFactory.ATTR_CONNECTION_ADDRESS);
    this.fPort = Integer.parseInt(attributes.get(ConfigFactory.ATTR_CONNECTION_PORT));
    this.fUserName = attributes.get(ConfigFactory.ATTR_LOGIN_USERNAME);
    this.fIsPasswordBasedAuth = Boolean.parseBoolean(attributes.get(ConfigFactory.ATTR_IS_PASSWORD_AUTH));
    this.fPassword = attributes.get(ConfigFactory.ATTR_LOGIN_PASSWORD);
    this.fPrivateKeyFile = attributes.get(ConfigFactory.ATTR_KEY_PATH);
    this.fPassphrase = attributes.get(ConfigFactory.ATTR_KEY_PASSPHRASE);
  }
  
  // --- Private code
  
  private boolean hasSameConnectionInfo(final Map<String, String> attributes) {
    if (this.fIsLocal == Boolean.parseBoolean(attributes.get(ConfigFactory.ATTR_LOCALHOST_SELECTION))) {
      if (this.fIsLocal) {
        return true;
      } else {
        if (! attributes.get(ConfigFactory.ATTR_CONNECTION_ADDRESS).equals(this.fHostName)) {
          return false;
        }
        if (this.fPort != Integer.parseInt(attributes.get(ConfigFactory.ATTR_CONNECTION_PORT))) {
          return false;
        }
        if (! attributes.get(ConfigFactory.ATTR_LOGIN_USERNAME).equals(this.fUserName)) {
          return false;
        }
        if (this.fIsPasswordBasedAuth == Boolean.parseBoolean(attributes.get(ConfigFactory.ATTR_IS_PASSWORD_AUTH))) {
          if (this.fIsPasswordBasedAuth) {
            return attributes.get(ConfigFactory.ATTR_LOGIN_PASSWORD).equals(this.fPassword);
          } else {
            if (! attributes.get(ConfigFactory.ATTR_KEY_PATH).equals(this.fPrivateKeyFile)) {
              return false;
            }
            return attributes.get(ConfigFactory.ATTR_KEY_PASSPHRASE).equals(this.fPassphrase);
          }
        } else {
          return false;
        }
      }
    } else {
      return false;
    }
  }
  
  // --- Fields
  
  boolean fIsLocal = true;
  
  String fConnectionName;
  
  String fHostName;
  
  int fPort;
  
  String fUserName;
  
  boolean fIsPasswordBasedAuth;
  
  String fPassword;
  
  String fPrivateKeyFile;
  
  String fPassphrase;
  
}
