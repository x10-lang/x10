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

package x10.constraint;

import java.util.List;
import java.util.Map;

/**
 * A representation of a Field.T is a FieldDef or a MethodDef.
 * @author vj
 *
 */
public class XField<T> extends XVar {
    private static final long serialVersionUID = -6911344390866611523L;
    public XVar receiver;
    public T field;
    public  boolean hasEQV;
    
    // used by XPromise_c to determine if this field should occur in the output
    // representation of a constraint or not. hidden true for fake fields.
    private boolean hidden;

    public XTerm accept(TermVisitor visitor) {
        XTerm res = visitor.visit(this);
        if (res!=null) return res;
        XVar newReceiver = (XVar) receiver.accept(visitor);
        if (newReceiver==receiver) return this;
        XField<T> newThis = (XField<T>) this.clone();
        newThis.receiver = newReceiver;
        return newThis;
    }

    public XField(XVar receiver, T field) { this(receiver, field, false);}

    public XField(XVar receiver, T field, boolean hidden) {
        super();
        this.receiver = receiver;
        this.field = field;
        this.hidden = hidden;
        this.hasEQV = receiver.hasEQV();
    }

    public boolean isHidden() { return hidden; }
    @Override public boolean hasEQV(){ return hasEQV;}
    public XTermKind kind() { return XTermKind.FIELD_ACCESS; }

    @Override
    public XTerm subst(XTerm y, XVar x, boolean propagate) {
        XTerm r = super.subst(y, x, propagate);
        if (! equals(r))
            return r;
        XVar newReceiver = (XVar) receiver.subst(y, x);
        if (newReceiver == receiver) return this;
        XField<T> result = clone();
        result.receiver = newReceiver;
        result.hasEQV = newReceiver.hasEQV();
        return result;
    }

    public boolean okAsNestedTerm() { return true; }
    public List<XEQV> eqvs() { return receiver().eqvs();}
    public T field() { return field;}

    /** 
     * if this is r.f, then return newReceiver.f.
     * @param newReceiver
     * @return
     */
    public XField<T> copyReceiver(XVar newReceiver) {
    	if (newReceiver == receiver) return this;
        return new XField<T>(newReceiver, field, hidden);
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
    public boolean hasVar(XVar v) { return equals(v) || receiver.hasVar(v);}
    public XVar receiver() { return receiver; }
    public int hashCode() { return receiver.hashCode() + field.hashCode(); }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XField) {
            XField<T> other = (XField<T>) o;
            return receiver.equals(other.receiver) && field.equals(other.field);
        }
        return false;
    }

    public String toString() {
        return (receiver == null ? "" : receiver.toString() + ".") + field;
    }


    @SuppressWarnings("unchecked")
    @Override
    public XField<T> clone() {
        XField<T> n = (XField<T>) super.clone();
        n.vars = null;
        return n;
    }

    // memoize rootVar and path.
    protected XVar[] vars;
    public XVar[] vars() {
        if (vars == null) initVars();
        return vars;
    }

    public XVar rootVar() {
        if (vars == null) initVars();
        return vars[0];
    }

    @SuppressWarnings("unchecked")
    protected void initVars() {
        int count = 0;
        for (XVar source = this; source instanceof XField; source = ((XField<T>) source).receiver())
            count++;
        vars = new XVar[count + 1];
        XVar f = this;
        for (int i = count; i >= 0; i--) {
            vars[i] = f;
            if (i > 0)
                f = ((XField<T>) f).receiver();
        }
    }
    @Override
	public XPromise nfp(XConstraint c) {
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
