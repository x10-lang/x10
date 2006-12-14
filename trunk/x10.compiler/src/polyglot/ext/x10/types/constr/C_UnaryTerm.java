/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

public interface C_UnaryTerm extends C_Term {
	String op();
	C_Term arg();
}
