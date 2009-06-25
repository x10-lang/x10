/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

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
	
	DepParameterExpr whereClause();
	X10ConstructorDecl whereClause(DepParameterExpr e);
	
}
