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
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.visit.TypeChecker;

public class TypeCheckReturnTypeGoal extends TypeCheckFragmentGoal {
	public TypeCheckReturnTypeGoal(Node parent, Node n, TypeChecker v, LazyRef r,
			boolean mightFail) {
		super(parent, n, v, r, mightFail);
	}

	@Override
	public boolean runTask() {
		
	    TypeSystem ts = v.typeSystem();
		boolean result = super.runTask();
		if (result) {
			if (r.getCached() instanceof UnknownType) {
				// Body had no return statement.  Set to void.
				((Ref<Type>) r).update(ts.Void());
			}
		}
		return result;
	}
}
