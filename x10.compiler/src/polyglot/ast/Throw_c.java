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
 * A <code>Throw</code> is an immutable representation of a <code>throw</code>
 * statement. Such a statement contains a single <code>Expr</code> which
 * evaluates to the object being thrown.
 */
public class Throw_c extends Stmt_c implements Throw
{
    protected Expr expr;

    public Throw_c(Position pos, Expr expr) {
	super(pos);
	assert(expr != null);
	this.expr = expr;
    }

    /** Get the expression to throw. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the expression to throw. */
    public Throw expr(Expr expr) {
	Throw_c n = (Throw_c) copy();
	n.expr = expr;
	return n;
    }

    /** Reconstruct the statement. */
    protected Throw_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    Throw_c n = (Throw_c) copy();
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
    public Node typeCheck(ContextVisitor tc) {
    	if (! expr.type().isThrowable()) {
	    Errors.issue(tc.job(),
	            new SemanticException("Can only throw subclasses of \"" +tc.typeSystem().CheckedThrowable() + "\".", expr.position()),
	            this);
	}

	return this;
    }

    public String toString() {
	return "throw " + expr + ";";
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("throw ");
	print(expr, w, tr);
	w.write(";");
    }

    public Term firstChild() {
        return expr;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(expr, this, EXIT);

        // Throw edges will be handled by visitor.
        return Collections.<S>emptyList();
    }

    public List<Type> throwTypes(TypeSystem ts) {
        // if the exception that a throw statement is given to throw is null,
        // then a NullPointerException will be thrown.
        return CollectionUtil.list(expr.type(), ts.NullPointerException());
    }
    

}
