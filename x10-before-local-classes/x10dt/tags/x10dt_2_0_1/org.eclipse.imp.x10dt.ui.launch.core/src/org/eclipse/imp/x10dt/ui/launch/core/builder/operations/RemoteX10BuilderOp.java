/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder.operations;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.remote.core.IRemoteFileManager;

/**
 * Implementation of {@link IX10BuilderOp} in case of a remote platform.
 * 
 * @author egeay
 */
public final class RemoteX10BuilderOp extends AbstractX10BuilderOp implements IX10BuilderOp {
  
  /**
   * Creates the implementation with the help of properties provided.
   * 
   * @param project The project associated with the compilation.
   * @param workspaceDir The workspace directory.
   * @param resourceManager The resource manager to consider.
   * @param targetOS The target OS for the remote system.
   */
  public RemoteX10BuilderOp(final IProject project, final String workspaceDir, final IResourceManager resourceManager,
                            final ETargetOS targetOS) {
    super(resourceManager, project, workspaceDir);
    this.fTargetOS = targetOS;
  }

  // --- Interface methods implementation

  public void transfer(final IContainer binaryContainer, final IProgressMonitor monitor) throws CoreException {
    final IRemoteFileManager fileManager = getRemoteFileManager();
    final IFileStore fileStore = fileManager.getResource(getWorkspaceDir());
    monitor.subTask(Messages.CPPB_TransferTaskName);
    copyGeneratedFiles(fileStore, binaryContainer.getLocation(), monitor);
  }
  
  // --- Private code
  
  private void copyGeneratedFiles(final IFileStore destDir, final IPath srcDir, 
                                  final IProgressMonitor monitor) throws CoreException {
    if (! destDir.fetchInfo().exists()) {
      destDir.mkdir(EFS.NONE, null);
    }
    final IFileStore curDirStore = EFS.getLocalFileSystem().getStore(srcDir);
    final IFileStore[] files = curDirStore.childStores(EFS.NONE, null);
    monitor.beginTask(null, files.length);
    for (final IFileStore fileStore : files) {
      final String name = fileStore.getName();
      if (fileStore.fetchInfo().isDirectory()) {
        copyGeneratedFiles(destDir.getChild(name), srcDir.append(name), new SubProgressMonitor(monitor, 1));
      } else {
        final IFileStore destFile = destDir.getChild(name);
        fileStore.copy(destFile, EFS.OVERWRITE, null);
        if (name.endsWith(CC_EXT)) {
          String destPath = destFile.toURI().getPath();
          if (this.fTargetOS == ETargetOS.WINDOWS && destPath.startsWith("/")) { //$NON-NLS-1$
        	  destPath = destPath.substring(1);
          }
          addCompiledFile(fileStore.toLocalFile(EFS.NONE, null), destPath);
        }
        monitor.worked(1);
      }
    }
  }
  
  // --- Fields
  
  private final ETargetOS fTargetOS;
  
}
