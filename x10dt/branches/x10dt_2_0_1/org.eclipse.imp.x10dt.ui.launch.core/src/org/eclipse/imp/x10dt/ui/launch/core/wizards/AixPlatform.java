/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;


final class AixPlatform extends AbstractDefaultX10Platform implements IDefaultX10Platform {
  
  AixPlatform(final boolean is64Arch) {
    super(is64Arch);
  }
  
  // --- Interface methods implementation
  
  public String getArchiver() {
    final String archiver = "ar"; //$NON-NLS-1$
    if (is64Arch()) {
      return archiver + " -X64"; //$NON-NLS-1$
    } else {
      return archiver;
    }
  }

  public String getArchivingOpts() {
    return "-r"; //$NON-NLS-1$
  }

  public String getCompiler() {
    return "mpCC_r"; //$NON-NLS-1$
  }

  public String getCompilerOptions() {
    final String cmpOpts = "-g -DTRANSPORT=lapi -qsuppress=1540-0809:1500-029 -qrtti=all -DX10_USE_BDWGC"; //$NON-NLS-1$
    if (is64Arch()) {
      return cmpOpts + " -q64"; //$NON-NLS-1$
    } else {
      return cmpOpts;
    }
  }

  public String getLinker() {
    return getCompiler();
  }

  public String getLinkingLibraries() {
    return "-lx10 -lgc -lupcrts_lapi -ldl -lm -lpthread"; //$NON-NLS-1$
  }

  public String getLinkingOptions() {
    final String linkOpts = "-g -DTRANSPORT=lapi -qrtti=all -bbigtoc -bexpfull -qsuppress=1540-0809:1500-029 -DX10_USE_BDWGC"; //$NON-NLS-1$
    if (is64Arch()) {
      return linkOpts + " -q64"; //$NON-NLS-1$
    } else {
      return linkOpts;
    }
  }

}
