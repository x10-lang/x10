/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.TypedList;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.jl.parse.Name;

import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


/**
 *
 */
public class Clocked_c extends Expr_c implements Clocked {
	
	protected List clocks;
	protected Stmt stmt;
	
	public Clocked_c(Position p, List clocks, Stmt stmt) {
		super(p);
		this.clocks = TypedList.copyAndCheck(clocks, Expr.class, true);
		this.stmt = stmt;
	}
	
	public Clocked_c(Position p) {
		super(p);
	}
	
	/** Expression */
	public List clocks() {
		return this.clocks;
	}
	
	/** clock */
	public Clocked expr(List clocks) {
		Clocked_c n = (Clocked_c) copy();
		n.clocks = clocks;
		return n;
	}
	
	/** statement */
	public Stmt stmt() {
		return this.stmt;
	}
	
	/** statement */
	public Clocked stmt(Stmt stmt) {
		Clocked_c n = (Clocked_c) copy();
		n.stmt = stmt;
		return n;
	}
	
	public Term entry() {
		// TODO:
		return stmt.entry();
	}
	protected Clocked_c reconstruct( List clocks, Stmt stmt ) {
		if ( clocks != this.clocks || stmt != this.stmt ) {
		    Clocked_c n = (Clocked_c) copy();
		    n.clocks = clocks;
		    n.stmt = stmt;
		    return n;
		}
		return this;
	    }

	  /** Type check the statement. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		
		/*
		 if (! ts.isSubtype(expr.type(), ts.Object()) ) {
		 throw new SemanticException(
		 "Cannot synchronize on an expression of type \"" +
		 expr.type() + "\".", expr.position());
		 }
		 */
		return this;
	}

	   /** Visit the children of the expression. */
    public Node visitChildren( NodeVisitor v ) {
    	TypedList result = new TypedList(new LinkedList(), Expr.class, false);
    	for (Iterator it = clocks.iterator(); it.hasNext();) {
    		result.add( visitChild((Expr) it.next(), v));
    	}
    	Stmt s = (Stmt) visitChild( this.stmt, v );
    	return reconstruct( result, s );
    }
	
	public List acceptCFG(CFGBuilder v, List succs) {
		for (Iterator it = clocks.iterator(); it.hasNext();) {
    		v.visitCFG((Expr) it.next(), this);
    	}
		//TODO v.visitCFG(clocks, this);
		v.visitCFG(stmt, this);
		return succs;
	}
}

