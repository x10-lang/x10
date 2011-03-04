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


/** An immutable representation of a point in a region.
 * @author vj
 *
 */
public interface Point extends Expr {
	
	/** Returns the rank of the point, i.e. its dimensionality.
	 * 
	 * @return The rank of the point.
	 */
	int rank();
	
	/** Returns the value of the point in the i'th dimension.
	 * @param i
	 * @return
	 */
	Expr valueAt(int i);
	
	/** Return a new Point whose value is given by this list of expressions.
	 * 
	 * @param l List of expressions for the new point.
	 * @return
	 */
	Point value( List l );

}
