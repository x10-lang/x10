/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.platform_conf;


final class ImmutablePlatformConf implements IX10PlatformConfiguration {
  
  ImmutablePlatformConf(final String name, final String resManagerId, final String targetOS, final String x10DistLoc, 
                        final String pgasLoc, final String compiler, final String compilerOpts, final String archiver, 
                        final String archivingOpts, final String linker, final String linkingOpts, 
                        final String linkingLibs, final boolean isCplusPlus, final boolean isLocal) {
    this.fName = name;
    this.fResManagerId = resManagerId;
    this.fTargetOS = targetOS;
    this.fX10DistribLoc = x10DistLoc;
    this.fPGASLoc = pgasLoc;
    this.fCompiler = compiler;
    this.fCompilerOpts = compilerOpts;
    this.fArchiver = archiver;
    this.fArchivingOpts = archivingOpts;
    this.fLinker = linker;
    this.fLinkingOpts = linkingOpts;
    this.fLinkingLibs = linkingLibs;
    this.fIsCplusPlus = isCplusPlus;
    this.fIsLocal = isLocal;
  }
  
  // --- Interface methods implementation
  
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

  public String getTargetOS() {
    return this.fTargetOS;
  }

  public String getX10DistribLocation() {
    return this.fX10DistribLoc;
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
  
  private String fArchiver;
  
  private String fArchivingOpts;
  
  private final String fCompiler;
  
  private final String fCompilerOpts;
  
  private final String fLinker;
  
  private final String fLinkingOpts;
  
  private final String fLinkingLibs;
  
  private final String fName;
  
  private final String fPGASLoc;
  
  private final String fX10DistribLoc;
  
  private final String fResManagerId;
  
  private final String fTargetOS;
  
  private final boolean fIsCplusPlus;
  
  private final boolean fIsLocal;

}
