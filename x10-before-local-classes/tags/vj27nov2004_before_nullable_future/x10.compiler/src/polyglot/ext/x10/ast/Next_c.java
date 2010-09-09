package polyglot.ext.x10.ast;

import java.util.List;
import java.util.ArrayList;

import polyglot.ast.Expr;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;

public class Next_c extends Stmt_c implements Next {

    public List clocks; 

    public Next_c(Position p, List clocks) {
        super(p);
        this.clocks = TypedList.copyAndCheck(clocks, Expr.class, true);
    }

    /** Get the clocks. */
    public List clocks() {
	return this.clocks;
    }

    /** Set the clocks. */
    public Next clocks(List clocks) {
	Next_c n = (Next_c) copy();
	n.clocks = TypedList.copyAndCheck(clocks, Expr.class, true);
	return n;
    }

    /** Append a statement to the statements. */
    public Next append(Expr clock) {
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
