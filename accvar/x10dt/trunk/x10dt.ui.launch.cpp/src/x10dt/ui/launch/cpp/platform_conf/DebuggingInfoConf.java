/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import x10dt.ui.launch.core.Constants;


final class DebuggingInfoConf implements IDebuggingInfoConf {
  
  DebuggingInfoConf() {
    this.fDebuggerFolder = Constants.EMPTY_STR;
    this.fPort = 8888;
  }
  
  DebuggingInfoConf(final IDebuggingInfoConf source) {
    this.fDebuggerFolder = source.getDebuggerFolder();
    this.fPort = source.getPort();
  }
  
  // --- Interface methods implementation

  public String getDebuggerFolder() {
    return (this.fDebuggerFolder == null) ? Constants.EMPTY_STR : this.fDebuggerFolder;
  }

  public int getPort() {
    return this.fPort;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final DebuggingInfoConf rhsObj = (DebuggingInfoConf) rhs;
    return this.fDebuggerFolder.equals(rhsObj.fDebuggerFolder) && (this.fPort == rhsObj.fPort);
  }
  
  public int hashCode() {
    return this.fDebuggerFolder.hashCode() + 342 * this.fPort;
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Debugger Folder: ").append(this.fDebuggerFolder) //$NON-NLS-1$
      .append("\nDebugging Port: ").append(this.fPort); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Internal services
  
  void applyChanges(final IDebuggingInfoConf source) {
    this.fDebuggerFolder = source.getDebuggerFolder();
    this.fPort = source.getPort();
  }
  
  // --- Fields
  
  String fDebuggerFolder;
  
  int fPort;

}
