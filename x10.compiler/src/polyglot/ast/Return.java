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
 * A <code>Return</code> represents a <code>return</code> statement in Java.
 * It may or may not return a value.  If not <code>expr()</code> should return
 * null.
 */
public interface Return extends Stmt
{
    /** The expression to return. */
    Expr expr();
    /** Set the expression to return. */
    Return expr(Expr expr);
}
