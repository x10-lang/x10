/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import java.io.File;
import java.util.Collection;
import java.util.List;

import polyglot.frontend.Compiler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.ExtensionInfo;

/**
 * Fast X10 compiler instance required for indexing purposes.
 * 
 * @author egeay
 */
final class IndexingCompiler {
  
  // --- Services
  
  public Collection<Job> compile(final String classPath, final List<File> sourcePath, 
                                 final Collection<Source> sources) {
    final ExtensionInfo extInfo = new IndexingExtensionInfo();
    final Options options = extInfo.getOptions();
    options.assertions = true;
    options.classpath = classPath;
    options.output_classpath = options.classpath;
    options.serialize_type_info = false;
    options.source_path = sourcePath;
    options.compile_command_line_only = true;
    options.post_compiler = null;
    
    final Compiler compiler = new Compiler(extInfo, new ShallowErrorQueue());
    compiler.compile(sources);
    return extInfo.scheduler().commandLineJobs();
  }
  
  // --- Private classes
  
  private static final class IndexingExtensionInfo extends ExtensionInfo {
    
    // --- Overridden methods
    
    protected Scheduler createScheduler() {
      return new X10Scheduler(this) {
        
        // --- Overridden methods
        
        public List<Goal> goals(final Job job) {
           return super.typecheckSourceGoals(job);
        }
        
      };
    }
    
  }
  
  private static final class ShallowErrorQueue implements ErrorQueue {

    // --- Interface methods implementation
    
    public void enqueue(final int type, final String message) {
    }

    public void enqueue(final int type, final String message, final Position position) {
    }

    public void enqueue(final ErrorInfo errorInfo) {
    }

    public void flush() {
    }

    public boolean hasErrors() {
      return false;
    }

    public int errorCount() {
      return 0;
    }

  }

}
