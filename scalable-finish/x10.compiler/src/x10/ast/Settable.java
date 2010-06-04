/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 11, 2005
 *
 * 
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
