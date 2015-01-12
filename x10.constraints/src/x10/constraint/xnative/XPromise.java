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

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import x10.constraint.xnative.XNativeConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.xnative.XNativeField;
import x10.constraint.xnative.XNativeLit;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.xnative.XNativeVar;
import x10.constraint.visitors.XGraphVisitor;
import x10.util.CollectionFactory;


/**
 * All nodes that occur in the graph maintained by a constraint
 * must implement Promise. Thus a Promise is a pointer into the state of some specific constraint.
 * <p>
 * A promise contains fields: 
 * <ol>
 * <li>XPromise value  -- if non-null, points to a promise this one has been equated with.
 * <li>Collection<XPromise> disequals -- contains set of other nodes this has been disequated with
 * <li>XTerm var  -- externally visible term labeling this promise
 * <li>Map<XName, XPromise> fields -- hashmap of fields of this promise (if any). 
 * </ol>
 * It maintains the invariant <code>> value != null</code> implies <code>disequals==null && fields == null</code>.
 * That is, if <code>value !=null</code> (in this case we say the promise ie <em>bound</em>), all further
 * information about this node in the graph is found by going to to <code>value</code>, except for the <code>XTerm</code>  
 * labeling this node (<code>var</code>). See <code>term()</code>.
 * 
 * <p>If <code>a.value==b</code>
 * we sometimes say that <code>b</code> has an incoming "eq" link, and <code>a</code> has an outgoing "eq" link.
 * 
 * <p> Constraint graphs may contain sequences of such bindings. These can be shortened without any
 * semantic consequence.
 * 
 * <p> Constraint graphs are always acyclic. 
 * 
 * <p> Note: This class is not public only so that extensions of this constraint system may use it.
 * This interface is <em>not</em> intended to be used by customers of the constraint system.
 * @author vj
 *
 */
public class XPromise implements Cloneable, Serializable{
    /**
     * The externally visible XTerm that this node represents in the constraint
     * graph. May be null, if this promise corresponds to an internal node.
     */
    protected XNativeTerm nodeLabel;

    /**
     * This node may have been equated to another node, n. If so, value contains
     * the reference to n. Can be null.
     */
    protected XPromise value;

    /**
     * Lazily created collection of XPromises to which this one has been disequated.
     */
    protected Collection<XPromise> disEquals;

    /**
     * fields captures constraints on fields of this variable, if any.
     * Invariant: if value is not null, then fields must be null, the
     * constraints are translated to the target of the value.
     * 
     * We represent composite terms (e.g., binary operators, unary operators,
     * calls) as promises with fields representing the operator and the
     * operands.
     */
    protected Map<Object, XPromise> fields;

    /**
     * Create a new promise labeled with the external term c.
     * @param c -- the term labeling the promise.
     */
    public XPromise(XNativeTerm term) {
        super();
        value = null;
        nodeLabel = term;
    }

    /**
     * Create a new promise with the given fields. 
     * @param fields -- the fields of the promise
     */
    public XPromise(Map<Object, XPromise> fields) {
        this(fields, null);
    }

    /**
     * Create a new promise with the given fields, and labeled with the given term.
     * @param fields -- the fields of the promise
     * @param term -- the term labeling the promise
     */
    public XPromise(Map<Object, XPromise> fields, XNativeTerm term) {
        this(term, null, fields);
    }

    /**
     * Create a new promise with the given fields, the given label (var) and
     * pointing to value. Either value==null || fields==null. var may also be null.
     * @param var -- the term labeling the promise
     * @param value -- the XTerm that this promise points to
     * @param fields -- the fields of the promise.
     */
    public XPromise(XNativeTerm var, XPromise value, Map<Object, XPromise> fields) {
        assert value==null || fields == null;
        this.nodeLabel = var;
        this.value = value;
        if (fields != null)
            this.fields = new LinkedHashMap<Object, XPromise>(fields);
    }



    /** A promise to which this promise is bound. */
    public XPromise value() { return value;  }

    /**
     * Return the term that labels this promise. This term is intended to be the canonical XTerm
     * labeling this promise, following the direct path from the root node.
     * Note: this promise may be forwarded to another; yet the term returned is this's term, 
     * not the term corresponding to the target promise.
     * @return null if this promise is an internal promise.
     */
    public XNativeTerm term() {return nodeLabel;}
    /** Map from field names f to promises term().f */
    public Map<Object, XPromise> fields() {return fields;}

    /**
     * Set the term corresponding to this promise. The term may be null, such a promise is 
     * called a silent promise. Silent promises are not dumped (that is, they do not 
     * correspond to constraints on externally visible terms). They correspond to internal
     * nodes that are used just for implementation efficiency.
     * @param term
     */
    public void setTerm(XNativeTerm term) {
        Set<XPromise> visited = CollectionFactory.newHashSet();
        visited.add(this);
        setTerm(term, visited);
    }

    public void setTerm(XNativeTerm term, Set<XPromise> visited) {
        nodeLabel = (XNativeTerm)term;
        if (nodeLabel != null && fields != null) {
            for (Map.Entry<Object, XPromise> entry : fields.entrySet()) {
                Object key = entry.getKey();
                XPromise p = entry.getValue();
                if (visited.contains(p)) continue;
                visited.add(p);
                assert p.term() instanceof XNativeField : term + "." + key + " = " + p + " (not a field)";
                XNativeField<?> f = (XNativeField<?>) p.term();
                assert f.field().equals(key) : term + "." + key + " = " + p + " (different field)";
                p.setTerm((XNativeTerm)f.copyReceiver((XNativeVar)term), visited);
            }
        }
    }

    /** Has this promise been forwarded?
     * 
     * @return true if it has been forwarded (its value !=null)
     */
    public boolean forwarded() {return value != null;}

    /** Does this node have children?
     * A node cannot both have children and be forwarded.
     */
    public boolean hasChildren() {return fields != null;}
    public void setLabel(XNativeVar v) {nodeLabel = v;}
    protected XPromise clone() {
        XPromise result = null;
        try {
            result = (XPromise) super.clone();
        }
        catch (CloneNotSupportedException z) {
            // But it is!
        }
        return result;
    }

    /** Follow the chain of non-null value's, returning the one at the end.
     * 
     * @return
     */
    public XPromise lookup() {
        if (value != null) return value.lookup();
        return this;
    }

    /**
     * this must be the promise corresponding to the index'th element in the list vars.
     * Return the node in the graph of constraint c obtained
     * by following the path specified by vars[index],.., vars[vars.length-1] from this
     * node. 
     * 
     * <p> Create new nodes if necessary so that this path is defined in the graph of c.  
     * @param vars vars must be a sequence XVar, XField, ... XField, satisfying the property
     * that the receiver of each element (other than the first) is the preceding element.
     * @param index
     * @param c
     * @return the node at the end of the path. This must not be a forwarded node.
     * @throws XFailure 
     */
    public XPromise intern(XNativeVar[] vars, int index) throws XFailure {
        return intern(vars, index, null);
    }


    /**
     * vars is as above. If last is not null, then do not create a new promise
     * for vars[index-1], instead use last.
     * @return
     * @throws XFailure 
     */
    public XPromise intern(XNativeVar[] vars, int index, XPromise last)  {
        assert index >= 1;

        // follow the eq link if there is one.
        if (value != null )      return value.intern(vars, index, last);
        if (index == vars.length)return this;
        // if not, we need to add this path here. Ensure fields is initialized.
        if (fields == null) fields = CollectionFactory.<Object, XPromise>newHashMap();
        assert vars[index] instanceof XNativeField;
        XNativeField<?> f = (XNativeField<?>) vars[index];
        Object s = f.field();
        // check this edge already exists.
        XPromise p = (XPromise) fields.get(s);
        if (p == null) {
            // no edge. Create a new promise and add this edge.
            p = (index == vars.length - 1 && last != null) 
            ? last : new XPromise(nodeLabel instanceof XNativeVar ? (XNativeTerm)f.copyReceiver((XNativeVar)nodeLabel) : f);
            fields.put(s, p);
        }
        // recursively, intern the rest of the path on the child.
        return p.intern(vars, index + 1, last);
    }

    /**
     * An eq link entering this has just been established. Now the 
     * children of the source of the link have to be transferred to this.
     * If this does not have an s-child, then install the child as its s-child.
     * If it does, then recursively bind the s-child to this's s child.
     * 
     * @param s -- the name of the field
     * @param child -- the s child of the source of the eq link.
     */
    public void addIn(Object s, XPromise orphan, XNativeConstraint parent) throws XFailure {
        if (value != null) {
            // Alternative is to fwd it blindly, that would be correct, but i
            // want to know
            // if this is happening. It should not happen.
            throw new XFailure("The node " + this + " is forwarded to " 
                               + value + "; the " + s + " child, " + orphan 
                               + ", cannot be added to it.");
        }

        if (fields == null) fields = new LinkedHashMap<Object, XPromise>();
        XPromise child = (XPromise) fields.get(s);

        if (child != null) {
            while (child.forwarded())  child = child.value();
            while (orphan.forwarded()) orphan = orphan.value();
            orphan.bind(child, parent);
            return;
        }
        fields.put(s, orphan);
    }


    /**
     * Bind this promise to the given target. All the children of this, if any, have to be
     * added into the target. target should satisfy the condition that it is not forwarded.
     * Return true if the execution of this operation caused a change to the constraint graph;
     * false otherwise.
     * @param target
     */
    public boolean bind(/* @nonnull */XPromise target, XNativeConstraint parent) throws XFailure {
        assert target.value() == null;

        if (disEquals != null) 
            for (XPromise i : disEquals) {
                // Note: i's value may be set.. so need to get to the end of the chain.
                boolean result = i.lookup().equals(target);
                if (result)
                    throw new XFailure("The promise " + this 
                                       + " cannot be bound to the already disequated " 
                                       + target + ".");
            }
        if (!target.equals(value) /*&& !target.equals(nodeLabel)*//*always true*/) {
            if (forwarded()) throw new XFailure("The promise " + this + 
                                   " is already bound to " + value 
                                   + "; cannot bind it to " + target + ".");
            if (this == target) return false;// nothing to do!
                

            // Check for cycles!
            if (canReach(target) || target.canReach(this))
                throw new XFailure("Binding " + this + " to " + target + " creates a cycle.");
            int pref = term().prefersBeingBound();
            int opref = target.term().prefersBeingBound();
            if (pref == XNativeTerm.TERM_MUST_NOT_BE_BOUND) {
                if (opref == XNativeTerm.TERM_MUST_NOT_BE_BOUND) {
                    if (term().equals(target.term())) return true;
                        throw new XFailure("Cannot bind literal " + term() + " to " + target.term());
                    } 
                return target.bind(this, parent);
            }
            if (pref == XNativeTerm.TERM_SHRUGS_ABOUT_BEING_BOUND &&
                    opref == XNativeTerm.TERM_PREFERS_BEING_BOUND)
                return target.bind(this, parent);
            value = target; // bind
        }
        if (fields != null) { // transfer fields
            for (Map.Entry<Object, XPromise> i : fields.entrySet()) {
                XPromise val = i.getValue();
                // Need to find out the value down this path.
                XNativeTerm t = val.term();
                if (t instanceof XNativeField) {
                    XNativeField<?> thisTerm = (XNativeField<?>) t;
                    XNativeVar root = thisTerm.rootVar();
                    if (root == this.term()) {
                        // Need to relabel, recursively, because this --> target
                        val.setTerm((XNativeTerm)thisTerm.copyReceiver((XNativeVar) target.term()));
                    }
                }
                target.addIn(i.getKey(), val, parent);
            }
            fields = null;
        }

        if (disEquals != null) { // transfer disequals links
            for (XPromise i : disEquals) target.addDisEquals(i.lookup());
            disEquals = null;
        }
        return true;
    }

    /**
     * Bind this promise to the given target using a NOT link. target should satisfy the condition
     * that it is not forwarded. Note that the following is consistent: X !=Y, X.f=Y.f.
     * @param target The term to be notBound to.
     * @return true if the execution of this operation caused a change in the constraint graph;
     * false otherwise.
     * @throws XFailure in case this is already bound to target.
     */
    public boolean disBind(/* @nonnull */XPromise target) throws XFailure {
        assert target.value() == null;
        assert forwarded() == false;
        if (disEquals == null) disEquals = CollectionFactory.newHashSet();
        boolean result = disEquals.add(target);
        return result;
        //if (!target.equals(value) && !target.equals(var)) {
    }

    
    /**
     * Is there a path from here to p? 
     * Can this promise reach p in the directed graph representing the
     * constraints?
     */
    public boolean canReach(XPromise p) {
        if (p == this)     return true;
        if (value != null) return value.canReach(p);
        if (fields != null)
            for (XPromise q : fields.values())
                if (q.canReach(p)) return true;
        return false;
    }

    /**
     * Let t1 be path (if path is not null), else the term labeling this promise.
     * If this promise has an outgoing edge to t2, and either dumpEQV is true or 
     * ! t1.hasEQV(), then output t1==t2 to result, unless hideFake is true and one of t1 or t2 is 
     * a fake field.
     * If this promise has fields, then recursively continue dumping with 
     * the children, passing them a path t1.f (for field f) if  
     * t1 is an instance of XVar, otherwise passing a path null (in this case t1 is an
     * atom and the children are its subterms). 
     * (This takes care of multiple paths entering the promise.)
     * @param result
     * @param oldSelf 
     */
    public boolean visit(XNativeVar path, XGraphVisitor xg, XNativeConstraint parent) {
        XNativeTerm t1 = path == null? term() : path;
        if (t1 == null) return true;
        if (t1.isAtomicFormula()) return xg.rawVisitAtomicFormula(t1);

        if (value != null) {
            XNativeTerm t2 = lookup().term(); //canonical term for this
            boolean result = xg.rawVisitEquals(t1, t2);
            if (! result) return result;
            // Continue processing the target node if the target is an eqv.
            // there may be fields that need to be processed.
           /* if (t2.hasEQV())
                result = value.visit((XVar) t1, xg, parent);*/
            return result;
        }

        if (fields != null) {
            XNativeVar v = t1 instanceof XNativeVar ? (XNativeVar) t1 : null;
            // If t1 is not an XVar, it is an atomic formula, and the fields 
            // are its sub-terms. Hence v should be null.
            for (Map.Entry<Object,XPromise> m : fields.entrySet()) {
                XPromise p=m.getValue();
                XNativeTerm t=p.term();  
                XNativeVar path2=v==null? null : (XNativeVar)((XNativeField<?>) t).copyReceiver(v);
                boolean result=p.visit(path2,xg, parent);
                if (!result) return result;
            }
        }
        if (disEquals != null) {
            for (XPromise i:disEquals) {
                XNativeTerm nf=i.lookup().term(); 
                boolean result = xg.rawVisitDisEquals(t1, nf);
                if (! result) return result;
            }
        }
        return true;
    }

    private boolean toStringMark = false;
    public String toString(String prefix) {
        if (toStringMark) return "...";
        toStringMark = true;
        String fieldsString = "{";
        if (fields != null) {
            for (Map.Entry<Object, XPromise> entry : fields.entrySet()) {
                fieldsString += "\n" /*prefix + "  " + entry.getKey() + "=" */
                    + entry.getValue().toString(prefix + "  ");
            }
        }
        fieldsString += "\n" + prefix + "}";
        String res = prefix + nodeLabel 
            + ((value != null) ? "->" + value 
                    : ((fields != null) ? fieldsString : "")
                       + (disEquals != null ? " != " + disEquals.toString() : ""));
        toStringMark = false;
        return res;
    }
    public String toString() { return toString(""); }

    /**
     * An eq link entering this has just been established. Now the 
     * disEquals targets of the source of the link have to be transferred to this.
     * 
     * @param other -- the XPromise this is to be disequated to
     */
    public void addDisEquals(XPromise other) {
        if (disEquals == null) 
            disEquals = CollectionFactory.newHashSet();
        disEquals.add(other);
    }
    public boolean hasDisBindings() { return disEquals != null && ! disEquals.isEmpty();}
    
    /**
     * Is this promise asserted to be disequal to other?
     * @param other
     * @return
     */
    public boolean isDisBoundTo(XPromise other) {
        if (term() instanceof XNativeLit) {
            XNativeLit term = (XNativeLit) term();
            XNativeTerm o = other.term();
            if (o instanceof XNativeLit) return ! (term.equals(o));
        }
        if (disEquals == null) return false;
        for (XPromise p : disEquals) 
            if (p.canReach(other)) return true;
        return false;
    }
    public void ensureFields() {
        if (this.fields == null) this.fields = new LinkedHashMap<Object, XPromise>();
    }
}
