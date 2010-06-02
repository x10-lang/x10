/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import static org.eclipse.ptp.rm.ibm.ll.core.IBMLLPreferenceConstants.LL_YES;
import static org.eclipse.ptp.rm.ibm.ll.core.IBMLLPreferenceConstants.LL_NO;

import java.util.Arrays;

import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CodingUtils;
import org.eclipse.ptp.remote.core.IRemoteProxyOptions;
import org.eclipse.ptp.rm.ibm.ll.core.rmsystem.IIBMLLResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;


final class LoadLevelerConf extends IBMCommunicationInterfaceConf implements ILoadLevelerConf {

  // --- ILoadLevelerConf's interface methods implementation

  public int getProxyMessageOptions() {
    return this.fProxyMsgOpts;
  }

  public String getTemplateFilePath() {
    return (this.fTemplateFilePath == null) ? Constants.EMPTY_STR : this.fTemplateFilePath;
  }

  public ELLTemplateOpt getTemplateOption() {
    return (this.fTemplateOpt == null) ? ELLTemplateOpt.ENeverWrite : this.fTemplateOpt;
  }
  
  public boolean shouldUsePortForwarding() {
    return this.fUsePortForwarding;
  }
  
  // --- ICommunicationInterfaceConf's interface methods implementation
  
  public boolean hasSameCommunicationInterfaceInfo(final IResourceManagerConfiguration rmConfiguration) {
    if (! (rmConfiguration instanceof IIBMLLResourceManagerConfiguration)) {
      return false;
    }
    final IIBMLLResourceManagerConfiguration llRMConf = (IIBMLLResourceManagerConfiguration) rmConfiguration;
    if (! llRMConf.getProxyServerPath().equals(getProxyServerPath())) {
      return false;
    }
    final boolean manualLaunch = (llRMConf.getOptions() & IRemoteProxyOptions.MANUAL_LAUNCH) != 0;
    if (manualLaunch != shouldLaunchProxyManually()) {
      return false;
    }
    final boolean portForwarding = (llRMConf.getOptions() & IRemoteProxyOptions.PORT_FORWARDING) != 0;
    if (portForwarding != shouldUsePortForwarding()) {
      return false;
    }
    switch (getClusterMode()) {
      case DEFAULT:
        if (! LL_YES.equals(llRMConf.getDefaultMulticluster())) {
          return false;
        }
        break;
      case LOCAL:
        if (! LL_YES.equals(llRMConf.getForceProxyLocal())) {
          return false;
        }
        break;
      case MULTI_CLUSTER:
        if (! LL_YES.equals(llRMConf.getForceProxyMulticluster())) {
          return false;
        }
        break;
    }
    if ((llRMConf.getJobPolling() != getJobPolling()) || (llRMConf.getMinNodePolling() != getNodePollingMin()) ||
        (llRMConf.getMaxNodePolling() != getNodePollingMax())) {
      return false;
    }
    if (! llRMConf.getLibraryPath().equals(getAlternateLibraryPath())) {
      return false;
    }
    final String traceOpt = ((this.fProxyMsgOpts & CLoadLevelerProxyMsgs.TRACE) == 0) ? LL_NO : LL_YES;
    if (! traceOpt.equals(llRMConf.getTraceOption())) {
      return false;
    }
    final String infoOpt = ((this.fProxyMsgOpts & CLoadLevelerProxyMsgs.INFO) == 0) ? LL_NO : LL_YES;
    if (! infoOpt.equals(llRMConf.getInfoMessage())) {
      return false;
    }
    final String warningOpt = ((this.fProxyMsgOpts & CLoadLevelerProxyMsgs.WARNING) == 0) ? LL_NO : LL_YES;
    if (! warningOpt.equals(llRMConf.getWarningMessage())) {
      return false;
    }
    final String errorOpt = ((this.fProxyMsgOpts & CLoadLevelerProxyMsgs.ERROR) == 0) ? LL_NO : LL_YES;
    if (! errorOpt.equals(llRMConf.getErrorMessage())) {
      return false;
    }
    final String fatalOpt = ((this.fProxyMsgOpts & CLoadLevelerProxyMsgs.FATAL) == 0) ? LL_NO : LL_YES;
    if (! fatalOpt.equals(llRMConf.getFatalMessage())) {
      return false;
    }
    final String argsOpt = ((this.fProxyMsgOpts & CLoadLevelerProxyMsgs.ARGS) == 0) ? LL_NO : LL_YES;
    if (! argsOpt.equals(llRMConf.getArgsMessage())) {
      return false;
    }
    if (! llRMConf.getTemplateFile().equals(getTemplateFilePath())) {
      return false;
    }
    switch (this.fTemplateOpt) {
      case EAlwaysWrite:
        if (! LL_YES.equals(llRMConf.getTemplateWriteAlways())) {
          return false;
        }
        break;
      case ENeverWrite:
        if (! LL_YES.equals(llRMConf.getSuppressTemplateWrite())) {
          return false;
        }
        break;
    }
    if (shouldSuspendProxyAtStartup()) {
      if (! LL_YES.equals(llRMConf.getDebugLoop())) {
        return false;
      }
    } else {
      if (LL_YES.equals(llRMConf.getDebugLoop())) {
        return false;
      }
    }
    return true;
  }
  
  public void visitInterfaceOptions(final ICIConfOptionsVisitor visitor) {
    visitor.visit(this);
  }
  
  // --- Abstract methods implementation
  
  AbstractCommunicationInterfaceConfiguration copy() {
    return new LoadLevelerConf(this);
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if (! (rhs instanceof LoadLevelerConf) || ! super.equals(rhs)) {
      return false;
    }
    final LoadLevelerConf rhsObj = (LoadLevelerConf) rhs;
    return Arrays.equals(new Object[] { this.fTemplateFilePath, this.fTemplateOpt }, 
                         new Object[] { rhsObj.fTemplateFilePath, rhsObj.fTemplateOpt }) &&
           (this.fProxyMsgOpts == rhsObj.fProxyMsgOpts);
  }
  
  public int hashCode() {
    return super.hashCode() + 
           CodingUtils.generateHashCode(7646, this.fProxyMsgOpts, this.fTemplateFilePath, this.fTemplateOpt);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nProxy message options: ").append(this.fProxyMsgOpts) //$NON-NLS-1$
      .append("\nTemplate file path: ").append(this.fTemplateFilePath) //$NON-NLS-1$
      .append("\nTemplate option: ").append(this.fTemplateOpt); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Private code
  
  LoadLevelerConf() {}
  
  LoadLevelerConf(final LoadLevelerConf source) {
    super(source);
    this.fProxyMsgOpts = source.fProxyMsgOpts;
    this.fTemplateFilePath = source.fTemplateFilePath;
    this.fTemplateOpt = source.fTemplateOpt;
  }
  
  // --- Fields
  
  int fProxyMsgOpts;
  
  String fTemplateFilePath;
  
  ELLTemplateOpt fTemplateOpt;
    
}
