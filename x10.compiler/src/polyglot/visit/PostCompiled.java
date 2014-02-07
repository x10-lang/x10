/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.visit;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;

import polyglot.frontend.*;
import polyglot.frontend.Compiler;
import polyglot.main.Options;
import polyglot.main.Reporter;
import polyglot.util.*;

/** The post compiler pass runs after all jobs complete.  It invokes the post-compiler on the output files stored in compiler.outputFiles(). */
public class PostCompiled extends AllBarrierGoal
{
    private static final long serialVersionUID = -1965473009038288138L;

    ExtensionInfo ext;
    Reporter reporter;

    /**
     * Create a Translator.  The output of the visitor is a collection of files
     * whose names are added to the collection <code>outputFiles</code>.
     */
    public PostCompiled(ExtensionInfo ext) {
	super("PostCompile",ext.scheduler());
	this.ext = ext;
	this.reporter = ext.getOptions().reporter;
    }

    public Goal prereqForJob(Job job) {
	if (scheduler.shouldCompile(job)) {
	    return scheduler.End(job);
	}
	else {
	    return new SourceGoal_c("DummyEnd", job) {
	        private static final long serialVersionUID = 3275850403775521984L;
	        public boolean runTask() { return true; }
	    }.intern(scheduler);
	}
    }

    public final static String postcompile = "postcompile";

    public boolean runTask() {
        Compiler compiler = ext.compiler();

        if (reporter.should_report(postcompile, 2)) reporter.report(2, "Output files: " + compiler.outputFiles());

        return invokePostCompiler(ext.getOptions(), compiler, compiler.errorQueue());

    }

    protected boolean invokePostCompiler(Options options,
                                      Compiler compiler,
                                      ErrorQueue eq) {
        if (options.post_compiler != null && !options.output_stdout) {
            Runtime runtime = Runtime.getRuntime();
            QuotedStringTokenizer st = new QuotedStringTokenizer(options.post_compiler, '?');
            int pc_size = st.countTokens();
            Collection<String> outputFiles = compiler.flatOutputFiles();
            String[] javacCmd = new String[pc_size+2+outputFiles.size()];
            int j = 0;
            for (int i = 0; i < pc_size; i++) {
                javacCmd[j++] = st.nextToken();
            }
            javacCmd[j++] = "-classpath";
            javacCmd[j++] = options.constructPostCompilerClasspath();

            Iterator<String> iter = outputFiles.iterator();
            for (; iter.hasNext(); j++) {
                javacCmd[j] = (String) iter.next();
            }

            if (reporter.should_report(postcompile, 1)) {
                StringBuffer cmdStr = new StringBuffer();
                for (int i = 0; i < javacCmd.length; i++)
                    cmdStr.append(javacCmd[i]+" ");
                reporter.report(1, "Executing post-compiler " + cmdStr);
            }

            try {
                Process proc = runtime.exec(javacCmd);

                InputStreamReader err = new InputStreamReader(proc.getErrorStream());

                try {
                    char[] c = new char[72];
                    int len;
                    StringBuffer sb = new StringBuffer();
                    while((len = err.read(c)) > 0) {
                        sb.append(String.valueOf(c, 0, len));
                    }

                    if (sb.length() != 0) {
                        eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, sb.toString());
                    }
                }
                finally {
                    err.close();
                }

                proc.waitFor();

                if (!options.keep_output_files) {
                  String[] rmCmd = new String[1+compiler.outputFiles().size()];
                  rmCmd[0] = "rm";
                  iter = outputFiles.iterator();
                  for (int i = 1; iter.hasNext(); i++)
                    rmCmd[i] = (String) iter.next();
                  runtime.exec(rmCmd);
                }

                if (proc.exitValue() > 0) {
                  eq.enqueue(ErrorInfo.POST_COMPILER_ERROR,
                                   "Non-zero return code: " + proc.exitValue());
                  return false;
                }
            }
            catch(Exception e) {
                eq.enqueue(ErrorInfo.POST_COMPILER_ERROR, e.getMessage());
                return false;
            }
        }
        return true;
    }

}
