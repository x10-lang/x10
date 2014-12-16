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

package x10.ast;

import java.util.List;

import polyglot.ast.Formal;
import polyglot.ast.TypeNode;

public interface FunctionTypeNode extends TypeNode, Guarded {

	/** Get the return type of the function type. */
	TypeNode returnType();

	/** Set the return type of the function type. */
	FunctionTypeNode returnType(TypeNode returnType);

	/** Get the type parameters of the function type. */
	List<TypeParamNode> typeParameters();

	/** Set the type parameters of the function type. */
	FunctionTypeNode typeParameters(List<TypeParamNode> typeParams);

	/** Get the formals of the function type. */
	List<Formal> formals();

	/** Set the formals of the function type. */
	FunctionTypeNode formals(List<Formal> formals);

	DepParameterExpr guard();
	FunctionTypeNode guard(DepParameterExpr guard);
}
