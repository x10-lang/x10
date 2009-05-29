/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Stmt_c;
import polyglot.ext.x10.types.X10Context;
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

/**
 * An immutable representation of the when statement.
 * [IP] Implements CompoundStmt, since it can be unreachable.
 */
public class When_c extends Stmt_c implements CompoundStmt, When {

	protected List/*<Position>*/ positions;
	protected List/*<Expr>*/ exprs;
	protected List/*<Stmt>*/ stmts;

	protected Expr expr;
	protected Stmt stmt;

	public Expr expr() { return this.expr; }
	public Stmt stmt() { return this.stmt; }

	public When_c(Position p, Expr expr, Stmt stmt) {
		super(p);
		this.expr = expr;
		this.stmt = stmt;
		this.positions = new TypedList(new LinkedList(), Position.class, false);
		this.exprs = new TypedList(new LinkedList(), Expr.class, false);
		this.stmts = new TypedList(new LinkedList(), Stmt.class, false);
	}

	public void addBranch(Position p, Expr e, Stmt s) {
		this.positions.add(p);
		this.exprs.add(e);
		this.stmts.add(s);
	}

	public List exprs() {
		return exprs;
	}

	public List stmts() {
		return stmts;
	}

	private boolean isSame(List a, List b) {
		if (a.size() != b.size()) return false;
		Iterator i = a.iterator();
		Iterator j = b.iterator();
		while (i.hasNext())
			if (i.next() != j.next()) return false;
		return true;
	}

	/** Reconstruct the statement. */
	public When reconstruct(Expr expr, Stmt stmt, List exprs, List stmts) {
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
		List es = visitList(exprs, v);
		List ss = visitList(stmts, v);
		return reconstruct(e, s, es, ss);
	}

	/** Type check the statement. */
	// TODO: cvp -> vj implement this
	public Node typeCheck(ContextVisitor tc) throws SemanticException {

    	X10Context c = (X10Context) tc.context();
    	if (c.inNonBlockingCode())
    		throw new SemanticException("The when statement cannot be used in nonblocking code.", position());
    	return super.typeCheck(tc);
		
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
			Iterator es = exprs.iterator();
			Iterator ss = stmts.iterator();
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
	public List acceptCFG(CFGBuilder v, List succs) {
		Expr e = expr;
		Stmt s = stmt;
		Expr ne = null;
		Iterator es = exprs.iterator();
		Iterator ss = stmts.iterator();
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

