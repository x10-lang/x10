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
import polyglot.ast.BooleanLit_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

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
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
	      X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		  X10Type Boolean = (X10Type) xts.Boolean();
		 
			C_Lit literal = value ? xts.TRUE() : xts.FALSE();
			Constraint c = Constraint_c.addSelfBinding(literal,null,xts);
		  X10Type newType  = X10TypeMixin.makeDepVariant(Boolean, c);
	    return type(newType);
	  }
}
