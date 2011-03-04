/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.cpp.builder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import polyglot.frontend.Compiler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.main.Options;
import polyglot.util.ErrorQueue;
import polyglot.visit.PostCompiled;
import x10cpp.ExtensionInfo;
import x10dt.ui.launch.cpp.LaunchMessages;


final class CppBuilderExtensionInfo extends ExtensionInfo {
  
  CppBuilderExtensionInfo(final IProgressMonitor monitor) {
    this.fMonitor = monitor;
  }
  
  // --- Overridden methods
  
  protected Scheduler createScheduler() {
    return new X10SchedulerWithMonitor(this);
  }
  
  // --- Private classes
  
  private final class X10SchedulerWithMonitor extends X10CPPScheduler {

    X10SchedulerWithMonitor(final ExtensionInfo extensionInfo) {
      super(extensionInfo);
    }
    
    // --- Overridden methods
    
    public Goal CodeGenerated(final Job job) {
      CppBuilderExtensionInfo.this.fMonitor.subTask(LaunchMessages.ES_GeneratingCppCilesTaskName);
      return super.CodeGenerated(job);
    }
    
    public Goal End(final Job job) {
      CppBuilderExtensionInfo.this.fMonitor.worked(1);
      return super.End(job);
    }
    
    public Goal Parsed(final Job job) {
      CppBuilderExtensionInfo.this.fMonitor.subTask(NLS.bind(LaunchMessages.ES_CompileTaskName, job.source().name()));
      return super.Parsed(job);
    }

    protected Goal PostCompiled() {
      final PostCompiled postCompileGoal = new PostCompiled(super.extInfo) {
        
        // --- Overridden methods

        protected boolean invokePostCompiler(final Options options, final Compiler postCompiler, final ErrorQueue eq) {
          return false;
        }

        // --- Fields
      
        private static final long serialVersionUID = 324842813729784957L;
      };
      return postCompileGoal.intern(this);
    }
    
    public boolean runToCompletion() {
      // Defines half of the ticks for source compilation and the rest for post-compilation.
      CppBuilderExtensionInfo.this.fMonitor.beginTask(null, super.jobs.size() * 2);
      try {
        final boolean okay = super.runToCompletion();
        return okay;
      } finally {
        CppBuilderExtensionInfo.this.fMonitor.done();
      }
    }

  }
  
  // --- Fields
  
  private final IProgressMonitor fMonitor;

}
