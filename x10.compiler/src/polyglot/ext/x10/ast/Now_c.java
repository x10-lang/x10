/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Block;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 *
 */
public class Now_c extends Expr_c implements Now {

    protected Expr clock;
    protected Stmt stmt;

    public Now_c(Position p, Expr clock, Stmt stmt) {
        super(p);
        this.clock = clock;
        this.stmt = stmt;
    }
    
    public Now_c(Position p) {
        super(p);
    }
    
    /** Expression */
    public Expr clock() {
	return this.clock;
    }

    /** Expression */
    public Now clock(Expr clock) {
        Now_c n = (Now_c) copy();
        n.clock = clock;
        return n;
    }

    /** statement */
    public Stmt body() {
	return this.stmt;
    }

    /** statement */
    public Now body(Stmt body) {
        Now_c n = (Now_c) copy();
        n.stmt = stmt;
        return n;
    }

    public Term entry() {
        // TODO:
        return stmt.entry();
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(clock, this);
        v.visitCFG(stmt, this);
        return succs;
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
	protected Now_c reconstruct( Expr clock, Stmt stmt ) {
		if ( clock != this.clock || stmt != this.stmt ) {
		    Now_c n = (Now_c) copy();
		    n.clock = clock;
		    n.stmt = stmt;
		    return n;
		}
		return this;
	    }
	   /** Visit the children of the expression. */
    public Node visitChildren( NodeVisitor v ) {
  
    	Expr c = (Expr) visitChild( this.clock, v);
    	Stmt s = (Stmt) visitChild( this.stmt, v );
    	return reconstruct( c, s );
    }
}

