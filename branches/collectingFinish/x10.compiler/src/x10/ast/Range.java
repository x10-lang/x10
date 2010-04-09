/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

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
