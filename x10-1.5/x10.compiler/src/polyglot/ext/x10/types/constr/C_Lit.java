/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;


public interface C_Lit extends C_Var, Promise {
	Object val();
	C_Lit not();
	C_Lit neg();
}
