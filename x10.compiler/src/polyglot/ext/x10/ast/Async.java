/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Stmt;
import polyglot.ast.Block;

/** The node constructed for the X10 construct async (P) {S}.
 * @author Christian Grothoff
 */
public interface Async extends Stmt, RemoteActivityInvocation {
    
    /** Set the Async's body */
    Async body(Block body);

    /** Get the body of the Async. */
    Block body();
}
