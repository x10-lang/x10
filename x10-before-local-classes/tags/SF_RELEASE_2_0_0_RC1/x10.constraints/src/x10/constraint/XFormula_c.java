/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class XFormula_c extends XTerm_c implements XFormula {
    public XName op;
    public List<XTerm> arguments;

    public XFormula_c(XName op, List<XTerm> args) {
        this.op = op;
        this.arguments = args;
    }
    public XFormula_c(XName op, XTerm... args) {
        this.op = op;
        this.arguments = new ArrayList<XTerm>(args.length);
        for (XTerm arg : args) {
            this.arguments.add(arg);
        }
    }
    public XTermKind kind() { return XTermKind.FN_APPLICATION;}
    public List<XEQV> eqvs() {
        List<XEQV> eqvs = new ArrayList<XEQV>();
        for (XTerm arg : arguments) {
            eqvs.addAll(arg.eqvs());
        }
        return eqvs;
    }

    public XTerm subst(XTerm y, XRoot x, boolean propagate) {
        List<XTerm> newArgs = new ArrayList<XTerm>(this.arguments().size());
        boolean changed = false;
        for (XTerm arg: this.arguments()) {
        	if (arg == null)
        		assert arg != null : "Argument to a formula cannot be null.";
            XTerm a = arg.subst(y, x, propagate);
            if (a != arg)
                changed = true;
            newArgs.add(a);
        }
        XFormula_c n = (XFormula_c) super.subst(y, x, propagate);
        if (! changed)
            return n;
        if (n == this) n = (XFormula_c) clone();
        n.arguments = newArgs;
        return n;
    }

    public XName operator() {
        return op;
    }

    public boolean isUnary() {
        return arguments.size() == 1;
    }
    public boolean isBinary() {
        return arguments.size() == 2;
    }
    public XTerm unaryArg() {
        if (isUnary())
            return arguments.get(0);
        return null;
    }
    public XTerm left() {
        if (isBinary())
            return arguments.get(0);
        return null;
    }
    public XTerm right() {
        if (isBinary())
            return arguments.get(1);
        return null;
    }

    public List<XTerm> arguments() { return arguments; }

    public boolean hasVar(XVar v) {
        for (XTerm arg : arguments) {
            if (arg.hasVar(v))
                return true;
        }
        return false;
    }

    public XPromise internIntoConstraint(XConstraint c, XPromise last) throws XFailure {
        assert last == null;
        // Evaluate left == right, if both are literals.
        XPromise result = c.lookup(this);
        if (result != null) // this term has already been interned.
            return result;
        HashMap<XName, XPromise> fields = new HashMap<XName, XPromise>();
        for (int i = 0; i < arguments.size(); i++) {
            XTerm arg = arguments.get(i);
            if (arg == null) {
            	System.err.println("XFormula_c: Golden: null arg in " + this);
            	continue;
            }
            	
            XPromise child = c.intern(arg);
            fields.put(new XNameWrapper<Integer>(i), child);
        }
        // C_Local_c v = new C_Local_c(op);
        XTerm v = this;
        // fields.put(new C_NameWrapper<Integer>(-1), c.intern(v));
        // create a new promise and return it.
        XPromise p = new XPromise_c(fields, v);
        c.addPromise(v, p);
        result = p;
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(op);
        sb.append("(");
        String sep = "";
        for (XTerm arg : arguments) {
            sb.append(sep);
            sep = ", ";
            sb.append(arg);
        }
        sb.append(")");
        return sb.toString();
    }

    public int hashCode() {
        int hash = 29;
        for (XTerm arg: arguments) {
        	if (arg != null)
            hash += arg.hashCode();
        }
        return hash;
    }
    
    public boolean hasEQV() {
        for (XTerm arg: arguments) {
            if (arg.hasEQV())
                return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof XFormula) {
            XFormula c = (XFormula) o;
            if (c.arguments().size() == arguments().size()) {
                for (int i = 0; i < arguments().size(); i++) {
                    XTerm ti = arguments().get(i);
                    XTerm ci = c.arguments().get(i);
                    if (! ti.equals(ci))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    public XPromise toPromise() {
        throw new Error("Not implemented yet.");
    }
}
