/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.If_c;
import polyglot.util.Position;

/**
 * @author vj
 *
 */
public class X10If_c extends If_c {

	/**
	 * @param pos
	 * @param cond
	 * @param consequent
	 * @param alternative
	 */
	public X10If_c(Position pos, Expr cond, Stmt consequent, Stmt alternative) {
		super(pos, cond, consequent, alternative);
		
	}

}
