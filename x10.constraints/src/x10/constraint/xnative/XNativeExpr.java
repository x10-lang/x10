/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraint.xnative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import x10.constraint.XConstraintSystem;
import x10.constraint.XExpr;
import x10.constraint.XOp;
import x10.constraint.XOp.Kind;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XVar;

/**
 * A representation of an atomic formula op(t1,..., tn).
 * 
 * @author vj
 *
 */
public class XNativeExpr<T extends XType> extends XNativeTerm<T> implements XExpr<T> {
    private static final long serialVersionUID = -124049106759891751L;
        public final XOp<T> op;
	    private final List<XNativeTerm<T>> children;
	    private final boolean hidden;

	    // children must be non-empty
	    public XNativeExpr(XOp<T> op,boolean hidden, List<XNativeTerm<T>> children) {
	    	super(op.type());
			assert op.isArityValid(children.size()); 
	        for (XNativeTerm<T> child : children) {
	        	assert child != null : "Expression subexpression cannot be null.";
	        }
			if (op.getKind() == XOp.Kind.EQ && children.size() != 2) {
				throw new IllegalArgumentException("eq size 2"); 
			}
			this.op = op;
	        this.hidden = hidden;
	        this.children = children;
	    }
	    
	    /**
	     * Create a formula with the given op and given list of arguments.
	     */
	    public XNativeExpr(XOp<T> op, boolean hidden, XNativeTerm<T>... children) {
	    	this(op, hidden, Arrays.asList(children));
	    }

	    public XNativeExpr(XNativeExpr<T> other) {
			super(other.type());
			this.op = other.op; 
			this.children = deepCopy(other.children);
			this.hidden = other.hidden; 		
		}

		private List<XNativeTerm<T>> deepCopy (List<XNativeTerm<T>> terms) {
			List<XNativeTerm<T>> res = new ArrayList<XNativeTerm<T>>(terms.size());
			for (XNativeTerm<T> t : terms) {
				res.add(t.copy());
			}
			return res; 
		}
		
		@Override
		public XOp<T> op() {
			return op;
		}

		@Override
		public boolean isHidden() {
			return hidden;
		}

		@Override
		public List<XNativeTerm<T>> children() {
			return children;
		}

		@Override
		public boolean isProjection() {
			return 	(op.getKind() == XOp.Kind.APPLY_LABEL || // field or method dereference  a.foo
					 op.getKind() == XOp.Kind.APPLY && children.get(0).isProjection()); // method call a.foo(x,y);
		}

		@Override
		public XNativeExpr<T> copy() {
			return new XNativeExpr<T>(this);
		}

		@Override
		public boolean isAtom() {
			return type().isBoolean(); 
		}

		@Override
		public XNativeTerm<T> get(int i) {
			return children.get(i);
		}

	    @Override
        public XNativeTerm<T> accept(TermVisitor<T> visitor) {
            XNativeTerm<T> res = (XNativeTerm<T>)visitor.visit(this);
            if (res!=null) return res;
    	    List<XNativeTerm<T>> newChildren = new ArrayList<XNativeTerm<T>>();
            boolean wasNew = false;
            for (int i = 0; i < children.size(); ++i ) {
            	XNativeTerm<T> xTerm = children.get(i);
                final XNativeTerm<T> newChild = xTerm.accept(visitor);
                wasNew |= newChild!=xTerm;
                newChildren.add(newChild);
            }
            
            if (!wasNew) return this;
    		XNativeExpr<T> newThis = new XNativeExpr<T>(this.op,this.hidden, newChildren); 
    		return newThis;
	    }

	    
	    @Override
	    public XNativeExpr<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2) {
	    	// TODO: do not use substitution but let binding in the future
	    	List<XNativeTerm<T>> newChildren = new ArrayList<XNativeTerm<T>>();
	        boolean changed = false;
	        for (XNativeTerm<T> child : children) {
	        	assert child != null : "Argument to a formula cannot be null.";
	            XNativeTerm<T> a = child.subst(sys, t1, t2);
	            assert a!=null : "Subtitution yielded an invalid term, "+this+"["+t1+" / "+t2+"]";
	            newChildren.add(a);
	            if (a != child)
	                changed = true;
	        }
	        
	        if (! changed) return this;
	        assert newChildren != null;
	        assert newChildren.size() > 0;
	        return new XNativeExpr<T>(op, hidden, newChildren);
	    }

	    @Override
	    public boolean hasVar(XVar<T> v) {
	        for (XNativeTerm<T> arg : children) 
	            if (arg.hasVar(v)) return true;
	        return false;
	    }

	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        if (op.getKind() == Kind.APPLY || op.getKind() == Kind.APPLY_LABEL || children.size()!=2) {
		        sb.append(op);
		        sb.append("(");
		        String sep = "";
		        for (XNativeTerm<T> ch : children) {
		            sb.append(sep);
		            sep = ", ";
		            sb.append(ch);
		        }
		        sb.append(")");
	        } else {
		        sb.append("(");
	        	XTerm<T> left = get(0);
	        	XTerm<T> right = get(1);
	            sb.append(left);
	            sb.append(" ");
	            sb.append(op.prettyPrint());
	            sb.append(" ");
	            sb.append(right);
		        sb.append(")");
	        }
	        return sb.toString();
	    }
	    
	    @Override
	    public int hashCode() {
	        int hash = 29;
	        for (XNativeTerm<T> arg: children)
	        	hash += arg.hashCode();
	        return hash;
	    }
	    
	    /**
	     * An XFormula is equal t another object if it is == to it, or the other object
	     * is an XFormula with equal ops and equal args.
	     */
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o instanceof XNativeExpr<?>) {
				@SuppressWarnings("unchecked")
	            XNativeExpr<T> c = (XNativeExpr<T>) o;
	            if (! c.op.equals(op)) return false;
	            if (c.children.size() == children.size()) {
	            	for (int i = 0; i < children.size(); i++) {
	                    XNativeTerm<T> ti = children.get(i);
	                    XNativeTerm<T> ci = c.children().get(i);
	                    if (! ti.equals(ci)) return false;
	                }
	                return true;
	            }
	        }
	        return false;
	    }
	    
	    @Override
	    public boolean okAsNestedTerm() { return false;}

}
