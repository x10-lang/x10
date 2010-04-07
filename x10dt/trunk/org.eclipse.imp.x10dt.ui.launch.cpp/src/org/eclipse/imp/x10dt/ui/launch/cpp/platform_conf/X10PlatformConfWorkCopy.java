/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import static org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.core.utils.EnumUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants;
import org.eclipse.imp.x10dt.ui.launch.cpp.editors.EOpenMPIVersion;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.cpp_commands.DefaultCPPCommandsFactory;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.cpp_commands.IDefaultCPPCommands;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.osgi.framework.Bundle;


final class X10PlatformConfWorkCopy extends X10PlatformConf implements IX10PlatformConfWorkCopy {
  
  X10PlatformConfWorkCopy(final X10PlatformConf source) {
    super(source);
    this.fSource = source;
    this.fId = source.fId;
    this.fName = source.fName;
    this.fDescription = source.fDescription;
  }
  
  public void applyChanges() {
    this.fSource.fId = super.fId;
    this.fSource.fName = super.fName;
    this.fSource.fDescription = super.fDescription;
    this.fSource.fConnectionConf.applyChanges(this.fConnectionConf);
    this.fSource.fCommInterfaceConf.applyChanges(this.fCommInterfaceConf);
    this.fSource.fCppCompilationConf.applyChanges(this.fCppCompilationConf);
    this.fIsDirty = false;
  }
  
  public void initializeToDefaultValues() {
    this.fCommInterfaceConf.fDefaultIntallLocation = true;
    this.fCommInterfaceConf.fDefaultToolCmds = true;
    if (this.fConnectionConf.fIsLocal) {
      initLocalCppCompilationCommands();
      initLocalX10DistribLocation();
    }
    if (this.fCommInterfaceConf.fServiceTypeId == null) {
      final boolean isWindows = this.fCppCompilationConf.getTargetOS() == ETargetOS.WINDOWS;
      this.fCommInterfaceConf.fServiceTypeId = isWindows ? MPICH2_SERVICE_PROVIDER_ID : OPEN_MPI_SERVICE_PROVIDER_ID;
      this.fCommInterfaceConf.fOpenMPIVersion = EOpenMPIVersion.EAutoDetect;
    }
    if (this.fCommInterfaceConf.fServiceModeId == null) {
      this.fCommInterfaceConf.fServiceModeId = PTPConstants.LAUNCH_SERVICE_ID;
    }
  }
  
  public boolean isDirty() {
    return this.fIsDirty;
  }
  
  public boolean isStale() {
    return this.fIsStale;
  }
  
  public void refresh() {
    this.fIsStale = false;
  }
  
  // --- IX10PlatformConf's setter methods
  
  public void setDescription(final String description) {
    super.fDescription = description;
    updateDirtyFlag();
  }
  
  public void setName(final String name) {
    super.fName = name;
    updateDirtyFlag();
  }
  
  // --- ICppCompilationConf's setter methods

  public void setArchitecture(final EArchitecture architecture) {
    super.fCppCompilationConf.fArchitecture = architecture;
    updateDirtyFlag();
  }
  
  public void setArchiver(final String archiver) {
    super.fCppCompilationConf.fArchiver = archiver;
    updateDirtyFlag();
  }
  
  public void setArchivingOpts(final String archivingOpts) {
    super.fCppCompilationConf.fArchivingOpts = archivingOpts;
    updateDirtyFlag();
  }
  
  public void setCompiler(final String compiler) {
    super.fCppCompilationConf.fCompiler = compiler;
    updateDirtyFlag();
  }
  
  public void setCompilingOpts(final String compilerOpts) {
    super.fCppCompilationConf.fCompilingOpts = compilerOpts;
    updateDirtyFlag();
  }
  
  public void setCppConfValidationErrorMessage(final String validationErrorMessage) {
    super.fCppCompilationConf.fValidationErrorMsg = validationErrorMessage;
    updateDirtyFlag();
  }
  
  public void setCppConfValidationStatus(final EValidationStatus validationStatus) {
    super.fCppCompilationConf.fValidationStatus = validationStatus;
    updateDirtyFlag();
  }
  
  public void setLinker(final String linker) {
    super.fCppCompilationConf.fLinker = linker;
    updateDirtyFlag();
  }
  
  public void setLinkingLibs(final String linkingLibs) {
    super.fCppCompilationConf.fLinkingLibs = linkingLibs;
    updateDirtyFlag();
  }
  
  public void setLinkingOpts(final String linkingOpts) {
    super.fCppCompilationConf.fLinkingOpts = linkingOpts;
    updateDirtyFlag();
  }
    
  public void setPGASLocation(final String pgasLocation) {
    super.fCppCompilationConf.fPGASLoc = pgasLocation;
    updateDirtyFlag();
  }
  
  public void setRemoteOutputFolder(final String remoteOutputFolder) {
    super.fCppCompilationConf.fRemoteOutputFolder = remoteOutputFolder;
    updateDirtyFlag();
  }
  
  public void setTargetOS(final ETargetOS targetOS) {
    super.fCppCompilationConf.fTargetOS = targetOS;
    updateDirtyFlag();
  }
  
  public void setX10DistribLocation(final String x10DistribLoc) {
    super.fCppCompilationConf.fX10DistLoc = x10DistribLoc;
    updateDirtyFlag();
  }
    
  // --- IConnectionConf's setter methods
  
  public void setConnectionName(final String connectionName) {
    super.fConnectionConf.fConnectionName = connectionName;
    updateDirtyFlag();
  }
  
  public void setHostName(final String hostName) {
    super.fConnectionConf.fHostName = hostName;
    updateDirtyFlag();
  }
  
  public void setIsLocalFlag(final boolean isLocal) {
    super.fConnectionConf.fIsLocal = isLocal;
    if (isLocal) {
      if (this.fCppCompilationConf.fTargetOS == null) {
        initLocalCppCompilationCommands();
      }
      initLocalX10DistribLocation();
    }
    updateDirtyFlag();
  }
  
  public void setIsPasswordBasedAuthenticationFlag(final boolean isPasswordBasedAuth) {
    super.fConnectionConf.fIsPasswordBasedAuth = isPasswordBasedAuth;
    updateDirtyFlag();
  }
  
  public void setPassphrase(final String passphrase) {
    super.fConnectionConf.fPassphrase = passphrase;
    updateDirtyFlag();
  }
  
  public void setPassword(final String password) {
    super.fConnectionConf.fPassword = password;
    updateDirtyFlag();
  }
  
  public void setPort(final int port) {
    super.fConnectionConf.fPort = port;
    updateDirtyFlag();
  }
  
  public void setPrivateKeyFile(final String privateKeyFile) {
    super.fConnectionConf.fPrivateKeyFile = privateKeyFile;
    updateDirtyFlag();
  }
  
  public void setTargetElement(final ITargetElement targetElement) {
    super.fConnectionConf.initValuesFromTargetElement(targetElement);
    updateDirtyFlag();
  }
  
  public void setUserName(final String userName) {
    super.fConnectionConf.fUserName = userName;
    updateDirtyFlag();
  }
  
  // --- ICommunicationInterface's setter methods
  
  public void setDebugCommand(final String debugCommand) {
    super.fCommInterfaceConf.fDebugCmd = debugCommand;
    updateDirtyFlag();
  }
  
  public void setDefaultInstallLocationFlag(final boolean shouldTakeInstallLocation) {
    super.fCommInterfaceConf.fDefaultIntallLocation = shouldTakeInstallLocation;
    updateDirtyFlag();
  }
  
  public void setDefaultToolCommands(final boolean shouldTakeDefaultToolCommands) {
    super.fCommInterfaceConf.fDefaultToolCmds = shouldTakeDefaultToolCommands;
    updateDirtyFlag();
  }
  
  public void setDiscoverCommand(final String discoverCommand) {
    super.fCommInterfaceConf.fDiscoverCmd = discoverCommand;
    updateDirtyFlag();
  }
  
  public void setInstallLocation(final String installLocation) {
    super.fCommInterfaceConf.fInstallLocation = installLocation;
    updateDirtyFlag();
  }
  
  public void setLaunchCommand(final String launchCommand) {
    super.fCommInterfaceConf.fLaunchCmd = launchCommand;
    updateDirtyFlag();
  }
  
  public void setMonitorCommand(final String monitorCommand) {
    super.fCommInterfaceConf.fMonitorCmd = monitorCommand;
    updateDirtyFlag();
  }
  
  public void setMonitorPeriod(final int monitorPeriod) {
    super.fCommInterfaceConf.fMonitoringPeriod = monitorPeriod;
    updateDirtyFlag();
  }
  
  public void setOpenMPIVersion(final EOpenMPIVersion openMPIVersion) {
    super.fCommInterfaceConf.fOpenMPIVersion = openMPIVersion;
    updateDirtyFlag();
  }
  
  public void setServiceModeId(final String seriveModeId) {
    super.fCommInterfaceConf.fServiceModeId = seriveModeId;
    updateDirtyFlag();
  }
  
  public void setServiceTypeId(final String serviceTypeId) {
    super.fCommInterfaceConf.fServiceTypeId =  serviceTypeId;
    updateDirtyFlag();
  }
  
  // --- Private code
  
  private void initLocalCppCompilationCommands() {
    this.fCppCompilationConf.fTargetOS = EnumUtils.getLocalOS();
    final boolean is64Arch = is64Arch();
    this.fCppCompilationConf.fArchitecture = is64Arch ? EArchitecture.E64Arch : EArchitecture.E32Arch;
    final IDefaultCPPCommands defaultCPPCommands;
    switch (this.fCppCompilationConf.fTargetOS) {
    case AIX:
      defaultCPPCommands = DefaultCPPCommandsFactory.createAixCommands(is64Arch);
      break;
    case LINUX:
      defaultCPPCommands = DefaultCPPCommandsFactory.createLinuxCommands(is64Arch);
      break;
    case MAC:
      defaultCPPCommands = DefaultCPPCommandsFactory.createMacCommands(is64Arch);
      break;
    case WINDOWS:
      defaultCPPCommands = DefaultCPPCommandsFactory.createCygwinCommands(is64Arch);
      break;
    default:
      defaultCPPCommands = DefaultCPPCommandsFactory.createUnkownUnixCommands(is64Arch);
    }
    this.fCppCompilationConf.fCompiler = defaultCPPCommands.getCompiler();
    this.fCppCompilationConf.fCompilingOpts = defaultCPPCommands.getCompilerOptions();
    this.fCppCompilationConf.fArchiver = defaultCPPCommands.getArchiver();
    this.fCppCompilationConf.fArchivingOpts = defaultCPPCommands.getArchivingOpts();
    this.fCppCompilationConf.fLinker = defaultCPPCommands.getLinker();
    this.fCppCompilationConf.fLinkingOpts = defaultCPPCommands.getLinkingOptions();
    this.fCppCompilationConf.fLinkingLibs = defaultCPPCommands.getLinkingLibraries();
    this.fCppCompilationConf.fArchitecture = (is64Arch) ? EArchitecture.E64Arch : EArchitecture.E32Arch;
  }

  private void initLocalX10DistribLocation() {
    final Bundle x10DistBundle = Platform.getBundle(Constants.X10_DIST_PLUGIN_ID);
    if (x10DistBundle != null) {
      final URL url = x10DistBundle.getResource("include"); //$NON-NLS-1$
      try {
        this.fCppCompilationConf.fX10DistLoc = new File(FileLocator.resolve(url).getFile()).getParent();
        this.fCppCompilationConf.fPGASLoc = this.fCppCompilationConf.fX10DistLoc;
      } catch (IOException except) {
        // Let's forget.
      }
    }
  }
  
  private boolean is64Arch() {
    return false; //TODO
  }
  
  private void updateDirtyFlag() {
    this.fIsDirty = (this.fIsDirty) ? ! this.equals(this.fSource) : true;
  }
  
  // --- Fields
  
  private final X10PlatformConf fSource;
  
  private boolean fIsStale;
  
  private boolean fIsDirty;

}
