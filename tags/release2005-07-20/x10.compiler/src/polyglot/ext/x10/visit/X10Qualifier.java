package polyglot.ext.x10.visit;

import polyglot.ast.Special;
import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.util.Position;


/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Qualifier extends AscriptionVisitor
{
    public X10Qualifier(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    public Expr ascribe(Expr e, Type toType) {
        if (e instanceof Special) {
            Position p = e.position();
            Special sc = (Special) e;
            if (sc.qualifier() == null /* && sc.type() != toType */) {
                Special ret = sc.qualifier(nf.CanonicalTypeNode(p, sc.type()));
                return ret;
            }
        }
  
        return e;
    }
}