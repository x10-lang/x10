/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.TypeNode;
import polyglot.ast.Expr;


/**
 * @author vj Dec 9, 2004
 * 
 */
public interface X10ArrayTypeNode /*extends ArrayTypeNode*/ extends TypeNode {
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
	Expr indexSet();
}
