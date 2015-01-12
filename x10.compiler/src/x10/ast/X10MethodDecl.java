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

import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.TypeNode;
import polyglot.types.MethodDef;
import x10.types.X10MethodDef;

public interface X10MethodDecl extends MethodDecl, Guarded {
	// The method's guard.
	DepParameterExpr guard();
	X10MethodDecl guard(DepParameterExpr e);

	List<TypeParamNode> typeParameters();
	X10MethodDecl typeParameters(List<TypeParamNode> typeParams);

	TypeNode offerType();
	X10MethodDecl offerType(TypeNode offerType);

	X10MethodDecl flags(FlagsNode flags);
	X10MethodDecl returnType(TypeNode returnType);
	X10MethodDecl name(Id name);
	X10MethodDecl formals(List<Formal> formals);
	X10MethodDecl methodDef(MethodDef mi);
	X10MethodDef methodDef();
	List<TypeNode> throwsTypes();
	X10MethodDecl throwsTypes(List<TypeNode> throwsTypes);
}
