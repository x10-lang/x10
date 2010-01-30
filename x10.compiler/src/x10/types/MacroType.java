/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.MemberInstance;
import polyglot.types.Named;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XConstraint;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;

public interface MacroType extends ParametrizedType, MemberInstance<TypeDef>, X10ProcedureInstance<TypeDef>, Named {
	Type definedType();
	Ref<? extends Type> definedTypeRef();
	MacroType definedTypeRef(Ref<? extends Type> t);
	
	TypeDef def();
	
	MacroType name(Name name);
	MacroType typeParameters(List<Type> typeParams);
	MacroType formals(List<XVar> formals);
	MacroType formalTypes(List<Type> formalTypes);
	MacroType guard(CConstraint guard);
}
