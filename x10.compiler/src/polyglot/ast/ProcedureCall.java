/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
