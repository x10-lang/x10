package x10.refactorings;

import java.util.ArrayList;
import java.util.Collection;

import polyglot.ast.Assign;
import polyglot.ast.Eval;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.visit.NodeVisitor;

public class ExtractAssignStmtsVisitor extends NodeVisitor {

	private Collection<Stmt> assignStmts = new ArrayList<Stmt>();

	public NodeVisitor enter(Node par, Node n) {
		if (n instanceof Eval) {
			Eval eval_stmt = (Eval) n;
			if (eval_stmt.expr() instanceof Assign) {
				assignStmts.add(eval_stmt);
			}
		} else if (n instanceof LocalDecl) {
			assignStmts.add((LocalDecl) n);
		}
		return super.enter(n);
	}

	/**
	 * Returns the assignments that have been visited.
	 * 
	 * @return a collection of assignment statements, ordered by their position
	 *         in the AST
	 */
	public Collection<Stmt> getAssignStmts() {
		return assignStmts;
	}

}
