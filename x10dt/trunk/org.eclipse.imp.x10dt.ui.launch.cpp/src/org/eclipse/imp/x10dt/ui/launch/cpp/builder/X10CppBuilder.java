/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.builder.AbstractX10Builder;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.ITargetOpHelper;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import org.eclipse.osgi.util.NLS;

import polyglot.main.Report;
import x10.ExtensionInfo;
import x10cpp.Configuration;
import x10cpp.X10CPPCompilerOptions;

/**
 * X10 builder for C++ back-end.
 * 
 * @author egeay
 */
public final class X10CppBuilder extends AbstractX10Builder {
  
  // --- Abstract methods implementation
  
  public void clearGeneratedAndCompiledFiles(final Collection<IFile> x10SourceFiles, 
                                             final SubMonitor monitor) throws CoreException {
    final IX10BuilderFileOp builderFileOp = createX10BuilderFileOp();
    if (! builderFileOp.hasAllPrerequisites()) {
      IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(LaunchMessages.XCB_CleanAbortedMsg, getProject().getName()), 
                                      IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      UIUtils.showProblemsView();
      return;
    }
    final ITargetOpHelper targetOpHelper = builderFileOp.getTargetOpHelper();
    
    final NullProgressMonitor nullMonitor = new NullProgressMonitor();
    try {
      final String workspaceDir = builderFileOp.getWorkspaceDir();
      monitor.beginTask(null, x10SourceFiles.size() + 1);
      final IPath wDirPath = new Path(workspaceDir);
      
      for (final IFile sourceFile : x10SourceFiles) {
        if (monitor.isCanceled()) {
          return;
        }
        final String rootName = sourceFile.getFullPath().removeFileExtension().lastSegment();
        
        targetOpHelper.getStore(wDirPath.append(rootName + ".cc").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
        targetOpHelper.getStore(wDirPath.append(rootName + ".h").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
        targetOpHelper.getStore(wDirPath.append(rootName + ".inc").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
        targetOpHelper.getStore(wDirPath.append(rootName + ".o").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
      
        monitor.worked(1);
      }
      
      final IFileStore parentStore = targetOpHelper.getStore(workspaceDir);
      parentStore.getChild("xxx_main_xxx.cc").delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
      parentStore.getChild("lib" + getProject().getName() + ".a").delete(EFS.NONE, nullMonitor); //$NON-NLS-1$ //$NON-NLS-2$
      
      final String execPath = getProject().getPersistentProperty(Constants.EXEC_PATH);
      if (execPath != null) {
        targetOpHelper.getStore(execPath).delete(EFS.NONE, nullMonitor);
      }
      monitor.worked(1);
    } finally {
      monitor.done();
    }
  }
  
  public ExtensionInfo createExtensionInfo(final String classPath, final List<File> sourcePath, final String localOutputDir,
                                           final boolean withMainMethod, final IProgressMonitor monitor) {
    final ExtensionInfo extInfo = new CppBuilderExtensionInfo(monitor);
    buildOptions(classPath, sourcePath, localOutputDir, (X10CPPCompilerOptions) extInfo.getOptions(), withMainMethod);
    return extInfo;
  }
  
  public IX10BuilderFileOp createX10BuilderFileOp() throws CoreException {
    final IX10PlatformConf platformConf = CppLaunchCore.getInstance().getPlatformConfiguration(getProject());
    if (platformConf.getConnectionConf().isLocal()) {
      return new LocalX10BuilderFileOp(getProject(), JavaProjectUtils.getProjectOutputDirPath(getProject()), platformConf);
    } else {
      return new RemoteX10BuilderFileOp(getProject(), platformConf);
    }
  }
  
  // --- Private code
  
  private void buildOptions(final String classPath, final List<File> sourcePath, final String localOutputDir,
                            final X10CPPCompilerOptions options, final boolean withMainMethod) {
    // Some useful Polyglot reports.
    Report.addTopic("postcompile", 1); //$NON-NLS-1$
    
    // We can now set all the Polyglot options for our extension.
    options.assertions = true;
    options.classpath = classPath;
    options.output_classpath = options.classpath;
    options.serialize_type_info = false;
    options.output_directory = new File(localOutputDir);
    options.source_path = sourcePath;
    options.compile_command_line_only = true;
    options.post_compiler = null;
    Configuration.MAIN_CLASS = (withMainMethod) ? null : ""; //$NON-NLS-1$
  }
  
}
