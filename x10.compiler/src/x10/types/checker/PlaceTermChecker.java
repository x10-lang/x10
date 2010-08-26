package x10.types.checker;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.visit.ContextVisitor;
import x10.ast.X10Special;


public class PlaceTermChecker extends ContextVisitor {
	public PlaceTermChecker(Job job) {
        super(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory());
    }
    @Override
    public Node override(Node n) {
        if (n instanceof Expr) {
            Expr e = (Expr) n;
            error = error || e.toString().startsWith("_place");
            return n;
        }
        return null;
    }
    public boolean error() { return error;}
}
