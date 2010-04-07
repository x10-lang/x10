/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import static org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus.UNKNOWN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CodingUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.editors.EOpenMPIVersion;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;


class X10PlatformConf implements IX10PlatformConf {
  
  X10PlatformConf(final IFile file) throws CoreException {
    this.fConnectionConf = new ConnectionConfiguration();
    this.fCommInterfaceConf = new CommunicationInterfaceConfiguration();
    this.fCppCompilationConf = new CppCompilationConfiguration();
    if (file.exists() && isNonEmpty(file.getContents())) {
      load(new BufferedReader(new InputStreamReader(file.getContents())));
    } else {
      this.fId = UUID.randomUUID().toString();
    }
  }
  
  // --- Interface methods implementation
  
  public final IX10PlatformConfWorkCopy createWorkingCopy() {
    return new X10PlatformConfWorkCopy(this);
  }
  
  public final ICommunicationInterfaceConf getCommunicationInterfaceConf() {
    return this.fCommInterfaceConf;
  }
  
  public final IConnectionConf getConnectionConf() {
    return this.fConnectionConf;
  }
  
  public final ICppCompilationConf getCppCompilationConf() {
    return this.fCppCompilationConf;
  }
  
  public final String getDescription() {
    return this.fDescription;
  }
  
  public String getId() {
    return this.fId;
  }
  
  public final String getName() {
    return this.fName;
  }
  
  public boolean isComplete(final boolean onlyCompilation) {
    if (hasData(this.fName) && isCppCompilationComplete() && isConnectionComplete()) {
      return onlyCompilation ? true : isCommunicationInterfaceComplete();
    } else {
      return false;
    }
  }
  
  public final void save(final Writer writer) throws IOException {
    final XMLMemento platformTag = XMLMemento.createWriteRoot(PLATFORM_TAG);
    
    platformTag.createChild(ID_TAG).putTextData(this.fId);
    if (hasData(this.fName)) {
      platformTag.createChild(NAME_TAG).putTextData(this.fName);
    }
    if (hasData(this.fDescription)) {
      platformTag.createChild(DESCRIPTION_TAG).putTextData(this.fDescription);
    }
    
    final IMemento connectionTag = platformTag.createChild(CONNECTION_TAG);
    connectionTag.putBoolean(IS_LOCAL_ATTR, this.fConnectionConf.fIsLocal);
    if (! this.fConnectionConf.fIsLocal) {
      if (hasData(this.fConnectionConf.fHostName)) {
        connectionTag.createChild(HOSTNAME_TAG).putTextData(this.fConnectionConf.fHostName);
      }
      connectionTag.createChild(PORT_TAG).putTextData(String.valueOf(this.fConnectionConf.fPort));
    }
    
    final IMemento communicationInterfaceTag = platformTag.createChild(COMMUNICATION_INTERFACE_TAG);
    communicationInterfaceTag.putBoolean(DEFAULT_TOOL_CMDS_ATTR, this.fCommInterfaceConf.fDefaultToolCmds);
    communicationInterfaceTag.putBoolean(DEFAULT_INSTALLATION_ATTR, this.fCommInterfaceConf.fDefaultIntallLocation);
    if (hasData(this.fCommInterfaceConf.fServiceTypeId)) {
      communicationInterfaceTag.createChild(SERVICE_TYPE_ID_TAG).putTextData(this.fCommInterfaceConf.fServiceTypeId);
    }
    if (hasData(this.fCommInterfaceConf.fServiceModeId)) {
      communicationInterfaceTag.createChild(SERVICE_MODE_ID_TAG).putTextData(this.fCommInterfaceConf.fServiceModeId);
    }
    final EOpenMPIVersion mpiVersion;
    if (this.fCommInterfaceConf.fOpenMPIVersion == null) {
      mpiVersion = EOpenMPIVersion.EAutoDetect;
    } else {
      mpiVersion = this.fCommInterfaceConf.fOpenMPIVersion;
    }
    communicationInterfaceTag.createChild(OPEN_MPI_VERSION_TAG).putTextData(mpiVersion.name());
    if (hasData(this.fCommInterfaceConf.fLaunchCmd)) {
      communicationInterfaceTag.createChild(LAUNCH_CMD_TAG).putTextData(this.fCommInterfaceConf.fLaunchCmd);
    }
    if (hasData(this.fCommInterfaceConf.fDebugCmd)) {
      communicationInterfaceTag.createChild(DEBUG_CMD_TAG).putTextData(this.fCommInterfaceConf.fDebugCmd);
    }
    if (hasData(this.fCommInterfaceConf.fDiscoverCmd)) {
      communicationInterfaceTag.createChild(DISCOVER_CMD_TAG).putTextData(this.fCommInterfaceConf.fDiscoverCmd);
    }
    if (hasData(this.fCommInterfaceConf.fMonitorCmd)) {
      communicationInterfaceTag.createChild(MONITOR_CMD_TAG).putTextData(this.fCommInterfaceConf.fMonitorCmd);
    }
    final String period = String.valueOf(this.fCommInterfaceConf.fMonitoringPeriod);
    communicationInterfaceTag.createChild(MONITOR_PERIOD_TAG).putTextData(period);
    if (hasData(this.fCommInterfaceConf.fInstallLocation)) {
      communicationInterfaceTag.createChild(INSTALL_LOCATION_TAG).putTextData(this.fCommInterfaceConf.fInstallLocation);
    }
    
    final IMemento cppCompilationTag = platformTag.createChild(CPP_COMPILATION_TAG);
    if (this.fCppCompilationConf.fTargetOS != null) {
      cppCompilationTag.createChild(TARGET_OS_TAG).putTextData(this.fCppCompilationConf.fTargetOS.name());
    }
    if (this.fCppCompilationConf.fArchitecture != null) {
      cppCompilationTag.createChild(ARCHITECTURE_TAG).putTextData(this.fCppCompilationConf.fArchitecture.name());
    }
    if (hasData(this.fCppCompilationConf.fCompiler)) {
      cppCompilationTag.createChild(COMPILER_TAG).putTextData(this.fCppCompilationConf.fCompiler);
    }
    if (hasData(this.fCppCompilationConf.fCompilingOpts)) {
      cppCompilationTag.createChild(COMPILING_OPTS_TAG).putTextData(this.fCppCompilationConf.fCompilingOpts);
    }
    if (hasData(this.fCppCompilationConf.fArchiver)) {
      cppCompilationTag.createChild(ARCHIVER_TAG).putTextData(this.fCppCompilationConf.fArchiver);
    }
    if (hasData(this.fCppCompilationConf.fArchivingOpts)) {
      cppCompilationTag.createChild(ARCHIVING_OPTS_TAG).putTextData(this.fCppCompilationConf.fArchivingOpts);
    }
    if (hasData(this.fCppCompilationConf.fLinker)) {
      cppCompilationTag.createChild(LINKER_TAG).putTextData(this.fCppCompilationConf.fLinker);
    }
    if (hasData(this.fCppCompilationConf.fLinkingOpts)) {
      cppCompilationTag.createChild(LINKING_OPTS_TAG).putTextData(this.fCppCompilationConf.fLinkingOpts);
    }
    if (hasData(this.fCppCompilationConf.fLinkingLibs)) {
      cppCompilationTag.createChild(LINKING_LIBS_TAG).putTextData(this.fCppCompilationConf.fLinkingLibs);
    }
    if (hasData(this.fCppCompilationConf.fX10DistLoc)) {
      cppCompilationTag.createChild(X10_DIST_LOC_TAG).putTextData(this.fCppCompilationConf.fX10DistLoc);
    }
    if (hasData(this.fCppCompilationConf.fPGASLoc)) {
      cppCompilationTag.createChild(PGAS_LOC_TAG).putTextData(this.fCppCompilationConf.fPGASLoc);
    }
    if (! this.fConnectionConf.fIsLocal) {
      if (hasData(this.fCppCompilationConf.fRemoteOutputFolder)) {
        cppCompilationTag.createChild(REMOTE_OUTPUT_FOLDER_TAG).putTextData(this.fCppCompilationConf.fRemoteOutputFolder);
      }
    }
    final EValidationStatus compValidStatus;
    if (this.fCppCompilationConf.fValidationStatus == null)  {
      compValidStatus = EValidationStatus.UNKNOWN;
    } else {
      compValidStatus = this.fCppCompilationConf.fValidationStatus;
    }
    platformTag.createChild(COMP_VALIDATION_STATUS_TAG).putTextData(compValidStatus.name());
    if (hasData(this.fCppCompilationConf.fValidationErrorMsg)) {
      platformTag.createChild(COMP_VALIDATION_ERR_MSG_TAG).putTextData(this.fCppCompilationConf.fValidationErrorMsg);
    }
    
    platformTag.save(writer);
  }
  
  // --- Overridden methods
  
  public final boolean equals(final Object rhs) {
    final X10PlatformConf rhsObj = (X10PlatformConf) rhs;
    if (CodingUtils.equals(this.fName, rhsObj.fName)) {
      return Arrays.equals(new Object[] { this.fConnectionConf, this.fCommInterfaceConf, this.fCppCompilationConf },
                           new Object[] { rhsObj.fConnectionConf, rhsObj.fCommInterfaceConf, rhsObj.fCppCompilationConf });
    } else {
      return false;
    }
  }
  
  public final int hashCode() {
    return CodingUtils.generateHashCode(565, this.fName, this.fDescription, this.fConnectionConf, this.fCommInterfaceConf,
                                        this.fCppCompilationConf);
  }
  
  public final String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Id: ").append(this.fId).append("\nName: ").append(this.fName) //$NON-NLS-1$ //$NON-NLS-2$
      .append("\nDescription: ").append(this.fDescription) //$NON-NLS-1$
      .append('\n').append(this.fConnectionConf).append('\n').append(this.fCommInterfaceConf)
      .append('\n').append(this.fCppCompilationConf);
    return sb.toString();
  }
  
  // --- Private code
  
  protected X10PlatformConf(final X10PlatformConf source) {
    this.fName = source.fName;
    this.fDescription = source.fDescription;
    this.fConnectionConf = new ConnectionConfiguration(source.fConnectionConf);
    this.fCommInterfaceConf = new CommunicationInterfaceConfiguration(source.fCommInterfaceConf);
    this.fCppCompilationConf = new CppCompilationConfiguration(source.fCppCompilationConf);
  }
  
  protected final boolean hasData(final String var) {
    return (var != null) && (var.trim().length() > 0);
  }
  
  private String getTextDataValue(final IMemento memento, final String tag) {
    final IMemento child = memento.getChild(tag);
    return (child == null) ? null : child.getTextData();
  }
  
  private boolean getBooleanValue(final IMemento memento, final String tag, final boolean defaultValue) {
    final Boolean value = memento.getBoolean(tag);
    return (value == null) ? defaultValue : value;
  }
 
  private boolean isCommunicationInterfaceComplete() {
    final ICommunicationInterfaceConf commConf = this.fCommInterfaceConf;
    if (hasData(commConf.getServiceTypeId()) && hasData(commConf.getServiceModeId())) {
      if (PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(commConf.getServiceTypeId()) &&
          (commConf.getOpenMPIVersion() == null)) {
        return false;
      }
      if (! commConf.shouldTakeDefaultToolCommands()) {
        if (! hasData(commConf.getLaunchCommand()) || ! hasData(commConf.getDebugCommand()) ||
            ! hasData(commConf.getDiscoverCommand())) {
          return false;
        }
      }
      if (! commConf.shouldTakeDefaultInstallLocation()) {
        return hasData(commConf.getInstallLocation());
      }
      return true;
    } else {
      return false;
    }
  }
  
  private boolean isConnectionComplete() {
    final IConnectionConf connConf = this.fConnectionConf;
    if (connConf.isLocal()) {
      return true;
    } else {
      if (hasData(connConf.getConnectionName()) && hasData(connConf.getHostName()) && hasData(connConf.getUserName())) {
        if (connConf.isPasswordBasedAuthentication()) {
          return true;
        } else {
          return hasData(connConf.getPrivateKeyFile());
        }
      } else {
        return false;
      }
    }
  }
  
  private boolean isCppCompilationComplete() {
    final ICppCompilationConf cppCompConf = this.fCppCompilationConf;
    if (cppCompConf.getTargetOS() == null) {
      return false;
    }
    if (hasData(cppCompConf.getCompiler()) && hasData(cppCompConf.getCompilingOpts(false)) && 
        hasData(cppCompConf.getArchiver()) && hasData(cppCompConf.getArchivingOpts(false)) &&
        hasData(cppCompConf.getLinker()) && hasData(cppCompConf.getLinkingOpts(false)) &&
        hasData(cppCompConf.getLinkingLibs(false))) {
      if (this.fConnectionConf.fIsLocal) {
        return true;
      } else {
        return hasData(cppCompConf.getX10DistribLocation()) && hasData(cppCompConf.getPGASLocation()) &&
               hasData(cppCompConf.getRemoteOutputFolder());
      }
    } else {
      return false;
    }
  }
  
  private boolean isNonEmpty(final InputStream inputStream) throws CoreException {
    try {
      return (inputStream.read() != -1);
    } catch (IOException except) {
      throw new CoreException(new Status(IStatus.ERROR, CppLaunchCore.PLUGIN_ID, LaunchMessages.XPC_InputStreamReadingError, 
                                         except));
    }
  }
  
  private void load(final Reader reader) throws WorkbenchException {
    final XMLMemento platformMemento = XMLMemento.createReadRoot(reader);
    
    this.fId = getTextDataValue(platformMemento, ID_TAG);
    this.fName = getTextDataValue(platformMemento, NAME_TAG);
    this.fDescription = getTextDataValue(platformMemento, DESCRIPTION_TAG);
    
    final IMemento connectionMemento = platformMemento.getChild(CONNECTION_TAG);
    this.fConnectionConf.fIsLocal = getBooleanValue(connectionMemento, IS_LOCAL_ATTR, true);
    if (! this.fConnectionConf.fIsLocal) {
      this.fConnectionConf.fHostName = getTextDataValue(connectionMemento, HOSTNAME_TAG);
      this.fConnectionConf.fPort = Integer.parseInt(getTextDataValue(connectionMemento, PORT_TAG));
    }
    this.fConnectionConf.initTargetElement();
    
    final IMemento ciMemento = platformMemento.getChild(COMMUNICATION_INTERFACE_TAG);
    this.fCommInterfaceConf.fServiceTypeId = getTextDataValue(ciMemento, SERVICE_TYPE_ID_TAG);
    this.fCommInterfaceConf.fServiceModeId = getTextDataValue(ciMemento, SERVICE_MODE_ID_TAG);
    final IMemento mpiMemento = ciMemento.getChild(OPEN_MPI_VERSION_TAG);
    this.fCommInterfaceConf.fOpenMPIVersion = (mpiMemento == null) ? null : EOpenMPIVersion.valueOf(mpiMemento.getTextData());
    this.fCommInterfaceConf.fDefaultToolCmds = getBooleanValue(ciMemento, DEFAULT_TOOL_CMDS_ATTR, true);
    this.fCommInterfaceConf.fLaunchCmd = getTextDataValue(ciMemento, LAUNCH_CMD_TAG);
    this.fCommInterfaceConf.fDebugCmd = getTextDataValue(ciMemento, DEBUG_CMD_TAG);
    this.fCommInterfaceConf.fDiscoverCmd = getTextDataValue(ciMemento, DISCOVER_CMD_TAG);
    this.fCommInterfaceConf.fMonitorCmd = getTextDataValue(ciMemento, MONITOR_CMD_TAG);
    final IMemento ciPeriodMemento = ciMemento.getChild(MONITOR_PERIOD_TAG);
    this.fCommInterfaceConf.fMonitoringPeriod = (ciPeriodMemento == null) ? 0 : Integer.valueOf(ciPeriodMemento.getTextData());
    this.fCommInterfaceConf.fDefaultIntallLocation = getBooleanValue(ciMemento, DEFAULT_INSTALLATION_ATTR, true);
    this.fCommInterfaceConf.fInstallLocation = getTextDataValue(ciMemento, INSTALL_LOCATION_TAG);
    
    final IMemento cppCmdsMemento = platformMemento.getChild(CPP_COMPILATION_TAG);
    final IMemento targetOSMemento = cppCmdsMemento.getChild(TARGET_OS_TAG);
    this.fCppCompilationConf.fTargetOS = (targetOSMemento == null) ? null : ETargetOS.valueOf(targetOSMemento.getTextData());
    final IMemento archMemento = cppCmdsMemento.getChild(ARCHITECTURE_TAG);
    this.fCppCompilationConf.fArchitecture = (archMemento == null) ? EArchitecture.E32Arch : 
                                                                     EArchitecture.valueOf(archMemento.getTextData());
    this.fCppCompilationConf.fCompiler = getTextDataValue(cppCmdsMemento, COMPILER_TAG);
    this.fCppCompilationConf.fCompilingOpts = getTextDataValue(cppCmdsMemento, COMPILING_OPTS_TAG);
    this.fCppCompilationConf.fArchiver = getTextDataValue(cppCmdsMemento, ARCHIVER_TAG);
    this.fCppCompilationConf.fArchivingOpts = getTextDataValue(cppCmdsMemento, ARCHIVING_OPTS_TAG);
    this.fCppCompilationConf.fLinker = getTextDataValue(cppCmdsMemento, LINKER_TAG);
    this.fCppCompilationConf.fLinkingOpts = getTextDataValue(cppCmdsMemento, LINKING_OPTS_TAG);
    this.fCppCompilationConf.fLinkingLibs = getTextDataValue(cppCmdsMemento, LINKING_LIBS_TAG);
    this.fCppCompilationConf.fX10DistLoc = getTextDataValue(cppCmdsMemento, X10_DIST_LOC_TAG);
    this.fCppCompilationConf.fPGASLoc = getTextDataValue(cppCmdsMemento, PGAS_LOC_TAG);
    this.fCppCompilationConf.fRemoteOutputFolder = getTextDataValue(cppCmdsMemento, REMOTE_OUTPUT_FOLDER_TAG);
    final String compStatus = getTextDataValue(cppCmdsMemento, COMP_VALIDATION_STATUS_TAG);
    this.fCppCompilationConf.fValidationStatus = (compStatus == null) ? UNKNOWN: EValidationStatus.valueOf(compStatus);
    this.fCppCompilationConf.fValidationErrorMsg = getTextDataValue(cppCmdsMemento, COMP_VALIDATION_ERR_MSG_TAG);
  }
  
  // --- Fields
  
  String fName;
  
  String fDescription;
  
  String fId;
    
  final ConnectionConfiguration fConnectionConf;
  
  final CommunicationInterfaceConfiguration fCommInterfaceConf;
  
  final CppCompilationConfiguration fCppCompilationConf;
  
  private static final String ID_TAG = "id"; //$NON-NLS-1$
    
  private static final String PLATFORM_TAG = "platform"; //$NON-NLS-1$
  
  private static final String NAME_TAG = "name"; //$NON-NLS-1$
  
  private static final String DESCRIPTION_TAG = "description"; //$NON-NLS-1$
  
  
  private static final String IS_LOCAL_ATTR = "is-local"; //$NON-NLS-1$
  
  private static final String CONNECTION_TAG = "connection"; //$NON-NLS-1$
  
  private static final String HOSTNAME_TAG = "hostname"; //$NON-NLS-1$
  
  private static final String PORT_TAG = "port"; //$NON-NLS-1$
    
  
  private static final String COMMUNICATION_INTERFACE_TAG = "communication-interface"; //$NON-NLS-1$
  
  private static final String SERVICE_TYPE_ID_TAG = "service-type"; //$NON-NLS-1$
  
  private static final String SERVICE_MODE_ID_TAG = "service-mode"; //$NON-NLS-1$
  
  private static final String OPEN_MPI_VERSION_TAG = "open-mpi-version"; //$NON-NLS-1$
  
  private static final String DEFAULT_TOOL_CMDS_ATTR = "default-tool-cmds"; //$NON-NLS-1$
  
  private static final String LAUNCH_CMD_TAG = "launch-cmd"; //$NON-NLS-1$
  
  private static final String DEBUG_CMD_TAG = "debug-cmd"; //$NON-NLS-1$
  
  private static final String DISCOVER_CMD_TAG = "discover-cmd"; //$NON-NLS-1$
  
  private static final String MONITOR_CMD_TAG = "monitor-cmd"; //$NON-NLS-1$
  
  private static final String MONITOR_PERIOD_TAG = "monitor-period"; //$NON-NLS-1$
  
  private static final String DEFAULT_INSTALLATION_ATTR = "default-installation"; //$NON-NLS-1$
  
  private static final String INSTALL_LOCATION_TAG = "install-location"; //$NON-NLS-1$


  private static final String CPP_COMPILATION_TAG = "cpp-compilation"; //$NON-NLS-1$
  
  private static final String TARGET_OS_TAG = "target-os"; //$NON-NLS-1$
  
  private static final String ARCHITECTURE_TAG = "architecture"; //$NON-NLS-1$
    
  private static final String COMPILER_TAG = "compiler"; //$NON-NLS-1$
  
  private static final String COMPILING_OPTS_TAG = "compiler-opts"; //$NON-NLS-1$
  
  private static final String ARCHIVER_TAG = "archiver"; //$NON-NLS-1$
  
  private static final String ARCHIVING_OPTS_TAG = "archiving-opts"; //$NON-NLS-1$
  
  private static final String LINKER_TAG = "linker"; //$NON-NLS-1$
  
  private static final String LINKING_OPTS_TAG = "linking-opts"; //$NON-NLS-1$
  
  private static final String LINKING_LIBS_TAG = "linking-libs"; //$NON-NLS-1$
  
  private static final String X10_DIST_LOC_TAG = "x10-dist-loc"; //$NON-NLS-1$
  
  private static final String PGAS_LOC_TAG = "pgas-loc"; //$NON-NLS-1$

  private static final String REMOTE_OUTPUT_FOLDER_TAG = "remote-output-folder"; //$NON-NLS-1$
  
  private static final String COMP_VALIDATION_STATUS_TAG = "compilation-validation-status"; //$NON-NLS-1$
  
  private static final String COMP_VALIDATION_ERR_MSG_TAG = "compilation-validation-error"; //$NON-NLS-1$
  
}
