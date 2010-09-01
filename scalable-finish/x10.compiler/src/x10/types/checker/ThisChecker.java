/**
 * 
 */
package x10.types.checker;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.visit.ContextVisitor;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.constraints.CConstraint;
import x10.util.Synthesizer;

/**
 * Flags an error if visited node contsains a mutable variable or field.
 * @author vj
 *
 */
public class ThisChecker extends ContextVisitor {
    public ThisChecker(Job job) {
    	   super(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory());
    }
    @Override
    public Node override(Node n) {
        if (n instanceof X10Special) {
            X10Special e = (X10Special) n;
            error = error || e.kind() == X10Special.THIS || e.kind() == X10Special.SUPER; 
            return n;
        }
        if (n instanceof X10CanonicalTypeNode) {
            CConstraint rc = X10TypeMixin.xclause(((X10CanonicalTypeNode) n).type());
            List<Expr> clauses = new Synthesizer((X10NodeFactory) nf, (X10TypeSystem) ts).makeExpr(rc, n.position());
            for (Expr c : clauses) {
                c.visit(this);
            }
        }
        return null;
    }
    public boolean error() { return error;}
    public void clearError() { error = false;}
}