/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
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
