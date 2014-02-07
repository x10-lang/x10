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
 * A Java language <code>do</code> statement. 
 * It contains a statement to be executed and an expression to be tested 
 * indicating whether to reexecute the statement.
 */ 
public interface Do extends Loop 
{
    /** Set the body of the do statement. */
    Do body(Stmt body);

    /** Set the condition of the do statement. */
    Do cond(Expr cond);
}
