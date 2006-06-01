/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import polyglot.ext.jl.ast.Stmt_c;

import polyglot.ast.Block;
import polyglot.ast.CompoundStmt;
import polyglot.ast.Stmt;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;

import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Type;

import polyglot.util.TypedList;
import polyglot.util.Position;
import polyglot.util.CodeWriter;

import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import polyglot.visit.AscriptionVisitor;
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
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();

		/*
		if (!ts.isSubtype(expr.type(), ts.Object())) {
			throw new SemanticException(
					"Cannot synchronize on an expression of type \"" +
					expr.type() + "\".", expr.position());
		}
		*/
		return super.typeCheck(tc);// this;
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
	public Term entry() {
		return expr.entry();
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
			v.visitCFG(e, FlowGraph.EDGE_KEY_TRUE, s.entry(),
						  FlowGraph.EDGE_KEY_FALSE, ne);
			v.visitCFG(s, this);
			e = ne;
			s = (Stmt) ss.next();
        }
		v.visitCFG(e, FlowGraph.EDGE_KEY_TRUE, s.entry(),
					  FlowGraph.EDGE_KEY_FALSE, this);
		v.visitCFG(s, this);
		return succs;
	}
}

