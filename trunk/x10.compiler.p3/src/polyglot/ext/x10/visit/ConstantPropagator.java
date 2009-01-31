package polyglot.ext.x10.visit;

import polyglot.ast.Conditional;
import polyglot.ast.Expr;
import polyglot.ast.FloatLit;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

/**
 * Very simple constant propagation pass. If an expr has a constant value,
 * replace it with the value. Replace branches on constants with the
 * consequent or alternative as appropriate.
 * TODO: Handle constant rails and conversions.
 * TODO: Propagate through rails A(0) = v; ... A(0) --> v.
 * TODO: Dead code elimination. 
 * visitor.
 * 
 * @param n
 * @return
 */
public  class ConstantPropagator extends NodeVisitor {
    Job job;
    NodeFactory nf;
    TypeSystem ts;

    public ConstantPropagator(Job job, TypeSystem ts, NodeFactory nf) {
        this.job = job;
        this.ts = ts;
        this.nf = nf;
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        Position pos = n.position();

        if (n instanceof Lit) {
            return n;
        }

        if (n instanceof Conditional) {
            Conditional c = (Conditional) n;
            if (c.cond().isConstant()) {
                boolean b = (boolean) (Boolean) c.cond().constantValue();
                if (b)
                    return c.consequent();
                else
                    return c.alternative();
            }
        }

        if (n instanceof If) {
            If c = (If) n;
            if (c.cond().isConstant()) {
                boolean b = (boolean) (Boolean) c.cond().constantValue();
                if (b)
                    return c.consequent();
                else
                    return c.alternative() != null ? c.alternative() : nf.Empty(pos);
            }
        }

        if (n instanceof Expr) {
            Expr e = (Expr) n;
            if (e.isConstant()) {
                Object o = e.constantValue();
                if (o instanceof Integer) {
                    return nf.IntLit(pos, IntLit.INT, (long) (int) (Integer) o);
                }
                if (o instanceof Long) {
                    return nf.IntLit(pos, IntLit.LONG, (long) (Long) o);
                }
                if (o instanceof Float) {
                    return nf.FloatLit(pos, FloatLit.FLOAT, (double) (float) (Float) o);
                }
                if (o instanceof Double) {
                    return nf.FloatLit(pos, FloatLit.DOUBLE, (double) (Double) o);
                }
                if (o instanceof Character) {
                    return nf.CharLit(pos, (char) (Character) o);
                }
                if (o instanceof Boolean) {
                    return nf.BooleanLit(pos, (boolean) (Boolean) o);
                }
                if (o instanceof String) {
                    return nf.StringLit(pos, (String) o);
                }
                if (o == null) {
                    return nf.NullLit(pos);
                }
            }
        }

        return super.leave(old, n, v);
    }
}
