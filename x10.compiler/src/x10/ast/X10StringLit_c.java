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

import polyglot.ast.Node;
import polyglot.ast.StringLit_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
import polyglot.types.Context;

import polyglot.types.TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;

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
	public Node typeCheck(ContextVisitor tc) {
		TypeSystem xts= (TypeSystem) tc.typeSystem();
		Type Type = xts.String();

		CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
		  try {
		    	XTerm term = xts.xtypeTranslator().translate(c, this.type(Type), tc.context());
		    	c.addSelfBinding(term);
		    } catch (IllegalConstraint z) {
		    	Errors.issue(tc.job(), z);
		    }
		
		Type newType = Types.toConstrainedType(Types.xclause(Type, c)).addNonNull(); // a literal is non-null
		return type(newType);
	}

}
