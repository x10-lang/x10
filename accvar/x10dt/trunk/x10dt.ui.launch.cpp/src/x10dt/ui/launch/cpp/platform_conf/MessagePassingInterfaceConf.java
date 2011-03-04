/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import java.util.Arrays;

import org.eclipse.ptp.rm.core.rmsystem.IToolRMConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.utils.CodingUtils;


abstract class MessagePassingInterfaceConf extends AbstractCommunicationInterfaceConfiguration 
                                           implements IMessagePassingInterfaceConf {
  
  // --- IMessagePassingInterfaceConf's interface methods implementation
  
  public final String getDebugCommand() {
    return (this.fDebugCmd == null) ? Constants.EMPTY_STR : this.fDebugCmd;
  }

  public final String getDiscoverCommand() {
    return (this.fDiscoverCmd == null) ? Constants.EMPTY_STR : this.fDiscoverCmd;
  }

  public final String getInstallLocation() {
    return (this.fInstallLocation == null) ? Constants.EMPTY_STR : this.fInstallLocation;
  }

  public final String getLaunchCommand() {
    return (this.fLaunchCmd == null) ? Constants.EMPTY_STR : this.fLaunchCmd;
  }

  public final String getMonitorCommand() {
    return (this.fMonitorCmd == null) ? Constants.EMPTY_STR : this.fMonitorCmd;
  }

  public final int getMonitorPeriod() {
    return this.fMonitoringPeriod;
  }
  
  public boolean hasSameCommunicationInterfaceInfo(final IResourceManagerConfiguration rmConf) {
    if (! (rmConf instanceof IToolRMConfiguration)) {
      return false;
    }
    final IToolRMConfiguration toolRMConf = (IToolRMConfiguration) rmConf;
    if (toolRMConf.getUseToolDefaults() != this.fDefaultToolCmds) {
      return false;
    }
    if (! this.fDefaultToolCmds) {
      if (! toolRMConf.getLaunchCmd().equals(getLaunchCommand())) {
        return false;
      }
      if (! toolRMConf.getDebugCmd().equals(getDebugCommand())) {
        return false;
      }
      if (! toolRMConf.getDiscoverCmd().equals(getDiscoverCommand())) {
        return false;
      }
      if (! toolRMConf.getPeriodicMonitorCmd().equals(getMonitorCommand())) {
        return false;
      }
      if (toolRMConf.getPeriodicMonitorTime() != this.fMonitoringPeriod) {
        return false;
      }
    }
    if (toolRMConf.getUseInstallDefaults() != this.fDefaultIntallLocation) {
      return false;
    }
    if (! this.fDefaultIntallLocation) {
      if (! toolRMConf.getRemoteInstallPath().equals(getInstallLocation())) {
        return false;
      }
    }
    return true;
  }

  public final boolean shouldTakeDefaultInstallLocation() {
    return this.fDefaultIntallLocation;
  }

  public final boolean shouldTakeDefaultToolCommands() {
    return this.fDefaultToolCmds;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if (! (rhs instanceof MessagePassingInterfaceConf) || ! super.equals(rhs)) {
      return false;
    }
    final MessagePassingInterfaceConf rhsObj = (MessagePassingInterfaceConf) rhs;
    if (CodingUtils.equals(this.fServiceTypeId, rhsObj.fServiceTypeId) && 
        CodingUtils.equals(this.fServiceModeId, rhsObj.fServiceModeId) && (this.fDefaultToolCmds == rhsObj.fDefaultToolCmds)) {
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
    return super.hashCode() + 
           CodingUtils.generateHashCode(123, this.fServiceTypeId, this.fServiceModeId, this.fDefaultToolCmds, this.fLaunchCmd,
                                        this.fDebugCmd, this.fDiscoverCmd, this.fMonitorCmd, this.fMonitoringPeriod, 
                                        this.fDefaultIntallLocation, this.fInstallLocation) + super.hashCode();
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nDefault Tools Commands: ").append(this.fDefaultToolCmds) //$NON-NLS-1$
      .append("\nLaunch Command: ").append(this.fLaunchCmd) //$NON-NLS-1$
      .append("\nDebug Command: ").append(this.fDebugCmd) //$NON-NLS-1$
      .append("\nDiscover Command: ").append(this.fDiscoverCmd) //$NON-NLS-1$
      .append("\nMonitor Command: ").append(this.fMonitorCmd) //$NON-NLS-1$
      .append("\nMonitor Period: ").append(this.fMonitoringPeriod) //$NON-NLS-1$
      .append("\nDefault Installation: ").append(this.fDefaultIntallLocation) //$NON-NLS-1$
      .append("\nInstall Location: ").append(this.fInstallLocation); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Internal code
  
  MessagePassingInterfaceConf() {}
  
  MessagePassingInterfaceConf(final MessagePassingInterfaceConf original) {
    super(original);
    this.fDefaultToolCmds = original.fDefaultToolCmds;
    this.fLaunchCmd = original.fLaunchCmd;
    this.fDebugCmd = original.fDebugCmd;
    this.fDiscoverCmd = original.fDiscoverCmd;
    this.fMonitorCmd = original.fMonitorCmd;
    this.fMonitoringPeriod = original.fMonitoringPeriod;
    this.fDefaultIntallLocation = original.fDefaultIntallLocation;
    this.fInstallLocation = original.fInstallLocation;
  }
  
  MessagePassingInterfaceConf(final IToolRMConfiguration rmConf) {
    super(rmConf);
    this.fDefaultToolCmds = rmConf.getUseToolDefaults();
    this.fLaunchCmd = rmConf.getLaunchCmd();
    this.fDebugCmd = rmConf.getDebugCmd();
    this.fDiscoverCmd = rmConf.getDiscoverCmd();
    this.fMonitorCmd = rmConf.getPeriodicMonitorCmd();
    this.fMonitoringPeriod = rmConf.getPeriodicMonitorTime();
    this.fDefaultIntallLocation = rmConf.getUseInstallDefaults();
    this.fInstallLocation = rmConf.getRemoteInstallPath();
  }
  
  void applyChanges(final IMessagePassingInterfaceConf source) {
    super.applyChanges(source);
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
    
  boolean fDefaultToolCmds;
  
  String fLaunchCmd;
  
  String fDebugCmd;
  
  String fDiscoverCmd;
  
  String fMonitorCmd;
  
  int fMonitoringPeriod;
  
  boolean fDefaultIntallLocation;
  
  String fInstallLocation;

}
