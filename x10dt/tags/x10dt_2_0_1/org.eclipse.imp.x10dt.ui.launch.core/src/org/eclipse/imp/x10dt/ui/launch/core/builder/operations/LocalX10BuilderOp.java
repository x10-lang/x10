/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder.operations;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.core.elements.IResourceManager;

/**
 * Implementation of {@link IX10BuilderOp} in case of a local platform.
 * 
 * @author egeay
 */
public final class LocalX10BuilderOp extends AbstractX10BuilderOp implements IX10BuilderOp {

  /**
   * Creates the implementation with the help of properties provided.
   * 
   * @param project The project associated with the compilation.
   * @param workspaceDir The workspace directory.
   * @param resourceManager The resource manager to consider.
   */
  public LocalX10BuilderOp(final IProject project, final String workspaceDir, final IResourceManager resourceManager) {
    super(resourceManager, project, workspaceDir);
  }
  
  // --- Interface methods implementation

  public void transfer(final IContainer binaryContainer, final IProgressMonitor monitor) throws CoreException {
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
