/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import org.eclipse.core.runtime.IProgressMonitor;

import polyglot.frontend.Scheduler;
import polyglot.ext.x10cpp.ExtensionInfo;


final class CppBuilderExtensionInfo extends ExtensionInfo {
  
  CppBuilderExtensionInfo(final IProgressMonitor monitor) {
    this.fMonitor = monitor;
  }
  
  // --- Overridden methods
  
  protected Scheduler createScheduler() {
    return new ExtendedX10Scheduler(this, this.fMonitor);
  }
  
  // --- Fields
  
  private final IProgressMonitor fMonitor;

}
