/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import java.util.List;

import polyglot.types.Def;
import polyglot.types.MemberInstance;
import polyglot.types.ObjectType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;

/**
 * Generalization of path types and typedef'd types.
 *
 * Path types:
 *     x.T == T(x:C)
 *
 * Typedefs:
 *     type Int(x: Int) = Int{self==x};
 *     Int(4) == Int(x: Int){x==4} == Int{self==x,x==4}
 *
 *     type nlist[T](x: Int) = List[T]{length==x};
 *     nlist[int](7) == nlist[T](x: Int){T==int,x==7} == List{ElementType==T,length==x,T==int,x==7}
 *
 */
public interface ParametrizedType extends X10NamedType, ReferenceType, StructType, ObjectType {
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
