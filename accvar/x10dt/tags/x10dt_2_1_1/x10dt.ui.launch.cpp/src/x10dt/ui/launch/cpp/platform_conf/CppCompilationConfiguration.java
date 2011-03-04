/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.EBitsArchitecture;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.utils.CodingUtils;



final class CppCompilationConfiguration extends StatusConfProvider implements ICppCompilationConf {
  
  // --- Interface methods implementation
  
  public EArchitecture getArchitecture() {
    return this.fArchitecture;
  }

  public String getArchiver() {
    return this.fArchiver;
  }

  public String getArchivingOpts(final boolean shouldBeInterpreted) {
    return shouldBeInterpreted ? interpretDistVariables(this.fArchivingOpts) : this.fArchivingOpts;
  }
  
  public EBitsArchitecture getBitsArchitecture() {
    return this.fBitsArchitecture;
  }

  public String getCompiler() {
    return this.fCompiler;
  }

  public String getCompilingOpts(final boolean shouldBeInterpreted) {
    return shouldBeInterpreted ? interpretDistVariables(this.fCompilingOpts) : this.fCompilingOpts;
  }

  public String getLinker() {
    return this.fLinker;
  }

  public String getLinkingLibs(final boolean shouldBeInterpreted) {
    return shouldBeInterpreted ? interpretDistVariables(this.fLinkingLibs) : this.fLinkingLibs;
  }

  public String getLinkingOpts(final boolean shouldBeInterpreted) {
    return shouldBeInterpreted ? interpretDistVariables(this.fLinkingOpts) : this.fLinkingOpts;
  }

  public String getPGASLocation() {
    return (this.fPGASLoc == null) ? getX10DistribLocation() : this.fPGASLoc;
  }

  public String getRemoteOutputFolder() {
    return this.fRemoteOutputFolder;
  }

  public ETargetOS getTargetOS() {
    return this.fTargetOS;
  }

  public String getX10DistribLocation() {
    if (this.fX10DistLoc == null) {
      final Bundle x10DistBundle = Platform.getBundle(Constants.X10_DIST_PLUGIN_ID);
      final URL url = x10DistBundle.getResource("include"); //$NON-NLS-1$
      try {
        return new File(FileLocator.resolve(url).getFile()).getParent();
      } catch (IOException except) {
        return null;
      }
    } else {
      return this.fX10DistLoc;
    }
  }

  public String[] getX10HeadersLocations() {
    final String x10DistLoc = getX10DistribLocation();
    final String pgasLoc = getPGASLocation();
    if (x10DistLoc.equals(pgasLoc)) {
      return new String[] { String.format(INCLUDE_FORMAT, x10DistLoc) };
    } else {
      return new String[] { String.format(INCLUDE_FORMAT, x10DistLoc), String.format(INCLUDE_FORMAT, pgasLoc) };
    }
  }

  public String[] getX10LibsLocations() {
    final String x10DistLoc = getX10DistribLocation();
    final String pgasLoc = getPGASLocation();
    if (x10DistLoc.equals(pgasLoc)) {
      return new String[] { String.format(LIB_FORMAT, x10DistLoc) };
    } else {
      return new String[] { String.format(LIB_FORMAT, x10DistLoc), String.format(LIB_FORMAT, pgasLoc) };
    }
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final CppCompilationConfiguration rhsObj = (CppCompilationConfiguration) rhs;
    if (! super.equals(rhs)) {
      return false;
    }
    return ((this.fTargetOS == rhsObj.fTargetOS) && (this.fBitsArchitecture == rhsObj.fBitsArchitecture) &&
            (this.fArchitecture == rhsObj.fArchitecture) &&
            Arrays.equals(new Object[] { this.fCompiler, this.fCompilingOpts, this.fArchiver, this.fArchivingOpts,
                                         this.fLinker, this.fLinkingOpts, this.fLinkingLibs, this.fPGASLoc,
                                         this.fX10DistLoc, this.fRemoteOutputFolder },
                          new Object[] { rhsObj.fCompiler, rhsObj.fCompilingOpts, rhsObj.fArchiver, rhsObj.fArchivingOpts,
                                         rhsObj.fLinker, rhsObj.fLinkingOpts, rhsObj.fLinkingLibs, rhsObj.fPGASLoc,
                                         rhsObj.fX10DistLoc, rhsObj.fRemoteOutputFolder }));
  }
  
  public int hashCode() {
    return super.hashCode() + CodingUtils.generateHashCode(134456, this.fTargetOS, this.fBitsArchitecture, this.fCompiler, 
                                                           this.fCompilingOpts, this.fArchiver, this.fArchivingOpts, 
                                                           this.fLinker,this.fLinkingOpts, this.fLinkingLibs, this.fPGASLoc,
                                                           this.fX10DistLoc, this.fRemoteOutputFolder, this.fArchitecture);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nTarget OS: ").append(this.fTargetOS.name()) //$NON-NLS-1$
      .append("\nBits Arch: ").append(this.fBitsArchitecture.name()) //$NON-NLS-1$
      .append("\nArchitecture: ").append(this.fArchitecture.name()) //$NON-NLS-1$
      .append("\nCompiler: ").append(this.fCompiler) //$NON-NLS-1$
      .append("\nCompiling Options: ").append(this.fCompilingOpts) //$NON-NLS-1$
      .append("\nArchiver: ").append(this.fArchiver) //$NON-NLS-1$
      .append("\nArchiving Options: ").append(this.fArchivingOpts) //$NON-NLS-1$
      .append("\nLinker: ").append(this.fLinker) //$NON-NLS-1$
      .append("\nLinking Options: ").append(this.fLinkingOpts) //$NON-NLS-1$
      .append("\nLinking Libs: ").append(this.fLinkingLibs) //$NON-NLS-1$
      .append("\nPGAS Location: ").append(getPGASLocation()) //$NON-NLS-1$
      .append("\nX10 Dist Location: ").append(getX10DistribLocation()) //$NON-NLS-1$
      .append("\nRemote Output Folder: ").append(this.fRemoteOutputFolder); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Internal code
  
  CppCompilationConfiguration() {}
  
  CppCompilationConfiguration(final CppCompilationConfiguration original) {
    this.fValidationStatus = original.fValidationStatus;
    this.fValidationErrorMsg = original.fValidationErrorMsg;
    this.fTargetOS = original.fTargetOS;
    this.fBitsArchitecture = original.fBitsArchitecture;
    this.fArchitecture = original.fArchitecture;
    this.fCompiler = original.fCompiler;
    this.fCompilingOpts = original.fCompilingOpts;
    this.fArchiver = original.fArchiver;
    this.fArchivingOpts = original.fArchivingOpts;
    this.fLinker = original.fLinker;
    this.fLinkingOpts = original.fLinkingOpts;
    this.fLinkingLibs = original.fLinkingLibs;
    this.fPGASLoc = original.fPGASLoc;
    this.fX10DistLoc = original.fX10DistLoc;
    this.fRemoteOutputFolder = original.fRemoteOutputFolder;
  }
  
  void applyChanges(final ICppCompilationConf source) {
    this.fValidationStatus = source.getValidationStatus();
    this.fValidationErrorMsg = source.getValidationErrorMessage();
    this.fTargetOS = source.getTargetOS();
    this.fBitsArchitecture = source.getBitsArchitecture();
    this.fArchitecture = source.getArchitecture();
    this.fCompiler = source.getCompiler();
    this.fCompilingOpts = source.getCompilingOpts(false);
    this.fArchiver = source.getArchiver();
    this.fArchivingOpts = source.getArchivingOpts(false);
    this.fLinker = source.getLinker();
    this.fLinkingOpts = source.getLinkingOpts(false);
    this.fLinkingLibs = source.getLinkingLibs(false);
    this.fX10DistLoc = source.getX10DistribLocation();
    this.fPGASLoc = source.getPGASLocation();
    this.fRemoteOutputFolder = source.getRemoteOutputFolder();
  }
  
  // --- Private code
  
  private String interpretDistVariables(final String option) {
    final String x10DistLoc = String.format("\"%s\"", getX10DistribLocation()); //$NON-NLS-1$
    final String pgasDistLoc = String.format("\"%s\"", getPGASLocation()); //$NON-NLS-1$
    return option.replace(X10_DIST_VAR, x10DistLoc).replace(PGAS_DIST_VAR, pgasDistLoc);
  }
  
  // --- Fields
  
  ETargetOS fTargetOS;
  
  EArchitecture fArchitecture;

  EBitsArchitecture fBitsArchitecture;
  
  String fCompiler;
  
  String fCompilingOpts;
  
  String fArchiver;
  
  String fArchivingOpts;
  
  String fLinker;
  
  String fLinkingOpts;
  
  String fLinkingLibs;
  
  String fX10DistLoc;
  
  String fPGASLoc;

  String fRemoteOutputFolder;
  
  
  private static final String PGAS_DIST_VAR = "${PGAS-DIST}"; //$NON-NLS-1$
  
  private static final String X10_DIST_VAR = "${X10-DIST}"; //$NON-NLS-1$
  
  private static final String INCLUDE_FORMAT = "%s/include"; //$NON-NLS-1$
  
  private static final String LIB_FORMAT = "%s/lib"; //$NON-NLS-1$

}
