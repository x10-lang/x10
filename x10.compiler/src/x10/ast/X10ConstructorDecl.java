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

package x10.ast;

import java.util.List;

import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.TypeNode;
import polyglot.types.ConstructorDef;
import x10.types.X10ConstructorDef;

public interface X10ConstructorDecl extends ConstructorDecl, Guarded {
	/**
	 * Return the returnType associated with this constructor declaration.
	 * @return
	 */
	TypeNode returnType();
	X10ConstructorDecl returnType(TypeNode returnType);
	
	DepParameterExpr guard();
	X10ConstructorDecl guard(DepParameterExpr e);
	
	TypeNode offerType();
	X10ConstructorDecl offerType(TypeNode offerType);
	
	List<TypeParamNode> typeParameters();
	X10ConstructorDecl typeParameters(List<TypeParamNode> typeParams);

	X10ConstructorDecl flags(FlagsNode flags);
	X10ConstructorDecl name(Id name);
	X10ConstructorDecl formals(List<Formal> formals);
	X10ConstructorDecl constructorDef(ConstructorDef ci);
	X10ConstructorDef constructorDef();
	List<TypeNode> throwsTypes();
	X10ConstructorDecl throwsTypes(List<TypeNode> processedThrowsTypes);
}
