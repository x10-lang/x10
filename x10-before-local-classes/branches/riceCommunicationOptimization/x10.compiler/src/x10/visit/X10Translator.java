/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.visit;

import java.io.File;
import java.io.IOException;
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
import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.visit.Translator;

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
	    	Collection outputFiles = job.compiler().outputFiles();
	    	
	    	try {
	    	    File of;
	    	    CodeWriter w;
	    	    
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
	    	}
	    }

}
