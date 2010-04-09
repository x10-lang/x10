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

package x10.ast;

import java.util.List;

import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

public interface X10ConstructorDecl extends ConstructorDecl {
	/**
	 * Return the returnType associated with this constructor declaration.
	 * @return
	 */
	TypeNode returnType();
	X10ConstructorDecl returnType(TypeNode returnType);
	
	DepParameterExpr guard();
	X10ConstructorDecl guard(DepParameterExpr e);
	
	List<TypeParamNode> typeParameters();
	X10ConstructorDecl typeParameters(List<TypeParamNode> typeParams);
}
