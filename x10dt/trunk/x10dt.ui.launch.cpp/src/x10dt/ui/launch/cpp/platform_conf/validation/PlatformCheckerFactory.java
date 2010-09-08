/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.validation;

/**
 * Factory method(s) to create instance(s) of {@link IX10PlatformChecker}.
 * 
 * @author egeay
 */
public final class PlatformCheckerFactory {
  
  /**
   * Returns the default X10 platform checker.
   * 
   * @return A non-null object.
   */
  public static IX10PlatformChecker create() {
    return new X10PlatformChecker();
  }
  
  // --- Private code
  
  private PlatformCheckerFactory() {}

}
