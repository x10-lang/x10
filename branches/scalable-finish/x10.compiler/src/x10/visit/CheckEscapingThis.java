package x10.visit;

import polyglot.ast.*;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.ast.*;
import x10.types.X10TypeMixin;
import x10.types.X10Flags;

import java.util.HashMap;

public class CheckEscapingThis extends NodeVisitor
{
    // we gather info on every procedure
    static class ProcedureInfo {

    }
    private final Job job;
    private final TypeSystem ts;
    private final X10ClassDecl_c xlass;
    private final Type xlassType;
    //private final HashMap<>

    public CheckEscapingThis(X10ClassDecl_c xlass, Job job, TypeSystem ts) {
        this.job = job;
        this.ts = ts;
        this.xlass = xlass;
        this.xlassType = X10TypeMixin.baseType(xlass.classDef().asType());
    }
    public void typeCheck() {
        // visit every ctor
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof ConstructorDecl)           // todo for native ctors, we don't have a body
                classMember.visit(this);
        }
    }

    @Override
    public Node visitEdgeNoOverride(Node parent, Node n) {
        // You can access "this" for field access and field assignment.
        // field assignment:
        if (n instanceof FieldAssign) {
            FieldAssign assign = (FieldAssign) n;
            if (assign.target() instanceof Special) {
                assign.right().visit(this);
                return n;
            }
        }
        // field access:
        if (n instanceof Field && ((Field)n).target() instanceof Special) {
            return n;
        }
        // You can also access "this" as the receiver of property calls (because they are MACROS that are expanded to field access)
        // and as the receiver of static/final calls
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
        // You cannot use "this" for anything else!
        if (n instanceof Special) {
            final Special special = (Special) n;
            if (special.kind()==Special.THIS &&
                ts.typeEquals(X10TypeMixin.baseType(special.type()), xlassType,null))
                    job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,"'this' cannot escape from a constructor!",n.position());
        }
        n.del().visitChildren(this);
        return n;
    }
}