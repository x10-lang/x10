/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/**
 *
 */
public class Clocked_c extends Expr_c implements Clocked {

    protected Expr clock;
    protected Stmt stmt;

    public Clocked_c(Position p, Expr clock, Stmt stmt) {
        super(p);
        this.clock = clock;
        this.stmt = stmt;
    }
    
    public Clocked_c(Position p) {
        super(p);
    }
    
    /** Expression */
    public Expr clock() {
	return this.clock;
    }

    /** clock */
    public Clocked expr(Expr clock) {
        Clocked_c n = (Clocked_c) copy();
        n.clock = clock;
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
        return this;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(clock, this);
        v.visitCFG(stmt, this);
        return succs;
    }
}

