/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A representation of constraints of the form X1=t1 && ... Xk == tk.
 * Note that there is no unification, only checking. So it is possible
 * to represent such a constraint directly as a mapping from Xi to ti.
 * The constraint is implemented as a Map from variables to terms.
 * 
 * @author vj
 *
 */
public class XConstraint_c implements XConstraint, Cloneable {

    public static final boolean DUMP_EQV = true;

    /** Variable to use for self in the constraint. */
    XRoot self;

    // Maps XTerms to nodes.
    protected HashMap<XTerm, XPromise> roots;

    public HashMap<XTerm, XPromise> roots() {
        return roots;
    }
    
    public XRoot self() {
        return self;
    }

    boolean consistent = true;
    boolean valid = true;

    public XConstraint_c() {
        self = genEQV(XTerms.makeFreshName("_self"), false);
//        self = genEQV(XTerms.makeName(new Object(), "self"), false);
    }

    /**
     * Copy this constraint logically; that is, create a new constraint
     * that contains the same equalities (if any) as the current one.
     */
    public XConstraint_c copy() {
        XConstraint_c c = new XConstraint_c();
        try {
            return copyInto(c);
        }
        catch (XFailure f) {
            c.setInconsistent();
            return c;
        }
    }

    /**
     * Return the result of copying this into c. Assume that c will be 
     * the depclause of the same base type as this, hence it is ok to 
     * copy self-clauses as is. 
     * @param c
     * @return
     */
    private XConstraint_c copyInto(XConstraint_c c) throws XFailure {
        c.addIn(this);
        return c;
    }

    /** Add in a constraint, unifying this.self and c.self */
    public XConstraint addIn(XConstraint c)  throws XFailure {
        if (c != null) {
            List<XTerm> result = c.constraints();
            if (result == null)
                return this;
            for (XTerm t : result) {
                addTerm(t.subst(self(), c.self()));
            }
        }
        return this;
    }

    public XVar bindingForVar(XVar v) {
        try {
            XPromise p = lookup(v);
            if (p != null && p.term() instanceof XVar && ! p.term().equals(v)) {
                return (XVar) p.term();
            }
        }
        catch (XFailure e) {
        }

        return null;
    }

    public XConstraint removeVarBindings(XVar v) {
        try {
            XConstraint c = new XConstraint_c();
            for (XTerm t : constraints()) {
                if (t instanceof XEquals) {
                    XEquals eq = (XEquals) t;
                    XTerm left = eq.left();
                    XTerm right = eq.right();
                    if (left.equals(v) || right.equals(v)) {
                        continue;
                    }
                }
                c.addTerm(t.subst(c.self(), self()));
            }
            return c;
        }
        catch (XFailure e) {
            return this;
        }
    }

    /**
     * Is the constraint consistent? i.e. X=s and X=t have not been added to it,
     * where s and t are not equal.
     */
    public boolean consistent() {
        if (consistent) {
            List<XTerm> atoms = constraints();
            if (atoms.size() == 0) {
                consistent = true;
            }
            else {
            	for (XTerm t : atoms) {
            		if (! consistent)
            			break;
            		Solver solver = t.solver();
            		if (solver != null) {
            			consistent &= solver.isConsistent(atoms);
            		}
                }
            }
        }		
        return consistent;
    }

    /** Is the constraint valid? i.e. vacuous.
     * 
     */
    public boolean valid() { 	
        if (valid) {
            if (! consistent)
                return false;
            List<XTerm> atoms = constraints();
            valid = atoms.size() == 0;
        }		
        return valid;
    }


    public XPromise intern(XTerm term) throws XFailure {
        return intern(term, null);
    }
    
    /**
     * Used to implement substitution:  if last != null, term, is substituted for 
     * the term that was interned previously to produce the promise last. This is accomplished by
     * returning last as the promise obtained by interning term, unless term is a literal, in which
     * case last is forwarded to term, and term is returned. This way incoming and outgoing edges 
     * (from fields) from last are preserved, but term now "becomes" last.
     * Required: on entry, last.value == null.
     * The code will work even if we have literals that are at types where properties are permitted.
     * @param term
     * @param last
     * @return
     */
    public XPromise intern(XTerm term, XPromise last) throws XFailure {
        if (term instanceof XPromise) {
            XPromise q = (XPromise) term;
            
            // this is the case for literals, for here
            if (last != null) {
                try {
                    last.bind(q);
                }
                catch (XFailure f) {
                    throw new XFailure("A term ( " + term + ") cannot be interned to a promise (" + last + ") whose value is not null.");
                }
            }
            return q;
        }

        // let the term figure out what to do for itself.
        return term.internIntoConstraint(this, last);
    }

    public XPromise internBaseVar(XVar baseVar, boolean replaceP, XPromise last) throws XFailure {
        if (roots == null)
            roots = new HashMap<XTerm, XPromise>();
        XPromise p = (XPromise) roots.get(baseVar);
        if (p == null) {
            p = (replaceP && last != null) ? last : new XPromise_c(baseVar);
            roots.put(baseVar, p);
        }
        return p;
    }
    
    public void addPromise(XTerm p, XPromise node) {
        if (roots == null)
            roots = new HashMap<XTerm, XPromise>();
        roots.put(p, node);
    }

    public void internRecursively(XVar v) throws XFailure {
        intern(v);
    }
    
    /**
     * Return the promise obtained by looking up term in this. Does not create new
     * nodes in the constraint graph. Does not return a forwarded promise.
     */
    public XPromise lookup(XTerm term) throws XFailure {
        XPromise result = lookupPartialOk(term);
        if (!(result instanceof XPromise_c))
            return result;
        // it must be the case that term is a XVar.
        if (term instanceof XVar) {
            XVar var = (XVar) term;
            XVar[] vars = var.vars();
            XPromise_c resultC = (XPromise_c) result;
            int index = resultC.lookupReturnValue();
            return (index == vars.length) ? result : null;
        }
        return null;
    }
    
    public XPromise lookupPartialOk(XTerm term) throws XFailure {
        if (term == null)
            return null;
        
        if (term instanceof XPromise)
            // this is the case for literals, for here
            return (XPromise) term;
        
        // otherwise it must be a XVar.
        if (roots == null)
            return null;
        
        if (term instanceof XVar) {
            XVar var = (XVar) term;
            XVar[] vars = var.vars();
            XVar baseVar = vars[0];
            XPromise p = (XPromise) roots.get(baseVar);
            if (p == null)
                return null;
            return p.lookup(vars, 1);
        }
        
        return null;
    }

    /**
     * Add t1=t2 to the constraint, unless it is inconsistent. 
     * @param var -- t1
     * @param val -- t2
     */
    public void addBinding(XTerm left, XTerm right) throws XFailure {
        assert left != null;
        assert right != null;

        if (!consistent)
            return;
        
        if (roots == null)
            roots = new HashMap<XTerm, XPromise>();

        XPromise p1 = intern(left);
        XPromise p2 = intern(right);
        
        boolean modified = p1.bind(p2);
        valid &= !modified;
    }

    /**
     * Add the atomic formula t.
     * @param t
     * @return
     */
    public void addAtom(XTerm t) throws XFailure {
        if (!consistent)
            return;
        
        if (roots == null)
            roots = new HashMap<XTerm, XPromise>();

        XPromise p = lookup(t);
        
        if (p != null)
            // nothing to do
            return;
        
        p = intern(t);
        
        addDerivedEqualities(t);
    }

    public void addDerivedEqualities(XTerm t) throws XFailure {
    	Solver solver = t.solver();
    	if (solver != null) {
            solver.addDerivedEqualitiesInvolving(this, t);
        }
    }

    public XConstraint addBindingPromise(XTerm t1, XPromise p)  {
        try {
            assert t1 != null;
            if (!consistent)
                return this;
            if (roots == null)
                roots = new HashMap<XTerm, XPromise>();
            XPromise p1 = intern(t1);
            boolean modified = p1.bind(p);
        }
        catch (XFailure z) {
            consistent = false;
        }
        return this;
    }

    public void addTerms(List<XTerm> terms) throws XFailure {
        for (XTerm t : terms) {
            addTerm(t);
        }
    }

    /**
     * If other is not inconsistent, and this is consistent,
     * checks that each binding X=t in other also exists in this.
     * @param other
     * @return
     */
    public boolean entails(XConstraint other) throws XFailure {
        if (!consistent())
            return true;
        if (other == null || other.valid())
            return true;
        List<XTerm> otherConstraints = other.constraints();
        XRoot otherSelf = other.self();
        return entails(otherConstraints, otherSelf);
    }

    public List<XTerm> constraints() {
        return constraints(new ArrayList<XTerm>());
    }
    
    public List<XTerm> constraints(List<XTerm> result) {
        if (roots == null)
            return result;
        for (XPromise p : roots.values()) {
            p.dump(result, self);
        }
        return result;
    }
    
    public List<XTerm> extConstraints() {
        return extConstraints(new ArrayList<XTerm>());
    }

    public List<XTerm> extConstraints(List<XTerm> result) {
        if (roots == null)
            return result;
        for (XPromise p : roots.values()) {
            p.extDump(result, self);
        }
        return result;
    }

    private boolean entails(List<XTerm> conjuncts, XRoot self) throws XFailure {
        XConstraint_c me = saturate();
        if (me == this) me = copy();
        
        List<XTerm> subst = new ArrayList<XTerm>(conjuncts.size());
        for (XTerm term : conjuncts) {
            XTerm t = term.subst(me.self(), self);
            subst.add(t);            
        }
        
        Set<XTerm> visited = new HashSet<XTerm>();
        for (XTerm term : subst) {
            term.saturate(me, visited);
        }
        visited = null; // free up for gc
        
        if (! me.consistent()) {
            return true;
        }
        
        class BruteForceEQVHelper {
            void put(Map<XTerm,List<XTerm>> m, XTerm v, XTerm t) {
                if (v.equals(t))
                    return;
                List<XTerm> l = m.get(v);
                if (l == null) {
                    l = new ArrayList<XTerm>(1);
                    m.put(v, l);
                }
                if (! l.contains(t))
                    l.add(t);
            }
            
            /** Add mappings for EQVs in the left term by unifying with the right term. */
            void unify(Map<XTerm,List<XTerm>> m, XTerm left, XTerm right) {
                if (left.isEQV()) {
                    put(m, left, right);
                }
                if (left instanceof XField && right instanceof XField) {
                    XField l = (XField) left;
                    XField r = (XField) right;
                    if (l.field().equals(r.field())) {
                        unify(m, l.receiver(), r.receiver());
                    }
                }
                if (left instanceof XEquals && right instanceof XEquals) {
                    XFormula l = (XFormula) left;
                    XFormula r = (XFormula) right;
                    if (DUMP_EQV) {
                        unify(m, l.left(), r.left());
                        unify(m, l.right(), r.right());
                        unify(m, l.right(), r.left());
                        unify(m, l.left(), r.right());
                    }
                    return;
                }
                if (left instanceof XFormula && right instanceof XFormula) {
                    XFormula l = (XFormula) left;
                    XFormula r = (XFormula) right;
                    if (l.operator().equals(r.operator()) && l.arguments().size() == r.arguments().size()) {
                        for (int i = 0; i < l.arguments().size(); i++) {
                            unify(m, l.arguments().get(i), r.arguments().get(i));
                        }
                    }
                }
            }
            void unify(Map<XTerm,List<XTerm>> m, XTerm term, XConstraint c) {
                // Try to find a term in c that unifies with term.
                for (XTerm t : c.constraints()) {
                    unify(m, term, t);
                }
            }
            
            /** Compute all possible bindings for EQVs in the terms list by unifying with the terms in c.  This should over-approximate the set of bindings. */
            Map<XTerm, List<XTerm>> eqvBindings(XConstraint c, List<XTerm> terms) {
                Map<XTerm,List<XTerm>> m = new HashMap<XTerm,List<XTerm>>();
                for (XTerm term : terms) {
                    if (term instanceof XEquals) {
                        XEquals eq = (XEquals) term;
                        XTerm left = eq.left();
                        XTerm right = eq.right();
                        unify(m, left, right);
                        unify(m, right, left);
                        if (! DUMP_EQV) {
                            continue;
                        }
                    }
                    unify(m, term, c);
                }
                return m;
            }

            /** Check if me entails terms (with me.self for self) by trying all possible EQV bindings. */
            boolean entailsWithEQV(XConstraint_c me, List<XTerm> terms, XRoot self, Map<XTerm, List<XTerm>> eqvBindings) throws XFailure {
                // If we've bound all the EQVs, check entailment.
                if (eqvBindings.isEmpty()) {
                    for (XTerm term : terms) {
                        XTerm t = term.subst(me.self(), self);
                        if (! me.entails(t))
                            return false;
                    }
                    return true;
                }
                
                // Otherwise, try all possible bindings for each EQV.
                // This loop binds one EQV, then recurses, removing that EQV from the map.
                for (Map.Entry<XTerm, List<XTerm>> e : eqvBindings.entrySet()) {
                    XTerm v = e.getKey();
                    List<XTerm> ts = e.getValue();
                    
                    Map<XTerm,List<XTerm>> m = new HashMap<XTerm, List<XTerm>>(eqvBindings);
                    m.remove(v);

                    if (ts == null || ts.size() == 0) {
                        if (entailsWithEQV(me, terms, self, m))
                            return true;
                    }
                    else {
                        for (int i = 0; i < ts.size(); i++) {
                            XTerm t = ts.get(i);
                            XConstraint_c me2 = me.copy();
                            try {
                                XTerm t2 = t.subst(me.self(), self);
                                XTerm t3 = t2.subst(me2.self(), me.self());
                                me2.addBinding(v, t3);
                            }
                            catch (XFailure z) {
                                continue;
                            }
                            if (! me2.consistent())
                                continue;
                            if (entailsWithEQV(me2, terms, self, m))
                                return true;
                        }
                    }
                }
                
                return false;
            }
        }
        
        BruteForceEQVHelper helper = new BruteForceEQVHelper();
        Map<XTerm,List<XTerm>> eqvBindings = helper.eqvBindings(me, conjuncts);
        
if (true)
    return helper.entailsWithEQV(me, conjuncts, self, eqvBindings);
        
        
        // DOES NOT WORK. NEEDS TO BE FIXED.
        // Fails entailment tests test9 and test10.
        /* Start Deletion. When deleting, uncomment related section in XPromise_c.dump.*/
if (false) {
        try {
            for (XTerm term : subst) {
                if (term.hasEQV()) {
                    me.addTerm(term);
                }
            }
        }
        catch (XFailure z) {
            return false;
        }
        
        if (! me.consistent()) {
            return false;
        }
        /* Stop Deletion. */
}

        for (XTerm term : subst) {
            if (! me.entails(term))
                return false;
        }
        
        return true;
    }

    /** Traverse the terms in the constraint, adding in their self constraints. */
    public XConstraint_c saturate() throws XFailure {
        XConstraint_c c = (XConstraint_c) copy();
        Set<XTerm> visited = new HashSet<XTerm>();
        for (XTerm term : constraints()) {
            term.subst(c.self(), self()).saturate(c, visited);
        }
        return c;
    }

    /** Return true if this constraint entails t. */
    private boolean entails(XTerm t) throws XFailure {
        if (t instanceof XEquals) {
            XEquals f = (XEquals) t;
            XTerm left = f.left();
            XTerm right = f.right();
            
            if (entails(left, right)) {
                return true;
            }
        }

        List<XTerm> atoms = constraints();

        if (t.solver() != null) {
        	if (t.solver().entails(atoms, t))
        		return true;
        }

        for (XTerm ta : atoms) {
        	if (ta.solver() != null && ta.solver() != t.solver()) {
        		if (ta.solver().entails(atoms, t))
        			return true;
        	}
        }

        return false;
    }

    /** Return true if this constraint entails that t1==t2. */
    private boolean entails(XTerm t1, XTerm t2) throws XFailure {
        if (!consistent)
            return true;
        
        XPromise p1 = lookupPartialOk(t1);
        if (p1 == null) // No match, the term t1 is not equated to anything by this.
            return false;

        int r1Count = 0;
        XVar[] vars1 = null;
        if (p1 instanceof XPromise_c) {
            r1Count = ((XPromise_c) p1).lookupReturnValue();
            vars1 = ((XVar) t1).vars();
        }
        
        XPromise p2 = lookupPartialOk(t2);
        if (p2 == null) // No match, the term t2 is not equated to anything by this.
            return false;

        int r2Count = 0;
        XVar[] vars2 = null;
        if (p2 instanceof XPromise_c) {
            r2Count = ((XPromise_c) p2).lookupReturnValue();
            vars2 = ((XVar) t2).vars();
        }

        if ((r1Count == 0 || r1Count == vars1.length) && (r2Count == 0 || r2Count == vars2.length)) {
            // exact lookups
            return p1.equals(p2);
        }

        // at least one of them had a suffix left over
        // Now the returned promises must match, and they must have the same
        // suffix.
        if (!p1.equals(p2))
            return false;

        // Now ensure that they have the same suffix left over.
        int residual1 = vars1.length - r1Count, residual2 = vars2.length - r2Count;
        if (residual1 != residual2)
            return false;

        for (int i = 0; i < residual1; i++) {
            XVar v1 = vars1[r1Count + i];
            XVar v2 = vars2[r2Count + i];
            if (v1 instanceof XField && v2 instanceof XField) {
                XField f1 = (XField) v1;
                XField f2 = (XField) v2;
                if (! f1.field().equals(f2.field())) {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }

    public boolean equiv(XConstraint other) throws XFailure {
        boolean result = entails(other);
        if (result) {
            if (other == null)
                result = valid;
            else
                result = other.entails(this);
        }
        return result;
    }

    public XTerm find(XName varName) throws XFailure {
        if (!consistent || roots == null)
            return null;
        XPromise self = (XPromise) roots.get(self());
        if (self == null)
            return null;
        XPromise result = self.lookup(varName);
        return result == null ? null : result.term();
    }
    
    private static boolean printEQV = false;

    public String toString() {
        XConstraint c = this;
        
        
        if (! c.consistent()) {
            return "{inconsistent}";
        }
        
        try {
            XConstraint c2 = this.substitute(this.genEQV(XTerms.makeName("xyzzy"), false), this.self());
            c = saturate();
        }
        catch (XFailure z) {
            return "{inconsistent}";
        }

        try {
            c = c.substitute(c.genEQV(XTerms.makeName("self"), false), c.self());
        }
        catch (XFailure z) {
            return "{inconsistent}";
        }

        String str;

        if (printEQV)
            str = c.constraints().toString();
        else
            str = c.extConstraints().toString();

        str = str.substring(1, str.length() - 1);

        return "{" + str + "}";
    }

    public XEQV genEQV() {
        return genEQV(true);
    }

    public XEQV genEQV(boolean hidden) {
        XName handle = XTerms.makeFreshName();
        return genEQV(handle, hidden);
    }

    public XEQV genEQV(XName name, boolean hidden) {
        XEQV result = new XEQV_c(name, hidden);
        return result;
    }

    public XConstraint substitute(XTerm y, XRoot x) throws XFailure {
        assert (y != null && x != null);
        
        if (y.equals(x))
            return this;
        
        if (! consistent)
            return this;

        // Don't do the quick occurrence check; x might occur in a self constraint.
        //		XPromise last = lookupPartialOk(x);
        //		if (last == null) return this; 	// x does not occur in this

        XConstraint result = new XConstraint_c();
        
        for (XTerm term : constraints()) {
            // if term is y==x.f, the subst will produce y==y.f, which is a cycle--bad!
            //		    if (term instanceof XEquals_c) {
            //		        XEquals_c eq = (XEquals_c) term;
            //		        XTerm l = eq.left();
            //		        XTerm r = eq.right();
            //		        if (y.equals(l) || y.equals(r))
            //		            continue;
            //		    }
            XTerm t = term.subst(y, x, true);
            
            try {
                XConstraint c = result.copy();
                t = t.subst(c.self(), self(), true);
                c.addTerm(t);
                result = c;
            }
            catch (XFailure z) {
                throw z;
            }
        }
        //		XConstraint_c result = clone();
        //		result.valid = true;
        //		result.applySubstitution(y,x);
        return result;
    }
    public XConstraint substitute(HashMap<XRoot, XTerm> subs) throws XFailure {
        XConstraint c = this;
        for (Map.Entry<XRoot,XTerm> e : subs.entrySet()) {
            XRoot x = e.getKey();
            XTerm y = e.getValue();
            c = c.substitute(y, x);            
        }
        return c;
    }
    public XConstraint substitute(XTerm[] ys, XRoot[] xs) throws XFailure {
        return substitute(ys, xs, true);
    }
    public XConstraint substitute(XTerm[] ys, XRoot[] xs, boolean propagate) throws XFailure {
        assert xs.length == ys.length;
        final int n = xs.length;
        XConstraint result = this;
        for (int i=0; i < n; i++) {
            XRoot x = xs[i];
            XTerm y = ys[i];
            result = result.substitute(y, x);
        }
        return result;
    }
    public void applySubstitution(HashMap<XRoot, XTerm> subs) throws XFailure {
        for (Map.Entry<XRoot, XTerm> e : subs.entrySet()) {
            XRoot x = e.getKey();
            XTerm y = e.getValue();
            applySubstitution(y,x);
        }
    }
    public void applySubstitution(XTerm y, XRoot x) throws XFailure {
        if (roots == null) {
            // nothing to substitute
            return;
        }
        
        // Get the node for p.
        XPromise p = (XPromise) roots.get(x);

        if (p == null) {
            // nothing to substitute
            return;	
        }

        // Remove x to avoid variable capture issues (y may be contain or be equal to x).
        roots.remove(x);

        // Get the node for y.  Since q may contain references to p or nodes reachable from p, interning y
        // may add back x to the root set.  For example, we might be replacing self with self.location.
        XPromise q = intern(y);

        // Replace references to p with references to q.
        replace(q, p);

        {
            HashMap<XName, XPromise> pfields = p.fields();
            HashMap<XName, XPromise> qfields = q.fields();

            if (pfields != null && qfields != null)
                for (XName field : pfields.keySet()) {
                    XPromise pf = pfields.get(field);
                    XPromise qf = qfields.get(field);
                    if (qf != null)
                        replace(qf, pf);
                }
        }

        // Substitute y for x in the promise terms.
        {
            Collection<XPromise> rootPs = roots.values();
            for (Map.Entry<XTerm, XPromise> e : ((Map<XTerm,XPromise>) roots.clone()).entrySet()) {
                if (!e.getKey().equals(p.term())) {
                    XPromise px = e.getValue();
                    XTerm t = px.term();
                    t = t.subst(q.term(), x);
                    XPromise tp = intern(t);
                    if (tp != px)
                        px.setTerm(tp.term());
                }
            }
        }

        // Now, add back x as a root, if we can.
        if (q.term().equals(x)) {
            // Cannot replace x with x.  Instead,
            // introduce an EQV and substitute that for x.

            XEQV v = genEQV();

            // Clone the root map, with the renaming map primed
            // with x -> v
            HashMap<XPromise, XPromise> renaming = new HashMap<XPromise,XPromise>();
            renaming.put(q, new XPromise_c(v));

            for (Map.Entry<XTerm, XPromise> m : roots.entrySet()) {
                XTerm var = m.getKey();
                XPromise p2 = m.getValue();
                XPromise q2 = p2.cloneRecursively(renaming);
                m.setValue(q2);
            }

            return;
        }

        if (p instanceof XLit) {
            q.bind(p);
            return;
        }

        XPromise xf = p.value();

        if (xf != null) {
            q.bind(xf);
        }
        else {
            // p is no longer a root, but fields reachable from p may still mention x rather than y (or more precisely, q.term()).
            // Replace the term in p with q's term; this will fix up fields of x to be fields of y.
            HashMap<XName,XPromise> fields = p.fields(); 
            if (fields != null) {
                for (Map.Entry<XName, XPromise> entry : fields.entrySet()) {
                    XPromise p1 = entry.getValue();
                    if (p1.term() instanceof XField) {
                        XName field = ((XField) p1.term()).field();
                        XTerm t = XConstraint_c.makeField(q.term(), field);
                        XPromise q1 = intern(t);
                        if (q1 == p1) {
                            p1.setTerm(t);
                        }
                        else {
                            // The old field node was replaced and so unifies with a different node.
                            p1.setTerm(q1.term());
                        }
                        if (p1.value() == p1) {
                            ((XPromise_c) p1).value = null;
                        }
                    }
                }
            }

            //		    if (fields != null) {
            //			for (Map.Entry<XName, XPromise> entry : fields.entrySet()) {
            //			    XName s = entry.getKey();
            //			    XPromise orphan = entry.getValue();
            //			    orphan.replaceDescendant(q, p);
            //			    XField oldTerm = (XField) orphan.term();
            //			    XName oldField = oldTerm.field();
            //			    XTerm t = makeField(q.term(), oldField);
            //			    XPromise tp = intern(t);
            //			    orphan.setTerm(tp.term());
            //			    q.addIn(s, orphan);
            //			}
            //		    }
        }
    }

    static XTerm makeField(XTerm target, XName field) {
        XTerm t;
        if (target instanceof XVar) {
            t = XTerms.makeField((XVar) target, field);
        }
        else {
            t = XTerms.makeAtom(field, target);
        }
        return t;
    }

    /** Replace all pointers entering x in this constraint with pointers entering y.
     * 
     * @param y
     * @param x
     * @param c TODO
     */
    private void replace(XPromise y, XPromise x) throws XFailure {
        //		HashMap<XPromise, XPromise> renaming = new HashMap<XPromise,XPromise>();
        //		renaming.put(x, y);
        //
        //		for (Map.Entry<XTerm, XPromise> m : roots.entrySet()) {
        //			XTerm var = m.getKey();
        //			XPromise p2 = m.getValue();
        //			XPromise q2 = p2.cloneRecursively(renaming);
        //			m.setValue(q2);
        //		}

        Collection<XPromise> rootPs = roots.values();
        for (Map.Entry<XTerm, XPromise> e : roots.entrySet()) {
            if (!e.getKey().equals(x.term())) {
                XPromise p = e.getValue();
                p.replaceDescendant(y, x, this);
            }
        }
    }

    public boolean hasVar(XRoot v) {
        if (roots == null)
            return false;
        return roots.keySet().contains(v);
    }

    public void addSelfBinding(XTerm var) throws XFailure {
        addBinding(self(), var);
    }

    // FIXME: need to convert f(g(x)) into \exists y. f(y) && g(x) = y when f and g both atoms
    // This is needed for Nelson-Oppen to work correctly.
    // Each atom should be a root.
    public void addTerm(XTerm term) throws XFailure {
        if (term.isAtomicFormula()) {
            addAtom(term);
        }
        else if (term instanceof XVar) {
            addBinding(term, XTerms.TRUE);
        }
        else if (term instanceof XNot) {
            XNot t = (XNot) term;
            if (t.unaryArg() instanceof XVar)
                addBinding(t.unaryArg(), XTerms.FALSE);
            if (t.unaryArg() instanceof XNot)
                addTerm(((XNot) t.unaryArg()).unaryArg());
        }
        else if (term instanceof XAnd) {
            XAnd t = (XAnd) term;
            addTerm(t.left());
            addTerm(t.right());
        }
        else if (term instanceof XEquals) {
            XEquals eq = (XEquals) term;
            XTerm left = eq.left();
            XTerm right = eq.right();
            addBinding(left, right);
        }
        else {
            throw new XFailure("Unexpected term |" + term + "|");
        }
        
        XConstraint s = term.selfConstraint();
        if (s != null) {
            s = s.substitute(term, s.self());
            addIn(s);
        }
    }

    public void setInconsistent() {
        this.consistent = false;
    }
}
