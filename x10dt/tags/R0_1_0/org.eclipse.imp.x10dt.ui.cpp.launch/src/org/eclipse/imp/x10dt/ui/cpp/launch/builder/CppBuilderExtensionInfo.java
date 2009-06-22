/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.builder;

import org.eclipse.core.runtime.IProgressMonitor;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.visit.X10DelegatingVisitor;
import polyglot.ext.x10cpp.ExtensionInfo;
import polyglot.ext.x10cpp.ast.X10CPPDelFactory_c;
import polyglot.ext.x10cpp.ast.X10CPPExtFactory_c;
import polyglot.frontend.Scheduler;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import x10c.util.StreamWrapper;


final class CppBuilderExtensionInfo extends ExtensionInfo {
  
  CppBuilderExtensionInfo(final IProgressMonitor monitor) {
    this.fMonitor = monitor;
  }
  
  // --- Overridden methods
  
  protected Scheduler createScheduler() {
    return new ExtendedX10Scheduler(this, this.fMonitor);
  }
  
  protected NodeFactory createNodeFactory() {
    return new X10NodeFactory_c(this, new X10CPPExtFactory_c(), new X10CPPDelFactory_c() {
      protected X10DelegatingVisitor makeCodeGenerator(final CodeWriter codeWriter, final Translator translator) {
        return new CppMessagingPassingCodeGenerator((StreamWrapper) codeWriter, translator);
      }
    }) {};
  }
  
  // --- Fields
  
  private final IProgressMonitor fMonitor;

}
