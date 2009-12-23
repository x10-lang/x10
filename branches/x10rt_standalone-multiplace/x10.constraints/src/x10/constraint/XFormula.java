/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.List;

public interface XFormula extends XTerm {
	List<XTerm> arguments();
	XName operator();

	boolean isUnary();
	XTerm unaryArg();
	
	boolean isBinary();
	XTerm left();
	XTerm right();
}
