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

import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.visit.PrettyPrinter;
import x10.types.constants.ConstantValue;

/**
 * An <code>Expr</code> represents any Java expression.  All expressions
 * must be subtypes of Expr.
 */
public interface Expr extends Receiver, Term
{
    /**
     * Return an equivalent expression, but with the type <code>type</code>.
     */
    Expr type(Type type);

    /** Get the precedence of the expression. */
    Precedence precedence();

    /**
     * Return whether the expression evaluates to a constant.
     * This is not valid until after disambiguation.
     */
    boolean isConstant();

    /** Returns the constant value of the expression, if any. */
    ConstantValue constantValue();
    

    /**
     * Correctly parenthesize the subexpression <code>expr<code>
     * based on its precedence and the precedence of this expression.
     *
     * If the sub-expression has the same precedence as this expression
     * we parenthesize if the sub-expression does not associate; e.g.,
     * we parenthesis the right sub-expression of a left-associative
     * operator.
     */
     void printSubExpr(Expr expr, boolean associative,
                       CodeWriter w, PrettyPrinter pp);

    /**
     * Correctly parenthesize the subexpression <code>expr<code>
     * based on its precedence and the precedence of this expression.
     *
     * This is equivalent to <code>printSubexpr(expr, true, w, pp)</code>
     */
    void printSubExpr(Expr expr, CodeWriter w, PrettyPrinter pp);
}
