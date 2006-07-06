package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Term;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

public class Next_c extends Stmt_c implements Next {


    public Next_c(Position p) {
        super(p);
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
