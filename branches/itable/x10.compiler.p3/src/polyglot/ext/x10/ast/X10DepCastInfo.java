/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.types.Type;

public interface X10DepCastInfo extends X10CastInfo {

	public DepParameterExpr depParameterExpr();
}
