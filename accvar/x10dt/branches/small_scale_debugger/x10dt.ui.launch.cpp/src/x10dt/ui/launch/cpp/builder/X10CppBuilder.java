/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.builder;

import static x10dt.ui.launch.core.Constants.CC_EXT;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import x10.ExtensionInfo;
import x10cpp.X10CPPCompilerOptions;
import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.builder.AbstractX10Builder;
import x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import x10dt.ui.launch.core.utils.IFilter;
import x10dt.ui.launch.core.utils.ProjectUtils;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;

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
      return new LocalX10BuilderFileOp(getProject(), ProjectUtils.getProjectOutputDirPath(getProject()), platformConf);
    } else {
      return new RemoteX10BuilderFileOp(getProject(), platformConf);
    }
  }
  
  public String getFileExtension(){
	  return CC_EXT;
  }
  
  // --- Private code
  
  private void buildOptions(final String classPath, final List<File> sourcePath, final String localOutputDir,
          final X10CPPCompilerOptions options, final boolean withMainMethod) {
	  super.buildOptions(classPath, sourcePath, localOutputDir, options, withMainMethod);
	  options.post_compiler = null;
	  x10cpp.Configuration.MAIN_CLASS = (withMainMethod) ? null : Constants.EMPTY_STR;
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
