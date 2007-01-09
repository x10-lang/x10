/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;


public interface C_Lit extends C_Var, Promise {
	
	public static final  C_Lit_c FALSE = new C_Lit_c(false);
	public static final  C_Lit_c TRUE = new C_Lit_c(true);
	public static final  C_Lit_c NEG_ONE = new C_Lit_c(new Integer(-1), Constraint_c.typeSystem.Int());
	public static final  C_Lit_c ZERO = new C_Lit_c(new Integer(0), Constraint_c.typeSystem.Int());
	public static final  C_Lit_c ONE = new C_Lit_c(new Integer(1), Constraint_c.typeSystem.Int());
	public static final  C_Lit_c TWO = new C_Lit_c(new Integer(2), Constraint_c.typeSystem.Int());
	public static final  C_Lit_c THREE = new C_Lit_c(new Integer(3), Constraint_c.typeSystem.Int());
	public static final C_Lit_c NULL = new C_Lit_c(null, Constraint_c.typeSystem.Null());
	Object val();
	C_Lit not();
	C_Lit neg();
}
