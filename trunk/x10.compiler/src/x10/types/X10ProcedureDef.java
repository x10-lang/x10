/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.LocalDef;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.types.constraints.CConstraint;

public interface X10ProcedureDef extends X10Def, ProcedureDef, X10MemberDef {
    Ref<? extends Type> returnType();

    Ref<CConstraint> guard();
    void setGuard(Ref<CConstraint> s);
    
    Ref<TypeConstraint> typeGuard();
    void setTypeGuard(Ref<TypeConstraint> s);

    List<Ref<? extends Type>> typeParameters();
    void setTypeParameters(List<Ref<? extends Type>> typeParameters);

    List<LocalDef> formalNames();
    void setFormalNames(List<LocalDef> formalNames);
}
