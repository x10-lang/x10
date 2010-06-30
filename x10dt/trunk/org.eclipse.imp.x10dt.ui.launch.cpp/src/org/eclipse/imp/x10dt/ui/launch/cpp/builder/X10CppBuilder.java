/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.builder.AbstractX10Builder;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;

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
  
  public ExtensionInfo createExtensionInfo(final String classPath, final List<File> sourcePath, final String localOutputDir,
                                           final boolean withMainMethod, final IProgressMonitor monitor) {
    final ExtensionInfo extInfo = new CppBuilderExtensionInfo(monitor);
    buildOptions(classPath, sourcePath, localOutputDir, (X10CPPCompilerOptions) extInfo.getOptions(), withMainMethod);
    return extInfo;
  }
  
  public IFilter<IFile> createNativeFilesFilter() {
    return new NativeFilesFilter();
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
  
  // --- Private classes
  
  private static final class NativeFilesFilter implements IFilter<IFile> {

    // --- Interface methods implementation
    
    public boolean accepts(final IFile element) {
      final String extension = '.' + element.getFileExtension();
      for (final String possibleExtension : POSSIBLE_EXTENSIONS) {
        if (possibleExtension.equals(extension)) {
          return true;
        }
      }
      return false;
    }
    
    // --- Fields
    
    private static final String[] POSSIBLE_EXTENSIONS = { Constants.CC_EXT, Constants.CPP_EXT, Constants.CXX_EXT,
                                                          Constants.H_EXT, Constants.HPP_EXT,
                                                          Constants.INC_EXT };
    
  }
  
}
