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

import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.Context;
import x10.types.X10TypeMixin;
import x10.types.TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;


/**
 * An immutable representation of a char lit, modified from JL 
 * to support a self-clause in the dep type.
 * @author vj
 *
 */
public class X10CharLit_c extends CharLit_c {

	/**
	 * @param pos
	 * @param value
	 */
	public X10CharLit_c(Position pos, char value) {
		super(pos, value);
	}
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		  TypeSystem xts = (TypeSystem) tc.typeSystem();
		Type charType = xts.Char();
			 
			  CConstraint c = new CConstraint();
			  XTerm term = xts.xtypeTranslator().trans(c, this.type(charType), (Context) tc.context());
			  try {
				  c.addSelfBinding(term);
			  }
			  catch (XFailure e) {
			  }
			  Type newType = X10TypeMixin.xclause(charType, c);
	    return type(newType);
	  }
}
