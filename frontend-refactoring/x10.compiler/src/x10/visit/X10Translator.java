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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.visit.Translator;
import x10.ast.Block;
import x10.ast.ClassDecl;
import x10.ast.ConstructorDecl;
import x10.ast.FieldDecl;
import x10.ast.MethodDecl;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.ast.SourceFile;
import x10.ast.Stmt;
import x10.ast.TopLevelDecl;
import x10.types.Package;
import x10.types.QName;
import x10.types.TypeSystem;

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

}
