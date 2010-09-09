/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.If_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

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
	  /** Type check the statement. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

	if (! ts.isSubtype(cond.type(), ts.Boolean())) {
	    throw new SemanticException(
		"Condition of if statement must have boolean type, and not " + cond.type() + ".",
		cond.position());
	}

	return this;
    }

}
