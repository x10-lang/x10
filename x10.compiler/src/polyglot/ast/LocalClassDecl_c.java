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

package polyglot.ast;

import java.util.*;

import polyglot.types.Context;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A local class declaration statement.  The node is just a wrapper around
 * a class declaration.
 */
public class LocalClassDecl_c extends Stmt_c implements LocalClassDecl
{
    protected ClassDecl decl;

    public LocalClassDecl_c(Position pos, ClassDecl decl) {
	super(pos);
	assert(decl != null);
	this.decl = decl;
    }

    /** Get the class declaration. */
    public ClassDecl decl() {
	return this.decl;
    }

    /** Set the class declaration. */
    public LocalClassDecl decl(ClassDecl decl) {
	LocalClassDecl_c n = (LocalClassDecl_c) copy();
	n.decl = decl;
	return n;
    }

    /** Reconstruct the statement. */
    protected LocalClassDecl_c reconstruct(ClassDecl decl) {
        if (decl != this.decl) {
	    LocalClassDecl_c n = (LocalClassDecl_c) copy();
	    n.decl = decl;
	    return n;
	}

	return this;
    }

    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term firstChild() {
        return decl();
    }

    /**
     * Visit this term in evaluation order.
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(decl(), this, EXIT);
        return succs;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
        Node decl = visitChild(this.decl, v);
        if (decl instanceof NodeList) {
          // Return a NodeList of LocalClassDecls.
          NodeList nl = (NodeList) decl;
          List<Node> decls = new ArrayList<Node>(nl.nodes());
          for (ListIterator<Node> it = decls.listIterator(); it.hasNext(); ) {
            ClassDecl cd = (ClassDecl) it.next();
            it.set(reconstruct(cd));
          }
          return nl.nodes(decls);
        }
        
        return reconstruct((ClassDecl) decl);
    }

    public void addDecls(Context c) {
        // We should now be back in the scope of the enclosing block.
        // Add the type.
        if (! decl.classDef().isLocal())
            throw new InternalCompilerError("Non-local " + decl.classDef() +
                                            " found in method body.");
        c.addNamed(decl.classDef().asType());
    }

    public String toString() {
	return decl.toString();
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        printBlock(decl, w, tr);
	w.write(";");
    }
    

}
