/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.platform_conf;


final class StoredPlatformConf implements IX10PlatformConfiguration {
  
  StoredPlatformConf(final String name, final EArchitecture architecture, final String resManagerId, 
                     final ETargetOS targetOS, final String x10DistLoc, final String pgasLoc, final String[] x10HeadersLocs,
                     final String[] x10LibsLocs, final String compiler, final String compilerOpts, final String archiver,
                     final String archivingOpts, final String linker, final String linkingOpts, final String linkingLibs,
                     final boolean isCplusPlus, final boolean isLocal, final EValidStatus validStatus,
                     final String validationErrorMsg) {
    this.fName = name;
    this.fArchitecture = architecture;
    this.fResManagerId = resManagerId;
    this.fTargetOS = targetOS;
    this.fX10DistLoc = x10DistLoc;
    this.fPGASLoc = pgasLoc;
    this.fX10HeadersLocs = x10HeadersLocs;
    this.fX10LibsLocs = x10LibsLocs;
    this.fCompiler = compiler;
    this.fCompilerOpts = compilerOpts;
    this.fArchiver = archiver;
    this.fArchivingOpts = archivingOpts;
    this.fLinker = linker;
    this.fLinkingOpts = linkingOpts;
    this.fLinkingLibs = linkingLibs;
    this.fIsCplusPlus = isCplusPlus;
    this.fIsLocal = isLocal;
    this.fValidStatus = validStatus;
    this.fValidationErrorMsg = validationErrorMsg;
  }
  
  // --- Interface methods implementation
  
  public void defineStatus(final EValidStatus validStatus) {
    this.fValidStatus = validStatus;
  }
  
  public void defineValidationErrorStatus(final String errorMessage) {
    this.fValidationErrorMsg = errorMessage;
  }
  
  public EArchitecture getArchitecture() {
    return this.fArchitecture;
  }
  
  public String getArchiver() {
    return this.fArchiver;
  }
  
  public String getArchivingOpts() {
    return this.fArchivingOpts;
  }

  public String getCompiler() {
    return this.fCompiler;
  }

  public String getCompilerOpts() {
    return this.fCompilerOpts;
  }

  public String getLinker() {
    return this.fLinker;
  }

  public String getLinkingLibs() {
    return this.fLinkingLibs;
  }

  public String getLinkingOpts() {
    return this.fLinkingOpts;
  }

  public String getName() {
    return this.fName;
  }
  
  public String getPGASLocation() {
    return this.fPGASLoc;
  }
  
  public String getResourceManagerId() {
    return this.fResManagerId;
  }

  public ETargetOS getTargetOS() {
    return this.fTargetOS;
  }
  
  public String getValidationErrorMessage() {
    return this.fValidationErrorMsg;
  }
  
  public EValidStatus getValidationStatus() {
    return this.fValidStatus;
  }

  public String[] getX10HeadersLocations() {
    return this.fX10HeadersLocs;
  }

  public String getX10DistribLocation() {
    return this.fX10DistLoc;
  }
  
  public String[] getX10LibsLocations() {
    return this.fX10LibsLocs;
  }
  
  public boolean hasArchivingStep() {
    return this.fArchiver != null;
  }

  public boolean hasLinkingStep() {
    return this.fLinker != null;
  }
  
  public boolean isCplusPlus() {
    return this.fIsCplusPlus;
  }

  public boolean isLocal() {
    return this.fIsLocal;
  }
  
  // --- Fields
  
  private final EArchitecture fArchitecture;
  
  private final String fArchiver;
  
  private final String fArchivingOpts;
  
  private final String fCompiler;
  
  private final String fCompilerOpts;
  
  private final String fLinker;
  
  private final String fLinkingOpts;
  
  private final String fLinkingLibs;
  
  private final String fName;
  
  private final String fX10DistLoc;
  
  private final String fPGASLoc;
  
  private final String[] fX10HeadersLocs;
  
  private final String[] fX10LibsLocs;
  
  private final String fResManagerId;
  
  private final ETargetOS fTargetOS;
  
  private final boolean fIsCplusPlus;
  
  private final boolean fIsLocal;
    
  private EValidStatus fValidStatus;
  
  private String fValidationErrorMsg;

}
