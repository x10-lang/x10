/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Stmt;

/** The node constructed for the X10 construct atomic(P) {S}.
 * @author Christian Grothoff
 */
public interface Atomic extends CompoundStmt {
    
    /** Set the Atomic's body */
    Atomic body(Stmt body);

    /** Get the body of the Atomic. */
    Stmt body();

    Expr place();
    
    Atomic place(Expr place);
}
