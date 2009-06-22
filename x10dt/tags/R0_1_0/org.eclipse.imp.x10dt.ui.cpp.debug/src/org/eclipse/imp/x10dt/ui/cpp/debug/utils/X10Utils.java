/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.utils;

import polyglot.ext.x10cpp.visit.Emitter;

/**
 * X10 utility methods.
 * 
 * @author egeay
 */
public final class X10Utils {
  
  public static String FMGL(final String name) {
    String mangled = Emitter.mangled_field_name(name);
    assert (mangled.startsWith("FMGL("));
    int paren = PDTUtils.findMatch(mangled, "FMGL".length());
    assert (paren != -1);
    return "x10__" + mangled.substring("FMGL(".length(), paren);
  }
  
  // --- Private code
  
  private X10Utils() {}

}
