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

package x10.types;

import java.util.List;

import polyglot.types.Def;
import polyglot.types.MemberInstance;
import polyglot.types.Named;
import polyglot.types.ObjectType;
import polyglot.types.Ref;

import polyglot.types.Name;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;

/**
 * Types with type and value parameters. The only such types in X10 are typedef calls.
 *
 * Typedefs:
 *     type Int(x: Int) = Int{self==x};
 *     type nlist[T](x: Int) = List[T]{length==x};
 *
 * Typedef calls:
 *     Int(4) == Int{self==x} with 4 substituted for x
 *            == Int{self == 4}
 *     nlist[int](7) 
 *            == List{T}{length==x} with int substituted for T and 7 for x
 *            == List[int]{length==7}
 *
 */
public interface ParametrizedType extends Named, Type {
	Name name();

	List<Type> typeParameters();
	List<XVar> formals();
	List<Type> formalTypes();
	CConstraint guard();
	
	Def def();

	ParametrizedType formals(List<XVar> formals);
	ParametrizedType newFormalTypes(List<Type> formalTypes); // NOTE: javac reports an error in MacroType if this method is named formalTypes
	ParametrizedType newTypeParameters(List<Type> typeParams); // NOTE: ditto
}
