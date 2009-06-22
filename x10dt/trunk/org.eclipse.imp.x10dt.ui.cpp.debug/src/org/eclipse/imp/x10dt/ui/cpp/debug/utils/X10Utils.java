/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.utils;

/**
 * X10 utility methods.
 * 
 * @author egeay
 */
public final class X10Utils {
  
  public static String FMGL(final String name) {
    return "x10__" + name;
  }
  
  // --- Private code
  
  private X10Utils() {}

}
