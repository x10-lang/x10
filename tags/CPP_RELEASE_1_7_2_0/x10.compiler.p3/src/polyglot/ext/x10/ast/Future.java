/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.types.Type;

/** The AST node for the X10 construct future (P) {e}
 *
 */
public interface Future extends Closure, RemoteActivityInvocation {
}
