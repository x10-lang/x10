/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;

/**
 *
 */
public interface Force extends Expr {

    /** Get the expression. */
    Expr expr();

    Force expr(Expr expr);
}
