package polyglot.frontend;

import polyglot.main.Report;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;
import x10.ast.Node;

/**
 * This goal does not fail when errors were produced.
 * @author igor
 */
public class ForgivingVisitorGoal extends VisitorGoal {
    private static final long serialVersionUID = -4239390029748390935L;

    public ForgivingVisitorGoal(String name, Job job, NodeVisitor v) {
        super(name, job, v);
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
                if (Report.should_report(Report.frontend, 3))
                    Report.report(3, "Running " + v_ + " on " + ast);
                ast = ast.visit(v_);
                v_.finish(ast);
                return true;
            }
            return false;
        }
        finally {
            job().ast(ast);
        }
    }
}