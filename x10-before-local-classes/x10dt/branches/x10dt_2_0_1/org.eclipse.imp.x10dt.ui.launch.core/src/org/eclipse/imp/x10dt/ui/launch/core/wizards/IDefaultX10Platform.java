/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

/**
 * Defines some constant values for X10 platform on a variety of OS.
 * 
 * @author egeay
 */
public interface IDefaultX10Platform {
  
  /**
   * Returns the archiver according to the OS selected in the platform configuration page.
   * 
   * @return A non-null non-empty string.
   */
  public String getArchiver();
  
  /**
   * Returns the archiving options according to the OS selected in the platform configuration page.
   * 
   * @return A non-null non-empty string.
   */
  public String getArchivingOpts();
  
  /**
   * Returns the compiler according to the OS selected in the platform configuration page.
   * 
   * @return A non-null non-empty string.
   */
  public String getCompiler();
  
  /**
   * Returns the compiler options according to the OS selected in the platform configuration page.
   * 
   * @return A non-null non-empty string.
   */
  public String getCompilerOptions();
  
  /**
   * Returns the linker according to the OS selected in the platform configuration page.
   * 
   * @return A non-null non-empty string.
   */
  public String getLinker();
  
  /**
   * Returns the linking libraries according to the OS selected in the platform configuration page.
   * 
   * @return A non-null non-empty string.
   */
  public String getLinkingLibraries();
  
  /**
   * Returns the linking options according to the OS selected in the platform configuration page.
   * 
   * @return A non-null non-empty string.
   */
  public String getLinkingOptions();

}
