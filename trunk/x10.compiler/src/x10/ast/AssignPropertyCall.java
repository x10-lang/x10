/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.util.TypedList;

/**
 * An immutable representation of a prop(e1,..., en) statement in the body of a constructor.
 * @author vj
 *
 */
public interface AssignPropertyCall extends Stmt {
	/** Return a copy of this node with this.expr equal to the given expr.
	   * @see x10.ast.Await#expr(polyglot.ast.Expr)
	   */
	public AssignPropertyCall args(List<Expr> args);
	public List<Expr> args();
}
