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

import java.util.List;

/**
 * An immutable representation of a Java language <code>for</code>
 * statement.  Contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */
public interface For extends Loop 
{    
    /** List of initialization statements.
     * @return A list of {@link polyglot.ast.ForInit ForInit}.
     */
    List<ForInit> inits();

    /** Set the list of initialization statements.
     * @param inits A list of {@link polyglot.ast.ForInit ForInit}.
     */
    For inits(List<ForInit> inits);

    /** Set the loop condition */
    For cond(Expr cond);

    /** List of iterator expressions.
     * @return A list of {@link polyglot.ast.ForUpdate ForUpdate}.
     */
    List<ForUpdate> iters();

    /** Set the list of iterator expressions.
     * @param iters A list of {@link polyglot.ast.ForUpdate ForUpdate}.
     */
    For iters(List<ForUpdate> iters);

    /** Set the loop body */
    For body(Stmt body);
}
