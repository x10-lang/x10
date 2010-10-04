package x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

/**
 * The interface implemented by an array literal. An array literal is simply a tuple with a 
 * user-specified return type.
 * @author vj
 *
 */
public interface ArrayLiteral extends Expr {
	Tuple_c tuple();
	TypeNode indexType();

}
