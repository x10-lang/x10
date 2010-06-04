/*
 * Created on Apr 12, 2007
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.ProcedureCall;
import polyglot.ast.TypeNode;
import x10.types.X10MethodInstance;

public interface ClosureCall extends Expr, X10ProcedureCall {
    /**
     * @return the target of this closure invocation, which should be an expression
     * of type <code>ClosureType</code>.
     */
    Expr target();

    /**
     * @return a new ClosureInvocation like the receiver, but with the given target.
     */
    ClosureCall target(Expr target);
    
 //   List<TypeNode> typeArgs();
 //   ClosureCall typeArgs(List<TypeNode> typeArgs);

    /** Get the method instance of the call. */
    public X10MethodInstance closureInstance();

    /** Set the method instance of the call. */
    public ClosureCall closureInstance(X10MethodInstance ci);
}
