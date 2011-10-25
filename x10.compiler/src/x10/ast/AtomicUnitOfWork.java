package x10.ast;

import java.util.List;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Stmt;

public interface AtomicUnitOfWork extends CompoundStmt {
    
    /** Set the Atomic's body */
    AtomicUnitOfWork body(Stmt body);

    /** Get the body of the Atomic. */
    Stmt body();
    
    /** Actual arguments to pass to the Atomic.
     * @return A list of {@link polyglot.ast.Expr Expr}.
     */
    List<Expr> arguments();

    /** Set the actual arguments to pass to the Atomic.
     * @param arguments A list of {@link polyglot.ast.Expr Expr}.
     */
    AtomicUnitOfWork arguments(List<Expr> arguments);
}
