package x10.visit;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.QName;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.ast.*;
import x10.types.X10TypeMixin;
import x10.types.X10Flags;

public class CheckEscapingThis extends NodeVisitor
{
    private final Job job;
    private final TypeSystem ts;
    private final Type type;

    public CheckEscapingThis(Job job, Type type, TypeSystem ts) {
        this.job = job;
        this.ts = ts;
        this.type = X10TypeMixin.baseType(type);
    }

    @Override
    public Node visitEdgeNoOverride(Node parent, Node n) {
        // You can access "this" for field access and field assignment.
        if (n instanceof FieldAssign) {
            FieldAssign assign = (FieldAssign) n;
            if (assign.target() instanceof Special) {
                assign.right().visit(this);
                return n;
            }
        }
        if (n instanceof Field && ((Field)n).target() instanceof Special) {
            return n;
        }
        // You can also access "this" as the receiver of property calls (because they are MACROS that are expanded to field access)
        if (n instanceof X10Call) {
            final X10Call call = (X10Call) n;
            if (call.target() instanceof Special &&
                call.methodInstance().flags().contains(X10Flags.PROPERTY)) {
                // it is enough to just recurse into the arguments
                for (Expr e : call.arguments())
                    e.visit(this);
                return n;
            }                        
        }
        if (n instanceof Special) {
            final Special special = (Special) n;
            if (special.kind()==Special.THIS &&
                ts.typeEquals(X10TypeMixin.baseType(special.type()),type,null))
                    job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,"'this' cannot escape from a constructor!",n.position());
        }
        n.del().visitChildren(this);
        return n;
    }
}