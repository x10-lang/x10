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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ptp.remotetools.RemotetoolsPlugin;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;
import org.eclipse.ptp.remotetools.utils.verification.IllegalAttributeException;

import x10dt.ui.launch.rms.core.RMSCoreActivator;


public final class ConfigFactory {
  
  public static final String ATTR_LOCALHOST_SELECTION = RMSCoreActivator.PLUGIN_ID + ".localhost-selection"; //$NON-NLS-1$

  public static final String ATTR_LOGIN_USERNAME = RMSCoreActivator.PLUGIN_ID + ".login-username"; //$NON-NLS-1$

  public static final String ATTR_LOGIN_PASSWORD = RMSCoreActivator.PLUGIN_ID + ".login-password"; //$NON-NLS-1$

  public static final String ATTR_CONNECTION_ADDRESS = RMSCoreActivator.PLUGIN_ID + ".connection-address"; //$NON-NLS-1$

  public static final String ATTR_CONNECTION_PORT = RMSCoreActivator.PLUGIN_ID + ".connection-port"; //$NON-NLS-1$

  public static final String ATTR_KEY_PATH = RMSCoreActivator.PLUGIN_ID + ".key-path"; //$NON-NLS-1$

  public static final String ATTR_KEY_PASSPHRASE = RMSCoreActivator.PLUGIN_ID + ".key-passphrase"; //$NON-NLS-1$

  public static final String ATTR_IS_PASSWORD_AUTH = RMSCoreActivator.PLUGIN_ID + ".is-passwd-auth"; //$NON-NLS-1$

  public static final String ATTR_CONNECTION_TIMEOUT = RMSCoreActivator.PLUGIN_ID + ".connection-timeout"; //$NON-NLS-1$

  public static final String ATTR_CIPHER_TYPE = RMSCoreActivator.PLUGIN_ID + ".cipher-type"; //$NON-NLS-1$
  
  // --- Constructors
  
  public ConfigFactory() {
    this(null);
  }

  public ConfigFactory(final ControlAttributes attrs) {
    if (attrs != null) {
      this.fAttr = attrs;
    } else {
      this.fAttr = new ControlAttributes();
    }
    createDefaultMap();
    if (attrs == null) {
      createCurrentMapFromPreferences();
    }
  }

  // --- Public services

  public TargetConfig createTargetConfig() throws CoreException {
    try {
      this.fAttr.verifyInt(AttributeNames.CONNECTION_PORT, ATTR_CONNECTION_PORT);
      this.fAttr.verifyInt(AttributeNames.CONNECTION_TIMEOUT, ATTR_CONNECTION_TIMEOUT);
      if (this.fAttr.getBoolean(ATTR_LOCALHOST_SELECTION)) {
        this.fAttr.setString(ATTR_CONNECTION_ADDRESS, "localhost"); //$NON-NLS-1$
      }
      return new TargetConfig(this.fAttr);
    } catch (IllegalAttributeException except) {
      throw new CoreException(new Status(IStatus.ERROR, RMSCoreActivator.PLUGIN_ID, except.getMessage(), except));
    }
  }
  
  public ControlAttributes getAttributes() {
    return this.fAttr;
  }

  // --- Private code

  private void createCurrentMapFromPreferences() {
    IPreferenceStore store = RMSCoreActivator.getInstance().getPreferenceStore();
    this.fAttr.setString(ATTR_LOGIN_USERNAME, store.getString(ATTR_LOGIN_USERNAME));
    this.fAttr.setString(ATTR_CONNECTION_ADDRESS, store.getString(ATTR_CONNECTION_ADDRESS));
    this.fAttr.setString(ATTR_CONNECTION_PORT, store.getString(ATTR_CONNECTION_PORT));
  }
  
  private void createDefaultMap() {
    this.fAttr.setDefaultString(ATTR_LOCALHOST_SELECTION, DefaultValues.LOCALHOST_SELECTION);
    this.fAttr.setDefaultString(ATTR_LOGIN_USERNAME, DefaultValues.LOGIN_USERNAME);
    this.fAttr.setDefaultString(ATTR_LOGIN_PASSWORD, DefaultValues.LOGIN_PASSWORD);
    this.fAttr.setDefaultString(ATTR_CONNECTION_PORT, DefaultValues.CONNECTION_PORT);
    this.fAttr.setDefaultString(ATTR_CONNECTION_ADDRESS, DefaultValues.CONNECTION_ADDRESS);
    this.fAttr.setDefaultString(ATTR_KEY_PATH, DefaultValues.KEY_PATH);
    this.fAttr.setDefaultString(ATTR_KEY_PASSPHRASE, DefaultValues.KEY_PASSPHRASE);
    this.fAttr.setDefaultString(ATTR_IS_PASSWORD_AUTH, DefaultValues.IS_PASSWORD_AUTH);
    this.fAttr.setDefaultString(ATTR_CONNECTION_TIMEOUT, DefaultValues.CONNECTION_TIMEOUT);
    this.fAttr.setDefaultString(ATTR_CIPHER_TYPE, RemotetoolsPlugin.CIPHER_DEFAULT);
  }

  // --- Fields

  private final ControlAttributes fAttr;
  
  
  final static String[] KEY_ARRAY = { ATTR_LOCALHOST_SELECTION, ATTR_LOGIN_USERNAME, ATTR_CONNECTION_PORT,
                                      ATTR_CONNECTION_ADDRESS, ATTR_KEY_PATH, ATTR_IS_PASSWORD_AUTH, ATTR_CONNECTION_TIMEOUT,
                                      ATTR_CIPHER_TYPE };

  final static String[] KEY_CIPHERED_ARRAY = { ATTR_KEY_PASSPHRASE, ATTR_LOGIN_PASSWORD };

}
