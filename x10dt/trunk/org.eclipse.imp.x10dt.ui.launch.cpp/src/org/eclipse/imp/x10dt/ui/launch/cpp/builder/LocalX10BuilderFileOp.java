/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.builder.operations.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;


final class LocalX10BuilderFileOp extends AbstractX10BuilderOp implements IX10BuilderFileOp {

  LocalX10BuilderFileOp(final IProject project, final String workspaceDir, final IX10PlatformConf platformConf) {
    super(platformConf, project, workspaceDir);
  }
  
  // --- Interface methods implementation

  public void transfer(final String localOutputDir, final IProgressMonitor monitor) throws CoreException {
    collectFilesToCompile(new File(getWorkspaceDir()));
  }
  
  // --- Private code
  
  private void collectFilesToCompile(final File dir) {
  	for (final File file : dir.listFiles()) {
  		if (file.isDirectory()) {
  			collectFilesToCompile(file);
  		} else {
  			if (file.getName().endsWith(CC_EXT)) {
  				addCompiledFile(file, file.getAbsolutePath());
  			}
  		}
  	}
  }
  
}
