/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.util.Position;

/** An immutable representation of an X10 array access update: a[point] op= expr;
 * TODO
 * Typechecking rules:
 * (1) point must be of a type (region) that can be cast to the array index type.
 * (2) expr must be of a type that can be implicitly cast to the base type of the array.
 * (3) The operator, if any, must be permitted on the underlying type.
 * (4) No operator is allowed if the array is a value array.
 * @author vj Dec 9, 2004
 * 
 */
public class X10ArrayAccessAssign_c extends ArrayAccessAssign_c {

	/**
	 * @param pos
	 * @param left
	 * @param op
	 * @param right
	 */
	public X10ArrayAccessAssign_c(Position pos, ArrayAccess left, Operator op,
			Expr right) {
		super(pos, left, op, right);
		
	}

}
