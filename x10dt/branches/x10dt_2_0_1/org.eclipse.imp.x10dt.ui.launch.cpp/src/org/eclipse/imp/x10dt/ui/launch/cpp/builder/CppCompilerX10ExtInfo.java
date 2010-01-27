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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ICompilerX10ExtInfo;

import polyglot.main.Report;
import x10.ExtensionInfo;
import x10cpp.Configuration;
import x10cpp.X10CPPCompilerOptions;

/**
 * Defines the extension info for C++ back-end.
 * 
 * @author egeay
 */
public final class CppCompilerX10ExtInfo implements ICompilerX10ExtInfo {

  // --- Interface methods implementation
  
  public ExtensionInfo createExtensionInfo(final String classPath, final List<File> sourcePath, final String workspaceDir,
                                           final boolean withMainMethod, final IProgressMonitor monitor) {
    final ExtensionInfo extInfo = new CppBuilderExtensionInfo(monitor);
    buildOptions(classPath, sourcePath, workspaceDir, (X10CPPCompilerOptions) extInfo.getOptions(), withMainMethod);
    return extInfo;
  }
  
  // --- Private code
  
  private void buildOptions(final String classPath, final List<File> sourcePath, final String workspaceDir,
                            final X10CPPCompilerOptions options, final boolean withMainMethod) {
    // Some useful Polyglot reports.
    Report.addTopic("postcompile", 1); //$NON-NLS-1$
    
    // We can now set all the Polyglot options for our extension.
    options.assertions = true;
    options.classpath = classPath;
    options.output_classpath = options.classpath;
    options.serialize_type_info = false;
    options.output_directory = new File(workspaceDir);
    options.source_path = sourcePath;
    options.compile_command_line_only = true;
    options.post_compiler = null;
    Configuration.MAIN_CLASS = (withMainMethod) ? null : ""; //$NON-NLS-1$
  }

}
