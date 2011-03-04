/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.validation;

import org.eclipse.core.runtime.SubMonitor;

/**
 * Responsible for checking the commands of compilation and linking provided in an X10 platform configuration.
 * 
 * @author egeay
 */
public interface ICppCompilationChecker {
  
  /**
   * Runs the archiving command on a default program created under the temp directory.
   * 
   * @param monitor The monitor to report progress or cancel the operation.
   * @return A non-null string containing the error message if we did not succeed, otherwise <b>null</b> if everything 
   * went well.
   * @throws Exception May occur for various reasons.
   */
  public String validateArchiving(final SubMonitor monitor) throws Exception;
  
  /**
   * Runs the compilation command on a default program created under the temp directory.
   * 
   * @param monitor The monitor to report progress or cancel the operation.
   * @throws Exception May occur for various reasons.
   * @return A non-null string containing the error message if we did not succeed, otherwise <b>null</b> if everything 
   * went well.
   */
  public String validateCompilation(final SubMonitor monitor) throws Exception;
  
  /**
   * Runs the linking command on a default program created under the temp directory.
   * 
   * @param monitor The monitor to report progress or cancel the operation.
   * @throws Exception May occur for various reasons.
   * @return A non-null string containing the error message if we did not succeed, otherwise <b>null</b> if everything 
   * went well.
   */
  public String validateLinking(final SubMonitor monitor) throws Exception;

}
