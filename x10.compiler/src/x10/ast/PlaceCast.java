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


/**
 * @author vj May 18, 2005
 * 
 */
public interface PlaceCast extends Expr {
	
	 /**
     * The type to cast to.
     */
    Expr placeCastType();

    /**
     * Set the type to cast to.
     */
    PlaceCast placeCastType(Expr place);

    /**
     * The expression to cast.
     */
    Expr expr();

    /**
     * Set the expression to cast.
     */
    PlaceCast expr(Expr expr);

}
