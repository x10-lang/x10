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
package polyglot.ext.x10.ast;

import polyglot.ast.IntLit_c;
import polyglot.ast.Node;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.XTypeTranslator;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XTerm;

/**
 * An immutable representation of an int lit, modified from JL 
 * to support a self-clause in the dep type.
 * @author vj
 *
 */
public class X10IntLit_c extends IntLit_c {

	/**
	 * @param pos
	 * @param kind
	 * @param value
	 */
	public X10IntLit_c(Position pos, Kind kind, long value) {
		super(pos, kind,value);
	
	}
	 /** Type check the expression. */
	  public Node typeCheck(ContextVisitor tc) throws SemanticException {
		  if (kind==INT) {
			  if ((value > (long)Integer.MAX_VALUE || value < (long)Integer.MIN_VALUE) &&
				  (value & ~0xFFFFFFFFL) != 0L)
			  {
				  throw new SemanticException("Integer literal " + value + " is out of range.",position());
			  }
		  }
		  X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		  X10Type Type = (X10Type) (kind==INT ? xts.Int() : xts.Long());
		  
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
