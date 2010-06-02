/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.imp.x10dt.ui.launch.core.utils.CodingUtils;
import org.eclipse.ptp.rm.ibm.pe.core.PEPreferenceConstants;
import org.eclipse.ptp.rm.ibm.pe.core.rmsystem.IPEResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;


final class ParallelEnvironmentConf extends IBMCommunicationInterfaceConf implements IParallelEnvironmentConf {

  // --- ICommunicationInterfaceConf's interface methods implementation
  
  public boolean hasSameCommunicationInterfaceInfo(final IResourceManagerConfiguration rmConfiguration) {
    if (! (rmConfiguration instanceof IPEResourceManagerConfiguration)) {
      return false;
    }
    final IPEResourceManagerConfiguration peRMConf = (IPEResourceManagerConfiguration) rmConfiguration;
    
    if (! peRMConf.getProxyServerPath().equals(getProxyServerPath())) {
      return false;
    }
    
    if (this.fUseLoadLeveler) {
      if (! PEPreferenceConstants.OPTION_YES.equals(peRMConf.getUseLoadLeveler())) {
        return false;
      }
    } else {
      if (! PEPreferenceConstants.OPTION_NO.equals(peRMConf.getUseLoadLeveler())) {
        return false;
      }
    }
    if (this.fUseLoadLeveler) {
      if (! peRMConf.getLibraryOverride().equals(getAlternateLibraryPath())) {
        return false;
      }
      if (! peRMConf.getJobPollInterval().equals(getJobPolling())) {
        return false;
      }
      switch (getClusterMode()) {
        case DEFAULT:
          if (! "d".equals(peRMConf.getLoadLevelerMode())) { //$NON-NLS-1$
            return false;
          }
          break;
        case LOCAL:
          if (! "n".equals(peRMConf.getLoadLevelerMode())) { //$NON-NLS-1$
            return false;
          }
          break;
        case MULTI_CLUSTER:
          if (! "y".equals(peRMConf.getLoadLevelerMode())) { //$NON-NLS-1$
            return false;
          }
          break;
      }
      if (! peRMConf.getNodeMinPollInterval().equals(getNodePollingMin())) {
        return false;
      }
      if (! peRMConf.getNodeMaxPollInterval().equals(getNodePollingMax())) {
        return false;
      }
    }
   
    if (this.fRunMiniProxy) {
      if (! PEPreferenceConstants.OPTION_YES.equals(peRMConf.getRunMiniproxy())) {
        return false;
      }
    } else {
      if (! PEPreferenceConstants.OPTION_NO.equals(peRMConf.getRunMiniproxy())) {
        return false;
      }
    }
    switch (getDebuggingLevel()) {
      case NONE:
        if (! PEPreferenceConstants.TRACE_NOTHING.equals(peRMConf.getDebugLevel())) {
          return false;
        }
        break;
      case FUNCTION:
        if (! PEPreferenceConstants.TRACE_FUNCTION.equals(peRMConf.getDebugLevel())) {
          return false;
        }
        break;
      case DETAILED:
        if (! PEPreferenceConstants.TRACE_DETAIL.equals(peRMConf.getDebugLevel())) {
          return false;
        }
        break;
    }
    if (this.fSuspendProxy) {
      if (! PEPreferenceConstants.OPTION_YES.equals(peRMConf.getSuspendProxy())) {
        return false;
      }
    } else {
      if (! PEPreferenceConstants.OPTION_NO.equals(peRMConf.getSuspendProxy())) {
        return false;
      }
    }
    return true;
  }

  public void visitInterfaceOptions(final ICIConfOptionsVisitor visitor) {
    visitor.visit(this);
  }
  
  // --- IParallelEnvironmentConf's interface methods implementation
  
  public ECIDebugLevel getDebuggingLevel() {
    return (this.fCIDebugLevel == null) ? ECIDebugLevel.NONE : this.fCIDebugLevel;
  }

  public boolean shouldRunMiniProxy() {
    return this.fRunMiniProxy;
  }

  public boolean shouldSuspendProxy() {
    return this.fSuspendProxy;
  }

  public boolean shouldUseLoadLeveler() {
    return this.fUseLoadLeveler;
  }
  
  // --- Abstract methods implementation
  
  AbstractCommunicationInterfaceConfiguration copy() {
    return new ParallelEnvironmentConf(this);
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if (! (rhs instanceof ParallelEnvironmentConf) || ! super.equals(rhs)) {
      return false;
    }
    final ParallelEnvironmentConf rhsObj = (ParallelEnvironmentConf) rhs;
    return CodingUtils.equals(this.fCIDebugLevel, rhsObj.fCIDebugLevel) &&
           (this.fRunMiniProxy == rhsObj.fRunMiniProxy) && (this.fSuspendProxy == rhsObj.fSuspendProxy) && 
           (this.fUseLoadLeveler == rhsObj.fUseLoadLeveler);
  }
  
  public int hashCode() {
    return super.hashCode() + CodingUtils.generateHashCode(34545, this.fCIDebugLevel, this.fRunMiniProxy, this.fSuspendProxy,
                                                           this.fUseLoadLeveler);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nDebugging level: ").append(this.fCIDebugLevel) //$NON-NLS-1$
      .append("\nRun mini proxy: ").append(this.fRunMiniProxy) //$NON-NLS-1$
      .append("\nSuspend proxy after startup: ").append(this.fSuspendProxy) //$NON-NLS-1$
      .append("\nUse LoadLeveler: ").append(this.fUseLoadLeveler); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Internal code
  
  ParallelEnvironmentConf() {}
  
  ParallelEnvironmentConf(final ParallelEnvironmentConf source) {
    super(source);
    this.fCIDebugLevel = source.fCIDebugLevel;
    this.fRunMiniProxy = source.fRunMiniProxy;
    this.fSuspendProxy = source.fSuspendProxy;
    this.fUseLoadLeveler = source.fUseLoadLeveler;
  }
  
  // --- Fields
  
  ECIDebugLevel fCIDebugLevel;
  
  boolean fRunMiniProxy;
  
  boolean fSuspendProxy;
  
  boolean fUseLoadLeveler;

}
