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

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import x10.types.X10Context;
import x10.types.X10TypeSystem;

/**
 * An immutable representation of the X10 statement: foreach (i : D) S
 * @author vj Dec 9, 2004
 * @author Christian Grothoff
 */
public class ForEach_c extends X10ClockedLoop_c implements ForEach, Clocked {

	/**
	 * @param pos
	 */
	public ForEach_c(Position pos) {
		super(pos);
		loopKind=LoopKind.FOREACH;
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param clocks
	 * @param body
	 */
	public ForEach_c(Position pos, Formal formal, Expr domain, List clocks, Stmt body) {
		super(pos, formal, domain, clocks, body);
		loopKind=LoopKind.FOREACH;
	}

	public String toString() {
		return "foreach (" + formal + ":" + domain + ")" + body;
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("foreach(");
		printBlock(formal, w, tr);
		w.write(" : ");
		printBlock(domain, w, tr);
		w.write(") ");
		printSubStmt(body, w, tr);
	}
}
