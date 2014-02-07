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

import java.util.Collections;
import java.util.List;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * A <code>Return</code> represents a <code>return</code> statement in Java.
 * It may or may not return a value.  If not <code>expr()</code> should return
 * null.
 */
public abstract class Return_c extends Stmt_c implements Return
{
    protected Expr expr;

    public Return_c(Position pos, Expr expr) {
	super(pos);
	assert(true); // expr may be null
	this.expr = expr;
    }

    /** Get the expression to return, or null. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the expression to return, or null. */
    public Return expr(Expr expr) {
	Return_c n = (Return_c) copy();
	n.expr = expr;
	return n;
    }

    /** Reconstruct the statement. */
    protected Return_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    Return_c n = (Return_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
	Expr expr = (Expr) visitChild(this.expr, v);
	return reconstruct(expr);
    }

    /** Type check the statement. */
    public abstract Node typeCheck(ContextVisitor tc);
  
    public String toString() {
	return "return" + (expr != null ? " " + expr : "") + ";";
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("return") ;
	if (expr != null) {
	    w.write(" ");
	    print(expr, w, tr);
	}
	w.write(";");
    }

    public Term firstChild() {
        if (expr != null) return expr;
        return null;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (expr != null) {
            v.visitCFG(expr, this, EXIT);
        }

        v.visitReturn(this);
        return Collections.<S>emptyList();
    }
    

}
