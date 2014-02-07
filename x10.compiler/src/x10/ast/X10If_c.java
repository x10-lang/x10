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

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.If_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;

/**
 * @author vj
 *
 */
public class X10If_c extends If_c {

	/**
	 * @param pos
	 * @param cond
	 * @param consequent
	 * @param alternative
	 */
	public X10If_c(Position pos, Expr cond, Stmt consequent, Stmt alternative) {
		super(pos, cond, consequent, alternative);
	}

    /** Type check the if statement. */
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();

        if (! ts.isSubtype(cond.type(), ts.Boolean(), tc.context())) {
            Errors.issue(tc.job(),
                    new Errors.IfStatementMustHaveBooleanType(cond.type(), cond.position()),
                    this);
        }

        return this;
    }
}
