/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import x10dt.ui.launch.core.Constants;


final class EncryptedInfoUtils {

  // --- Internal services
  
  static String getPassphrase(final String hostName, final String userName) throws StorageException {
    return getUserNameNode(hostName, userName).get(PASSPHRASE_KEY, Constants.EMPTY_STR);
  }
  
  static String getPassword(final String hostName, final String userName) throws StorageException {
    return getUserNameNode(hostName, userName).get(PASSWORD_KEY, Constants.EMPTY_STR);
  }
  
  static boolean hasPassphrase(final String hostName, final String userName) throws StorageException {
    return hasData(hostName, userName, PASSPHRASE_KEY);
  }
  
  static boolean hasPassword(final String hostName, final String userName) throws StorageException {
    return hasData(hostName, userName, PASSWORD_KEY);
  }
  
  static void setPassphrase(final String hostName, final String userName) throws StorageException {
    getUserNameNode(hostName, userName).put(PASSPHRASE_KEY, Constants.EMPTY_STR, true /* encrypt */);
  }
  
  static void setPassword(final String hostName, final String userName) throws StorageException {
    getUserNameNode(hostName, userName).put(PASSWORD_KEY, Constants.EMPTY_STR, true /* encrypt */);
  }
  
  // --- Private code
  
  private EncryptedInfoUtils() {}
  
  private static ISecurePreferences getHostNameNode(final String hostName) {
    final ISecurePreferences root = SecurePreferencesFactory.getDefault();
    return root.node(String.format(RM_HOST_NODE_SEGMENT, EncodingUtils.encodeSlashes(hostName)));
  }
  
  private static ISecurePreferences getUserNameNode(final String hostName, final String userName) {
    return getHostNameNode(hostName).node(String.format(NODE_PATH_FRMT, userName));
  }
  
  private static boolean hasData(final String hostName, final String userName, final String nodeKey) throws StorageException {
    if ((hostName.trim().length() == 0) || (userName.trim().length() == 0)) {
      return false;
    }
    final ISecurePreferences root = SecurePreferencesFactory.getDefault();
    final String hostPath = String.format(RM_HOST_NODE_SEGMENT, EncodingUtils.encodeSlashes(hostName));
    if (root.nodeExists(hostPath)) {
      final ISecurePreferences hostNode = root.node(hostPath);
      final String userPath = String.format(NODE_PATH_FRMT, userName);
      if (hostNode.nodeExists(userPath)) {
        return hostNode.node(userPath).get(nodeKey, Constants.EMPTY_STR).length() > 0;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  // --- Fields
  
  private static final String RM_HOST_NODE_SEGMENT = "/rm_connection/%s"; //$NON-NLS-1$
  
  private static final String PASSPHRASE_KEY = "passphrase"; //$NON-NLS-1$
  
  private static final String PASSWORD_KEY = "password"; //$NON-NLS-1$
  
  private static final String NODE_PATH_FRMT = "/%s"; //$NON-NLS-1$

}
