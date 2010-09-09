package polyglot.ext.x10.visit;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Boxer extends AscriptionVisitor
{
    public X10Boxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public Expr ascribe(Expr e, Type toType) {
        Type fromType = e.type();

        if (toType == null) {
            return e;
        }

        Position p = e.position();

        // Insert a cast.  Translation of the cast will insert the
        // correct boxing/unboxing code.
        if (toType.isReference() && fromType.isPrimitive()) {
            return nf.Cast(p, nf.CanonicalTypeNode(p, ts.Object()), e);
        }

        return e;
    }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        n = super.leaveCall(old, n, v);

        if (n.ext() instanceof X10Ext) {
            return ((X10Ext) n.ext()).rewrite((X10TypeSystem) typeSystem(),
                                              nodeFactory());
        }

        return n;
    }
}
