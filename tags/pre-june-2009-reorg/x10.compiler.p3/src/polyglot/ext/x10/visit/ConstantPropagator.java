package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Conditional;
import polyglot.ast.Empty;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.frontend.Job;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XNameWrapper;
import x10.constraint.XPromise;
import x10.constraint.XRoot;
import x10.constraint.XTerms;

/**
 * Very simple constant propagation pass. If an expr has a constant value,
 * replace it with the value. Replace branches on constants with the consequent
 * or alternative as appropriate. TODO: Handle constant rails and conversions.
 * TODO: Propagate through rails A(0) = v; ... A(0) --> v. TODO: Dead code
 * elimination. visitor.
 * 
 * @param theValueIfBindingTimeIsStatic
 * @return
 */
public class ConstantPropagator extends ContextVisitor {
    public ConstantPropagator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Position pos = n.position();

        if (n instanceof Expr || n instanceof Stmt) {
        }
        else {
            return n;
        }
        
        if (n instanceof LocalDecl) {
            LocalDecl d = (LocalDecl) n;
            if (d.flags().flags().isFinal() && d.init() != null && isConstant(d.init())) {
                d.localDef().setConstantValue(constantValue(d.init()));
                return nf.Empty(d.position());
            }
        }

        if (n instanceof Lit) {
            return n;
        }

        if (n instanceof Conditional) {
            Conditional c = (Conditional) n;
            Expr cond = c.cond();
            if (isConstant(cond)) {
                boolean b = (boolean) (Boolean) constantValue(cond);
                if (b)
                    return c.consequent();
                else
                    return c.alternative();
            }
        }

        if (n instanceof If) {
            If c = (If) n;
            Expr cond = c.cond();
            if (isConstant(cond)) {
                Object o = constantValue(cond);
                if (o instanceof Boolean) {
                    boolean b = (boolean) (Boolean) o;
                    if (b)
                        return c.consequent();
                    else
                        return c.alternative() != null ? c.alternative() : nf.Empty(pos);
                }
            }
        }

        if (n instanceof Expr) {
            Expr e = (Expr) n;
            if (isConstant(e)) {
                Object o = constantValue(e);
                Expr result = toExpr(o, e.position());
                if (result != null)
                    return result;
            }
        }
        
        if (n instanceof Block) {
            Block b = (Block) n;
            List<Stmt> ss = new ArrayList<Stmt>();
            for (Stmt s : b.statements()) {
                if (s instanceof Empty) {
                }
                else {
                    ss.add(s);
                }
            }
            if (ss.size() != b.statements().size())
                return b.statements(ss);
        }

        return super.leaveCall(parent, old, n, v);
    }

    public static Object constantValue(Expr e) {
        if (e.isConstant())
            return e.constantValue();
        
        if (e.type().isNull())
            return null;

        if (e instanceof Field) {
            Field f = (Field) e;
            if (f.target() instanceof Expr) {
                Expr target = (Expr) f.target();
                Type t = target.type();
                XConstraint c = X10TypeMixin.xclause(t);
                if (c != null) {
                    try {
                        XPromise p = c.lookup(XTerms.makeField(c.self(), XTerms.makeName(f.fieldInstance().def(), f.name().id().toString())));
                        if (p != null && p.term() instanceof XLit) {
                            XLit l = (XLit) p.term();
                            return l.val();
                        }
                    }
                    catch (XFailure e1) {
                    }
                }
            }
        }

        Type t = e.type();
        XConstraint c = X10TypeMixin.xclause(t);
        if (c != null) {
            XRoot r = c.self();
            if (r instanceof XLit) {
                XLit l = (XLit) r;
                return l.val();
            }
        }
        return null;
    }

    public static boolean isConstant(Expr e) {
        if (e.isConstant())
            return true;

        if (e.type().isNull())
            return true;
        
        if (e instanceof Field) {
            Field f = (Field) e;
            if (f.target() instanceof Expr) {
                Expr target = (Expr) f.target();
                Type t = target.type();
                XConstraint c = X10TypeMixin.xclause(t);
                if (c != null) {
                    try {
                        XPromise p = c.lookup(XTerms.makeField(c.self(), XTerms.makeName(f.fieldInstance().def(), f.name().id().toString())));
                        if (p != null && p.term() instanceof XLit) {
                            return true;
                        }
                    }
                    catch (XFailure e1) {
                    }
                }
            }
        }

        Type t = e.type();
        XConstraint c = X10TypeMixin.xclause(t);
        if (c != null) {
            XRoot r = c.self();
            if (r instanceof XLit) {
                XLit l = (XLit) r;
                return true;
            }
        }
        return false;
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
