/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Variable;
import polyglot.util.Position;

/** An immutable representation of the X10 statement: ateach (i : D) S
 * @author vj Dec 9, 2004
 * 
 */
public class AtEach_c extends X10Loop_c {

	/**
	 * @param pos
	 */
	public AtEach_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param body
	 */
	public AtEach_c(Position pos, Variable formal, Expr domain, Stmt body) {
		super(pos, formal, domain, body);
		// TODO Auto-generated constructor stub
	}

}
