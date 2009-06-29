package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ext.x10.types.XTypeTranslator.XTypeLit_c;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Pair;

import x10.constraint.Solver;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XEquals;
import x10.constraint.XEquals_c;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XFormula_c;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XRoot;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerm_c;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class SubtypeSolver implements Solver {
    X10TypeSystem ts;

    public SubtypeSolver(X10TypeSystem ts) {
        this.ts = ts;
        // FIXME: should not be static
        XTerms.addExternalSolvers(this);
    }

    public static class XSubtype_c extends XFormula_c {
        public XSubtype_c(XTerm left, XTerm right) {
            super(XTerms.makeName("<:"), left, right);
            markAsAtomicFormula();
        }

        public Type subtype() {
            return getType(left());
        }

        public Type supertype() {
            return getType(right());
        }

        Type subst(Type t, XTerm y, XRoot x) {
            if (t instanceof ConstrainedType) {
                Type ct = t;
                Type base = X10TypeMixin.baseType(ct);
                XConstraint c = X10TypeMixin.xclause(ct);
                Type newBase = subst(base, y, x);
                if (x instanceof XSelf) {
                    return X10TypeMixin.xclause(newBase, c);
                }
                try {
                    return X10TypeMixin.xclause(newBase, c.substitute(y, x));
                }
                catch (XFailure e) {
                    return t;
                }
            }
            if (t instanceof ParameterType) {
                X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
                XTerm tt = ts.xtypeTranslator().trans(t);
                if (x.equals(tt))
                    return SubtypeSolver.getType(y);
            }
            if (t instanceof X10ParsedClassType) {
                X10ParsedClassType ct = (X10ParsedClassType) t;
                if (ct.isIdentityInstantiation()) {
                    List<Type> args = new ArrayList<Type>();
                    boolean changed = false;
                    for (Type ti : ct.x10Def().typeParameters()) {
                        Type ti2 = subst(ti, y, x);
                        if (ti2 != ti)
                            changed = true;
                        args.add(ti2);
                    }
                    if (changed)
                        return ct.typeArguments(args);
                    return ct;
                }
                else {
                    List<Type> args = new ArrayList<Type>();
                    boolean changed = false;
                    for (Type ti : ct.typeArguments()) {
                        Type ti2 = subst(ti, y, x);
                        if (ti2 != ti)
                            changed = true;
                        args.add(ti2);
                    }
                    if (changed)
                        return ct.typeArguments(args);
                    return ct;
                }
            }
            if (t instanceof MacroType) {
                MacroType mt = (MacroType) t;
                return subst(mt.definedType(), y, x);
            }
            if (t instanceof PathType) {
                PathType pt = (PathType) t;
                XVar base = pt.base();
                Type baseType = pt.baseType();
                XTerm newBase = base.subst(y, x);
                if (newBase == base)
                    return pt;
                if (newBase instanceof XVar)
                    return pt.base((XVar) newBase, subst(baseType, y, x));
                return pt;
            }
            return t;
        }

        public XTerm subst(XTerm y, XRoot x, boolean propagate) {
            List<XTerm> newArgs = new ArrayList<XTerm>();
            boolean changed = false;
            Type left = subtype();
            Type l = subst(left, y, x);
            Type right = supertype();
            Type r = subst(right, y, x);
            if (l == left && r == right && !propagate)
                return this;
            XSubtype_c n = (XSubtype_c) super.subst(y, x, propagate);
            X10TypeSystem ts = (X10TypeSystem) left.typeSystem();
            XTerm lterm = ts.xtypeTranslator().trans(l);
            XTerm rterm = ts.xtypeTranslator().trans(r);
            n.arguments = CollectionUtil.list(lterm, rterm);
            return n;
        }

        @Override
        public boolean saturate(XConstraint c, Set<XTerm> visited) throws XFailure {
            boolean fresh = super.saturate(c, visited);
            if (fresh) {
                for (XTerm arg : this.arguments()) {
                    arg.saturate(c, visited);
                }
            }
            return fresh;
        }

        @Override
        public String toString() {
            return left() + " <: " + right();
        }
    }

    public void addDerivedEqualitiesInvolving(XConstraint c, XTerm t) throws XFailure {
        if (false && isSubtypeAtom(t)) {
            XFormula f = (XFormula) t;
            XTerm sub = f.left();
            XTerm sup = f.right();
            XTerm t2 = new XFormula_c(f.operator(), sup, sub);
            if (c.entails(t2))
                c.addBinding(sub, sup);
        }
    }

    public boolean entails(List<XTerm> atoms, XTerm t) {
        if (isSubtypeAtom(t)) {
            XSubtype_c f = (XSubtype_c) t;
            // A bit of a hack: apply substitutions to capture any equality constraints in the atoms.
            // Should really pass the constraint into isSubtype and use as an oracle; this would include any subtyping constraints also.
            for (XTerm term : atoms) {
                if (term instanceof XEquals) {
                    XEquals eq = (XEquals) term;
                    if (eq.left() instanceof XRoot)
                        f = (XSubtype_c) f.subst(eq.right(), (XRoot) eq.left());
                    else if (eq.right() instanceof XRoot)
                        f = (XSubtype_c) f.subst(eq.left(), (XRoot) eq.right());
                }
            }
            Type t1 = f.subtype();
            Type t2 = f.supertype();
            if (t1 != null && t2 != null && ts.isSubtype(t1, t2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSubtypeAtom(XTerm t) {
        return t instanceof XSubtype_c;
    }

    private static XConstraint getConstraint(XTerm t) {
        if (t instanceof XLit) {
            XLit l = (XLit) t;
            if (l.val() instanceof XConstraint) {
                return (XConstraint) l.val();
            }
        }
        return null;
    }

    public static Type getType(XTerm t) {
        if (t instanceof XTypeLit_c) {
            XTypeLit_c lit = (XTypeLit_c) t;
            return lit.type();
        }
        if (t instanceof XLit) {
            XLit l = (XLit) t;
            if (l.val() instanceof Type) {
                return (Type) l.val();
            }
        }
        // ParameterType
        if (t instanceof XLocal) {
            XLocal l = (XLocal) t;
            XName name = l.name();
            if (name instanceof XNameWrapper) {
                XNameWrapper<?> w = (XNameWrapper<?>) name;
                if (w.val() instanceof Type) {
                    return (Type) w.val();
                }
            }
        }
        // PathType
        if (t instanceof XField) {
            XField f = (XField) t;
            XVar base = f.receiver();
            XName field = f.field();
            if (field instanceof XNameWrapper) {
                XNameWrapper<?> w = (XNameWrapper<?>) field;
                if (w.val() instanceof TypeProperty) {
                    TypeProperty p = (TypeProperty) w.val();
                    return PathType_c.pathBase(p.asType(), base, Types.get(p.container()));
                }
            }
        }
        return null;
    }

    public boolean isConsistent(List<XTerm> atoms) {
        return true;
    }

    public boolean isValid(List<XTerm> atoms) {
        return false;
    }

}
