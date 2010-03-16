/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import x10.ExtensionInfo;

/**
 * Provides a way of creating a back-end specific extension for future compilation with Polyglot.
 * 
 * @author egeay
 */
public interface ICompilerX10ExtInfo {
  
  /**
   * Returns the appropriate extension info for compilation from the list of parameters provided.
   * 
   * @param classPath The class path to consider for compilation.
   * @param sourcePath The source path to use.
   * @param workspaceDir The workspace directory to use as output directory.
   * @param withMainMethod Indicates if we should add the main method or not.
   * @param monitor The progress monitor to consider.
   * @return A non-null extension info.
   */
  public ExtensionInfo createExtensionInfo(final String classPath, final List<File> sourcePath, final String workspaceDir,
                                           final boolean withMainMethod, final IProgressMonitor monitor);

}
