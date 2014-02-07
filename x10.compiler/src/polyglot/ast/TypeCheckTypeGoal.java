/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import polyglot.types.LazyRef;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.visit.TypeChecker;

public class TypeCheckTypeGoal extends TypeCheckFragmentGoal<Type> {
	private static final long serialVersionUID = -7957786015606044283L;

	public TypeCheckTypeGoal(Node parent, Node n, TypeChecker v, LazyRef<Type> r, boolean mightFail) {
		super(parent, n, v, r, mightFail);
	}

	@Override
	public boolean runTask() {
		boolean result = super.runTask();
		if (result) {
			if (r().getCached() instanceof UnknownType) {
				v().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not compute type.", n().position());
				return false;
			}
		}
		return result;
	}
}
