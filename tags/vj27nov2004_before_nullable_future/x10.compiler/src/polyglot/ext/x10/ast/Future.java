/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;

/**
 *
 */
public interface Future extends Expr, RemoteActivityInvocation {

    /** Set the RemoteActivity's body */
    Future body(Expr body);

    /** Get the body of the RemoteActivity. */
    Expr body();

}
