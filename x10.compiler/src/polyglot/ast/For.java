/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
