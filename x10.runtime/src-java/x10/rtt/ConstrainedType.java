/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.rtt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XFormula_c;
import x10.constraint.XLit;
import x10.constraint.XLit_c;
import x10.constraint.XPromise;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.core.fun.Fun_0_1;

// new List[int{self==x}]();
// ->
// new List(Types.constrained(Type.INT, (self) => self==x));    // this works for instanceof T, but not for <:

public class ConstrainedType<T> extends RuntimeType<T> {
    Type<T> base;
    Fun_0_1<T,Boolean> tester;
    XConstraint constraint;

    public ConstrainedType(Type<T> base, Fun_0_1<T, Boolean> t, XConstraint constraint) {
        super(base.getJavaClass());
        this.base = base;
        this.tester = t;
        this.constraint = constraint;
    }
    
    public boolean instanceof$(Object o) {
        return base.instanceof$(o) && (tester == null || tester.apply((T) o));
    }
    
    public boolean isSubtype(Type<?> o) {
    	if (!(o instanceof ConstrainedType))
    		return base.isSubtype(o);
    	ConstrainedType<?> ct = (ConstrainedType<?>) o;
    	return base.isSubtype(ct.base) && ct.constraint.entails(constraint, null);

    }
    
    public XConstraint getConstraint() {
        return constraint;
    }
    
    public String toString() {
        return base.toString()+"{"+constraint.toString()+"}";
    }
    
	public static XTerm trans(Type<?> t) {
		return new XTypeLit_c(t.getClass().getCanonicalName());
	}
    
    public static Type<?> getType(XTerm t) {
        if (t instanceof XTypeLit_c) {
            XTypeLit_c lit = (XTypeLit_c) t;
            return lit.type();
        }
        if (t instanceof XLit) {
            XLit l = (XLit) t;
            if (l.val() instanceof Type) {
                return (Type<?>) l.val();
            }
        }
        // TODO
//        // ParameterType
//        if (t instanceof XLocal) {
//            XLocal l = (XLocal) t;
//            XName name = l.name();
//            if (name instanceof XNameWrapper) {
//                XNameWrapper<?> w = (XNameWrapper<?>) name;
//                if (w.val() instanceof Type) {
//                    return (Type) w.val();
//                }
//            }
//        }
        return null;
    }
    
    public static class XSubtype_c extends XFormula_c {
        public XSubtype_c(XTerm left, XTerm right) {
            super(XTerms.makeName("<:"), left, right);
            markAsAtomicFormula();
        }

        public Type<?> subtype() {
            return getType(left());
        }

        public Type<?> supertype() {
            return getType(right());
        }

        public XPromise internIntoConstraint(XConstraint_c c, XPromise last) throws XFailure {
            Type<?> l = subtype();
            Type<?> r = supertype();
            // Check that l descends from r.  See TODO in SubtypeSolver.java
            if (!l.isSubtype(r)) {
                throw new XFailure("Interning " + this + " makes constraint inconsistent.");
            }
            return super.internIntoConstraint(c, last);
        }

        public static <T> Type<T> subst(Type<T> t, XTerm y, XRoot x) {
            if (t instanceof ConstrainedType) {
                ConstrainedType<T> ct = (ConstrainedType<T>) t;
                Type<T> base = subst(ct.base, y, x);
                try {
                    XConstraint c = ct.getConstraint();
                    return new ConstrainedType<T>(base, ct.tester, c.substitute(y, x));
                }
                catch (XFailure e) {
                    return t;
                }
            }
            if (t instanceof RuntimeType) {
                RuntimeType<T> rt = (RuntimeType<T>) t;
                List<Type<?>> parms = rt.getTypeParameters();
                List<Type<?>> args = new ArrayList<Type<?>>();
                boolean changed = false;
                for (Type<?> ti : parms) {
                	Type ti2 = subst(ti, y, x);
                	if (ti2 != ti)
                		changed = true;
                	args.add(ti2);
                }
                if (changed)
                	return rt.reinstantiate(args);
                return rt;
            }
            return t;
        }

        public XTerm subst(XTerm y, XRoot x, boolean propagate) {
            List<XTerm> newArgs = new ArrayList<XTerm>();
            boolean changed = false;
            Type<?> left = subtype();
            Type<?> l = subst(left, y, x);
            Type<?> right = supertype();
            Type<?> r = subst(right, y, x);
            if (l == left && r == right && !propagate)
                return this;
            XSubtype_c n = (XSubtype_c) super.subst(y, x, propagate);
            XTerm lterm = trans(l);
            XTerm rterm = trans(r);
            n.arguments = Arrays.asList(new XTerm[] { lterm, rterm });
            return n;
        }

        @Override
        public String toString() {
            return left() + " <: " + right();
        }
    }
    
    public static class XTypeLit_c extends XLit_c {
        public XTypeLit_c(Type<?> type) {
        	super(type);
        }

        public XTypeLit_c(String name) {
            this(toType(name));
        }

        public Type<?> type() {
        	return (Type<?>) val;
        }

        private static Type<?> toType(String val) {
            try {
				return (Type<?>) Class.forName((String) val).getField("it").get(null);
			} catch (IllegalAccessException e) {
			} catch (NoSuchFieldException e) {
			} catch (ClassNotFoundException e) {
			}
			return null;
        }

        public XTerm subst(XTerm y, XRoot x, boolean propagate) {
            XTypeLit_c n = (XTypeLit_c) super.subst(y, x, propagate);
            if (n == this) n = (XTypeLit_c) clone();
            n.val = XSubtype_c.subst(type(), y, x);
            return n;
        }
    }
   
}
 