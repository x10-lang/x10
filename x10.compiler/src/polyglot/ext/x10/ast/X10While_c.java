/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.While_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * @author VijaySaraswat
 *
 */
public class X10While_c extends While_c {

	/**
	 * @param pos
	 * @param cond
	 * @param body
	 */
	public X10While_c(Position pos, Expr cond, Stmt body) {
		super(pos, cond, body);
		// TODO Auto-generated constructor stub
	}
	
	 /** Type check the statement. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
	TypeSystem ts = tc.typeSystem();
	
	if (! ts.isSubtype(cond.type(), ts.Boolean())) {
	    throw new SemanticException(
		"Condition of while statement must have boolean type, and not " + cond.type() + ".",
		cond.position());
	}
	
	return this;
    }

}
