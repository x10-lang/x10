/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.BooleanLit_c;
import polyglot.ast.Node;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XSelf;
import x10.constraint.XTerm;

/**
 * @author vj
 *
 */
public class X10BooleanLit_c extends BooleanLit_c {

	/**
	 * @param pos
	 * @param value
	 */
	public X10BooleanLit_c(Position pos, boolean value) {
		super(pos, value);
		
	}
	 /** Type check the expression. */
	  public Node typeCheck(ContextVisitor tc) throws SemanticException {
	      X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		  X10Type Boolean = (X10Type) xts.Boolean();
		 
		  XConstraint c = new XConstraint_c();
		  XTerm term = xts.xtypeTranslator().trans(this.type(Boolean));
		  try {
			  c.addSelfBinding(term);
		  }
		  catch (XFailure e) {
		  }

		  Type newType = X10TypeMixin.xclause(Boolean, c);
		  return type(newType);
	  }
}
