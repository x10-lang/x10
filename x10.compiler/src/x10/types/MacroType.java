/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import java.util.List;

import polyglot.types.MemberInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XConstraint;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;

public interface MacroType extends ParametrizedType, MemberInstance<TypeDef>, X10ProcedureInstance<TypeDef> {
	Type definedType();
	MacroType definedType(Type t);
	Ref<? extends Type> definedTypeRef();
	MacroType definedTypeRef(Ref<? extends Type> t);
	
	TypeDef def();
	
	MacroType name(Name name);
	MacroType returnType(Type returnType);
	MacroType returnTypeRef(Ref<? extends Type> returnTypeRef);
	//MacroType typeParameters(List<Type> typeParams); // Sigh.  Stupid javac
	MacroType formals(List<XVar> formals);
	MacroType formalTypes(List<Type> formalTypes);
	MacroType guard(CConstraint guard);
}
