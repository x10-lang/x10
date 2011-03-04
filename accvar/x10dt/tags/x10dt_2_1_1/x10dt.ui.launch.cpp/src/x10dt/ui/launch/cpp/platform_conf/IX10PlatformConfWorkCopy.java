/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.core.resources.IProject;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;

import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.EBitsArchitecture;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.cpp.editors.EOpenMPIVersion;

/**
 * Provides a mutable working copy of an X10 platform configuration.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public interface IX10PlatformConfWorkCopy extends IX10PlatformConf {
  
  /**
   * Reconciles the current working copy with the original platform configuration. After that call, the previous platform
   * configuration has now all the parameter values of the working copy.
   * 
   * <p>Note that also after such call {@link #isDirty()} will return false.
   */
  public void applyChanges();
  
  /**
   * Initializes the working copy with some default values in local mode.
   * 
   * @param project The project that contains the platform configuration.
   */
  public void initializeToDefaultValues(final IProject project);
  
  /**
   * Indicates if the working copy has been altered in comparison to the original configuration.
   * 
   * @return True if it has been modified, false otherwise.
   */
  public boolean isDirty();
  
  /**
   * Indicates if the models has been altered under the covers independently of any end-user interactions.
   * 
   * @return True if the model/configuration has been altered, false otherwise.
   */
  public boolean isStale();
  
  /**
   * Reconciles the model with the current working copy when the current configuration is stale.
   * 
   * <p>As a result after such call a call to {@link #isStale()} will return false.
   */
  public void refresh();
  
  // --- IX10PlatformConf's setter methods
  
  public void setDescription(final String description);
  
  public void setName(final String name);
  
  // --- ICppCompilationConf's setter methods
  
  public void setArchitecture(final EArchitecture architecture);
  
  public void setArchiver(final String archiver);
  
  public void setArchivingOpts(final String archivingOpts);
  
  public void setBitsArchitecture(final EBitsArchitecture bitsArch);
  
  public void setCompiler(final String compiler);
  
  public void setCompilingOpts(final String compilerOpts);
  
  public void setCppConfValidationErrorMessage(final String validationErrorMessage);
  
  public void setCppConfValidationStatus(final EValidationStatus validationStatus);
  
  public void setLinker(final String linker);
  
  public void setLinkingLibs(final String linkingLibs);
  
  public void setLinkingOpts(final String linkingOpts);
    
  public void setPGASLocation(final String pgasLocation);
  
  public void setRemoteOutputFolder(final String remoteOutputFolder);
  
  public void setTargetOS(final ETargetOS targetOS);
  
  public void setX10DistribLocation(final String x10DistribLoc);
  
  public void updateCompilationCommands();
  
  // --- IConnectionConf's setter methods
  
  public void setConnectionName(final String connectionName);
  
  public void setConnectionTimeout(final int timeout);
  
  public void setHostName(final String hostName);
  
  public void setIsLocalFlag(final boolean isLocal);
  
  public void setIsPasswordBasedAuthenticationFlag(final boolean isPasswordBasedAuth);
  
  public void setLocalAddress(final String localAddress);
  
  public void setPassphrase(final String passphrase);
  
  public void setPassword(final String password);
  
  public void setPort(final int port);
  
  public void setPrivateKeyFile(final String privateKeyFile);
  
  public void setShouldUsePortForwarding(final boolean usePortForwarding);
  
  public void setTargetElement(final ITargetElement targetElement);
  
  public void setUserName(final String userName);
      
  // --- ICommunicationInterface's setter methods
  
  /// Message Passing Interface
  
  public void setDebugCommand(final String ciType, final String debugCommand);
  
  public void setDefaultInstallLocationFlag(final String ciType, final boolean shouldTakeInstallLocation);
  
  public void setDefaultToolCommands(final String ciType, final boolean shouldTakeDefaultToolCommands);
  
  public void setDiscoverCommand(final String ciType, final String discoverCommand);
  
  public void setInstallLocation(final String ciType, final String installLocation);
  
  public void setLaunchCommand(final String ciType, final String launchCommand);
  
  public void setMonitorCommand(final String ciType, final String monitorCommand);
  
  public void setMonitorPeriod(final String ciType, final int monitorPeriod);
  
  public void setOpenMPIVersion(final EOpenMPIVersion openMPIVersion);
  
  public void setServiceModeId(final String ciType, final String serviceModeId);
  
  public void setServiceTypeId(final String ciType);
  
  /// IBM Parallel Environment & LoadLeveler
  
  public void setAlternateLibraryPath(final String ciType, final String libPath);
  
  public void setClusterMode(final String ciType, final EClusterMode mode);
  
  public void setJobPolling(final String ciType, final int jobPolling);
  
  public void setNodeMinPolling(final String ciType, final int nodeMinPolling);
  
  public void setNodeMaxPolling(final String ciType, final int nodeMaxPolling);
  
  public void setProxyServerPath(final String ciType, final String proxyServerPath);
  
  public void setLaunchProxyManuallyFlag(final String ciType, final boolean shouldLaunchProxyManually);
  
  public void setSuspendProxyAtStartupFlag(final String ciType, final boolean shouldSuspendProxy);
  
  /// IBM Parallel Environment
  
  public void setDebuggingLevel(final ECIDebugLevel debugLevel);
  
  public void setRunMiniProxyFlag(final boolean shouldRunMiniProxy);
  
  public void setUseLoadLeveler(final boolean shouldUseLoadLeveler);
  
  /// IBM LoadLeveler
  
  public void setProxyMessageOptions(final int options);
  
  public void setTemplateFilePath(final String templateFilePath);
  
  public void setTemplateOption(final ELLTemplateOpt templateOpt);
  
}
