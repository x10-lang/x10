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
 * A <code>Block</code> represents a Java block statement -- an immutable
 * sequence of statements.
 */
public interface Block extends CompoundStmt
{
    /**
     * Get the statements in the block.
     * @return A list of {@link polyglot.ast.Stmt Stmt}.
     */
    List<Stmt> statements();

    /**
     * Set the statements in the block.
     * @param statements A list of {@link polyglot.ast.Stmt Stmt}.
     */
    Block statements(List<Stmt> statements);

    /**
     * Append a statement to the block, returning a new block.
     */
    Block append(Stmt stmt);

    /**
     * Prepend a statement to the block, returning a new block.
     */
    Block prepend(Stmt stmt);
}
