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
import polyglot.ast.CharLit_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

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
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		  X10Type charType = (X10Type) tc.typeSystem().Char();
		 
			C_Lit_c literal = new C_Lit_c(new Character((char) value), charType);
			Constraint c = Constraint_c.addSelfBinding(literal,null,(X10TypeSystem) tc.typeSystem());
		  X10Type newType  = X10TypeMixin.makeDepVariant(charType, c);
		  //Report.report(1, "X10CharLit: type for " + this + " is " + newType+".");
	    return type(newType);
	  }
}
