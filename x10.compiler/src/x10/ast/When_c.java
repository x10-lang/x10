/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Stmt_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.types.Context;
import x10.errors.Errors;

/**
 * An immutable representation of the when statement.
 * [IP] Implements CompoundStmt, since it can be unreachable.
 */
public class When_c extends Stmt_c implements CompoundStmt, When {

	protected List<Position> positions;
	protected List<Expr> exprs;
	protected List<Stmt> stmts;

	protected Expr expr;
	protected Stmt stmt;

	public Expr expr() { return this.expr; }
	public Stmt stmt() { return this.stmt; }

	public When_c(Position p, Expr expr, Stmt stmt) {
		super(p);
		this.expr = expr;
		this.stmt = stmt;
		this.positions = new TypedList<Position>(new LinkedList<Position>(), Position.class, false);
		this.exprs = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
		this.stmts = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
	}

	public void addBranch(Position p, Expr e, Stmt s) {
		this.positions.add(p);
		this.exprs.add(e);
		this.stmts.add(s);
	}

	public List<Expr> exprs() {
		return exprs;
	}

	public List<Stmt> stmts() {
		return stmts;
	}

	private <T> boolean isSame(List<T> a, List<T> b) {
		if (a.size() != b.size()) return false;
		Iterator<T> i = a.iterator();
		Iterator<T> j = b.iterator();
		while (i.hasNext())
			if (i.next() != j.next()) return false;
		return true;
	}

	/** Reconstruct the statement. */
	public When reconstruct(Expr expr, Stmt stmt, List<Expr> exprs, List<Stmt> stmts) {
		if (expr == this.expr && stmt == this.stmt &&
			isSame(exprs, this.exprs) && isSame(stmts, this.stmts))
		{
			return this;
		}
		When_c n = (When_c) copy();
		n.expr = expr;
		n.stmt = stmt;
		n.exprs = TypedList.copyAndCheck(exprs, Expr.class, true);
		n.stmts = TypedList.copyAndCheck(stmts, Stmt.class, true);
		return n;
	}

	/** Visit the children of the statement. */
	public Node visitChildren(NodeVisitor v) {
		Expr e = (Expr) visitChild(expr, v);
		Stmt s = (Stmt) visitChild(stmt, v);
		List<Expr> es = visitList(exprs, v);
		List<Stmt> ss = visitList(stmts, v);
		return reconstruct(e, s, es, ss);
	}

	public String toString() {
		return "when (" + expr + ")" + stmt + (exprs.size() > 0 ? "..." : "");
	}

	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("when (");
		printBlock(expr, w, tr);
		w.write(") ");
		printSubStmt(stmt, w, tr);
		if (exprs.size() > 0) {
			Iterator<Expr> es = exprs.iterator();
			Iterator<Stmt> ss = stmts.iterator();
			while (es.hasNext()) {
				Expr e = (Expr) es.next();
				Stmt s = (Stmt) ss.next();
				w.write("or (");
				printBlock(e, w, tr);
				w.write(") ");
				printSubStmt(s, w, tr);
			}
		}
	}

	@Override
	public Node typeCheck(ContextVisitor tc) {
	    if (!expr().type().isBoolean()) {
	        Errors.issue(tc.job(), new Errors.ArgumentOfWhenMustBeBoolean(position()));
	    }
	    return this;
	}

	/**
	 * Return the first (sub)term performed when evaluating this
	 * term.
	 */
	public Term firstChild() {
		return expr;
	}

	/**
	 * Visit this term in evaluation order.
	 * Each expression goes to the corresponding statement if true or to the
	 * next expression if false.  The last expression wraps around to the
	 * first.  Each statement goes to this "when".
	 */
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		Expr e = expr;
		Stmt s = stmt;
		Expr ne = null;
		Iterator<Expr> es = exprs.iterator();
		Iterator<Stmt> ss = stmts.iterator();
        while (es.hasNext()) {
			ne = (Expr) es.next();
			v.visitCFG(e, FlowGraph.EDGE_KEY_TRUE, s,
						  ENTRY, FlowGraph.EDGE_KEY_FALSE, ne, ENTRY);
			v.visitCFG(s, this, EXIT);
			e = ne;
			s = (Stmt) ss.next();
        }
		v.visitCFG(e, FlowGraph.EDGE_KEY_TRUE, s,
					  ENTRY, FlowGraph.EDGE_KEY_FALSE, expr, ENTRY);
		v.visitCFG(s, this, EXIT);
		return succs;
	}
}

