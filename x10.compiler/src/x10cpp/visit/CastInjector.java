package x10cpp.visit;

import polyglot.ast.Assign_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.types.checker.Converter;

/**
 * A pass to run right before final C++ codegeneration
 * to insert upcasts that are required by the C++ backend,
 * but are not explicitly injected by the front-end.
 * These casts can all be unchecked.
 * They fall into two main catagories:
 *   (a) casts injected for actual parameters of a call
 *       so that the static types match the callee method.
 *       These are required to make overloading work.
 *   (b) casts injected on other assignment like operations
 *       whose purpose is to ensure that representation level
 *       boxing/unboxing operations are performed.
 */
public class CastInjector extends ContextVisitor {
    
    public CastInjector(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        if (n instanceof Assign_c) {
            Assign_c assign = (Assign_c)n;
            return assign.right(cast(assign.right(), assign.left().type()));
        }
        
        return n;
    }
    
    
    private Expr cast(Expr a, Type fType) {
        if (!ts.typeDeepBaseEquals(fType, a.type(), context)) {
            Position pos = a.position();
            return nf.X10Cast(pos, nf.CanonicalTypeNode(pos, fType), a,
                           Converter.ConversionType.UNCHECKED).type(fType);
        } else {
            return a;
        }
    }
    
}
