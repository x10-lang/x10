/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import java.util.Map;

import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;

/**
 * Encapsulates the information for connecting to a remote machine or identifying a local connection.
 * 
 * @author egeay
 */
public interface IConnectionConf {
  
  /**
   * Returns the list of attributes as expected by PTP defining the equivalent connection to this current X10 configuration.
   * 
   * @return A non-null non-empty map.
   */
  public Map<String, String> getAttributes();
  
  /**
   * Returns the name for the connection for identification by the end-user.
   * 
   * @return A non-null, possibly empty, string.
   */
  public String getConnectionName();

  /**
   * Returns the name of the machine to connect to.
   * 
   * @return A non-null, possibly empty, string.
   */
  public String getHostName();
  
  /**
   * Returns the passphrase to use associated with a private key for login identification.
   * 
   * @return A non-null, possibly empty, string.
   */
  public String getPassphrase();
  
  /**
   * Returns the password to use for login identification.
   * 
   * @return A non-null, possibly empty, string.
   */
  public String getPassword();
  
  /**
   * Returns the port to consider for accessing the remote machine.
   * 
   * @return A natural number.
   */
  public int getPort();
  
  /**
   * Returns the path to a private key file for login identification.
   * 
   * @return A non-null, possibly empty, string.
   */
  public String getPrivateKeyFile();
  
  /**
   * Returns the user name to consider for login identification.
   * 
   * @return A non-null, possibly empty, string.
   */
  public String getUserName();
  
  /**
   * Returns if yes or no the PTP Remote Connection transmitted has similar data to the current connection configuration.
   * 
   * @param remoteConnection The PTP remote connection to use.
   * @return True if it is equivalent, false otherwise.
   */
  public boolean hasSameConnectionInfo(final IRemoteConnection remoteConnection);
  
  /**
   * Returns if yes or no the PTP Target Element transmitted has similar data to the current connection configuration.
   * 
   * @param targetElement The PTP target element to use.
   * @return True if it is equivalent, false otherwise.
   */
  public boolean hasSameConnectionInfo(final ITargetElement targetElement);
  
  /**
   * Returns if the connection is either local or remote. In the case of a local connection, the other parameters of the
   * interface are not relevant and so not used.
   * 
   * @return True if we consider the localhost, false otherwise.
   */
  public boolean isLocal();
  
  /**
   * Returns if a password-based authentication is used or not.
   * 
   * @return True if the authentication occurs with a password, false if it occurs through a pair of private key file and
   * passphrase.
   */
  public boolean isPasswordBasedAuthentication();

}
