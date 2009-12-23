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

import polyglot.ast.FloatLit_c;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.X10Context;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator;

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
	  public Node typeCheck(ContextVisitor tc) throws SemanticException {
		  X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		  X10Type Type = (X10Type) (kind==FLOAT ? xts.Float() : xts.Double());
		  
			  XConstraint c = new XConstraint_c();
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
