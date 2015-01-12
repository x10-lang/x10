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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

/**
 * A <code>Conditional</code> is a representation of a Java ternary
 * expression.  That is, <code>(cond ? consequent : alternative)</code>.
 */
public interface Conditional extends Expr {
    /** Get the condition to test. */
    Expr cond();
    /** Set the condition to test. */
    Conditional cond(Expr cond);

    /** Get the expression to evaluate when the condition is true. */
    Expr consequent();
    /** Set the expression to evaluate when the condition is true. */
    Conditional consequent(Expr consequent);

    /** Get the expression to evaluate when the condition is false. */
    Expr alternative();
    /** Set the expression to evaluate when the condition is false. */
    Conditional alternative(Expr alternative);
}
