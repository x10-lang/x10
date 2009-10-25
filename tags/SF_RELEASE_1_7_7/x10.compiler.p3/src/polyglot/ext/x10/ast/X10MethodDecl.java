/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ast.TypeNode;

public interface X10MethodDecl extends X10ProcedureDecl, MethodDecl {
	X10MethodDecl guard(DepParameterExpr e);
	
	X10MethodDecl typeParameters(List<TypeParamNode> typeParams);
}
