/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public abstract class C_Var_c extends C_Term_c implements C_Var {
	public C_Var_c() {
		super();
	}

	public C_Var substitute(C_Var value, C_Var var) {
		return equals(var) ? value : this;
	}

	public Promise internIntoConstraint(Constraint c, Promise last) throws Failure {
		C_Var[] vars = vars();
		C_Var baseVar = vars[0];
		Promise p = c.internBaseVar(baseVar, vars.length == 1, last);
		return p.intern(vars, 1, last);
	}
}
