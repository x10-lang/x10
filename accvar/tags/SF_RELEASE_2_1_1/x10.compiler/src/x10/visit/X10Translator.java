/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.visit;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.Compiler;
import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.main.Report;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.QuotedStringTokenizer;
import polyglot.visit.Translator;
import x10.X10CompilerOptions;

public class X10Translator extends Translator {
    public X10Translator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
           super(job, ts, nf, tf);
           inInnerClass = false;
    }
    
    boolean inInnerClass;

    public void print(Node parent, Node n, CodeWriter w) {
        if (n != null && n.position().line() > 0 &&
                ((n instanceof Stmt && (! (n instanceof Block))) ||
                 (n instanceof FieldDecl) ||
                 (n instanceof MethodDecl) ||
                 (n instanceof ConstructorDecl) ||
                 (n instanceof ClassDecl)))
            w.write("\n//#line " + n.position().line() + "\n");

        super.print(parent, n, w);
    }


	public boolean inInnerClass() {
		return inInnerClass;
	}

	public X10Translator inInnerClass(boolean inInnerClass) {
		if (inInnerClass == this.inInnerClass) return this;
		X10Translator tr = (X10Translator) copy();
		tr.inInnerClass = inInnerClass;
		return tr;
	}
	
	/** Override to not open a new file for each declaration. */
	@Override
	protected boolean translateSource(SourceFile sfn) {
	    TypeSystem ts = typeSystem();
	    NodeFactory nf = nodeFactory();
	    TargetFactory tf = this.tf;
	    int outputWidth = job.compiler().outputWidth();
	    Collection<String> outputFiles = job.compiler().outputFiles();
	    CodeWriter w= null;

	    try {
	        File of;

	        QName pkg = null;

	        if (sfn.package_() != null) {
	            Package p = sfn.package_().package_().get();
	            pkg = p.fullName();
	        }

	        // Use the source name to derive a default output file name.
	        of = tf.outputFile(pkg, sfn.source());

	        String opfPath = of.getPath();
	        if (!opfPath.endsWith("$")) outputFiles.add(of.getPath());
	        w = tf.outputCodeWriter(of, outputWidth);

	        writeHeader(sfn, w);

	        for (Iterator<TopLevelDecl> i = sfn.decls().iterator(); i.hasNext(); ) {
	            TopLevelDecl decl = i.next();

	            translateTopLevelDecl(w, sfn, decl);

	            if (i.hasNext()) {
	                w.newline(0);
	            }
	        }

	        w.flush();
	        return true;
	    }
	    catch (IOException e) {
	        job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
	                "I/O error while translating: " + e.getMessage());
	        return false;
	    } finally {
	        if (w != null) {
	            try {
	                w.close();
	            } catch (IOException e) {
	                job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
	                        "I/O error while closing output file: " + e.getMessage());
	            }
	        }
	    }
	}

    public static final String postcompile = "postcompile";

    public static boolean postCompile(X10CompilerOptions options, Compiler compiler, ErrorQueue eq) {
        if (eq.hasErrors())
            return false;

        if (options.post_compiler != null && !options.output_stdout) {
            Runtime runtime = Runtime.getRuntime();
            QuotedStringTokenizer st = new QuotedStringTokenizer(options.post_compiler, '?');
            int pc_size = st.countTokens();
            String[] javacCmd = new String[pc_size+2+compiler.outputFiles().size()];
            int j = 0;
            for (int i = 0; i < pc_size; i++) {
                javacCmd[j++] = st.nextToken();
            }
            javacCmd[j++] = "-classpath";
            javacCmd[j++] = options.constructPostCompilerClasspath();

            Iterator<String> iter = compiler.outputFiles().iterator();
            for (; iter.hasNext(); j++) {
                javacCmd[j] = (String) iter.next();
            }

            if (Report.should_report(postcompile, 1)) {
                StringBuffer cmdStr = new StringBuffer();
                for (int i = 0; i < javacCmd.length; i++)
                    cmdStr.append(javacCmd[i]+" ");
                Report.report(1, "Executing post-compiler " + cmdStr);
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
                    iter = compiler.outputFiles().iterator();
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
