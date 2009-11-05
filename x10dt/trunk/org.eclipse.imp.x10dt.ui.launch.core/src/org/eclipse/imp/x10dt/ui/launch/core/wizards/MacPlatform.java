/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;


final class MacPlatform implements IDefaultX10Platform {
  
  // --- Interface methods implementation
  
  public String getArchiver() {
    return "ar"; //$NON-NLS-1$
  }

  public String getArchivingOpts() {
    return "r"; //$NON-NLS-1$
  }

  public String getCompiler() {
    return "g++"; //$NON-NLS-1$
  }

  public String getCompilerOptions() {
    return "-g -DTRANSPORT=sockets -Wno-long-long -Wno-unused-parameter -pthread -msse2 -mfpmath=sse -DX10_USE_BDWGC -m32"; //$NON-NLS-1$
  }

  public String getLinker() {
    return getCompiler();
  }

  public String getLinkingLibraries() {
    return "-lx10 -lgc -lxlpgas_sockets -ldl -lm -lpthread"; //$NON-NLS-1$
  }

  public String getLinkingOptions() {
    return "-g -DTRANSPORT=sockets -Wno-long-long -Wno-unused-parameter -msse2 -mfpmath=sse -DX10_USE_BDWGC -m32"; //$NON-NLS-1$
  }

}
