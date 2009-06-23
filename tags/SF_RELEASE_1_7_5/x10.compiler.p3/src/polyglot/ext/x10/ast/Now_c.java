/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

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

    public Term firstChild() {
        return stmt;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(clock, this, EXIT);
        v.visitCFG(stmt, this, EXIT);
        return succs;
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

