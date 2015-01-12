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

import polyglot.ast.FloatLit_c;
import polyglot.ast.Node;
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
 * An immutable representation of a float lit, modified from JL 
 * to support a self-clause in the dep type.
 * @author vj
 *
 */
public class X10FloatLit_c extends FloatLit_c {

	/**
	 * @param pos
	 * @param kind
	 * @param value
	 */
	public X10FloatLit_c(Position pos, Kind kind, double value) {
		super(pos, kind, value);
	}

	public Node typeCheck(ContextVisitor tc) {
	    TypeSystem xts = (TypeSystem) tc.typeSystem();
	    Type type = (kind == FLOAT ? xts.Float() : xts.Double());

	    CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
	    try {
	    	XTerm term = xts.xtypeTranslator().translate(c, this.type(type),  tc.context());
	    	 c.addSelfBinding(term);
	    } catch (IllegalConstraint z) {
	    	Errors.issue(tc.job(), z);
	    }
	    Type newType = Types.xclause(type, c);
	    return type(newType);
	}

}
