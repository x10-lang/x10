/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidStatus;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;


final class X10PlatformConfiguration implements IX10PlatformConfiguration {
  
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
  
  // --- Internal services
  
  void setArchitecture(final EArchitecture architecture) {
    this.fArchitecture = architecture;
  }
  
  void setArchiver(final String archiver) {
    this.fArchiver = archiver;
  }
  
  void setArchivingOpts(final String archivingOpts) {
    this.fArchivingOpts = archivingOpts;
  }
  
  void setCompiler(final String compiler) {
    this.fCompiler = compiler;
  }
  
  void setCompilerOpts(final String compilerOpts) {
    this.fCompilerOpts = compilerOpts;
  }
  
  void setFlags(final boolean isCplusPlus, final boolean isLocal) {
    this.fIsCplusPlus = isCplusPlus;
    this.fIsLocal = isLocal;
  }
  
  void setLinker(final String linker) {
    this.fLinker = linker;
  }
  
  void setLinkingLibs(final String linkingLibs) {
    this.fLinkingLibs = linkingLibs;
  }
  
  void setLinkingOpts(final String linkingOpts) {
    this.fLinkingOpts = linkingOpts;
  }
  
  void setName(final String name) {
    this.fName = name;
  }
  
  void setPGASLoc(final String location) {
    this.fPGASLoc = location;
  }
  
  void setResManagerId(final String resManagerId) {
    this.fResManagerId = resManagerId;
  }
  
  void setTargetOS(final ETargetOS targetOS) {
    this.fTargetOS = targetOS;
  }
  
  void setX10HeadersLoc(final String[] locations) {
    this.fX10HeadersLocs = locations;
  }
  
  void setX10DistLoc(final String location) {
    this.fX10DistLoc = location;
  }
  
  void setX10LibsLoc(final String[] locations) {
    this.fX10LibsLocs = locations;
  }
  
  // --- Fields
  
  private EArchitecture fArchitecture;
  
  private String fArchiver;
  
  private String fArchivingOpts;
  
  private String fCompiler;
  
  private String fCompilerOpts;
  
  private String fLinker;
  
  private String fLinkingOpts;
  
  private String fLinkingLibs;
  
  private String fName;
  
  private String fX10DistLoc;
  
  private String fPGASLoc;
  
  private String[] fX10HeadersLocs;
  
  private String[] fX10LibsLocs;
  
  private String fResManagerId;
  
  private ETargetOS fTargetOS;
  
  private boolean fIsCplusPlus;
  
  private boolean fIsLocal;
  
  private EValidStatus fValidStatus;
  
  private String fValidationErrorMsg;

}
