/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.cpp.editors.EOpenMPIVersion;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;

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
   */
  public void initializeToDefaultValues();
  
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
  
  // --- IConnectionConf's setter methods
  
  public void setConnectionName(final String connectionName);
  
  public void setHostName(final String hostName);
  
  public void setIsLocalFlag(final boolean isLocal);
  
  public void setIsPasswordBasedAuthenticationFlag(final boolean isPasswordBasedAuth);
  
  public void setPassphrase(final String passphrase);
  
  public void setPassword(final String password);
  
  public void setPort(final int port);
  
  public void setPrivateKeyFile(final String privateKeyFile);
  
  public void setTargetElement(final ITargetElement targetElement);
  
  public void setUserName(final String userName);
      
  // --- ICommunicationInterface's setter methods
  
  public void setDebugCommand(final String debugCommand);
  
  public void setDefaultInstallLocationFlag(final boolean shouldTakeInstallLocation);
  
  public void setDefaultToolCommands(final boolean shouldTakeDefaultToolCommands);
  
  public void setDiscoverCommand(final String discoverCommand);
  
  public void setInstallLocation(final String installLocation);
  
  public void setLaunchCommand(final String launchCommand);
  
  public void setMonitorCommand(final String monitorCommand);
  
  public void setMonitorPeriod(final int monitorPeriod);
  
  public void setOpenMPIVersion(final EOpenMPIVersion openMPIVersion);
  
  public void setServiceModeId(final String seriveModeId);
  
  public void setServiceTypeId(final String serviceTypeId);
  
}
