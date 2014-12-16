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

import polyglot.ast.Node;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.types.LazyRef;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.visit.TypeChecker;

public class TypeCheckReturnTypeGoal extends TypeCheckFragmentGoal<Type> {
	private static final long serialVersionUID = -7110597916418023302L;

	public TypeCheckReturnTypeGoal(Node parent, Node n, TypeChecker v, LazyRef<Type> r) {
	    this(parent, new Node[0], n, v, r);
	}

	public TypeCheckReturnTypeGoal(Node parent, Node[] prereqs, Node n, TypeChecker v, LazyRef<Type> r) {
	    this(parent, prereqs, n, v, r, v.typeSystem().Void());
	}

	public TypeCheckReturnTypeGoal(Node parent, Node n, TypeChecker v, LazyRef<Type> r, Type defaultValue) {
	    this(parent, new Node[0], n, v, r, defaultValue);
	}

	public TypeCheckReturnTypeGoal(Node parent, Node[] prereqs, Node n, TypeChecker v, LazyRef<Type> r, Type defaultValue) {
	    super(parent, prereqs, n, v, r, true);
	    this.defaultValue = defaultValue;
	}

	protected final Type defaultValue;

	@Override
	protected Type defaultRecursiveValue() {
	    // To preserve current behavior.
	    // TODO Yoav: we should report an error
	    //v().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not infer type; possible recursive function invocation.", n().position());
	    return defaultValue;
	}

	@Override
	protected Node process(Node parent, Node n, TypeChecker v) {
		Node m = super.process(parent, n, v);
		if (!r().known() && r().getCached() instanceof UnknownType) {
			// Body had no return statement.  Set to void.
			r().update(v.typeSystem().Void());
		}
		return m;
	}
}
