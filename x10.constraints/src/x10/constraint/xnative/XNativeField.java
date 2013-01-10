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

import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XField;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XVar;


/**
 * A representation of a Field.T is a FieldDef or a MethodDef.
 * @author vj
 *
 */
public class XNativeField<T extends XType, F extends XDef<T>> extends XNativeExpr<T> implements XField<T, F> {
    private static final long serialVersionUID = -6911344390866611523L;
    public XNativeTerm<T> receiver;
    public F field;
    //public  boolean hasEQV;
    
    // used by XPromise_c to determine if this field should occur in the output
    // representation of a constraint or not. hidden true for fake fields.
    private boolean hidden;

    @SuppressWarnings("unchecked")
	public XNativeField(XNativeTerm<T> receiver, F field, boolean hidden) {
        super(XOp.makeLabelOp(field), hidden, receiver);
        this.receiver = receiver;
        this.field = field;
        this.hidden = hidden;
        //this.hasEQV = this.receiver.hasEQV();
    }

    
    public XNativeField(XNativeField<T, F> other) {
    	super(other);
    	this.receiver = other.receiver.copy();
    	this.field = other.field;
    	this.hidden = other.hidden;
	}


    @Override
    public XNativeTerm<T> accept(TermVisitor<T> visitor) {
        XNativeTerm<T> res = (XNativeTerm<T>)visitor.visit(this);
        if (res!=null) return res;
        XNativeTerm<T> newReceiver = (XNativeTerm<T>)receiver.accept(visitor);
        if (newReceiver==receiver) return this;
        XNativeField<T,F> newThis = (XNativeField<T,F>) this.copy();
        newThis.receiver = newReceiver;
        //newThis.hasEQV= newReceiver.hasEQV();
        return newThis;
    }

	public boolean isHidden() { return hidden; }
    //@Override 
    //public boolean hasEQV(){ return hasEQV;}
    
    @SuppressWarnings("unchecked")
	@Override
    public XNativeField<T,F> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2) {
		if (this.equals(t2))
			return (XNativeField<T,F>)t1; 
		
		XNativeTerm<T> new_receiver = receiver().subst(sys, t1, t2);
		if (new_receiver != receiver()) {
			return (XNativeField<T, F>) sys.makeField(new_receiver, field, hidden);
		}
		return this; 
    }

    @Override
    public boolean okAsNestedTerm() { return receiver.okAsNestedTerm(); }
    /*
    @Override
    public List<XNativeEQV> eqvs() { return receiver.eqvs();}
    */
    @Override
    public F field() { return field;}

    /** 
     * if this is r.f, then return newReceiver.f.
     * @param newReceiver
     * @return
     */
    public XNativeField<T,F> copyReceiver(XConstraintSystem<T> sys, XNativeTerm<T> newReceiver) {
    	if (newReceiver == receiver) return this;
        return (XNativeField<T,F>) sys.makeField(newReceiver, field, hidden);
    }
    /** If this is r.f1.f2..fn, then return newRoot.f1.f2...fn.
     * 
     * @param newRoot
     * @return
     
    public XField<T> copyRoot(XVar newRoot) {
        if (receiver instanceof XField) {
            XVar newReceiver = ((XField<?>) receiver).copyRoot(newRoot);
            return copyReceiver(newReceiver);
        }
        // replace the root.
        return copyReceiver(newRoot);
    }
    */
    @Override
    public boolean hasVar(XVar<T> v) { return equals(v) || receiver.hasVar(v);}
    @Override
    public XNativeTerm<T> receiver() { return receiver; }
    @Override
    public int hashCode() { return receiver.hashCode() + field.hashCode(); }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XNativeField) {
            XNativeField<T,F> other = (XNativeField<T,F>) o;
            return receiver.equals(other.receiver) && field.equals(other.field);
        }
        return false;
    }
    @Override
    public String toString() {
        return (receiver == null ? "" : receiver.toString() + ".") + field.getName();
    }


    @Override
    public XNativeField<T,F> copy() {
        return new XNativeField<T,F>(this);
    }

    // memoize rootVar and path.
    protected XNativeTerm<T>[] vars;
    public XNativeTerm<T>[] vars() {
        if (vars == null) initVars();
        return vars;
    }

    public XNativeTerm<T> rootVar() {
        if (vars == null) initVars();
        return vars[0];
    }

    @SuppressWarnings("unchecked")
    protected void initVars() {
        int count = 0;
        for (XNativeTerm<T> source = this; source instanceof XNativeField; source = ((XNativeField<T,F>) source).receiver)
            count++;
        vars = new XNativeTerm[count + 1];
        XNativeTerm<T> f = this;
        for (int i = count; i >= 0; i--) {
            vars[i] = f;
            if (i > 0)
                f = ((XNativeField<T,F>) f).receiver;
        }
    }
   
}
