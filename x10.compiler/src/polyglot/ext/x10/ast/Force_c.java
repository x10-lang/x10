/*
 *
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/**
 *
*/
public class Force_c extends Expr_c implements Force {

    protected Expr expr;

    public Force_c(Position p, Expr expr) {
        super(p);
        this.expr = expr;
    }
    
    public Force_c(Position p) {
        super(p);
    }
    
    /** Expression */
    public Expr expr() {
	return this.expr;
    }

    /** Expression */
    public Force expr(Expr expr) {
        Force_c n = (Force_c) copy();
        n.expr = expr;
        return n;
    }

    public Term entry() {
        // TODO:
        return this;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(expr, this);
        return succs;
    }
}

