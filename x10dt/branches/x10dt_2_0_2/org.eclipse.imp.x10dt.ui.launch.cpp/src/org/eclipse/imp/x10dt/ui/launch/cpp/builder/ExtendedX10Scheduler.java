/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.osgi.util.NLS;

import polyglot.frontend.Compiler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.main.Options;
import polyglot.util.ErrorQueue;
import polyglot.visit.PostCompiled;
import x10cpp.ExtensionInfo;
import x10cpp.ExtensionInfo.X10CPPScheduler;
import x10cpp.visit.X10CPPTranslator;


final class ExtendedX10Scheduler extends X10CPPScheduler {

  ExtendedX10Scheduler(final ExtensionInfo extensionInfo, final IProgressMonitor monitor) {
    super(extensionInfo);
    this.fMonitor = monitor;
  }
  
  // --- Overridden methods
  
  public Goal End(Job job) {
    this.fMonitor.worked(1);
    return super.End(job);
  }
  
  public Goal Parsed(final Job job) {
    this.fMonitor.subTask(NLS.bind(LaunchMessages.ES_CompileTaskName, job.source().name()));
    return super.Parsed(job);
  }

  protected Goal PostCompiled() {
    final PostCompiled postCompileGoal = new PostCompiled(super.extInfo) {
      
      // --- Overridden methods

      protected boolean invokePostCompiler(final Options options, final Compiler compiler, final ErrorQueue eq) {
        ExtendedX10Scheduler.this.fMonitor.subTask(LaunchMessages.ES_LinkingTaskName);
        return X10CPPTranslator.postCompile(options, compiler, eq);
      }
    
      // --- Fields
    
      private static final long serialVersionUID = 324842813729784957L;
    };
    return postCompileGoal.intern(this);
  }
  
  public boolean runToCompletion() {
    // Defines half of the ticks for source compilation and the rest for post-compilation.
    this.fMonitor.beginTask(null, super.jobs.size() * 2);
    try {
      final boolean okay = super.runToCompletion();
      return okay;
    } finally {
      this.fMonitor.done();
    }
  }
  
  
  // --- Fields
  
  private final IProgressMonitor fMonitor;

}
