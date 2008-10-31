/*
 * Created on Apr 12, 2007
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.ProcedureCall;

public interface ClosureCall extends Expr, ProcedureCall {
    /**
     * @return the target of this closure invocation, which should be an expression
     * of type <code>ClosureType</code>.
     */
    Expr target();

    /**
     * @return a new ClosureInvocation like the receiver, but with the given target.
     */
    ClosureCall target(Expr target);
}
