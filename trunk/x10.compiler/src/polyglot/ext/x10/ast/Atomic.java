/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Stmt;

/**
 * @author Christian Grothoff
 */
public interface Atomic extends Stmt, RemoteActivityInvocation {
    
    /** Set the Atomic's body */
    Atomic body(Stmt body);

    /** Get the body of the Atomic. */
    Stmt body();
}
