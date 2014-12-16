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

import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;

/**
 * A <code>ProcedureCall</code> is an interface representing a
 * method or constructor call.
 */
public interface ProcedureCall extends Term
{
    /**
     * The call's actual arguments.
     * @return A list of {@link polyglot.ast.Expr Expr}.
     */
    List<Expr> arguments();

    /**
     * Set the call's actual arguments.
     * @param arguments A list of {@link polyglot.ast.Expr Expr}.
     * @return a copy of this ProcedureCall with the new arguments.
     */
    ProcedureCall arguments(List<Expr> arguments);

    /**
     * The type object of the method we are calling.  This is, generally, only
     * valid after the type-checking pass.
     */
    ProcedureInstance<? extends ProcedureDef> procedureInstance();

    /**
     * Set the type object for instance being called.
     * @return a copy of this ProcedureCall with the new type object.
     */
    ProcedureCall procedureInstance(ProcedureInstance<? extends ProcedureDef> pi);
                                       
    List<TypeNode> typeArguments();

    /**
     * Set the call's type arguments.
     * @param arguments A list of {@link polyglot.ast.TypeNode TypeNode}.
     * @return a copy of this ProcedureCall with the new type arguments.
     */
    ProcedureCall typeArguments(List<TypeNode> typeArguments);
}
