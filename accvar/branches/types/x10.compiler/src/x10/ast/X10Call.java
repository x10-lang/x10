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

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import x10.types.MethodInstance;

public interface X10Call extends Call, X10ProcedureCall {
	List<TypeNode> typeArguments();

	X10Call typeArguments(List<TypeNode> args);

	X10Call target(Receiver target);
	X10Call name(Id name);
	X10Call targetImplicit(boolean targetImplicit);
	X10Call arguments(List<Expr> arguments);
	MethodInstance methodInstance();
	X10Call methodInstance(MethodInstance mi);
}
