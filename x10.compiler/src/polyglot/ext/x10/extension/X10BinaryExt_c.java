package polyglot.ext.x10.extension;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.MethodInstance;
import polyglot.main.Report;

public class X10BinaryExt_c extends X10Ext_c {
    // Rewrite == and != to invoke Primitive.equals(o, p).
    public Node rewrite(X10TypeSystem ts, NodeFactory nf) {
        Binary b = (Binary) node();
        Expr l = b.left();
        Expr r = b.right();

       
        if (b.operator() == Binary.EQ || b.operator() == Binary.NE) {
            MethodInstance mi = ((X10TypeSystem) ts).primitiveEquals();

            if (Report.should_report("debug", 5)) {
            	Report.report(5, "[X10BinaryExt_c] |" + this + "|.rewrite(), b=|" + b + "|, b.type=|" + b.type() + "|:");
            	Report.report(5, "[X10BinaryExt_c] ... l=|" + l + "|, r=|" + r + "|.");
            	Report.report(5, "[X10BinaryExt_c] ... l.type()=|" + l.type() + "|, r.type=|" + r.type()
            			+ "|, mi.container=|" + mi.container()+"|.");
            }
            if (ts.isSubtype(l.type(), mi.container()) ||
                ts.equals(l.type(), ts.Object())) {

                // left is possibly a boxed primitive
                if (r.type().isReference()) {
                    TypeNode x = nf.CanonicalTypeNode(b.position(),
                                                      mi.container());
                    Call y = nf.Call(b.position(), x, mi.name(), l, r).methodInstance(mi);
                    y = (Call) y.type(mi.returnType());

                    if (b.operator() == Binary.NE) {
                        return nf.Unary(b.position(), Unary.NOT, y).type(mi.returnType());
                    }
                    else {
                        return y;
                    }
                }
            }
        }

        return super.rewrite(ts, nf);
    }
}
