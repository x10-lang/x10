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
import polyglot.ast.StringLit_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

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
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem xts= (X10TypeSystem) tc.typeSystem();
		X10Type Type = (X10Type) xts.String();
		 
			C_Lit_c literal = new C_Lit_c(value, Type);
			Constraint c = Constraint_c.addSelfBinding(literal,null,xts);
		  X10Type newType  = X10TypeMixin.makeDepVariant(Type, c);
	    return type(newType);
	  }

}
