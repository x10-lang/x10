/**
 * Copyright (c) 2010 IBM Corporation.
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ptp.remotetools.core.IAuthInfo;
import org.eclipse.ptp.remotetools.environment.control.ITargetConfig;
import org.eclipse.swt.widgets.Display;

import x10dt.ui.launch.rms.core.Messages;

final class AuthInfo implements IAuthInfo {

  AuthInfo(final ITargetConfig config) {
    this.fConfig = config;
  }
  
  // --- Interface methods implementation
  
  public String getKeyPath() {
    return this.fConfig.getKeyPath();
  }

  public String getPassphrase() {
    if (this.fPassphrase != null) {
      final String res = this.fPassphrase;
      this.fPassphrase = null;
      return res;
    }
    return this.fConfig.getKeyPassphrase();
  }

  public String getPassword() {
    if (this.fPassword != null) {
      final String res = this.fPassword;
      this.fPassword = null;
      return res;
    }
    return this.fConfig.getLoginPassword();
  }

  public String getUsername() {
    return this.fConfig.getLoginUsername();
  }

  public boolean isPasswordAuth() {
    return this.fConfig.isPasswordAuth();
  }
  
  public String[] promptKeyboardInteractive(final String destination, final String name, final String instruction,
                                            final String[] prompt, final boolean[] echo) {
    return new String[0];
  }

  public boolean promptPassphrase(final String message) {
    return true;
  }

  public boolean promptPassword(final String message) {
    return true;
  }

  public boolean promptYesNo(final String message) {
    final boolean[] retval = new boolean[1];
    getDisplay().syncExec(new Runnable() {
      public void run() {
        retval[0] = MessageDialog.openQuestion(null, Messages.AI_RCWarning, message);
      }
    });
    return retval[0];
  }

  public void setKeyPath(final String keyPath) {
    this.fConfig.setKeyPath(keyPath);
  }
  
  public void setPassphrase(final String passphrase) {
    this.fConfig.setKeyPassphrase(passphrase);
  }
  
  public void setPassword(final String password) {
    this.fConfig.setLoginPassword(password);
  }

  public void setUsername(final String username) {
    this.fConfig.setLoginUsername(username);
  }
  
  public void setUsePassword(final boolean usePassword) {
    this.fConfig.setPasswordAuth(usePassword);
  }
  
  public void showMessage(final String message) {
    getDisplay().syncExec(new Runnable() {
      public void run() {
        MessageDialog.openInformation(null, Messages.AI_RCInfo, message);
      }
    });
  }
  
  // --- Private code

  private static Display getDisplay() {
    final Display display = Display.getCurrent();
    if (display == null) {
      return Display.getDefault();
    }
    return display;
  }

  // --- Fields

  private final ITargetConfig fConfig;

  private String fPassword = null;

  private String fPassphrase = null;

}
