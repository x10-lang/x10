/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.util.Position;
import java.util.List;

/**
 * The AST node representing the X10 construct when (c) {S} or (c) {S} ...
 * @author vj
 */
public interface When extends Stmt {
	/**
	 * Return the expression of the first branch.
	 */
	Expr expr();

	/**
	 * Return the statement of the first branch.
	 */
	Stmt stmt();

	/**
	 * Add a branch to the list. To be called only by the parser.
	 * @param p
	 * @param e
	 * @param s
	 */
	void addBranch(Position p, Expr e, Stmt s);

	/**
	 * Return the expressions on the other branches of the when statement.
	 */
	List exprs();

	/**
	 * Return the statements on the other branches of the when statement.
	 */
	List stmts();
}
