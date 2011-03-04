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
import polyglot.ast.Variable;

/**
 * @author vj Jan 11, 2005
 * 
 */
public interface Settable extends Variable {
	
	/**
	 * Array to access.
	 */
	Expr array();
	
	/**
	 * Set the array to access.
	 */
	Settable array(Expr array);
	
	/**
	 * Index into the array.
	 */
	List<Expr> index();
	
	/**
	 * Set the index into the array.
	 */
	Settable index(List<Expr> index);
	


}
