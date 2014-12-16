/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.constraint.xnative;

import java.util.List;
import java.util.Map;

import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;


/**
 * A representation of a Field.T is a FieldDef or a MethodDef.
 * @author vj
 *
 */
public class XNativeField<T> extends XNativeVar implements XField<T> {
    private static final long serialVersionUID = -6911344390866611523L;
    public XNativeVar receiver;
    public T field;
    public  boolean hasEQV;
    
    // used by XPromise_c to determine if this field should occur in the output
    // representation of a constraint or not. hidden true for fake fields.
    private boolean hidden;
    @Override
    public XNativeTerm accept(TermVisitor visitor) {
        XNativeTerm res = (XNativeTerm)visitor.visit(this);
        if (res!=null) return res;
        XNativeVar newReceiver = (XNativeVar)receiver.accept(visitor);
        if (newReceiver==receiver) return this;
        XNativeField<T> newThis = (XNativeField<T>) this.clone();
        newThis.receiver = newReceiver;
        newThis.hasEQV= newReceiver.hasEQV();
        return newThis;
    }

    public XNativeField(XVar receiver, T field) { this(receiver, field, false);}

    public XNativeField(XVar receiver, T field, boolean hidden) {
        super();
        this.receiver = (XNativeVar)receiver;
        this.field = field;
        this.hidden = hidden;
        this.hasEQV = this.receiver.hasEQV();
    }

    
    public boolean isHidden() { return hidden; }
    @Override 
    public boolean hasEQV(){ return hasEQV;}
    @Override
    public XTermKind kind() { return XTermKind.FIELD_ACCESS; }
    
    @Override
    public XNativeTerm subst(XTerm y, XVar x) {
    	return subst((XNativeTerm)y, x, true); 
    }

    @Override
    public XNativeTerm subst(XTerm y, XVar x, boolean propagate) {
        XNativeTerm r = super.subst(y, x, propagate);
        if (! equals(r))
            return r;
        XNativeVar newReceiver = (XNativeVar) receiver.subst(y, x);
        if (newReceiver == receiver) return this;
        XNativeField<T> result = clone();
        result.receiver = newReceiver;
        result.hasEQV = newReceiver.hasEQV();
        return result;
    }

    @Override
    public boolean okAsNestedTerm() { return true; }
    @Override
    public List<XNativeEQV> eqvs() { return receiver.eqvs();}
    @Override
    public T field() { return field;}

    /** 
     * if this is r.f, then return newReceiver.f.
     * @param newReceiver
     * @return
     */
    @Override
    public XNativeField<T> copyReceiver(XVar newReceiver) {
    	if (newReceiver == receiver) return this;
        return new XNativeField<T>(newReceiver, field, hidden);
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
    public String name() { return field.toString();}
    @Override
    public boolean hasVar(XVar v) { return equals(v) || receiver.hasVar(v);}
    @Override
    public XNativeVar receiver() { return receiver; }
    @Override
    public int hashCode() { return receiver.hashCode() + field.hashCode(); }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XNativeField) {
            XNativeField<T> other = (XNativeField<T>) o;
            return receiver.equals(other.receiver) && field.equals(other.field);
        }
        return false;
    }
    @Override
    public String toString() {
        return (receiver == null ? "" : receiver.toString() + ".") + field;
    }


    @SuppressWarnings("unchecked")
    @Override
    public XNativeField<T> clone() {
        XNativeField<T> n = (XNativeField<T>) super.clone();
        n.vars = null;
        return n;
    }

    // memoize rootVar and path.
    protected XNativeVar[] vars;
    @Override
    public XNativeVar[] vars() {
        if (vars == null) initVars();
        return vars;
    }

    @Override
    public XNativeVar rootVar() {
        if (vars == null) initVars();
        return vars[0];
    }

    @SuppressWarnings("unchecked")
    protected void initVars() {
        int count = 0;
        for (XNativeVar source = this; source instanceof XNativeField; source = ((XNativeField<T>) source).receiver)
            count++;
        vars = new XNativeVar[count + 1];
        XNativeVar f = this;
        for (int i = count; i >= 0; i--) {
            vars[i] = f;
            if (i > 0)
                f = ((XNativeField<T>) f).receiver;
        }
    }
    @Override
	public XPromise nfp(XNativeConstraint c) {
		assert c != null;
		XPromise root =  receiver.nfp(c);
		root.ensureFields();
		Map<Object, XPromise>  map = root.fields(); 
		assert map != null;
		XPromise entry = map.get(field);
		if (entry == null) {
			entry = new XPromise(this);
			map.put(field, entry);
			return entry;
		}
		entry = entry.lookup();
		return entry;
    }
   
}
