/**
 * 
 */
package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/**
 * An implementation of collecting finish.
 * @author vj
 *
 */
public interface FinishExpr extends Expr{


	Expr reducer();
	Stmt body();
}
