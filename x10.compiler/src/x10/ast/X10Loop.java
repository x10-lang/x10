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

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Loop;
import polyglot.ast.Stmt;

/**
 * @author vj Dec 9, 2004
 * @author igor Jan 19, 2006
 */
public interface X10Loop extends Loop {
	public enum LoopKind {
		FOR,
		FOREACH,
		ATEACH
	}
	Stmt body();
	X10Loop body(Stmt body);
	Formal formal();
	X10Loop formal(Formal formal);
	Expr domain();
	X10Loop domain(Expr domain);
	List<Stmt> locals();
	X10Loop locals(List<Stmt> locals);
}

