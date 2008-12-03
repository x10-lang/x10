/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;

/** An immutable representation of the range expression lb .. ub : stride.
 * (1) Stride defaults to 1.
 * (2) lb, ub, stride must be  implicitly castable to the same fixed point type.
 * (3) lb, ub and stride must be immutable expressions.
 * @author vj Dec 9, 2004
 * 
 */
public interface Range extends Expr {
	Expr lowerBound();
	Expr upperBound();
	Expr stride();
	Range lowerBound( Expr lb );
	Range upperBound( Expr ub );
	Range stride( Expr stride );
	
}
