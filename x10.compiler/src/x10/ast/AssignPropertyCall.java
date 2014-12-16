/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import x10.types.X10FieldInstance;

/**
 * An immutable representation of a property(e1,..., en) statement in the body of a constructor.
 */
public interface AssignPropertyCall extends Stmt {
	public List<Expr> arguments();
	public AssignPropertyCall arguments(List<Expr> args);
	public List<X10FieldInstance> properties();
	public AssignPropertyCall properties(List<X10FieldInstance> fi);
}
