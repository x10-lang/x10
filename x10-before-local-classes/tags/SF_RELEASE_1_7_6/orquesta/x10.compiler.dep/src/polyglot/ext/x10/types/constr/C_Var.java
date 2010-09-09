/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.List;

public interface C_Var extends C_Term {
	
	/** The name of this variable. Only Localvars/specials have names, not field selections.*/
	String name();
	
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
	
	/** In case this is a field selection x.f1...fn, return the path f1...fn, else null. */
	Path path();
}
