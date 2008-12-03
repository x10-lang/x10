/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import polyglot.ast.Block;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
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
}
