/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Block;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/**
 *
 */
public class Now_c extends Expr_c implements Now {

    protected Expr clock;
    protected Block body;

    public Now_c(Position p, Expr clock, Block body) {
        super(p);
        this.clock = clock;
        this.body = body;
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
    public Block body() {
	return this.body;
    }

    /** statement */
    public Now body(Block body) {
        Now_c n = (Now_c) copy();
        n.body = body;
        return n;
    }

    public Term entry() {
        // TODO:
        return this;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(clock, this);
        v.visitCFG(body, this);
        return succs;
    }
}

