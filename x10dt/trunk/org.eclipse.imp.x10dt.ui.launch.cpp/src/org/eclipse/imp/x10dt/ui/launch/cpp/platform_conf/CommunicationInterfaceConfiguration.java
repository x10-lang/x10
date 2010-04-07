/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import java.util.Arrays;

import org.eclipse.imp.x10dt.ui.launch.core.utils.CodingUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.editors.EOpenMPIVersion;
import org.eclipse.ptp.rm.core.rmsystem.IToolRMConfiguration;
import org.eclipse.ptp.rm.mpi.openmpi.core.rmsystem.IOpenMPIResourceManagerConfiguration;


final class CommunicationInterfaceConfiguration implements ICommunicationInterfaceConf {

  // --- Interface methods implementation
  
  public String getDebugCommand() {
    return this.fDebugCmd;
  }

  public String getDiscoverCommand() {
    return this.fDiscoverCmd;
  }

  public String getInstallLocation() {
    return this.fInstallLocation;
  }

  public String getLaunchCommand() {
    return this.fLaunchCmd;
  }

  public String getMonitorCommand() {
    return this.fMonitorCmd;
  }

  public int getMonitorPeriod() {
    return this.fMonitoringPeriod;
  }
  
  public EOpenMPIVersion getOpenMPIVersion() {
    return this.fOpenMPIVersion;
  }

  public String getServiceModeId() {
    return this.fServiceModeId;
  }

  public String getServiceTypeId() {
    return this.fServiceTypeId;
  }
  
  public boolean hasSameCommunicationInterfaceInfo(final IToolRMConfiguration rmConf) {
    if (rmConf.getUseToolDefaults() != this.fDefaultToolCmds) {
      return false;
    }
    if (! this.fDefaultToolCmds) {
      if (! rmConf.getLaunchCmd().equals(this.fLaunchCmd)) {
        return false;
      }
      if (! rmConf.getDebugCmd().equals(this.fDebugCmd)) {
        return false;
      }
      if (! rmConf.getDiscoverCmd().equals(this.fDiscoverCmd)) {
        return false;
      }
      if (! rmConf.getPeriodicMonitorCmd().equals(this.fMonitorCmd)) {
        return false;
      }
      if (rmConf.getPeriodicMonitorTime() != this.fMonitoringPeriod) {
        return false;
      }
    }
    if (rmConf.getUseInstallDefaults() != this.fDefaultIntallLocation) {
      return false;
    }
    if (! this.fDefaultIntallLocation) {
      if (! rmConf.getRemoteInstallPath().equals(this.fInstallLocation)) {
        return false;
      }
    }
    if (rmConf instanceof IOpenMPIResourceManagerConfiguration) {
      final IOpenMPIResourceManagerConfiguration openMPIRMConf = (IOpenMPIResourceManagerConfiguration) rmConf;
      switch (this.fOpenMPIVersion) {
        case EVersion_1_2:
          return IOpenMPIResourceManagerConfiguration.VERSION_12.equals(openMPIRMConf.getVersionId());
        case EVersion_1_3:
          return IOpenMPIResourceManagerConfiguration.VERSION_13.equals(openMPIRMConf.getVersionId());
        case EVersion_1_4:
          return IOpenMPIResourceManagerConfiguration.VERSION_14.equals(openMPIRMConf.getVersionId());
        default:
          return IOpenMPIResourceManagerConfiguration.VERSION_AUTO.equals(openMPIRMConf.getVersionId());
      }
    }
    return true;
  }

  public boolean shouldTakeDefaultInstallLocation() {
    return this.fDefaultIntallLocation;
  }

  public boolean shouldTakeDefaultToolCommands() {
    return this.fDefaultToolCmds;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final CommunicationInterfaceConfiguration rhsObj = (CommunicationInterfaceConfiguration) rhs;
    if (CodingUtils.equals(this.fServiceTypeId, rhsObj.fServiceTypeId) && 
        CodingUtils.equals(this.fServiceModeId, rhsObj.fServiceModeId) &&
        (this.fOpenMPIVersion == rhsObj.fOpenMPIVersion) && (this.fDefaultToolCmds == rhsObj.fDefaultToolCmds)) {
      if (! this.fDefaultToolCmds) {
        if (! (Arrays.equals(new Object[] { this.fLaunchCmd, this.fDebugCmd, this.fDiscoverCmd, this.fMonitorCmd },
                             new Object[] { rhsObj.fLaunchCmd, rhsObj.fDebugCmd, rhsObj.fDiscoverCmd,
                                                 rhsObj.fMonitorCmd }) &&
            (this.fMonitoringPeriod == rhsObj.fMonitoringPeriod))) {
          return false;
        }
      }
      
      if (this.fDefaultIntallLocation == rhsObj.fDefaultIntallLocation) {
        if (this.fDefaultIntallLocation) {
          return true;
        } else {
          return CodingUtils.equals(this.fInstallLocation, rhsObj.fInstallLocation);
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  public int hashCode() {
    return CodingUtils.generateHashCode(123, this.fServiceTypeId, this.fServiceModeId, this.fOpenMPIVersion, 
                                        this.fDefaultToolCmds, this.fLaunchCmd, this.fDebugCmd, this.fDiscoverCmd, 
                                        this.fMonitorCmd, this.fMonitoringPeriod, this.fDefaultIntallLocation, 
                                        this.fInstallLocation);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Service Type Id: ").append(this.fServiceTypeId) //$NON-NLS-1$
      .append("\nService Mode Id: ").append(this.fServiceModeId) //$NON-NLS-1$
      .append("\nOpen MPI Version: ").append(this.fOpenMPIVersion.name()) //$NON-NLS-1$
      .append("\nDefault Tools Commands: ").append(this.fDefaultToolCmds) //$NON-NLS-1$
      .append("\nLaunch Command: ").append(this.fLaunchCmd) //$NON-NLS-1$
      .append("\nDebug Command: ").append(this.fDebugCmd) //$NON-NLS-1$
      .append("\nDiscover Command: ").append(this.fDiscoverCmd) //$NON-NLS-1$
      .append("\nMonitor Command: ").append(this.fDiscoverCmd) //$NON-NLS-1$
      .append("\nMonitor Period: ").append(this.fMonitoringPeriod) //$NON-NLS-1$
      .append("\nDefault Installation: ").append(this.fDefaultIntallLocation) //$NON-NLS-1$
      .append("\nInstall Location: ").append(this.fInstallLocation); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Internal code
  
  CommunicationInterfaceConfiguration() {}
  
  CommunicationInterfaceConfiguration(final CommunicationInterfaceConfiguration original) {
    this.fServiceTypeId = original.fServiceTypeId;
    this.fServiceModeId = original.fServiceModeId;
    this.fOpenMPIVersion = original.fOpenMPIVersion;
    this.fDefaultToolCmds = original.fDefaultToolCmds;
    this.fLaunchCmd = original.fLaunchCmd;
    this.fDebugCmd = original.fDebugCmd;
    this.fDiscoverCmd = original.fDiscoverCmd;
    this.fMonitorCmd = original.fMonitorCmd;
    this.fMonitoringPeriod = original.fMonitoringPeriod;
    this.fDefaultIntallLocation = original.fDefaultIntallLocation;
    this.fInstallLocation = original.fInstallLocation;
  }
  
  void applyChanges(final ICommunicationInterfaceConf source) {
    this.fServiceTypeId = source.getServiceTypeId();
    this.fServiceModeId = source.getServiceModeId();
    this.fOpenMPIVersion = source.getOpenMPIVersion();
    this.fDefaultToolCmds = source.shouldTakeDefaultToolCommands();
    this.fLaunchCmd = source.getLaunchCommand();
    this.fDebugCmd = source.getDebugCommand();
    this.fDiscoverCmd = source.getDiscoverCommand();
    this.fMonitorCmd = source.getMonitorCommand();
    this.fMonitoringPeriod = source.getMonitorPeriod();
    this.fDefaultIntallLocation = source.shouldTakeDefaultInstallLocation();
    this.fInstallLocation = source.getInstallLocation();
  }
  
  // --- Fields
  
  String fServiceTypeId;
  
  String fServiceModeId;
  
  EOpenMPIVersion fOpenMPIVersion;
  
  boolean fDefaultToolCmds;
  
  String fLaunchCmd;
  
  String fDebugCmd;
  
  String fDiscoverCmd;
  
  String fMonitorCmd;
  
  int fMonitoringPeriod;
  
  boolean fDefaultIntallLocation;
  
  String fInstallLocation;

}
