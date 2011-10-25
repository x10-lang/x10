package x10.ast;

import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Stmt;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

public class AtomicUnitOfWork_c extends Stmt_c 
implements AtomicUnitOfWork {
	
	public Stmt body;
	public List<Expr> arguments;
	
	public AtomicUnitOfWork_c(Position p, Expr place, List<Expr> arguments, Stmt body) {
		super(p);
		this.body = body;
		this.arguments = arguments;
	}
	
	public AtomicUnitOfWork_c(Position p) {
		super(p);
	}
	
	/* (non-Javadoc)
	 * @see x10.ast.Future#body()
	 */
	public Stmt body() {
		return body;
	}
	
	/** Set the body of the statement. */
	public AtomicUnitOfWork body(Stmt body) {
		AtomicUnitOfWork_c n = (AtomicUnitOfWork_c) copy();
		n.body = body;
		return n;
	}
	
	public AtomicUnitOfWork arguments(List<Expr> arguments) {
		AtomicUnitOfWork_c n = (AtomicUnitOfWork_c) copy();
		n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
		return n;
	}
	
	public List<Expr> arguments() {
		return this.arguments;
	}
    
	/** Reconstruct the statement. */
	protected AtomicUnitOfWork reconstruct( List<Expr> arguments, Stmt body ) {
		if ( body != this.body || ! CollectionUtil.allEqual(arguments, this.arguments) ) {
			AtomicUnitOfWork_c n = (AtomicUnitOfWork_c) copy();
			n.body = body;
			n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
			return n;
		}
		
		return this;
	}
	
	/** Visit the children of the statement. */
	public Node visitChildren( NodeVisitor v ) {
		Stmt body = (Stmt) visitChild(this.body, v);
		List<Expr> arguments = visitList(this.arguments, v);
		return reconstruct(arguments, body);
	}
	
	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) {		
		return this;
	}
	
	public Context enterScope(Context c) {
		Context cc = (Context) super.enterScope(c);
		 cc = cc.pushAtomicBlock();
		return cc;
		    
	}
	public String toString() {
		return "atomic (...) " + body;
	}
	
	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("atomic (...)");
		printSubStmt(body, w, tr);
	}
	
	/**
	 * Return the first (sub)term performed when evaluating this
	 * term.
	 */
	public Term firstChild() {
		return listChild(arguments, body);
	}
	
	/**
	 * Visit this term in evaluation order.
	 */
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		//CFGBuilder v1 = v.push(this);
		v.visitCFGList(arguments, body(), ENTRY); 
		v.visitCFG(body, this, EXIT);
		return succs;
	}
	

	
}
