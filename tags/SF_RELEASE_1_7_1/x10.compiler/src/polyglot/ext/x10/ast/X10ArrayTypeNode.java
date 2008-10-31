/*
 *
 * (C) Copyright IBM Corporation 2006
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
import polyglot.ast.TypeNode;


/**
 * @author vj Dec 9, 2004
 * 
 */
public interface X10ArrayTypeNode /*extends ArrayTypeNode*/ extends TypeNode {
	TypeNode base();
	/**
	 * Was this array declared to be a value array? 
	 * @return
	 */
	boolean isValueType();
	
	/** Return the indexedset over which this array is defined.
	 * This may be an int n (standing for the range 1..n),
	 * a range, a region, or a distribution.
	 * 
	 * @author vj Dec 9, 2004
	 *
	 */
	Expr distribution();
	
	DepParameterExpr indexedSet();
	
	X10ArrayTypeNode reconstruct(TypeNode base,  Expr indexedSet);
}
