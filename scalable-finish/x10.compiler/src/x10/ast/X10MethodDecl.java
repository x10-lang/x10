/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ast.TypeNode;

public interface X10MethodDecl extends MethodDecl {
	// The method's guard.
	DepParameterExpr guard();
	X10MethodDecl guard(DepParameterExpr e);
	
	List<TypeParamNode> typeParameters();
	X10MethodDecl typeParameters(List<TypeParamNode> typeParams);
}
