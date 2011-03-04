/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.builder;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import x10dt.ui.launch.cpp.builder.target_op.ITargetOpHelper;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;


final class LocalX10BuilderFileOp extends AbstractX10BuilderOp implements IX10BuilderFileOp {

  LocalX10BuilderFileOp(final IProject project, final String workspaceDir, 
                        final IX10PlatformConf platformConf) throws CoreException {
    super(platformConf, project, workspaceDir);
  }
  
  LocalX10BuilderFileOp(final IProject project, final String workspaceDir, final IX10PlatformConf platformConf,
                        final ITargetOpHelper targetOpHelper) {
    super(project, workspaceDir, platformConf, targetOpHelper);
  }
  
  // --- Interface methods implementation

  public void transfer(final Collection<File> files, final IProgressMonitor monitor) throws CoreException {
    for (final File file : files) {
      addCppFile(file.getAbsolutePath(), file.getAbsolutePath());
    }
  }
  
}
