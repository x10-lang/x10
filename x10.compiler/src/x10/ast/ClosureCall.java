/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.Name;
import x10.types.MethodInstance;

public interface ClosureCall extends Expr, InlinableCall {

    /** The name of the method to use for closure invocations */
    public static final Name APPLY = OperatorNames.APPLY;

    /**
     * @return the target of this closure invocation, which should be an expression
     * of type <code>ClosureType</code>.
     */
    Expr target();

    /**
     * @return a new closure invocation like the receiver, but with the given target.
     */
    ClosureCall target(Expr target);

    /**
     * @return the arguments of this closure invocation.
     */
    List<Expr> arguments();

    /**
     * @return a new closure invocation like the receiver, but with the given arguments.
     */
    ClosureCall arguments(List<Expr> arguments);

 //   List<TypeNode> typeArgs();
 //   ClosureCall typeArgs(List<TypeNode> typeArgs);

    /** Get the method instance of the call. */
    public MethodInstance closureInstance();

    /** Set the method instance of the call. */
    public ClosureCall closureInstance(MethodInstance ci);
}
