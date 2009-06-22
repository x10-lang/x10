/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.builder;

import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10cpp.visit.MessagePassingCodeGenerator;
import polyglot.visit.Translator;
import x10c.util.StreamWrapper;


final class CppMessagingPassingCodeGenerator extends MessagePassingCodeGenerator {
  
  CppMessagingPassingCodeGenerator(final StreamWrapper streamWrapper, final Translator translator) {
    super(streamWrapper, translator);
  }
  
  // --- Overridden methods
  
  protected void processMain(final X10ClassType container) {
    // Do nothing!
  }

}
