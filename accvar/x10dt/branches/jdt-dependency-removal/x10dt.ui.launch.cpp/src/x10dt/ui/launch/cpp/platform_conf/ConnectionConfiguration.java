/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remotetools.environment.control.ITargetStatus;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.utils.CodingUtils;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.cpp.utils.PTPConfUtils;
import x10dt.ui.launch.rms.core.environment.ConfigFactory;


final class ConnectionConfiguration implements IConnectionConf {
  
  // --- Interface methods implementation
  
  public ControlAttributes getAttributes() {
    final ControlAttributes controlAttr = new ConfigFactory().getAttributes();
    controlAttr.setBoolean(ConfigFactory.ATTR_LOCALHOST_SELECTION, this.fIsLocal);
    controlAttr.setString(ConfigFactory.ATTR_LOGIN_USERNAME, this.fUserName);
    controlAttr.setInt(ConfigFactory.ATTR_CONNECTION_PORT, this.fPort);
    controlAttr.setString(ConfigFactory.ATTR_CONNECTION_ADDRESS, this.fHostName);
    controlAttr.setString(ConfigFactory.ATTR_KEY_PATH, this.fPrivateKeyFile);
    controlAttr.setString(ConfigFactory.ATTR_IS_PASSWORD_AUTH, this.fPassphrase);
    controlAttr.setBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH, this.fIsPasswordBasedAuth);
    controlAttr.setInt(ConfigFactory.ATTR_CONNECTION_TIMEOUT, this.fTimeout);
    return controlAttr;
  }
  
  public String getConnectionName() {
    return (this.fConnectionName == null) ? Constants.EMPTY_STR : this.fConnectionName;
  }
  
  public int getConnectionTimeout() {
    return this.fTimeout;
  }

  public String getHostName() {
    return (this.fHostName == null) ? Constants.EMPTY_STR : this.fHostName;
  }
  
  public String getLocalAddress() {
    return (this.fLocalAddress == null) ? Constants.EMPTY_STR : this.fLocalAddress;
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
    final ControlAttributes ctrAttr = new ControlAttributes(remoteConnection.getAttributes());
    final ConfigFactory cfgFactory = new ConfigFactory(new ControlAttributes());
    for (final String key : cfgFactory.getAttributes().keySet()) {
      ctrAttr.setDefaultString(key, cfgFactory.getAttributes().getString(key));
    }
    return hasSameConnectionInfo(ctrAttr);
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
  
  public boolean shouldUsePortForwarding() {
    return this.fUsePortForwarding;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final ConnectionConfiguration rhsObj = (ConnectionConfiguration) rhs;
    if (this.fIsLocal == rhsObj.fIsLocal) {
      if (this.fIsLocal) {
        return true;
      } else {
        if (Arrays.equals(new Object[] { this.fConnectionName, this.fHostName, this.fUserName, this.fLocalAddress }, 
                          new Object[] { rhsObj.fConnectionName, rhsObj.fHostName, rhsObj.fUserName, rhsObj.fLocalAddress }) && 
            (getPort() == rhsObj.getPort()) && (this.fUsePortForwarding == rhsObj.fUsePortForwarding) &&
            (this.fTimeout == rhsObj.fTimeout)) {
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
                                        this.fIsPasswordBasedAuth, this.fPassword, this.fPrivateKeyFile, this.fPassphrase,
                                        this.fLocalAddress, this.fTimeout, (this.fUsePortForwarding) ? 345345 : 4565);
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
      .append("\nPassphrase: ").append(this.fPassphrase) //$NON-NLS-1$
      .append("\nTime out: ").append(this.fTimeout) //$NON-NLS-1$
      .append("\nUse Port Forwarding: ").append(this.fUsePortForwarding); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Private code
  
  ConnectionConfiguration() {}
  
  ConnectionConfiguration(final IResourceManagerConfiguration rmConf) {
    final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmConf.getRemoteServicesId());
    final IRemoteConnection rmConn = remoteServices.getConnectionManager().getConnection(rmConf.getConnectionName());
    this.fIsLocal = PTPConstants.LOCAL_CONN_SERVICE_ID.equals(remoteServices.getId());
    this.fConnectionName = rmConf.getConnectionName();
    final Map<String, String> attributes = rmConn.getAttributes();
    this.fHostName = attributes.get(ConfigFactory.ATTR_CONNECTION_ADDRESS);
    this.fPort = Integer.parseInt(attributes.get(ConfigFactory.ATTR_CONNECTION_PORT));
    this.fUserName = attributes.get(ConfigFactory.ATTR_LOGIN_USERNAME);
    this.fIsPasswordBasedAuth = Boolean.parseBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH);
    this.fPassword = attributes.get(ConfigFactory.ATTR_LOGIN_PASSWORD);
    this.fPrivateKeyFile = attributes.get(ConfigFactory.ATTR_KEY_PATH);
    this.fPassphrase = attributes.get(ConfigFactory.ATTR_KEY_PASSPHRASE);
    this.fTimeout = Integer.parseInt(attributes.get(ConfigFactory.ATTR_CONNECTION_TIMEOUT));
  }
  
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
    this.fLocalAddress = original.fLocalAddress;
    this.fUsePortForwarding = original.fUsePortForwarding;
    this.fTimeout = original.fTimeout;
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
    this.fLocalAddress = source.fLocalAddress;
    this.fUsePortForwarding = source.fUsePortForwarding;
    this.fTimeout = source.fTimeout;
  }
  
  void initTargetElement() {
    if (! this.fIsLocal && (this.fHostName != null)) {
      final Collection<ITargetElement> founds = new ArrayList<ITargetElement>();
      for (final ITargetElement targetElement : PTPConfUtils.getTargetElements()) {
        if (this.fHostName.equals(targetElement.getAttributes().getString(ConfigFactory.ATTR_CONNECTION_ADDRESS))) {
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
    final ControlAttributes attributes = targetElement.getAttributes();
    this.fIsLocal = false;
    this.fConnectionName = targetElement.getName();
    this.fHostName = attributes.getString(ConfigFactory.ATTR_CONNECTION_ADDRESS);
    this.fPort = attributes.getInt(ConfigFactory.ATTR_CONNECTION_PORT);
    this.fUserName = attributes.getString(ConfigFactory.ATTR_LOGIN_USERNAME);
    this.fIsPasswordBasedAuth = attributes.getBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH);
    this.fPassword = attributes.getString(ConfigFactory.ATTR_LOGIN_PASSWORD);
    this.fPrivateKeyFile = attributes.getString(ConfigFactory.ATTR_KEY_PATH);
    this.fPassphrase = attributes.getString(ConfigFactory.ATTR_KEY_PASSPHRASE);
    this.fTimeout = attributes.getInt(ConfigFactory.ATTR_CONNECTION_TIMEOUT);
  }
  
  // --- Private code
  
  private boolean hasSameConnectionInfo(final ControlAttributes attributes) {
    if (this.fIsLocal == attributes.getBoolean(ConfigFactory.ATTR_LOCALHOST_SELECTION)) {
      if (this.fIsLocal) {
        return true;
      } else {
        if (! attributes.getString(ConfigFactory.ATTR_CONNECTION_ADDRESS).equals(this.fHostName)) {
          return false;
        }
        if (this.fPort != attributes.getInt(ConfigFactory.ATTR_CONNECTION_PORT)) {
          return false;
        }
        if (! attributes.getString(ConfigFactory.ATTR_LOGIN_USERNAME).equals(this.fUserName)) {
          return false;
        }
        if (this.fTimeout != attributes.getInt(ConfigFactory.ATTR_CONNECTION_TIMEOUT)) {
          return false;
        }
        if (this.fIsPasswordBasedAuth == attributes.getBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH)) {
          if (this.fIsPasswordBasedAuth) {
            return attributes.getString(ConfigFactory.ATTR_LOGIN_PASSWORD).equals(this.fPassword);
          } else {
            if (! attributes.getString(ConfigFactory.ATTR_KEY_PATH).equals(this.fPrivateKeyFile)) {
              return false;
            }
            return attributes.getString(ConfigFactory.ATTR_KEY_PASSPHRASE).equals(this.fPassphrase);
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
  
  String fLocalAddress;
  
  boolean fUsePortForwarding;
  
  int fTimeout;
  
}
