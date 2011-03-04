/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.ptp.remote.core.IRemoteProxyOptions;
import org.eclipse.ptp.rm.ibm.pe.core.PEPreferenceConstants;
import org.eclipse.ptp.rm.ibm.pe.core.rmsystem.IPEResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.services.core.IServiceProvider;

import x10dt.ui.launch.core.utils.CodingUtils;


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
    final boolean manualLaunch = (peRMConf.getOptions() & IRemoteProxyOptions.MANUAL_LAUNCH) != 0;
    if (manualLaunch != shouldLaunchProxyManually()) {
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
      if (! peRMConf.getNodeMinPollInterval().equals(String.valueOf(getNodePollingMin()))) {
        return false;
      }
      if (! peRMConf.getNodeMaxPollInterval().equals(String.valueOf(getNodePollingMax()))) {
        return false;
      }
      if (! peRMConf.getJobPollInterval().equals(String.valueOf(getJobPolling()))) {
        return false;
      }
      if (! peRMConf.getLibraryOverride().equals(getAlternateLibraryPath())) {
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
    if (shouldSuspendProxyAtStartup()) {
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
           (this.fRunMiniProxy == rhsObj.fRunMiniProxy) && (this.fUseLoadLeveler == rhsObj.fUseLoadLeveler);
  }
  
  public int hashCode() {
    return super.hashCode() + CodingUtils.generateHashCode(34545, this.fCIDebugLevel, this.fRunMiniProxy, 
                                                           this.fUseLoadLeveler);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nDebugging level: ").append(this.fCIDebugLevel) //$NON-NLS-1$
      .append("\nRun mini proxy: ").append(this.fRunMiniProxy) //$NON-NLS-1$
      .append("\nUse LoadLeveler: ").append(this.fUseLoadLeveler); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Internal code
  
  ParallelEnvironmentConf() {}
  
  ParallelEnvironmentConf(final IPEResourceManagerConfiguration rmConf) {
    super.fServiceModeId = ((IServiceProvider) rmConf).getServiceId();
    super.fServiceTypeId = ((IServiceProvider) rmConf).getId();
    
    super.fAlternateLibPath = rmConf.getLibraryOverride();
    if ("n".equals(rmConf.getLoadLevelerMode())) { //$NON-NLS-1$
      super.fClusterMode = EClusterMode.LOCAL;
    } else if ("y".equals(rmConf.getLoadLevelerMode())) { //$NON-NLS-1$
      super.fClusterMode = EClusterMode.MULTI_CLUSTER;
    } else {
      super.fClusterMode = EClusterMode.DEFAULT;
    }
    super.fJobPolling = Integer.parseInt(rmConf.getJobPollInterval());
    super.fNodePollingMin = Integer.parseInt(rmConf.getNodeMinPollInterval());
    super.fNodePollingMax = Integer.parseInt(rmConf.getNodeMaxPollInterval());
    super.fProxyServerPath = rmConf.getProxyServerPath();
    super.fLaunchProxyManually = (rmConf.getOptions() & IRemoteProxyOptions.MANUAL_LAUNCH) != 0;
    super.fSuspendProxyAtStartup = PEPreferenceConstants.OPTION_YES.equals(rmConf.getSuspendProxy());
    
    if (PEPreferenceConstants.TRACE_FUNCTION.equals(rmConf.getDebugLevel())) {
      this.fCIDebugLevel = ECIDebugLevel.FUNCTION;
    } else if (PEPreferenceConstants.TRACE_DETAIL.equals(rmConf.getDebugLevel())) {
      this.fCIDebugLevel = ECIDebugLevel.DETAILED;
    } else {
      this.fCIDebugLevel = ECIDebugLevel.NONE;
    }
    this.fRunMiniProxy = PEPreferenceConstants.OPTION_YES.equals(rmConf.getRunMiniproxy());
    this.fUseLoadLeveler = PEPreferenceConstants.OPTION_YES.equals(rmConf.getUseLoadLeveler());
  }
  
  ParallelEnvironmentConf(final ParallelEnvironmentConf source) {
    super(source);
    this.fCIDebugLevel = source.fCIDebugLevel;
    this.fRunMiniProxy = source.fRunMiniProxy;
    this.fUseLoadLeveler = source.fUseLoadLeveler;
  }
  
  // --- Fields
  
  ECIDebugLevel fCIDebugLevel;
  
  boolean fRunMiniProxy;
  
  boolean fUseLoadLeveler;

}
