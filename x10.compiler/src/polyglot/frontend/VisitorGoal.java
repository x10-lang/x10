package polyglot.frontend;

import polyglot.ast.Node;
import polyglot.main.Reporter;
import polyglot.util.*;
import polyglot.visit.NodeVisitor;


public class VisitorGoal extends SourceGoal_c {
    private static final long serialVersionUID = -3527290837213063959L;

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
		        Reporter reporter = scheduler.extensionInfo().getOptions().reporter;
		        int nErrsBefore = q.errorCount();
		
		        if (reporter.should_report(Reporter.frontend, 3))
		            reporter.report(3, "Running " + v_ + " on " + ast);
		
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
		catch (RuntimeException e) {
		    fail();
		    throw e;
		}
		catch (Error e) {
		    fail();
		    throw e;
		}
		finally {
		    job().ast(ast);
		}
    }
}
