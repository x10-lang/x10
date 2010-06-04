/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import polyglot.ast.Expr;

public interface Contains extends Expr {
	Expr item();
	Contains item(Expr sub);

	Expr collection();
	Contains collection(Expr sup);

	Contains isSubsetTest(boolean f);
	boolean isSubsetTest();

}
