/**
 * 
 */
package x10.types.checker;

import polyglot.frontend.Job;
import polyglot.visit.ContextVisitor;
import x10.ast.Field;
import x10.ast.NamedVariable;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.errors.Errors;
import x10.types.SemanticException;
import x10.types.TypeSystem;
import x10.types.X10TypeEnv_c;

/**
 * Flags an error if visited node contains a mutable variable or field.
 * @author vj
 *
 */
public class VarChecker extends ContextVisitor {
    public VarChecker(Job job) {
        super(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory());
    }
    public SemanticException error = null;
    @Override
    public Node override(Node n) {
        if (n instanceof NamedVariable) {
            NamedVariable e = (NamedVariable) n;
            if (! e.flags().isFinal())
                error = new Errors.VarMustBeFinalInTypeDef(e.name().toString(), e.position()); 

            if (n instanceof Field) {
                Field l = (Field) n;
                if (! new X10TypeEnv_c(context).isAccessible(l.fieldInstance())) {
                    error = new Errors.VarMustBeAccessibleInTypeDef(l.fieldInstance(), e.position()); 
                }
            }
            return n;
        }

        return null;
    }
}
