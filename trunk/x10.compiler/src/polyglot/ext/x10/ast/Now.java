/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

/**
 * 
 */
public interface Now extends Stmt {

    /** Get the expression. */
    Expr clock();

    Now clock(Expr clock);

    /** Get the expression. */
    Stmt stmt();

    Now stmt(Stmt stmt);
}
