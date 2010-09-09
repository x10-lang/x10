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
import polyglot.ast.StringLit_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.X10Context;

import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;

/**
 * @author vj
 *
 */
public class X10StringLit_c extends StringLit_c {

	/**
	 * @param pos
	 * @param value
	 */
	public X10StringLit_c(Position pos, String value) {
		super(pos, value);
	}
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10TypeSystem xts= (X10TypeSystem) tc.typeSystem();
		Type Type = xts.String();

		CConstraint c = new CConstraint_c();
		XTerm term = xts.xtypeTranslator().trans(c, this.type(Type), (X10Context) tc.context());
		try {
			c.addSelfBinding(term);
		}
		catch (XFailure e) {
		}
		Type newType = X10TypeMixin.xclause(Type, c);
		return type(newType);
	}

}
