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

import polyglot.ast.Node;
import polyglot.types.LazyRef;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.visit.TypeChecker;

public class TypeCheckReturnTypeGoal extends TypeCheckFragmentGoal<Type> {
	private static final long serialVersionUID = -7110597916418023302L;
		
	public TypeCheckReturnTypeGoal(Node parent, Node n, TypeChecker v, LazyRef<Type> r) {
		super(parent, n, v, r, true);
	}

	@Override
	protected Type defaultRecursiveValue() {
		// To preserve current behavior.
		return v.typeSystem().Void();
	}

	@Override
	protected Node process(Node parent, Node n, TypeChecker v) {
		Node m = super.process(parent, n, v);
		if (r().getCached() instanceof UnknownType) {
			// Body had no return statement.  Set to void.
			r().update(v.typeSystem().Void());
		}
		return m;
	}
}
