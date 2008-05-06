/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public interface C_Var extends C_Term {
	/**
	 * Return the result of substituting value for var in this.
	 * @param y -- the value to be substituted
	 * @param x  -- the variable which is being substituted for
	 * @return  the term with the substitution applied
	 */

	C_Var substitute(C_Var y, C_Var x);

	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	C_Var[] vars();

	/** In case this is a field selection x.f1...fn, return x, else this. */
	C_Var rootVar();

}
