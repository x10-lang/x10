/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.preferences;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;

/**
 * Responsible for checking the commands of compilation and linking provided in an X10 platform configuration.
 * 
 * @author egeay
 */
public interface IPlatformConfChecker {
  
  /**
   * Runs the archiving command on a default program created under the temp directory.
   * 
   * @param archiver The archiver to use.
   * @param archivingOptions The archiving options to consider.
   * @param monitor The monitor to report progress or cancel the operation.
   * @return A non-null string containing the error message if we did not succeed, otherwise <b>null</b> if everything 
   * went well.
   * @throws Exception May occur for various reasons.
   */
  public String validateArchiving(final String archiver, final String archivingOptions, 
                                  final SubMonitor monitor) throws Exception;
  
  /**
   * Runs the compilation command on a default program created under the temp directory.
   * 
   * @param language The language to consider for the generation of the test X10 file.
   * @param compiler The compiler to use.
   * @param compilingOptions The compiling options to use.
   * @param x10DistLoc The location of X10 distribution.
   * @param pgasDistLoc The PGAS distribution location.
   * @param x10HeadersLocs The X10 locations where headers are installed.
   * @param x10LibsLocs The X10 locations where libraries are installed.
   * @param monitor The monitor to report progress or cancel the operation.
   * @throws Exception May occur for various reasons.
   * @return A non-null string containing the error message if we did not succeed, otherwise <b>null</b> if everything 
   * went well.
   */
  public String validateCompilation(final ELanguage language, final String compiler, final String compilingOptions, 
                                    final String x10DistLoc, final String pgasDistLoc, final String[] x10HeadersLocs,
                                    final String[] x10LibsLocs, final SubMonitor monitor) throws Exception;
  
  /**
   * Runs the linking command on a default program created under the temp directory.
   * 
   * @param linker The linker to use.
   * @param linkingOptions The linking options to use.
   * @param linkingLibs The linking libraries to use.
   * @param x10HeadersLocs The X10 locations where headers are installed.
   * @param x10LibsLocs The X10 locations where libraries are installed.
   * @param monitor The monitor to report progress or cancel the operation.
   * @throws Exception May occur for various reasons.
   * @return A non-null string containing the error message if we did not succeed, otherwise <b>null</b> if everything 
   * went well.
   */
  public String validateLinking(final String linker, final String linkingOptions, final String linkingLibs, 
                                final String[] x10HeadersLocs, final String[] x10LibsLocs,
                                final SubMonitor monitor) throws Exception;

}
