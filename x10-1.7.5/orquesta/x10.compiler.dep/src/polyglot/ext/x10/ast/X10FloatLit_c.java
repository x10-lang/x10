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
import polyglot.ast.FloatLit.Kind;
import polyglot.ast.FloatLit_c;
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
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
		  X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		  X10Type Type = (X10Type) (kind==FLOAT ? xts.Float() : xts.Double());
			C_Lit_c literal = new C_Lit_c(constantValue(), Type);
			Constraint c = Constraint_c.addSelfBinding(literal,null,xts);
		  X10Type newType  = X10TypeMixin.makeDepVariant(Type, c);
	    return type(newType);
	  }

}
