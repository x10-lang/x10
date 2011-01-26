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

/**
 * A representation of a Field.
 * @author vj
 *
 */
public class XField extends XVar {

    public XVar receiver;
    public XName field;
    
    // used by XPromise_c to determine if this field should occur in the output
    // representation of a constraint or not. hidden true for fake fields.
    private boolean hidden;

    public XField(XVar receiver, XName field) {
        this(receiver, field, false);
    }

    public XField(XVar receiver, XName field, boolean hidden) {
        super();
        this.receiver = receiver;
        this.field = field;
        this.hidden = hidden;
    }

    public boolean isHidden() { return hidden; }

    public XTermKind kind() { return XTermKind.FIELD_ACCESS; }

    @Override
    public XTerm subst(XTerm y, XVar x, boolean propagate) {
        XTerm r = super.subst(y, x, propagate);
        if (! equals(r))
            return r;
        XVar newReceiver = (XVar) receiver.subst(y, x);
        if (newReceiver == receiver) {
            return this;
        }
        XField result = clone();
        result.receiver = newReceiver;
        return result;
    }

    public List<XEQV> eqvs() {
        return receiver().eqvs();
    }

    public XName field() {
        return field;
    }

    public String name() {
        return field.toString();
    }
/*
    public boolean hasVar(XVar v) {
        return equals(v) || receiver.hasVar(v);
    }
*/
    public XVar receiver() {
        return receiver;
    }

    public int hashCode() {
        return receiver.hashCode() + field.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XField) {
            XField other = (XField) o;
            return receiver.equals(other.receiver) && field.equals(other.field);
        }
        return false;
    }

    public String toString() {
        return (receiver == null ? "" : receiver.toString() + ".") + field;
    }

    public boolean hasEQV() {
        if (receiver() == null)
            assert false;
        return receiver().hasEQV();
    }

    @Override
    public XField clone() {
        XField n = (XField) super.clone();
        n.vars = null;
        return n;
    }

    // memoize rootVar and path.
    protected XVar[] vars;

    public XVar[] vars() {
        if (vars == null)
            initVars();
        return vars;
    }

    public XVar rootVar() {
        if (vars == null)
            initVars();
        return vars[0];
    }

  /*  public boolean prefixes(XTerm t) {
        if (equals(t))
            return true;
        if (!(t instanceof XVar))
            return false;
        XVar[] vars = ((XVar) t).vars();
        boolean result = false;
        for (int i = 0; (!result) && i < vars.length; i++) {
            result = equals(vars[i]);
        }
        return result;
    }
*/
    protected void initVars() {
        int count = 0;
        for (XVar source = this; source instanceof XField; source = ((XField) source).receiver())
            count++;
        vars = new XVar[count + 1];
        XVar f = this;
        for (int i = count; i >= 0; i--) {
            vars[i] = f;
            if (i > 0)
                f = ((XField) f).receiver();
        }
    }
}
