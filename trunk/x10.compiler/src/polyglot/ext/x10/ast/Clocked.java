/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

/**
 *
 */
public interface Clocked extends Stmt {

    /** Get the clock. */
    Expr clock();

    Clocked expr(Expr clock);

    /** Get the statement. */
    Stmt stmt();

    Clocked stmt(Stmt stmt);
}
