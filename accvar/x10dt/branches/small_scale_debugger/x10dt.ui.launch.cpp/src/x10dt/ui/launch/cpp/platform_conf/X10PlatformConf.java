/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import static x10dt.ui.launch.core.platform_conf.EValidationStatus.UNKNOWN;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.osgi.framework.Bundle;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.EBitsArchitecture;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.platform_conf.ETransport;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.CodingUtils;
import x10dt.ui.launch.core.utils.CoreResourceUtils;
import x10dt.ui.launch.core.utils.EnumUtils;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.editors.EOpenMPIVersion;
import x10dt.ui.launch.cpp.platform_conf.cpp_commands.DefaultCPPCommandsFactory;
import x10dt.ui.launch.cpp.platform_conf.cpp_commands.IDefaultCPPCommands;
import x10dt.ui.launch.cpp.utils.PlatformConfUtils;


class X10PlatformConf implements IX10PlatformConf {
  
  X10PlatformConf(final IFile file) {
    this.fConnectionConf = new ConnectionConfiguration();
    this.fCppCompilationConf = new CppCompilationConfiguration();
    this.fCommInterfaceFact = new CommInterfaceFactory();
    try {
			if (isNonEmpty(file)) {
			  load(file, new BufferedReader(new InputStreamReader(file.getContents())));
			} else {
			  this.fId = UUID.randomUUID().toString();
			}
		} catch (CoreException except) {
			CppLaunchCore.log(except.getStatus());
			// We could not load the file content. Let's just consider an empty configuration file then.
			this.fId = UUID.randomUUID().toString();
		}
  }
  
  X10PlatformConf(final IServiceProvider serviceProvider) {
    final IResourceManagerConfiguration rmConf = (IResourceManagerConfiguration) serviceProvider;
    this.fConnectionConf = new ConnectionConfiguration(rmConf);
    this.fCppCompilationConf = new CppCompilationConfiguration();
    this.fCommInterfaceFact = new CommInterfaceFactory(rmConf);
    initLocalCppCompilationCommands();
    initLocalX10DistribLocation();
    this.fDescription = rmConf.getDescription();
    this.fName = rmConf.getName();
    this.fId = UUID.randomUUID().toString();
  }
  
  // --- Interface methods implementation
  
  public final IX10PlatformConfWorkCopy createWorkingCopy() {
    return new X10PlatformConfWorkCopy(this);
  }
  
  public final ICommunicationInterfaceConf getCommunicationInterfaceConf() {
    return this.fCommInterfaceFact.getCurrentCommunicationInterface();
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
      return onlyCompilation ? true : this.fCommInterfaceFact.getCurrentCommunicationInterface().isComplete();
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
    connectionTag.putBoolean(USE_PORT_FORWARDING_TAG, this.fConnectionConf.fUsePortForwarding);
    if (! this.fConnectionConf.fIsLocal) {
      if (hasData(this.fConnectionConf.fHostName)) {
        connectionTag.createChild(HOSTNAME_TAG).putTextData(this.fConnectionConf.fHostName);
      }
      connectionTag.createChild(PORT_TAG).putTextData(String.valueOf(this.fConnectionConf.fPort));
      connectionTag.createChild(LOCAL_ADDRESS_TAG).putTextData(this.fConnectionConf.fLocalAddress);
      connectionTag.createChild(TIMEOUT_TAG).putTextData(String.valueOf(this.fConnectionConf.fTimeout));
    }
    
    final IMemento communicationInterfaceTag = platformTag.createChild(COMMUNICATION_INTERFACE_TAG);
    final ICommunicationInterfaceConf commInterfaceConf = this.fCommInterfaceFact.getCurrentCommunicationInterface();
    if (hasData(commInterfaceConf.getServiceTypeId())) {
      communicationInterfaceTag.createChild(SERVICE_TYPE_ID_TAG).putTextData(commInterfaceConf.getServiceTypeId());
    }
    if (hasData(commInterfaceConf.getServiceModeId())) {
      communicationInterfaceTag.createChild(SERVICE_MODE_ID_TAG).putTextData(commInterfaceConf.getServiceModeId());
    }
    if (commInterfaceConf instanceof OpenMPIInterfaceConf) {
      save(communicationInterfaceTag, (OpenMPIInterfaceConf) commInterfaceConf);
    } else if (commInterfaceConf instanceof MPICH2InterfaceConf) {
      save(communicationInterfaceTag, (MessagePassingInterfaceConf) commInterfaceConf);
    } else if (commInterfaceConf instanceof ParallelEnvironmentConf) {
      save(communicationInterfaceTag, (ParallelEnvironmentConf) commInterfaceConf);
    } else if (commInterfaceConf instanceof LoadLevelerConf) {
      save(communicationInterfaceTag, (LoadLevelerConf) commInterfaceConf);
    }
    
    final IMemento cppCompilationTag = platformTag.createChild(CPP_COMPILATION_TAG);
    if (this.fCppCompilationConf.fTargetOS != null) {
      cppCompilationTag.createChild(TARGET_OS_TAG).putTextData(this.fCppCompilationConf.fTargetOS.name());
    }
    if (this.fCppCompilationConf.fBitsArchitecture != null) {
      cppCompilationTag.createChild(BITS_ARCH_TAG).putTextData(this.fCppCompilationConf.fBitsArchitecture.name());
    }
    if (this.fCppCompilationConf.fArchitecture != null) {
      cppCompilationTag.createChild(ARCH_TAG).putTextData(this.fCppCompilationConf.fArchitecture.name());
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
    if (! this.fConnectionConf.fIsLocal) {
      if (hasData(this.fCppCompilationConf.fX10DistLoc)) {
        cppCompilationTag.createChild(X10_DIST_LOC_TAG).putTextData(this.fCppCompilationConf.fX10DistLoc);
      }
      if (hasData(this.fCppCompilationConf.fPGASLoc)) {
        cppCompilationTag.createChild(PGAS_LOC_TAG).putTextData(this.fCppCompilationConf.fPGASLoc);
      }
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
      return Arrays.equals(new Object[] { this.fConnectionConf, this.fCommInterfaceFact, this.fCppCompilationConf },
                           new Object[] { rhsObj.fConnectionConf, rhsObj.fCommInterfaceFact, rhsObj.fCppCompilationConf });
    } else {
      return false;
    }
  }
  
  public final int hashCode() {
    return CodingUtils.generateHashCode(565, this.fName, this.fDescription, this.fConnectionConf, this.fCommInterfaceFact,
                                        this.fCppCompilationConf);
  }
  
  public final String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Id: ").append(this.fId).append("\nName: ").append(this.fName) //$NON-NLS-1$ //$NON-NLS-2$
      .append("\nDescription: ").append(this.fDescription) //$NON-NLS-1$
      .append('\n').append(this.fConnectionConf).append('\n').append(this.fCommInterfaceFact)
      .append('\n').append(this.fCppCompilationConf);
    return sb.toString();
  }
  
  // --- Code for descendants
  
  protected final boolean hasData(final String var) {
    return (var != null) && (var.trim().length() > 0);
  }
  
  protected final void initLocalCppCompilationCommands() {
    this.fCppCompilationConf.fTargetOS = EnumUtils.getLocalOS();
    final boolean is64Arch = is64Arch();
    this.fCppCompilationConf.fBitsArchitecture = is64Arch ? EBitsArchitecture.E64Arch : EBitsArchitecture.E32Arch;
    this.fCppCompilationConf.fArchitecture = EArchitecture.x86;
    
    final String serviceTypeId = this.fCommInterfaceFact.getCurrentCommunicationInterface().fServiceTypeId; 
    final ETransport transport = PlatformConfUtils.getTransport(serviceTypeId, this.fCppCompilationConf.fTargetOS);
    
    final IDefaultCPPCommands defaultCPPCommands;
    switch (this.fCppCompilationConf.fTargetOS) {
      case AIX:
        defaultCPPCommands = DefaultCPPCommandsFactory.createAixCommands(is64Arch, this.fCppCompilationConf.fArchitecture,
                                                                         transport);
        break;
      case LINUX:
        defaultCPPCommands = DefaultCPPCommandsFactory.createLinuxCommands(is64Arch, this.fCppCompilationConf.fArchitecture,
                                                                           transport);
        break;
      case MAC:
        defaultCPPCommands = DefaultCPPCommandsFactory.createMacCommands(is64Arch, this.fCppCompilationConf.fArchitecture,
                                                                         transport);
        break;
      case WINDOWS:
        defaultCPPCommands = DefaultCPPCommandsFactory.createCygwinCommands(is64Arch, this.fCppCompilationConf.fArchitecture,
                                                                            transport);
        break;
      default:
        defaultCPPCommands = DefaultCPPCommandsFactory.createUnkownUnixCommands(is64Arch, 
                                                                                this.fCppCompilationConf.fArchitecture,
                                                                                transport);
    }
    this.fCppCompilationConf.fCompiler = defaultCPPCommands.getCompiler();
    this.fCppCompilationConf.fCompilingOpts = defaultCPPCommands.getCompilerOptions();
    this.fCppCompilationConf.fArchiver = defaultCPPCommands.getArchiver();
    this.fCppCompilationConf.fArchivingOpts = defaultCPPCommands.getArchivingOpts();
    this.fCppCompilationConf.fLinker = defaultCPPCommands.getLinker();
    this.fCppCompilationConf.fLinkingOpts = defaultCPPCommands.getLinkingOptions();
    this.fCppCompilationConf.fLinkingLibs = defaultCPPCommands.getLinkingLibraries();
    this.fCppCompilationConf.fBitsArchitecture = (is64Arch) ? EBitsArchitecture.E64Arch : EBitsArchitecture.E32Arch;
  }

  protected final void initLocalX10DistribLocation() {
    final Bundle x10DistBundle = Platform.getBundle(Constants.X10_DIST_PLUGIN_ID);
    final URL url = x10DistBundle.getResource("include"); //$NON-NLS-1$
    try {
      this.fCppCompilationConf.fX10DistLoc = new File(FileLocator.resolve(url).getFile()).getParent();
      this.fCppCompilationConf.fPGASLoc = this.fCppCompilationConf.fX10DistLoc;
    } catch (IOException except) {
      // Let's forget.
    }
  }
  
  protected final boolean is64Arch() {
    return false; //TODO
  }
  
  // --- Private code
  
  protected X10PlatformConf(final X10PlatformConf source) {
    this.fName = source.fName;
    this.fDescription = source.fDescription;
    this.fId = source.fId;
    this.fConnectionConf = new ConnectionConfiguration(source.fConnectionConf);
    this.fCommInterfaceFact = new CommInterfaceFactory(source.fCommInterfaceFact);
    this.fCppCompilationConf = new CppCompilationConfiguration(source.fCppCompilationConf);
  }
  
  private String getTextDataValue(final IMemento memento, final String tag) {
    final IMemento child = memento.getChild(tag);
    return (child == null) ? null : child.getTextData();
  }
  
  private boolean getBooleanValue(final IMemento memento, final String tag, final boolean defaultValue) {
    final Boolean value = memento.getBoolean(tag);
    return (value == null) ? defaultValue : value;
  }
  
  private int getIntegerValue(final IMemento memento, final String tag, final int defaultValue) {
    final Integer value = memento.getInteger(tag);
    return (value == null) ? defaultValue : value;
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
  
  private boolean isNonEmpty(final IFile file) throws CoreException {
		if (! file.isSynchronized(IResource.DEPTH_ZERO)) {
			file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		}
  	if (file.exists()) {
  		try {
        return (file.getContents().read() != -1);
      } catch (IOException except) {
        throw new CoreException(new Status(IStatus.ERROR, CppLaunchCore.PLUGIN_ID, LaunchMessages.XPC_InputStreamReadingError, 
                                           except));
      }
  	} else {
  		return false;
  	}
  }
  
  private void load(final IFile file, final Reader reader) throws WorkbenchException {
    final XMLMemento platformMemento = XMLMemento.createReadRoot(reader);
    
    this.fId = getTextDataValue(platformMemento, ID_TAG);
    this.fName = getTextDataValue(platformMemento, NAME_TAG);
    this.fDescription = getTextDataValue(platformMemento, DESCRIPTION_TAG);
    
    final IMemento connectionMemento = platformMemento.getChild(CONNECTION_TAG);
    this.fConnectionConf.fIsLocal = getBooleanValue(connectionMemento, IS_LOCAL_ATTR, true);
    if (! this.fConnectionConf.fIsLocal) {
      this.fConnectionConf.fHostName = getTextDataValue(connectionMemento, HOSTNAME_TAG);
      this.fConnectionConf.fPort = Integer.parseInt(getTextDataValue(connectionMemento, PORT_TAG));
      this.fConnectionConf.fUsePortForwarding = getBooleanValue(connectionMemento, USE_PORT_FORWARDING_TAG, false);
      final String timeout = getTextDataValue(connectionMemento, USE_PORT_FORWARDING_TAG);
      if (timeout != null) {
        this.fConnectionConf.fTimeout = Integer.parseInt(timeout);
      }
      this.fConnectionConf.fLocalAddress = getTextDataValue(connectionMemento, LOCAL_ADDRESS_TAG);
    }
    this.fConnectionConf.initTargetElement();
    
    final IMemento ciMemento = platformMemento.getChild(COMMUNICATION_INTERFACE_TAG);
    final String ciType = getTextDataValue(ciMemento, SERVICE_TYPE_ID_TAG);
    final AbstractCommunicationInterfaceConfiguration ciConf = this.fCommInterfaceFact.getOrCreate(ciType);
    if (ciConf == null) {
      CoreResourceUtils.addPlatformConfMarker(file, LaunchMessages.XPC_CITypeNotSupported, 
                                              IMarker.SEVERITY_ERROR, IMarker.PRIORITY_NORMAL);
    } else {
      this.fCommInterfaceFact.defineCurrentCommInterfaceType(ciType);
      ciConf.fServiceModeId = getTextDataValue(ciMemento, SERVICE_MODE_ID_TAG);
      ciConf.fServiceTypeId = ciType;
      if (ciConf instanceof OpenMPIInterfaceConf) {
        load(ciMemento, (OpenMPIInterfaceConf) ciConf);
      } else if (ciConf instanceof MPICH2InterfaceConf) {
        load(ciMemento, (MessagePassingInterfaceConf) ciConf);
      } else if (ciConf instanceof ParallelEnvironmentConf) {
        load(ciMemento, (ParallelEnvironmentConf) ciConf);
      } else if (ciConf instanceof LoadLevelerConf) {
        load(ciMemento, (LoadLevelerConf) ciConf);
      }
    }
    
    final IMemento cppCmdsMemento = platformMemento.getChild(CPP_COMPILATION_TAG);
    final IMemento targetOSMemento = cppCmdsMemento.getChild(TARGET_OS_TAG);
    this.fCppCompilationConf.fTargetOS = (targetOSMemento == null) ? null : ETargetOS.valueOf(targetOSMemento.getTextData());
    final IMemento bitsArchMmt = cppCmdsMemento.getChild(BITS_ARCH_TAG);
    this.fCppCompilationConf.fBitsArchitecture = (bitsArchMmt == null) ? EBitsArchitecture.E32Arch : 
                                                                         EBitsArchitecture.valueOf(bitsArchMmt.getTextData());
    final IMemento archMemento = cppCmdsMemento.getChild(ARCH_TAG);
    this.fCppCompilationConf.fArchitecture = (archMemento == null) ? EArchitecture.x86 : 
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
  
  private void load(final IMemento ciMemento, final OpenMPIInterfaceConf ciConf) {
    load(ciMemento, (MessagePassingInterfaceConf) ciConf);
    
    final IMemento mpiMemento = ciMemento.getChild(OPEN_MPI_VERSION_TAG);
    ciConf.fOpenMPIVersion = (mpiMemento == null) ? null : EOpenMPIVersion.valueOf(mpiMemento.getTextData());   
  }
  
  private void load(final IMemento ciMemento, final MessagePassingInterfaceConf ciConf) {
    ciConf.fDefaultToolCmds = getBooleanValue(ciMemento, DEFAULT_TOOL_CMDS_ATTR, true);
    ciConf.fLaunchCmd = getTextDataValue(ciMemento, LAUNCH_CMD_TAG);
    ciConf.fDebugCmd = getTextDataValue(ciMemento, DEBUG_CMD_TAG);
    ciConf.fDiscoverCmd = getTextDataValue(ciMemento, DISCOVER_CMD_TAG);
    ciConf.fMonitorCmd = getTextDataValue(ciMemento, MONITOR_CMD_TAG);
    final IMemento ciPeriodMemento = ciMemento.getChild(MONITOR_PERIOD_TAG);
    ciConf.fMonitoringPeriod = (ciPeriodMemento == null) ? 0 : Integer.valueOf(ciPeriodMemento.getTextData());
    ciConf.fDefaultIntallLocation = getBooleanValue(ciMemento, DEFAULT_INSTALLATION_ATTR, true);
    ciConf.fInstallLocation = getTextDataValue(ciMemento, INSTALL_LOCATION_TAG);
  }
  
  private void load(final IMemento ciMemento, final IBMCommunicationInterfaceConf ciConf) {
    ciConf.fAlternateLibPath = getTextDataValue(ciMemento, ALTERNATE_LL_LIB_PATH);
    final IMemento clusterMM = ciMemento.getChild(LL_CLUSTER_MODE);
    ciConf.fClusterMode = (clusterMM == null) ? EClusterMode.DEFAULT : EClusterMode.valueOf(clusterMM.getTextData());
    ciConf.fJobPolling = getIntegerValue(ciMemento, JOB_POLLING, 0);
    ciConf.fNodePollingMin = getIntegerValue(ciMemento, NODE_POLLING_MIN, 0);
    ciConf.fNodePollingMax = getIntegerValue(ciMemento, NODE_POLLING_MAX, 0);
    ciConf.fProxyServerPath = getTextDataValue(ciMemento, PROXY_SERVER_PATH);
    ciConf.fLaunchProxyManually = getBooleanValue(ciMemento, LAUNCH_PROXY_MANUALLY, false);
    ciConf.fSuspendProxyAtStartup = getBooleanValue(ciMemento, SUSPEND_PROXY_AT_STARTUP, false);
  }
  
  private void load(final IMemento ciMemento, final ParallelEnvironmentConf ciConf) {
    load(ciMemento, (IBMCommunicationInterfaceConf) ciConf);
    
    final IMemento dbgLvl = ciMemento.getChild(PE_DEBUG_LEVEL);
    ciConf.fCIDebugLevel = (dbgLvl == null) ? null : ECIDebugLevel.valueOf(dbgLvl.getTextData());
    ciConf.fRunMiniProxy = getBooleanValue(ciMemento, PE_RUN_MINI_PROXY, false);
    ciConf.fUseLoadLeveler = getBooleanValue(ciMemento, PE_USE_LOADLEVELER, false);
  }
  
  private void load(final IMemento ciMemento, final LoadLevelerConf ciConf) {
    load(ciMemento, (IBMCommunicationInterfaceConf) ciConf);
    
    ciConf.fProxyMsgOpts = getIntegerValue(ciMemento, LL_PROXY_MSG_OPTS, 0);
    ciConf.fTemplateFilePath = getTextDataValue(ciMemento, LL_TEMPLATE_FILE_PATH);
    final IMemento tmpOpt = ciMemento.getChild(LL_TEMPLATE_OPT);
    ciConf.fTemplateOpt = (tmpOpt == null) ? null : ELLTemplateOpt.valueOf(tmpOpt.getTextData());
  }
  
  private void save(final IMemento communicationInterfaceTag, final MessagePassingInterfaceConf ciConf) {
    communicationInterfaceTag.putBoolean(DEFAULT_TOOL_CMDS_ATTR, ciConf.fDefaultToolCmds);
    communicationInterfaceTag.putBoolean(DEFAULT_INSTALLATION_ATTR, ciConf.fDefaultIntallLocation);
    if (hasData(ciConf.fLaunchCmd)) {
      communicationInterfaceTag.createChild(LAUNCH_CMD_TAG).putTextData(ciConf.fLaunchCmd);
    }
    if (hasData(ciConf.fDebugCmd)) {
      communicationInterfaceTag.createChild(DEBUG_CMD_TAG).putTextData(ciConf.fDebugCmd);
    }
    if (hasData(ciConf.fDiscoverCmd)) {
      communicationInterfaceTag.createChild(DISCOVER_CMD_TAG).putTextData(ciConf.fDiscoverCmd);
    }
    if (hasData(ciConf.fMonitorCmd)) {
      communicationInterfaceTag.createChild(MONITOR_CMD_TAG).putTextData(ciConf.fMonitorCmd);
    }
    final String period = String.valueOf(ciConf.fMonitoringPeriod);
    communicationInterfaceTag.createChild(MONITOR_PERIOD_TAG).putTextData(period);
    if (hasData(ciConf.fInstallLocation)) {
      communicationInterfaceTag.createChild(INSTALL_LOCATION_TAG).putTextData(ciConf.fInstallLocation);
    }
  }
  
  private void save(final IMemento communicationInterfaceTag, final OpenMPIInterfaceConf ciConf) {
    save(communicationInterfaceTag, (MessagePassingInterfaceConf) ciConf);
    
    final EOpenMPIVersion mpiVersion;
    if (ciConf.fOpenMPIVersion == null) {
      mpiVersion = EOpenMPIVersion.EAutoDetect;
    } else {
      mpiVersion = ciConf.fOpenMPIVersion;
    }
    communicationInterfaceTag.createChild(OPEN_MPI_VERSION_TAG).putTextData(mpiVersion.name());
  }
  
  private void save(final IMemento ciMemento, final IBMCommunicationInterfaceConf ciConf, 
                    final boolean shouldSaveAlternateInfo) {
    if (hasData(ciConf.fProxyServerPath)) {
      ciMemento.createChild(PROXY_SERVER_PATH).putTextData(ciConf.fProxyServerPath);
    }
    ciMemento.putBoolean(LAUNCH_PROXY_MANUALLY, ciConf.fLaunchProxyManually);
    
    if (shouldSaveAlternateInfo) {
      if (hasData(ciConf.fAlternateLibPath)) {
        ciMemento.createChild(ALTERNATE_LL_LIB_PATH).putTextData(ciConf.fAlternateLibPath);
      }
      if (ciConf.fClusterMode != null) {
        ciMemento.createChild(LL_CLUSTER_MODE).putTextData(ciConf.fClusterMode.name());
      }
      ciMemento.putInteger(JOB_POLLING, ciConf.fJobPolling);
      ciMemento.putInteger(NODE_POLLING_MIN, ciConf.fNodePollingMin);
      ciMemento.putInteger(NODE_POLLING_MAX, ciConf.fNodePollingMax);
      ciMemento.putBoolean(SUSPEND_PROXY_AT_STARTUP, ciConf.fSuspendProxyAtStartup);
    }
  }
  
  private void save(final IMemento ciMemento, final ParallelEnvironmentConf ciConf) {
    save(ciMemento, ciConf, ciConf.fUseLoadLeveler);
   
    if (ciConf.fCIDebugLevel != null) {
      ciMemento.createChild(PE_DEBUG_LEVEL).putTextData(ciConf.fCIDebugLevel.name());
    }
    ciMemento.putBoolean(PE_RUN_MINI_PROXY, ciConf.fRunMiniProxy);
    ciMemento.putBoolean(PE_USE_LOADLEVELER, ciConf.fUseLoadLeveler);
  }

  private void save(final IMemento ciMemento, final LoadLevelerConf ciConf) {
    save(ciMemento, ciConf, true);
    
    ciMemento.putInteger(LL_PROXY_MSG_OPTS, ciConf.fProxyMsgOpts);
    if (hasData(ciConf.fTemplateFilePath)) {
      ciMemento.createChild(LL_TEMPLATE_FILE_PATH).putTextData(ciConf.fTemplateFilePath);
    }
    if (ciConf.fTemplateOpt != null) {
      ciMemento.createChild(LL_TEMPLATE_OPT).putTextData(ciConf.fTemplateOpt.name());
    }
  }
  
  // --- Fields
  
  String fName;
  
  String fDescription;
  
  String fId;
    
  final ConnectionConfiguration fConnectionConf;
  
  final CppCompilationConfiguration fCppCompilationConf;
  
  final CommInterfaceFactory fCommInterfaceFact;
  
  
  private static final String ID_TAG = "id"; //$NON-NLS-1$
    
  private static final String PLATFORM_TAG = "platform"; //$NON-NLS-1$
  
  private static final String NAME_TAG = "name"; //$NON-NLS-1$
  
  private static final String DESCRIPTION_TAG = "description"; //$NON-NLS-1$
  
  
  private static final String IS_LOCAL_ATTR = "is-local"; //$NON-NLS-1$
  
  private static final String CONNECTION_TAG = "connection"; //$NON-NLS-1$
  
  private static final String HOSTNAME_TAG = "hostname"; //$NON-NLS-1$
  
  private static final String PORT_TAG = "port"; //$NON-NLS-1$
  
  private static final String USE_PORT_FORWARDING_TAG = "use-port-forwarding"; //$NON-NLS-1$
  
  private static final String LOCAL_ADDRESS_TAG = "local-address"; //$NON-NLS-1$
  
  private static final String TIMEOUT_TAG = "timeout"; //$NON-NLS-1$
    
  
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
  
  private static final String ALTERNATE_LL_LIB_PATH = "alternate-ll-lib-path"; //$NON-NLS-1$
  
  private static final String LL_CLUSTER_MODE = "ll-cluster-mode"; //$NON-NLS-1$
  
  private static final String JOB_POLLING = "job-polling"; //$NON-NLS-1$
  
  private static final String NODE_POLLING_MIN = "node-polling-min"; //$NON-NLS-1$
  
  private static final String NODE_POLLING_MAX = "node-mode-max"; //$NON-NLS-1$
  
  private static final String PROXY_SERVER_PATH = "proxy-server-path"; //$NON-NLS-1$
  
  private static final String LAUNCH_PROXY_MANUALLY = "launch-proxy-manually"; //$NON-NLS-1$
  
  private static final String PE_DEBUG_LEVEL = "pe-debug-level"; //$NON-NLS-1$
  
  private static final String PE_RUN_MINI_PROXY = "pe-run-mini-proxy"; //$NON-NLS-1$
  
  private static final String SUSPEND_PROXY_AT_STARTUP = "pe-suspend-proxy"; //$NON-NLS-1$
  
  private static final String PE_USE_LOADLEVELER = "pe-user-loadleveler"; //$NON-NLS-1$
  
  private static final String LL_PROXY_MSG_OPTS = "ll-proxy-msg-opts"; //$NON-NLS-1$
  
  private static final String LL_TEMPLATE_FILE_PATH = "ll-template-file"; //$NON-NLS-1$
  
  private static final String LL_TEMPLATE_OPT = "ll-template-opt"; //$NON-NLS-1$


  private static final String CPP_COMPILATION_TAG = "cpp-compilation"; //$NON-NLS-1$
  
  private static final String TARGET_OS_TAG = "target-os"; //$NON-NLS-1$
  
  private static final String ARCH_TAG = "arch"; //$NON-NLS-1$
  
  private static final String BITS_ARCH_TAG = "bits-arch"; //$NON-NLS-1$
    
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
