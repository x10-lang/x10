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
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.PrettyPrinter;

/**
 * An immutable representation of the when statement.
 * [IP] Implements CompoundStmt, since it can be unreachable.
 */
public class When_c extends Stmt_c implements CompoundStmt, When {

    public static class Branch_c extends Stmt_c implements Branch {
    	public Expr expr;
    	public Stmt stmt;
    	public Expr expr() { return this.expr; }
    	public Stmt stmt() { return this.stmt; }

        public Branch branch(Expr expr, Stmt stmt) {
    		if (expr != this.expr || stmt != this.stmt) {
    			return new Branch_c(position, expr, stmt);
    		}
    		return this;
    	}

        public Branch_c(Position p, Expr expr, Stmt stmt) {
    		super(p);
            this.expr = expr;
    		this.stmt = stmt;
    	}

//      TODO: cvp -> vj implement this
        public Node typeCheck(TypeChecker tc) throws SemanticException {
    	    TypeSystem ts = tc.typeSystem();
    	    return super.typeCheck(tc);
    	}

    	/** Visit the children of the statement. */
    	public Node visitChildren( NodeVisitor v ) {
    	    Expr e = (Expr) visitChild(expr, v);
    	    Stmt s = (Stmt) visitChild(stmt, v);
    	    return reconstruct(e, s);
    	}

    	/** Reconstruct the statement. */
    	protected Node reconstruct( Expr e, Stmt s) {
    	    if ( e != this.expr || s != this.stmt ) {
    	        Branch_c n = (Branch_c) copy();
    	        n.expr = e;
    	        n.stmt = s;
    	        return n;
    	    }
    	    return this;
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
         */
        public List acceptCFG(CFGBuilder v, List succs) {
            v.visitCFG(expr, stmt.entry());
            v.visitCFG(stmt, this);
            return succs;
        }
    }

    // Optimize the implementation of single-armed whens.
    Expr expr;
    Stmt stmt;

    public Expr expr() { return this.expr; }

    public Stmt stmt() { return this.stmt; }

	public /*final*/ List/*<Branches>*/ rest;


    public When_c(Position p, Expr expr, Stmt stmt) {
    	super(p);
    	this.expr = expr;
    	this.stmt = stmt;
    	this.rest = new TypedList(new LinkedList(), Branch_c.class, false);
    }


    public void add( Branch b) {
    	this.rest.add( b );
    }

    public List branches() {
    	List result = new LinkedList();
    	result.add(new Branch_c(position, expr, stmt));
    	result.addAll( rest);
    	return result;
    }

    /** Set the branches of the statement. */
    public When branches( Expr expr, Stmt stmt, List branches ) {
    	When_c n = (When_c) copy();
    	n.expr = expr;
    	n.stmt = stmt;
    	n.rest = TypedList.copyAndCheck(branches, Branch_c.class, true);
    	return n;
    }

    /** Visit the children of the statement. */
    public Node visitChildren( NodeVisitor v ) {
    	Expr e = (Expr) visitChild(expr, v);
    	Stmt s = (Stmt) visitChild(stmt, v);
    	List result = visitList(rest, v);
    	return branches( e, s, result);
    }

    /** Type check the statement. */
//  TODO: cvp -> vj implement this
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	TypeSystem ts = tc.typeSystem();

    	/*
    	 if (! ts.isSubtype(expr.type(), ts.Object()) ) {
    	 throw new SemanticException(
    	 "Cannot synchronize on an expression of type \"" +
    	 expr.type() + "\".", expr.position());
    	 }
    	 */
    	return super.typeCheck(tc);// this;
    }


    public String toString() {
    	return "when (" + expr + ")" + stmt
		+ (rest.size() > 0 ? "..." : "");
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	w.write("when (");
    	printBlock(expr, w, tr);
    	w.write(") ");
    	printSubStmt(stmt, w, tr);
    	if (rest.size() > 0)
    		for (Iterator it = rest.iterator(); it.hasNext();) {
    			Branch_c branch = (Branch_c) it.next();
    			w.write("or (" );
    			printBlock( branch.expr, w, tr);
    			w.write(") ");
    			printSubStmt( branch.stmt, w, tr);
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
     */
    public List acceptCFG(CFGBuilder v, List succs) {
    	v.visitCFG(expr, stmt.entry());
    	v.visitCFG(stmt, listEntry(rest, this));
    	v.visitCFGList(rest, this);
    	return succs;
    }
}

