/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;

/**
 * @author Christian Grothoff
 */
public interface Future extends Expr, RemoteActivityInvocation {

    /** Set the RemoteActivity's body */
    RemoteActivityInvocation body(Expr body);

    /** Get the body of the RemoteActivity. */
    Expr body();

}
