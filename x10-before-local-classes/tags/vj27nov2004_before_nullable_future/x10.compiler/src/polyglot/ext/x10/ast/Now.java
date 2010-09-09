/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Block;

/**
 * 
 */
public interface Now extends Stmt {

    /** Get the expression. */
    Expr clock();

    Now clock(Expr clock);

    /** Get the expression. */
    Block body();

    Now body(Block body);
}
