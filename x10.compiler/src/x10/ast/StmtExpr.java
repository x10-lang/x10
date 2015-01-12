/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Stmt;

/**
 * A StmtExpr is an immutable representation of a statement expression.
 * A statement expression is essentially a block (with the same scoping
 * properties), but ends in an expression, which is the value of the
 * whole statement expression.  The final expression is within the
 * block scope, and may use the locals declared in the preceding
 * statements.
 * 
 * @author igor
 */
public interface StmtExpr extends Expr, Block, ForInit, ForUpdate {

    /**
     * Append a list of statements to the statement expression (just before the result),
     * returning a new statement expression.
     */
    public StmtExpr append(List<Stmt> stmts);

    /**
     * Prepend a list of statements to the statement expression, returning a new
     * statement expression.
     */
    public StmtExpr prepend(List<Stmt> stmts);

    /**
     * Get the result of the statement expression.
     */
    public Expr result();

    /**
     * Set the result of the statement expression.
     */
    public StmtExpr result(Expr result);
}
