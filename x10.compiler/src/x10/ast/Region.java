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

import java.util.List;

import polyglot.ast.Expr;

/** An immutable representation of the X10 construct [range1,...,rangen] 
 * representing an n-ary region. (We may choose to represent this instead as
 * new region(range1, ..., rangen);)
 * @author vj Dec 9, 2004
 * 
 */
public interface Region extends Expr {
	int rank();
	Expr index(int i);
	List<Expr> ranges();
	
	// Do we really need updaters?

}
