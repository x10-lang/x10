/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import java.util.Arrays;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.utils.CodingUtils;

abstract class IBMCommunicationInterfaceConf extends AbstractCommunicationInterfaceConfiguration 
                                             implements IIBMCommunicationInterfaceConf {
  
  // --- IIBMCommunicationInterfaceConf's interface methods implementation

  public String getAlternateLibraryPath() {
    return (this.fAlternateLibPath == null) ? Constants.EMPTY_STR : this.fAlternateLibPath;
  }

  public EClusterMode getClusterMode() {
    return (this.fClusterMode == null) ? EClusterMode.DEFAULT : this.fClusterMode;
  }

  public int getJobPolling() {
    return this.fJobPolling;
  }

  public int getNodePollingMax() {
    return this.fNodePollingMax;
  }

  public int getNodePollingMin() {
    return this.fNodePollingMin;
  }

  public String getProxyServerPath() {
    return (this.fProxyServerPath == null) ? Constants.EMPTY_STR : this.fProxyServerPath;
  }

  public boolean shouldLaunchProxyManually() {
    return this.fLaunchProxyManually;
  }
  
  public boolean shouldSuspendProxyAtStartup() {
    return this.fSuspendProxyAtStartup;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if (! (rhs instanceof IBMCommunicationInterfaceConf) || ! super.equals(rhs)) {
      return false;
    }
    final IBMCommunicationInterfaceConf rhsObj = (IBMCommunicationInterfaceConf) rhs;
    return Arrays.equals(new Object[] { this.fAlternateLibPath, this.fClusterMode, this.fProxyServerPath }, 
                         new Object[] { rhsObj.fAlternateLibPath, rhsObj.fClusterMode, rhsObj.fProxyServerPath }) &&
           (this.fJobPolling == rhsObj.fJobPolling) && (this.fNodePollingMin == rhsObj.fNodePollingMin) &&
           (this.fNodePollingMax == rhsObj.fNodePollingMax) && (this.fLaunchProxyManually == rhsObj.fLaunchProxyManually) &&
           (this.fSuspendProxyAtStartup == rhsObj.fSuspendProxyAtStartup);
  }
  
  public int hashCode() {
    return super.hashCode() + 
           CodingUtils.generateHashCode(7646, this.fAlternateLibPath, this.fClusterMode, this.fJobPolling, 
                                        this.fNodePollingMin, this.fNodePollingMax, this.fProxyServerPath, 
                                        this.fLaunchProxyManually, this.fSuspendProxyAtStartup);
  }
  
  public boolean isComplete() {
    if (super.isComplete()) {
      return hasData(this.fProxyServerPath);
    } else {
      return false;
    }
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nAlternate library path: ").append(this.fAlternateLibPath) //$NON-NLS-1$
      .append("\nCluster mode: ").append(this.fClusterMode) //$NON-NLS-1$
      .append("\nJob polling: ").append(this.fJobPolling) //$NON-NLS-1$
      .append("\nNode polling min: ").append(this.fNodePollingMin) //$NON-NLS-1$
      .append("\nMode polling max: ").append(this.fNodePollingMax) //$NON-NLS-1$
      .append("\nProxy server path: ").append(this.fProxyServerPath) //$NON-NLS-1$
      .append("\nLaunch proxy manually: ").append(this.fLaunchProxyManually) //$NON-NLS-1$
      .append("\nSuspend proxy at startup: ").append(this.fSuspendProxyAtStartup); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Private code
  
  IBMCommunicationInterfaceConf() {}
  
  IBMCommunicationInterfaceConf(final IBMCommunicationInterfaceConf source) {
    super(source);
    this.fAlternateLibPath = source.fAlternateLibPath;
    this.fClusterMode = source.fClusterMode;
    this.fJobPolling = source.fJobPolling;
    this.fNodePollingMin = source.fNodePollingMin;
    this.fNodePollingMax = source.fNodePollingMax;
    this.fProxyServerPath = source.fProxyServerPath;
    this.fLaunchProxyManually = source.fLaunchProxyManually;
    this.fSuspendProxyAtStartup = source.fSuspendProxyAtStartup;
  }
  
  // --- Fields
  
  String fAlternateLibPath;
  
  EClusterMode fClusterMode;
  
  int fJobPolling;
  
  int fNodePollingMin;
  
  int fNodePollingMax;
  
  String fProxyServerPath;
  
  boolean fLaunchProxyManually;
  
  boolean fSuspendProxyAtStartup;

}
