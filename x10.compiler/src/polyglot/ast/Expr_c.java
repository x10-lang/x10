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

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.types.constants.ConstantValue;

/**
 * An <code>Expr</code> represents any Java expression.  All expressions
 * must be subtypes of Expr.
 */
public abstract class Expr_c extends Term_c implements Expr
{
	protected Type type;

    public Expr_c(Position pos) {
	super(pos);
    }

    /**
     * Get the type of the expression.  This may return an
     * <code>UnknownType</code> before type-checking, but should return the
     * correct type after type-checking.
     */
    public Type type() {
    	return this.type;
    }
    
    /** Set the type of the expression. */
    public Expr type(Type type) {
    	if (type == this.type) return this;
    	Expr_c n = (Expr_c) copy();
    	n.type = type;
    	return n;
    }

    public void dump(CodeWriter w) {
        super.dump(w);

	if (type != null) {
	    w.allowBreak(4, " ");
	    w.begin(0);
	    w.write("(type " + type + ")");
	    w.end();
	}
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() {
	return Precedence.UNKNOWN;
    }

    public boolean isConstant() {
        return false;
    }

    public ConstantValue constantValue() {
        return null;
    }

    public Node buildTypes(TypeBuilder tb) {
        return type(tb.typeSystem().unknownType(position()));
    }

    /**
     * Correctly parenthesize the subexpression <code>expr<code> given
     * the its precendence and the precedence of the current expression.
     *
     * If the sub-expression has the same precedence as this expression
     * we do not parenthesize.
     *
     * @param expr The subexpression.
     * @param w The output writer.
     * @param pp The pretty printer.
     */
    public void printSubExpr(Expr expr, CodeWriter w, PrettyPrinter pp) {
        printSubExpr(expr, true, w, pp);
    }

    /**
     * Correctly parenthesize the subexpression <code>expr<code> given
     * the its precendence and the precedence of the current expression.
     *
     * If the sub-expression has the same precedence as this expression
     * we parenthesize if the sub-expression does not associate; e.g.,
     * we parenthesis the right sub-expression of a left-associative
     * operator.
     *
     * @param expr The subexpression.
     * @param associative Whether expr is the left (right) child of a left-
     * (right-) associative operator.
     * @param w The output writer.
     * @param pp The pretty printer.
     */
    public void printSubExpr(Expr expr, boolean associative,
                             CodeWriter w, PrettyPrinter pp) {
        if (! associative && precedence().equalsPrecedence(expr.precedence()) ||
	    precedence().isTighter(expr.precedence())) {
    		w.write("(");
    		printBlock(expr, w, pp);
    		w.write(")");
	}
        else {
            print(expr, w, pp);
        }
    }
}
