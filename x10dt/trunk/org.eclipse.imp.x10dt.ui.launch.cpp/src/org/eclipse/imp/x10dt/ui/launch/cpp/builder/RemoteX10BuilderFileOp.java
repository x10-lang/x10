/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;


final class RemoteX10BuilderFileOp extends AbstractX10BuilderOp implements IX10BuilderFileOp {
  
  RemoteX10BuilderFileOp(final IProject project, final IX10PlatformConf platformConf) {
    super(platformConf, project, platformConf.getCppCompilationConf().getRemoteOutputFolder());
    this.fTargetOS = platformConf.getCppCompilationConf().getTargetOS();
  }

  // --- Interface methods implementation

  public void transfer(final String localOutputDir, final IProgressMonitor monitor) throws CoreException {
    final IFileStore fileStore = getTargetOpHelper().getStore(getWorkspaceDir());
    monitor.subTask(Messages.CPPB_TransferTaskName);
    copyGeneratedFiles(fileStore, new Path(localOutputDir), monitor);
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
