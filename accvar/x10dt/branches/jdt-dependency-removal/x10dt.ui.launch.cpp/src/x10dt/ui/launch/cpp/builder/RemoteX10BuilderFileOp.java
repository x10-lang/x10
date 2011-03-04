/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.builder;

import static x10dt.ui.launch.core.Constants.CC_EXT;
import static x10dt.ui.launch.core.Constants.H_EXT;
import static x10dt.ui.launch.core.Constants.INC_EXT;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;

import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.utils.CoreResourceUtils;
import x10dt.ui.launch.core.utils.ICountableIterable;
import x10dt.ui.launch.core.utils.ProjectUtils;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.builder.target_op.ITargetOpHelper;
import x10dt.ui.launch.cpp.builder.target_op.TargetOpHelperFactory;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;


final class RemoteX10BuilderFileOp extends AbstractX10BuilderOp implements IX10BuilderFileOp {
  
  RemoteX10BuilderFileOp(final IProject project, final IX10PlatformConf platformConf) throws CoreException {
    super(platformConf, project, platformConf.getCppCompilationConf().getRemoteOutputFolder());
    this.fTargetOS = platformConf.getCppCompilationConf().getTargetOS();
    
    final ITargetOpHelper localTargetOpHelper = TargetOpHelperFactory.create(true, false, null);
    this.fLocalX10BuilderOp = new LocalX10BuilderFileOp(project, ProjectUtils.getProjectOutputDirPath(project), platformConf,
                                                        localTargetOpHelper);
  }

  // --- Interface methods implementation

  public void transfer(final Collection<File> files, final IProgressMonitor monitor) throws CoreException {
    try {
      final IFileStore fileStore = getTargetOpHelper().getStore(getWorkspaceDir());
      monitor.subTask(Messages.CPPB_TransferTaskName);
      copyGeneratedFiles(fileStore, files, monitor);
    } catch (CoreException except) {
      CoreResourceUtils.addBuildMarkerTo(getProject(), LaunchMessages.RXBFO_TransferFailureMsg, IMarker.SEVERITY_ERROR, 
                                         IMarker.PRIORITY_HIGH);
      throw except;
    }
  }
  
  // --- Overridden methods
  
  public void cleanFiles(final ICountableIterable<IFile> files, final SubMonitor monitor) throws CoreException {
    monitor.beginTask(null, 2);
    this.fLocalX10BuilderOp.cleanFiles(files, monitor.newChild(1));
    super.cleanFiles(files, monitor.newChild(1));
  }
  
  // --- Private code
  
  private void copyGeneratedFiles(final IFileStore destDir, final Collection<File> files, 
                                  final IProgressMonitor monitor) throws CoreException {
    if (! destDir.fetchInfo().exists()) {
      destDir.mkdir(EFS.NONE, null);
    }
    final IFileSystem localFileSystem = EFS.getLocalFileSystem();
    monitor.beginTask(null, files.size());
    for (final File file : files) {
      final String ccFilePath = file.getAbsolutePath();
      addCppFile(ccFilePath, copyGeneratedFile(destDir, localFileSystem, ccFilePath));
      copyGeneratedFile(destDir, localFileSystem, ccFilePath.replace(CC_EXT, H_EXT));
      final File incFile = new File(ccFilePath.replace(CC_EXT, INC_EXT));
      if (incFile.exists()) {
        copyGeneratedFile(destDir, localFileSystem, incFile.getAbsolutePath());
      }
      monitor.worked(1);
    }
  }

  private String copyGeneratedFile(final IFileStore destDir, final IFileSystem localFileSystem, 
                                 final String filePath) throws CoreException {
    final IFileStore fileStore = localFileSystem.getStore(new Path(filePath));
    final String name = fileStore.getName();
    final IFileStore destFile = destDir.getChild(name);
    fileStore.copy(destFile, EFS.OVERWRITE, null);
    final String destPath = destFile.toURI().getPath();
    if (this.fTargetOS == ETargetOS.WINDOWS && destPath.startsWith("/")) { //$NON-NLS-1$
      return destPath.substring(1);
    } else {
      return destPath;
    }
  }
  
  // --- Fields
  
  private final ETargetOS fTargetOS;
  
  private final LocalX10BuilderFileOp fLocalX10BuilderOp;
  
}
