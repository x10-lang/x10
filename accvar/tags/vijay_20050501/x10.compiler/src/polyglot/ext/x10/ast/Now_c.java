/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Block;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

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
        return this;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(clock, this);
        v.visitCFG(stmt, this);
        return succs;
    }
}

