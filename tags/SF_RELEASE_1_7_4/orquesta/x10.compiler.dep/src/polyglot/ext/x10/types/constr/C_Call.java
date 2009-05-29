/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.List;

import polyglot.types.MethodInstance;

public interface C_Call extends C_Term {
	C_Term receiver();
	List<C_Term> args();
	MethodInstance mi();
}
