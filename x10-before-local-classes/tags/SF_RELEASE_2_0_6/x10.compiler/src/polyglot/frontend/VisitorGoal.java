package polyglot.frontend;

import polyglot.ast.Node;
import polyglot.main.Report;
import polyglot.util.*;
import polyglot.visit.NodeVisitor;


public class VisitorGoal extends SourceGoal_c {
    NodeVisitor v;

    public VisitorGoal(Job job, NodeVisitor v) {
        super(StringUtil.getShortNameComponent(v.getClass().getName()), job);
        this.v = v;
    }

    public VisitorGoal(String name, Job job, NodeVisitor v) {
        super(name, job);
        this.v = v;
    }
    
    public NodeVisitor visitor() {
    	return v;
    }

    public boolean runTask() {
    	NodeVisitor v = visitor();
    	
    	Node ast = job().ast();
		
		if (ast == null) {
		    throw new InternalCompilerError("Null AST for job " + job() + ": did the parser run?");
		}
		
		try {
		    NodeVisitor v_ = v.begin();
		
		    if (v_ != null) {
		        ErrorQueue q = job().compiler().errorQueue();
		        int nErrsBefore = q.errorCount();
		
		        if (Report.should_report(Report.frontend, 3))
		            Report.report(3, "Running " + v_ + " on " + ast);
		
		        ast = ast.visit(v_);
		        v_.finish(ast);
		
		        int nErrsAfter = q.errorCount();
		
		        if (nErrsBefore != nErrsAfter) {
		            // because, if they're equal, no new errors occurred,
		            // so the run was successful.
		            return false;
		        }
		
		        return true;
		    }
		
		    return false;
		}
		finally {
		    job().ast(ast);
		}
    }
}
