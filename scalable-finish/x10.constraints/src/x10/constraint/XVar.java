/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public interface XVar extends XTerm {
	/** In case this is a field selection x.f1...fn, return x, x.f1, x.f1.f2, ... x.f1.f2...fn */
	XVar[] vars();

	/** In case this is a field selection x.f1...fn, return x, else this. */
	XVar rootVar();
}
