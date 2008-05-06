/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.List;

public interface C_Formula extends C_Term {
	List<C_Term> arguments();

	C_Name operator();

	boolean isUnary();
	C_Term unaryArg();
	
	boolean isBinary();
	C_Term left();
	C_Term right();
}
