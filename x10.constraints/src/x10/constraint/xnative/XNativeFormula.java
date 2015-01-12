/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.constraint.xnative;

import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.util.CollectionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A representation of an atomic formula op(t1,..., tn).
 * 
 * @author vj
 *
 */
public class XNativeFormula<T> extends XNativeTerm implements XFormula<T> {
    private static final long serialVersionUID = -124049106759891751L;
        public final T op;
	    public final T asExprOp;
	    public XNativeTerm[] arguments;
	    public final boolean isAtomicFormula;

	    @Override
        public XNativeTerm accept(TermVisitor visitor) {
            XNativeTerm res = (XNativeTerm)visitor.visit(this);
            if (res!=null) return res;
            XNativeTerm[] newArgs = new XNativeTerm[arguments.length];
            boolean wasNew = false;
            for (int i = 0; i < arguments.length; ++i ) {
            	XNativeTerm xTerm = arguments[i];
                final XNativeTerm newArg = xTerm.accept(visitor);
                wasNew |= newArg!=xTerm;
                newArgs[i] = newArg;
            }
            
            if (!wasNew) return this;
            XNativeFormula<T> newThis = (XNativeFormula<T>)this.clone();
            newThis.arguments = newArgs;
            return newThis;
        }

	    /**
	     * Create a formula with the given op and given list of arguments.
	     * @param op
	     * @param args
	     */

 
	    public XNativeFormula(T op, T opAsExpr, boolean isAtomicFormula, XTerm... args) {
	        this.op = op;
	        this.asExprOp = opAsExpr;
	        this.isAtomicFormula = isAtomicFormula;
	        XNativeTerm[] newargs = new XNativeTerm[args.length];
	        for (int i = 0; i < args.length; ++i) {
	        	newargs[i] = (XNativeTerm)args[i];
	        }
	        this.arguments = newargs; 
	    }
	    @Override
	    public boolean isAtomicFormula() {
	        return isAtomicFormula;
	    }
	    @Override
	    public XTermKind kind() { return XTermKind.FN_APPLICATION;}
	    @Override
	    public List<XNativeEQV> eqvs() {
	        List<XNativeEQV> eqvs = new ArrayList<XNativeEQV>();
	        for (XNativeTerm arg : arguments) {
	            eqvs.addAll(arg.eqvs());
	        }
	        return eqvs;
	    }

	    @Override
	    public XNativeTerm subst(XTerm y, XVar x) {
	    	return subst((XNativeTerm)y, x, true);
	    }
	    @Override
	    public XNativeTerm subst(XTerm y, XVar x, boolean propagate) {
	        XNativeTerm[] newArgs = new XNativeTerm[this.arguments().length];
	        boolean changed = false;
	        for (int i = 0; i < arguments().length; ++i) {
	        	XNativeTerm arg = arguments[i];
	        	if (arg == null)
	        		assert arg != null : "Argument to a formula cannot be null.";
	            XNativeTerm a = arg.subst(y, x, propagate);
	            if (a != arg)
	                changed = true;
	            newArgs[i] = a;
	        }
	        
	        XNativeFormula<?> n = (XNativeFormula<?>) super.subst(y, x, propagate);
	        if (! changed) return n;
	        if (n == this) n = (XNativeFormula<?>) clone();
	        n.arguments = newArgs;
	        return n;
	    }
	    @Override
	    public T operator() {
	        return op;
	    }
	    @Override
	    public boolean isUnary() {
	        return arguments.length == 1;
	    }
	    @Override
	    public boolean isBinary() {
	        return arguments.length == 2;
	    }
	    @Override
	    public XNativeTerm unaryArg() {
	        if (isUnary())
	            return arguments[0];
	        return null;
	    }
	    @Override
	    public XNativeTerm left() {
	        if (isBinary())
	            return arguments[0];
	        return null;
	    }
	    @Override
	    public XNativeTerm right() {
	        if (isBinary())
	            return arguments[1];
	        return null;
	    }
	    
	    @Override
	    public XNativeTerm[] arguments() { return arguments; }

	    @Override
	    public boolean hasVar(XVar v) {
	        for (XNativeTerm arg : arguments) 
	            if (arg.hasVar(v)) return true;
	        return false;
	    }
	    @Override
	    public XPromise internIntoConstraint(XNativeConstraint c, XPromise last)  {
	        assert last == null;
	        // Evaluate left == right, if both are literals.
	        XPromise result = nfp(c);
	        if (result != null) return result; // this term has already been interned.
	        Map<Object, XPromise> fields = CollectionFactory.newHashMap();
	        for (int i = 0; i < arguments.length; i++) {
	            XNativeTerm arg = arguments[i];
	            if (arg == null) continue;
	            if (c == null) return null;
	            XPromise child = c.intern(arg);
	            fields.put(new Integer(i), child);
	        }
	        // C_Local_c v = new C_Local_c(op);
	        XNativeTerm v = this;
	        // fields.put(new C_NameWrapper<Integer>(-1), c.intern(v));
	        // create a new promise and return it.
	        XPromise p = new XPromise(fields, v);
	        c.addPromise(v, p);
	        result = p;
	        return result;
	    }

	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        sb.append(op);
	        sb.append("(");
	        String sep = "";
	        for (XNativeTerm arg : arguments) {
	            sb.append(sep);
	            sep = ", ";
	            sb.append(arg);
	        }
	        sb.append(")");
	        return sb.toString();
	    }
	    @Override
	    public int hashCode() {
	        int hash = 29;
	        for (XNativeTerm arg: arguments)
	        	if (arg != null) hash += arg.hashCode();
	        return hash;
	    }
	    
	    @Override
	    public boolean hasEQV() {
	        for (XNativeTerm arg: arguments) if (arg.hasEQV()) return true;
	        return false;
	    }

	    /**
	     * An XFormula is equal t another object if it is == to it, or the other object
	     * is an XFormula with equal ops and equal args.
	     */
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o instanceof XNativeFormula) {
	            XNativeFormula<?> c = (XNativeFormula<?>) o;
	            if (! c.op.equals(op)) return false;
	            if (c.arguments().length == arguments().length) {
	                for (int i = 0; i < arguments().length; i++) {
	                    XNativeTerm ti = arguments()[i];
	                    XNativeTerm ci = c.arguments()[i];
	                    if (! ti.equals(ci)) return false;
	                }
	                return true;
	            }
	        }
	        return false;
	    }
	    
	    @Override
	    public boolean okAsNestedTerm() { return false;}
	    @Override 
	    public XPromise nfp(XNativeConstraint c) {
	    	assert c!=null;
	    	XPromise p;
	    	if (c.roots == null) {
				c.roots = CollectionFactory.<XNativeTerm, XPromise> newHashMap();
				p = new XPromise(this);
				c.roots.put(this, p);
				return p;
			} else {
				p = c.roots.get(this);
				if (p == null) {
					p = new XPromise(this);
					c.roots.put(this, p);
					return p;
				}
			}
			return p.lookup();
	    }
	    @Override
		public T asExprOperator() {
			return asExprOp; 
		}
}
