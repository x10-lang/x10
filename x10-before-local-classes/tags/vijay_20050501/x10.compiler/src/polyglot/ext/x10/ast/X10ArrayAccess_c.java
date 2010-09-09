/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ext.jl.ast.ArrayAccess_c;
import polyglot.util.Position;

/** An immutable representation of a (multdimensional) X10 array access.
 *  TODO:
 *  (1) The index must be a Point whose static type (region) can be 
 *      cast to the index type of the array.
 *      Otherwise a semantic error must be thrown.
 * (2) Check if Java catches IndexOutOfBounds errors at compiletime for a constant point. 
 *      If so, X10 should do the same.
 * (3) The X10 runtime should throw a "PointOutOfRegion" error when a variable of type
 *     region takes a value (e.g. as a result of arithmetic operations) not in the region.
 * 
 * @author vj Dec 9, 2004
 * 
 */
public class X10ArrayAccess_c extends ArrayAccess_c {

	/**
	 * @param pos
	 * @param array
	 * @param index
	 */
	public X10ArrayAccess_c(Position pos, Expr array, Expr index) {
		super(pos, array, index);
		
	}

}
