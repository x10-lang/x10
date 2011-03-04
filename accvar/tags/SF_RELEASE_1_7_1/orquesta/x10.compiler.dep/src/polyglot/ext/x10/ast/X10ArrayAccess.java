/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 11, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Variable;

/**
 * @author vj Jan 11, 2005
 * 
 */
public interface X10ArrayAccess extends Variable {
	
	/**
	 * Array to access.
	 */
	Expr array();
	
	/**
	 * Set the array to access.
	 */
	X10ArrayAccess array(Expr array);
	
	/**
	 * Index into the array.
	 */
	List/*<Expr>*/ index();
	
	/**
	 * Set the index into the array.
	 */
	X10ArrayAccess index(List/*<Expr>*/ index);
	


}
