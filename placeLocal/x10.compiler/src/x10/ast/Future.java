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
package x10.ast;

import polyglot.ast.Expr;
import polyglot.types.Type;
import x10.visit.ExprFlattener;

/** The AST node for the X10 construct future (P) {e}
 *
 */
public interface Future extends Closure, RemoteActivityInvocation {
}
