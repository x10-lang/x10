/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Stmt;

/**
 * @author Christian Grothoff
 */
public interface Async extends Stmt, RemoteActivityInvocation {
    
    /** Set the Async's body */
    Async body(Stmt body);

    /** Get the body of the Async. */
    Stmt body();
}
