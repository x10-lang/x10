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
 *  (C) Copyright IBM Corporation 2007-2012.
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * An immutable representation of a Java language <code>synchronized</code>
 * block. Contains an expression being tested and a statement to be executed
 * while the expression is <code>true</code>.
 */
public class Synchronized_c extends Stmt_c implements Synchronized
{
    protected Expr expr;
    protected Block body;

    public Synchronized_c(Position pos, Expr expr, Block body) {
	super(pos);
	assert(expr != null && body != null);
	this.expr = expr;
	this.body = body;
    }

    /** Get the expression to synchronize. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the expression to synchronize. */
    public Synchronized expr(Expr expr) {
	Synchronized_c n = (Synchronized_c) copy();
	n.expr = expr;
	return n;
    }

    /** Get the body of the statement. */
    public Block body() {
	return this.body;
    }

    /** Set the body of the statement. */
    public Synchronized body(Block body) {
	Synchronized_c n = (Synchronized_c) copy();
	n.body = body;
	return n;
    }

    /** Reconstruct the statement. */
    protected Synchronized_c reconstruct(Expr expr, Block body) {
	if (expr != this.expr || body != this.body) {
	    Synchronized_c n = (Synchronized_c) copy();
	    n.expr = expr;
	    n.body = body;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
	Expr expr = (Expr) visitChild(this.expr, v);
	Block body = (Block) visitChild(this.body, v);
	return reconstruct(expr, body);
    }

    /** Type check the statement. */
    public Node typeCheck(ContextVisitor tc) {
	TypeSystem ts = tc.typeSystem();

	if (! ts.isSubtype(expr.type(), ts.Object(), tc.context()) ) {
	     Errors.issue(tc.job(),
	             new SemanticException("Cannot synchronize on an expression of type \"" + expr.type() + "\".", expr.position()),
	             this);
	}

	return this;
    }

    public String toString() {
	return "synchronized (" + expr + ") { ... }";
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("synchronized (");
	printBlock(expr, w, tr);
	w.write(") ");
	printSubStmt(body, w, tr);
    }

    public Term firstChild() {
        return expr;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(expr, body, ENTRY);
        v.visitCFG(body, this, EXIT);
        return succs;
    }
    

}
