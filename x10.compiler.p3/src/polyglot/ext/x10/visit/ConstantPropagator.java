package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Conditional;
import polyglot.ast.Expr;
import polyglot.ast.FloatLit;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * Very simple constant propagation pass. If an expr has a constant value,
 * replace it with the value. Replace branches on constants with the consequent
 * or alternative as appropriate. TODO: Handle constant rails and conversions.
 * TODO: Propagate through rails A(0) = v; ... A(0) --> v. TODO: Dead code
 * elimination. visitor.
 * 
 * @param n
 * @return
 */
public class ConstantPropagator extends ContextVisitor {
    public ConstantPropagator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
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
                Expr result = toExpr(o, e.position());
                if (result != null)
                    return result;
            }
        }

        return super.leaveCall(parent, old, n, v);
    }

    public Expr toExpr(Object o, Position pos) throws SemanticException {
        X10NodeFactory nf = (X10NodeFactory) this.nf;
        if (o == null) {
            return X10Cast_c.check(nf.NullLit(pos), this);
        }
        if (o instanceof Integer) {
            return X10Cast_c.check(nf.IntLit(pos, IntLit.INT, (long) (int) (Integer) o), this);
        }
        if (o instanceof Long) {
            return X10Cast_c.check(nf.IntLit(pos, IntLit.LONG, (long) (Long) o), this);
        }
        if (o instanceof Float) {
            return X10Cast_c.check(nf.FloatLit(pos, FloatLit.FLOAT, (double) (float) (Float) o), this);
        }
        if (o instanceof Double) {
            return X10Cast_c.check(nf.FloatLit(pos, FloatLit.DOUBLE, (double) (Double) o), this);
        }
        if (o instanceof Character) {
            return X10Cast_c.check(nf.CharLit(pos, (char) (Character) o), this);
        }
        if (o instanceof Boolean) {
            return X10Cast_c.check(nf.BooleanLit(pos, (boolean) (Boolean) o), this);
        }
        if (o instanceof String) {
            return X10Cast_c.check(nf.StringLit(pos, (String) o), this);
        }
        if (o instanceof Object[]) {
            Object[] a = (Object[]) o;
            List<Expr> args = new ArrayList<Expr>(a.length);
            for (Object ai : args) {
                Expr ei = toExpr(ai, pos);
                if (ei == null)
                    return null;
                args.add(ei);
            }
            return X10Cast_c.check(nf.Tuple(pos, args), this);
        }
        return null;
    }

}
