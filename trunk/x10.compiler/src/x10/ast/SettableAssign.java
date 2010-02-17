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

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.types.ClassDef;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import polyglot.util.TypedList;

public interface SettableAssign extends Assign {
    /** Get the array of the expression. */
	public Expr array();
	
	/** Set the array of the expression. */
	public SettableAssign array(Expr array);
	
	/** Get the index of the expression. */
	public List<Expr> index();
	
	/** Set the index of the expression. */
	public SettableAssign index(List<Expr> index) ;
	
	/**
	 * The ClassDef for the class in which this assignment occurs.
	 * @return
	 */
	public ClassDef invokingClass();

	public MethodInstance methodInstance();
	SettableAssign methodInstance(MethodInstance mi);
}
