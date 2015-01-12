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

import java.util.List;

import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.types.ConstructorInstance;
import x10.types.X10ConstructorInstance;
import x10.types.MethodInstance;

public interface X10ConstructorCall extends ConstructorCall {
	List<TypeNode> typeArguments();

	X10ConstructorCall typeArguments(List<TypeNode> args);

	X10ConstructorCall qualifier(Expr qualifier);
	X10ConstructorCall kind(Kind kind);
	X10ConstructorCall arguments(List<Expr> arguments);
	X10ConstructorInstance constructorInstance();
	X10ConstructorCall constructorInstance(ConstructorInstance ci);
	X10ConstructorCall target(Expr target);
}
