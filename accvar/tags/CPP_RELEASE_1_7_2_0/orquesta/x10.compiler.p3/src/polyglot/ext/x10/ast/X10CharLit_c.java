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

import polyglot.ast.CharLit_c;
import polyglot.ast.Node;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XTerm;

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
		  X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		Type charType = xts.Char();
			 
			  XConstraint c = new XConstraint_c();
			  XTerm term = xts.xtypeTranslator().trans(this.type(charType));
			  try {
				  c.addSelfBinding(term);
			  }
			  catch (XFailure e) {
			  }
			  Type newType = X10TypeMixin.xclause(charType, c);
	    return type(newType);
	  }
}
