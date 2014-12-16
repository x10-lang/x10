/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import polyglot.ast.Expr;

/** The immutable representation of the X10 expression ( Expr ).
 * The parentheses need to be propagated to the output; it is not correct for the parser
 * to just drop them.
 * @author vj Feb 4, 2005
 * 
 */
public interface ParExpr extends Expr {
	Expr expr();
	ParExpr expr(Expr expr);

}
