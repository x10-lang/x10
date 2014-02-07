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

/**
 * An immutable representation of a Java language <code>if</code> statement.
 * Contains an expression whose value is tested, a "then" statement 
 * (consequent), and optionally an "else" statement (alternate).
 */
public interface If extends CompoundStmt 
{
    /** Get the if's condition. */
    Expr cond();
    /** Set the if's condition. */
    If cond(Expr cond);

    /** Get the if's then clause. */
    Stmt consequent();
    /** Set the if's then clause. */
    If consequent(Stmt consequent);

    /** Get the if's else clause, or null. */
    Stmt alternative();
    /** Set the if's else clause. */
    If alternative(Stmt alternative);
}
