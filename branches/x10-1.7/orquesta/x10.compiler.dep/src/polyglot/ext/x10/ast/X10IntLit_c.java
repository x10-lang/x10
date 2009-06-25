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

import polyglot.ast.Node;
import polyglot.ast.IntLit.Kind;
import polyglot.ast.IntLit_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

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
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
		  if (kind==INT) {
			  if (value > ((long)Integer.MAX_VALUE)+1  )
			  throw new SemanticException("Integer literal " + value + " is out of range.",position());
			  if (boundary()) {
				  value = - (int) value;
			  }
		  }
		  X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		  X10Type Type = (X10Type) (kind==INT ? xts.Int() : xts.Long());
		 
			C_Lit_c literal = new C_Lit_c(constantValue(), Type);
			Constraint c = Constraint_c.addSelfBinding(literal,null,xts);
			X10Type newType  = X10TypeMixin.makeDepVariant(Type, c);
	    return type(newType);
	  }
}
