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

package polyglot.types;

import x10.types.constraints.CConstraint;
import x10.types.constraints.CRequirement;
import x10.types.constraints.CRequirementCollection;
import x10.types.constraints.TypeConstraint;
import x10.types.X10CodeDef;
import x10.types.X10MemberDef;

import java.util.List;

/**
 * A <code>ProcedureInstance</code> contains the type information for a Java
 * procedure (either a method or a constructor).
 */
public interface ProcedureDef extends CodeDef, X10CodeDef, X10MemberDef
{
    /**
     * List of formal parameter types.
     * @return A list of <code>Type</code>.
     * @see polyglot.types.Type
     */
    List<Ref<? extends Type>> formalTypes();
    void setFormalTypes(List<Ref<? extends Type>> l);
    
    
    /**
     * Returns a String representing the signature of the procedure.
     * This includes just the name of the method (or name of the class, if
     * it is a constructor), and the argument types.
     */
    String signature();

    /**
     * String describing the kind of procedure, (e.g., "method" or "constructor").
     */
    String designator();

    Ref<? extends Type> returnType();
    void setReturnType(Ref<? extends Type> rt);

    /** Set a flag indicating we should infer the return type. */
    boolean inferReturnType();
    void inferReturnType(boolean r);

    Ref<CConstraint> guard(); // yoav todo: I think the guard should be kept as a DepParameterExpr (and not as a CContstraint). see e.g., Desugarer.desugarCall that converts a constraint to an expression.
    void setGuard(Ref<CConstraint> s);

    Ref<CConstraint> sourceGuard();
    void setSourceGuard(Ref<CConstraint> s);

    Ref<TypeConstraint> typeGuard();
    void setTypeGuard(Ref<TypeConstraint> s);

    List<LocalDef> formalNames();
    void setFormalNames(List<LocalDef> formalNames);

    Ref<? extends Type> offerType();

    List<Ref<? extends Type>> throwTypes();
    void setThrowTypes(List<Ref<? extends Type>> l);

    /** Set a flag indicating we should infer addition constraints in the guard. */
    boolean inferGuard();
    void inferGuard(boolean r);
    CRequirementCollection requirements();
}
