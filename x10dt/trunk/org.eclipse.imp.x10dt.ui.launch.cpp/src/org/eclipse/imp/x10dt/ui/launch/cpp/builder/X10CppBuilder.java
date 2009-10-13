/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import java.io.File;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.builder.AbstractX10Builder;
import org.eclipse.imp.x10dt.ui.launch.core.builder.CpEntryAsStringFunc;
import org.eclipse.imp.x10dt.ui.launch.core.builder.IPathToFileFunc;
import org.eclipse.imp.x10dt.ui.launch.core.builder.RuntimeFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ListUtils;
import org.eclipse.jdt.core.IJavaProject;

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
  
  protected ExtensionInfo createExtensionInfo(final IJavaProject javaProject, final IContainer binaryContainer,
                                              final IProgressMonitor monitor) throws CoreException {
    final ExtensionInfo extInfo = new CppBuilderExtensionInfo(monitor);
    buildOptions(javaProject, binaryContainer, (X10CPPCompilerOptions) extInfo.getOptions());
    return extInfo;
  }
  
  // --- Private code
  
  private void buildOptions(final IJavaProject javaProject, final IContainer binaryContainer,
                            final X10CPPCompilerOptions options) throws CoreException {
    // Sets the class path
    final Set<String> cps = JavaProjectUtils.getFilteredCpEntries(javaProject, new CpEntryAsStringFunc(), 
                                                                  new AlwaysTrueFilter<IPath>());
    final StringBuilder cpBuilder = new StringBuilder();
    int i = -1;
    for (final String cpEntry : cps) {
      if (++i > 0) {
        cpBuilder.append(File.pathSeparatorChar);
      }
      cpBuilder.append(cpEntry);
    }
    // Sets the source path.
    final Set<IPath> srcPaths = JavaProjectUtils.getFilteredCpEntries(javaProject, new IdentityFunctor<IPath>(),
                                                                      new RuntimeFilter());
    // Set the output dir.
    final File outputDir = new File(binaryContainer.getLocationURI());
   
    // Some useful Polyglot reports.
    //Report.addTopic("verbose", 1); //$NON-NLS-1$
    Report.addTopic("postcompile", 1); //$NON-NLS-1$
    
    // We can now set all the Polyglot options for our extension.
    options.assertions = true;
    options.classpath = cpBuilder.toString();
    options.output_classpath = options.classpath;
    options.serialize_type_info = false;
    options.output_directory = outputDir;
    options.source_path = ListUtils.transform(srcPaths, new IPathToFileFunc());
    options.compile_command_line_only = true;
    options.post_compiler = null;
    Configuration.MAIN_CLASS = ""; //$NON-NLS-1$ We do generate main class stub during partial compilation.
  }

}
