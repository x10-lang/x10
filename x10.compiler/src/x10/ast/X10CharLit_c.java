/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package x10.ast;

import polyglot.ast.CharLit_c;
import polyglot.ast.Node;
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
			 
			  CConstraint c = new CConstraint_c();
			  XTerm term = xts.xtypeTranslator().trans(c, this.type(charType), (X10Context) tc.context());
			  try {
				  c.addSelfBinding(term);
			  }
			  catch (XFailure e) {
			  }
			  Type newType = X10TypeMixin.xclause(charType, c);
	    return type(newType);
	  }
}
