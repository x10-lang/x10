/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.List;

import x10.ast.InlinableCall;
import x10.ast.X10Call;
import x10.types.MethodInstance;

/**
 * A <code>Call</code> is an immutable representation of a Java
 * method call.  It consists of a method name and a list of arguments.
 * It may also have either a Type upon which the method is being
 * called or an expression upon which the method is being called.
 */
public interface Call extends Expr, InlinableCall
{
    /**
     * The call's target object.
     */
    Receiver target();

    /**
     * Set the call's target.
     */
    X10Call target(Receiver target);
    
    /**
     * The name of the method to call.
     */
    Id name();
    
    /**
     * Set the name of the method to call.
     */
    X10Call name(Id name);
    
    /**
     * @return whether or not the non-virtual bit is set
     */
    boolean nonVirtual();
    
    /**
     * @param nv whether the call should be non-virtual
     * @return a copy of the Call node with the non-virtual bit set to nv
     */
    X10Call nonVirtual(boolean nv);

    /**
     * Indicates if the target of this call is implicit, that 
     * is, was not specified explicitly in the syntax.  
     * @return boolean indicating if the target of this call is implicit
     */
    boolean isTargetImplicit();
    
    /**
     * Set whether the target of this call is implicit.
     */
    X10Call targetImplicit(boolean targetImplicit);
    
    /**
     * The call's actual arguments.
     * @return A list of {@link polyglot.ast.Expr Expr}.
     */
    List<Expr> arguments();

    /**
     * Set the call's actual arguments.
     * @param arguments A list of {@link polyglot.ast.Expr Expr}.
     */
    X10Call arguments(List<Expr> arguments);

    /**
     * The type object of the method we are calling.  This is, generally, only
     * valid after the type-checking pass.
     */
    MethodInstance methodInstance();

    /**
     * Set the type object of the method we are calling.
     */
    X10Call methodInstance(MethodInstance mi);


	X10Call typeArguments(List<TypeNode> args);
}
