/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Block;
import polyglot.ast.Stmt;

/**
 * @author Christian Grothoff
 */
public interface Async extends Stmt, RemoteActivityInvocation {
    
    /** Set the Async's body */
    RemoteActivityInvocation body(Block body);

    /** Get the body of the Async. */
    Block body();
       
}
