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

import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * An <code>Instanceof</code> is an immutable representation of
 * the use of the <code>instanceof</code> operator.
 */
public abstract class Instanceof_c extends Expr_c implements Instanceof
{
    protected Expr expr;
    protected TypeNode compareType;

    public Instanceof_c(Position pos, Expr expr, TypeNode compareType) {
	super(pos);
	assert(expr != null && compareType != null);
	this.expr = expr;
	this.compareType = compareType;
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() {
	return Precedence.INSTANCEOF;
    }

    /** Get the expression to be tested. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the expression to be tested. */
    public Instanceof expr(Expr expr) {
	Instanceof_c n = (Instanceof_c) copy();
	n.expr = expr;
	return n;
    }

    /** Get the type to be compared against. */
    public TypeNode compareType() {
	return this.compareType;
    }

    /** Set the type to be compared against. */
    public Instanceof compareType(TypeNode compareType) {
	Instanceof_c n = (Instanceof_c) copy();
	n.compareType = compareType;
	return n;
    }

    /** Reconstruct the expression. */
    protected Instanceof_c reconstruct(Expr expr, TypeNode compareType) {
	if (expr != this.expr || compareType != this.compareType) {
	    Instanceof_c n = (Instanceof_c) copy();
	    n.expr = expr;
	    n.compareType = compareType;
	    return n;
	}

	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
	Expr expr = (Expr) visitChild(this.expr, v)  ;
	TypeNode compareType = (TypeNode) visitChild(this.compareType, v);
	return reconstruct(expr, compareType);
    }

    /** Type check the expression. */
    public abstract Node typeCheck(ContextVisitor tc);

    public String toString() {
	return expr + " instanceof " + compareType;
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	printSubExpr(expr, w, tr);
	w.write(" instanceof ");
	print(compareType, w, tr);
    }

    public Term firstChild() {
        return expr;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(expr, compareType, ENTRY);
        v.visitCFG(compareType, this, EXIT);
        return succs;
    }
    

}
