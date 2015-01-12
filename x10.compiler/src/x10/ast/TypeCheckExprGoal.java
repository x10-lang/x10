/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.types.LazyRef;
import polyglot.types.Type;
import polyglot.util.ErrorInfo;
import polyglot.visit.TypeChecker;
import x10.types.X10ClassType;

public class TypeCheckExprGoal extends TypeCheckFragmentGoal<Type> {
	private static final long serialVersionUID = -6191310441875644253L;

	public TypeCheckExprGoal(Node parent, Expr n, TypeChecker v, LazyRef<Type> r) {
		super(parent, n, v, r, false);
	}

	@Override
	protected Type defaultRecursiveValue() {
		v().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not infer type; possible recursive or ambiguous initialization.", n().position());
		return v().typeSystem().unknownType(n().position());
	}

	@Override
	protected Node process(Node parent, Node n, TypeChecker v) {
		Node m = super.process(parent, n, v);
		if (m instanceof Expr) {
			Type t = ((Expr) m).type();
			if (t instanceof X10ClassType) {
				X10ClassType ct = (X10ClassType) t;
				if (ct.isAnonymous()) {
					if (ct.interfaces().size() > 0)
						t = ct.interfaces().get(0);
					else
						t = ct.superClass();
				}
			}
			r().update(t);
		}
		return m;
	}

	@Override
	public String toString() {
		return name() + "[" + n() + "] (" + stateString() + ")";
	}
}
