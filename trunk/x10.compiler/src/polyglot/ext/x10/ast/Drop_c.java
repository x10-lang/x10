package polyglot.ext.x10.ast;

import java.util.List;
import java.util.ArrayList;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;

public class Drop_c extends Stmt_c implements Drop {

    public List clocks; 

    public Drop_c(Position p, List clocks) {
        super(p);
        this.clocks = TypedList.copyAndCheck(clocks, Expr.class, true);
    }

    /** Get the clocks. */
    public List clocks() {
	return this.clocks;
    }

    /** Set the clocks. */
    public Drop clocks(List clocks) {
	Drop_c n = (Drop_c) copy();
	n.clocks = TypedList.copyAndCheck(clocks, Expr.class, true);
	return n;
    }

    /** Append a new clock to the clock list. */
    public Drop append(Expr clock) {
	List l = new ArrayList(clocks.size()+1);
	l.addAll(clocks);
	l.add(clock);
	return clocks(l);
    }

    public Term entry() {
        // TODO:
        return this;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        // TODO:
        return succs;
    }
}
