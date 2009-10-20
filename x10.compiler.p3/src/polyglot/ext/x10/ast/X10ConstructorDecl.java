/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

public interface X10ConstructorDecl extends X10ProcedureDecl, ConstructorDecl {
	/**
	 * Return the returnType associated with this constructor declaration.
	 * @return
	 */
	TypeNode returnType();
	X10ConstructorDecl returnType(TypeNode returnType);
	
	X10ConstructorDecl guard(DepParameterExpr e);
	
	X10ConstructorDecl typeParameters(List<TypeParamNode> typeParams);
}
