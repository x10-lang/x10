/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.platform_conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * Manages persistence of X10 platform information and other services for {@link IX10PlatformConfiguration}.
 * 
 * @author egeay
 */
public final class X10PlatformsManager {
  
  // --- Public services
  
  /**
   * Creates a new X10 platform configuration with the new name provided and the rest of the configuration taken from the
   * one transmitted.
   * 
   * @param configuration The original configuration to consider for creating the new configuration.
   * @param name The name of the new configuration.
   * @return A non-null instance of {@link IX10PlatformConfiguration}.
   */
  public static IX10PlatformConfiguration createNewConfigurationName(final IX10PlatformConfiguration configuration,
                                                                     final String name) {
    return new StoredPlatformConf(name, configuration.getArchitecture(), configuration.getResourceManagerId(), 
                                  configuration.getTargetOS(), configuration.getX10DistribLocation(), 
                                  configuration.getPGASLocation(), configuration.getX10HeadersLocations(), 
                                  configuration.getX10LibsLocations(), configuration.getCompiler(), 
                                  configuration.getCompilerOpts(), configuration.getArchiver(), 
                                  configuration.getArchivingOpts(), configuration.getLinker(), 
                                  configuration.getLinkingOpts(), configuration.getLinkingLibs(), 
                                  configuration.isCplusPlus(), configuration.isLocal(), 
                                  configuration.getValidationStatus(), configuration.getValidationErrorMessage());
  }
  
  /**
   * Creates a new X10 platform configuration with the new resource manager id provided and the rest of the 
   * configuration taken from the one transmitted.
   * 
   * @param configuration The original configuration to consider for creating the new configuration.
   * @param newRMId The new resource manager id.
   * @return A non-null instance of {@link IX10PlatformConfiguration}.
   */
  public static IX10PlatformConfiguration createNewConfigurationRMId(final IX10PlatformConfiguration configuration,
                                                                     final String newRMId) {
    return new StoredPlatformConf(configuration.getName(), configuration.getArchitecture(), newRMId, 
                                  configuration.getTargetOS(), configuration.getX10DistribLocation(), 
                                  configuration.getPGASLocation(), configuration.getX10HeadersLocations(), 
                                  configuration.getX10LibsLocations(), configuration.getCompiler(), 
                                  configuration.getCompilerOpts(), configuration.getArchiver(), 
                                  configuration.getArchivingOpts(), configuration.getLinker(), 
                                  configuration.getLinkingOpts(), configuration.getLinkingLibs(), 
                                  configuration.isCplusPlus(), configuration.isLocal(), 
                                  configuration.getValidationStatus(), configuration.getValidationErrorMessage());
  }
  
  /**
   * Loads the X10 platform configurations from the workspace metadata.
   * 
   * @return A non-null map, but possibly empty.
   * @throws IOException Occurs if we could not read the internal file for some particular reason.
   * @throws WorkbenchException Occurs if we could not read properly the XML data structure within the file.
   */
  public static Map<String, IX10PlatformConfiguration> loadPlatformsConfiguration() throws IOException, WorkbenchException {
    final File file = LaunchCore.getInstance().getStateLocation().append(X10_PLATFORMS_FILE).toFile();
    final Map<String, IX10PlatformConfiguration> platforms = new HashMap<String, IX10PlatformConfiguration>();
    if (file.exists()) {
      final XMLMemento rootMemento = XMLMemento.createReadRoot(new BufferedReader(new FileReader(file)));
      for (final IMemento platformMemento : rootMemento.getChildren(PLATFORM_TAG)) {
        final String name = platformMemento.getString(NAME_TAG);
        final EArchitecture architecture = X10BuilderUtils.getArchitecture(platformMemento.getString(ARCH_TAG));
        final String x10DistLoc = platformMemento.getString(X10_DIST_LOC_TAG);
        final String pgasLoc = platformMemento.getString(PGAS_LOC_TAG);
        final String[] x10HeadersLocs = platformMemento.getString(X10_DIST_HEADERS_LOC_TAG).split(PATH_SEP);
        final String[] x10LibsLocs = platformMemento.getString(X10_DIST_LIBS_LOC_TAG).split(PATH_SEP);
        final String compiler = platformMemento.getString(COMPILER_TAG);
        final String compilerOpts = platformMemento.getString(COMPILER_OPTS_TAG);
        final String archiver = platformMemento.getString(ARCHIVER_TAG);
        final String archivingOpts = platformMemento.getString(ARCHIVING_OPTS_TAG);
        final String linker = platformMemento.getString(LINKER_TAG);
        final String linkingOpts = platformMemento.getString(LINKING_OPTS_TAG);
        final String linkingLibs = platformMemento.getString(LINKING_LIBS_TAG);
        final String resManagerId = platformMemento.getString(RES_MANAGER_ID_TAG);
        final String osName = platformMemento.getString(TARGET_OS_TAG);
        final ETargetOS targetOS = (osName == null) ? null : ETargetOS.valueOf(osName);
        final boolean isCplusPlus = platformMemento.getBoolean(IS_CPLUS_PLUS_TAG);
        final boolean isLocal = platformMemento.getBoolean(IS_LOCAL_TAG);
        final String validationTag = platformMemento.getString(VALIDATION_STATUS_TAG);
        final EValidStatus validStatus = (validationTag == null) ? EValidStatus.UNKNOWN : EValidStatus.valueOf(validationTag);
        final String validationErrorMsg = platformMemento.getString(VALIDATION_ERR_MSG_TAG);
        
        platforms.put(name, new StoredPlatformConf(name, architecture, resManagerId, targetOS, x10DistLoc, pgasLoc, 
                                                   x10HeadersLocs, x10LibsLocs, compiler, compilerOpts, archiver, 
                                                   archivingOpts, linker, linkingOpts, linkingLibs, isCplusPlus, isLocal,
                                                   validStatus, validationErrorMsg));
      }
    }
    return platforms;
  }
  
  /**
   * Saves the X10 platform configurations within the workspace metadata.
   * 
   * @param platforms The different platform configurations to save.
   * @throws IOException Occurs if for some particular reason we could not save the different platform configurations within
   * the file.
   */
  public static void savePlatformsConfiguration(final Map<String, IX10PlatformConfiguration> platforms) throws IOException {
    final File file = LaunchCore.getInstance().getStateLocation().append(X10_PLATFORMS_FILE).toFile();
    final Writer writer = new BufferedWriter(new FileWriter(file));
    final XMLMemento rootMemento = XMLMemento.createWriteRoot(PLATFORMS_TAG);
    for (final IX10PlatformConfiguration platformConf : platforms.values()) {
      final IMemento platformMemento = rootMemento.createChild(PLATFORM_TAG);
      platformMemento.putString(NAME_TAG, platformConf.getName());
      platformMemento.putString(X10_DIST_LOC_TAG, platformConf.getX10DistribLocation());
      platformMemento.putString(PGAS_LOC_TAG, platformConf.getPGASLocation());
      final StringBuilder headersBuilder = new StringBuilder();
      int i = 0;
      for (final String headerLoc : platformConf.getX10HeadersLocations()) {
        if (i == 0) {
          i = 1;
        } else {
          headersBuilder.append(PATH_SEP);
        }
        headersBuilder.append(headerLoc);
      }
      platformMemento.putString(X10_DIST_HEADERS_LOC_TAG, headersBuilder.toString());
      final StringBuilder libsBuilder = new StringBuilder();
      i = 0;
      for (final String libLoc : platformConf.getX10LibsLocations()) {
        if (i == 0) {
          i = 1;
        } else {
          libsBuilder.append(PATH_SEP);
        }
        libsBuilder.append(libLoc);
      }
      platformMemento.putString(X10_DIST_LIBS_LOC_TAG, libsBuilder.toString());
      platformMemento.putString(COMPILER_TAG, platformConf.getCompiler());
      platformMemento.putString(COMPILER_OPTS_TAG, platformConf.getCompilerOpts());
      if (platformConf.getArchiver() != null) {
        platformMemento.putString(ARCHIVER_TAG, platformConf.getArchiver());
        platformMemento.putString(ARCHIVING_OPTS_TAG, platformConf.getArchivingOpts());
      }
      if (platformConf.getLinker() != null) {
        platformMemento.putString(LINKER_TAG, platformConf.getLinker());
        platformMemento.putString(LINKING_OPTS_TAG, platformConf.getLinkingOpts());
        platformMemento.putString(LINKING_LIBS_TAG, platformConf.getLinkingLibs());
      }
      if (platformConf.getResourceManagerId() != null) {
        platformMemento.putString(RES_MANAGER_ID_TAG, platformConf.getResourceManagerId());
      }
      platformMemento.putString(TARGET_OS_TAG, platformConf.getTargetOS().name());
      platformMemento.putBoolean(IS_CPLUS_PLUS_TAG, platformConf.isCplusPlus());
      platformMemento.putBoolean(IS_LOCAL_TAG, platformConf.isLocal());
      platformMemento.putString(ARCH_TAG, platformConf.getArchitecture().name());
      final EValidStatus validStatus = platformConf.getValidationStatus();
      platformMemento.putString(VALIDATION_STATUS_TAG, validStatus.name());
      if ((validStatus == EValidStatus.ERROR) || (validStatus == EValidStatus.FAILURE)) {
        platformMemento.putString(VALIDATION_ERR_MSG_TAG, platformConf.getValidationErrorMessage());
      }
    }
    rootMemento.save(writer);
  }
  
  // --- Fields
  
  private static final String ARCH_TAG = "architecture"; //$NON-NLS-1$
  
  private static final String PLATFORMS_TAG = "platforms"; //$NON-NLS-1$
  
  private static final String PLATFORM_TAG = "platform"; //$NON-NLS-1$
  
  private static final String NAME_TAG = "name"; //$NON-NLS-1$
  
  private static final String X10_DIST_HEADERS_LOC_TAG = "x10-dist-headers-loc"; //$NON-NLS-1$
  
  private static final String X10_DIST_LIBS_LOC_TAG = "x10-dist-libs-loc"; //$NON-NLS-1$
  
  private static final String X10_DIST_LOC_TAG = "x10-dist-loc"; //$NON-NLS-1$
  
  private static final String PGAS_LOC_TAG = "pgas-loc"; //$NON-NLS-1$
  
  private static final String COMPILER_TAG = "compiler"; //$NON-NLS-1$
  
  private static final String COMPILER_OPTS_TAG = "compiler-opts"; //$NON-NLS-1$
  
  private static final String ARCHIVER_TAG = "archiver"; //$NON-NLS-1$
  
  private static final String ARCHIVING_OPTS_TAG = "archiving-opts"; //$NON-NLS-1$
  
  private static final String LINKER_TAG = "linker"; //$NON-NLS-1$
  
  private static final String LINKING_OPTS_TAG = "linking-opts"; //$NON-NLS-1$
  
  private static final String LINKING_LIBS_TAG = "linking-libs"; //$NON-NLS-1$
  
  private static final String RES_MANAGER_ID_TAG = "res-manager-id"; //$NON-NLS-1$
  
  private static final String TARGET_OS_TAG = "target-os"; //$NON-NLS-1$
  
  private static final String IS_CPLUS_PLUS_TAG = "is-cpp"; //$NON-NLS-1$
  
  private static final String IS_LOCAL_TAG = "is-local"; //$NON-NLS-1$
  
  private static final String VALIDATION_STATUS_TAG = "validation-status"; //$NON-NLS-1$
  
  private static final String VALIDATION_ERR_MSG_TAG = "validation-error"; //$NON-NLS-1$
  
  
  private static final String X10_PLATFORMS_FILE = "x10_platforms.xml"; //$NON-NLS-1$
  
  private static final String PATH_SEP = "#"; //$NON-NLS-1$

}
