/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 5, 2004
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Stmt_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

/**
 * @author Philippe Charles
 * @author vj
 */
public class Atomic_c extends Stmt_c 
implements Atomic {
	
	public Stmt body;
	
	public Expr place; 
	
	public Atomic_c(Position p, Expr place, Stmt body) {
		super(p);
		this.place = place;
		this.body = body;
	}
	
	public Atomic_c(Position p) {
		super(p);
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.Future#body()
	 */
	public Stmt body() {
		return body;
	}
	
	/** Set the body of the statement. */
	public Atomic body(Stmt body) {
		Atomic_c n = (Atomic_c) copy();
		n.body = body;
		return n;
	}
	
	/** Get the RemoteActivity's place. */
	public Expr place() {
		return place;
	}
	
	/** Set the Atomic's place. */
	public Atomic place(Expr place) {
		this.place = place;
		return this;
	}
	
	/** Reconstruct the statement. */
	protected Atomic reconstruct( Expr place, Stmt body ) {
		if ( place != this.place || body != this.body ) {
			Atomic_c n = (Atomic_c) copy();
			n.place = place;
			n.body = body;
			return n;
		}
		
		return this;
	}
	
	/** Visit the children of the statement. */
	public Node visitChildren( NodeVisitor v ) {
		Expr place = (Expr) visitChild(this.place, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct(place, body);
	}
	
	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {		
		/*
		 if (! ts.isSubtype(expr.type(), ts.Object()) ) {
		 throw new SemanticException(
		 "Cannot synchronize on an expression of type \"" +
		 expr.type() + "\".", expr.position());
		 }
		 */
		return this;
	}
	
	public Context enterScope(Context c) {
		X10Context cc = (X10Context) super.enterScope(c);
		 cc = cc.pushAtomicBlock();
		
		
		
		return cc;
		    
	}
	// not sure how this works.. vj. Copied from Synchronized_c.
	public Type childExpectedType(Expr child, AscriptionVisitor av) {
		X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
		
		if ( child == place ) {
			return ts.Place();
		}
		
		return child.type();
	}
	
	public String toString() {
		return "atomic (" + place + ") { ... }";
	}
	
	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("atomic (");
		printBlock(place, w, tr);
		w.write(") ");
		printSubStmt(body, w, tr);
	}
	
	/**
	 * Return the first (sub)term performed when evaluating this
	 * term.
	 */
	public Term firstChild() {
		return place;
	}
	
	/**
	 * Visit this term in evaluation order.
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		v.visitCFG(place, body, ENTRY);
		v.push(this).visitCFG(body, this, EXIT);
		return succs;
	}
	
}
