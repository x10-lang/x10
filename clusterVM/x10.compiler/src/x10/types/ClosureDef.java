/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.FunctionDef;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface ClosureDef extends FunctionDef, X10Def, X10ProcedureDef {
    
    ClosureInstance asInstance();
    public ClosureType asType();
    
    /** Set a flag indicating we should infer the return type. */
    boolean inferReturnType();
    void inferReturnType(boolean r);

    Ref<? extends CodeInstance<?>> methodContainer();
    void setMethodContainer(Ref<? extends CodeInstance<?>> mi);
    
    Ref<? extends ClassType> typeContainer();
    void setTypeContainer(Ref<? extends ClassType> ct);
    
    List<Ref<? extends Type>> typeParameters();
    void setTypeParameters(List<Ref<? extends Type>> typeParameters);
}
