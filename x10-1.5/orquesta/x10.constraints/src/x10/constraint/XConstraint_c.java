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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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

	// Maps C_Var's to nodes.
	protected HashMap<XTerm,XPromise> roots;
	public HashMap<XTerm,XPromise> roots() { return roots; }

	// Map from variables to values for negative bindings.
	// protected Map negBindings;

	boolean consistent = true;
	boolean valid = true;
	
	public XConstraint_c() {
	}

	public XConstraint_c(int eqvCount, boolean consistent, Map<XTerm, XPromise> roots, boolean valid) {
		this.consistent = consistent;
		this.roots = new HashMap<XTerm,XPromise>(roots);
		this.valid = valid;
	}

	/** Copy this constraint logically -- that is create a new constraint which contains
	 * the same equalities (as any) as the current one.
	 * */

	public XConstraint copy() {
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
	 * Clone this constraint, physically. Note that this is the usual default shallow copy.
	 */
	public XConstraint_c clone() {
		try {
			XConstraint_c clone = (XConstraint_c) super.clone();
			if (roots != null) {
				clone.roots = new HashMap<XTerm, XPromise>();
				HashMap<XPromise, XPromise> renaming = new HashMap<XPromise,XPromise>();
				for (Map.Entry<XTerm, XPromise> m : roots.entrySet()) {
					XTerm var = m.getKey();
					XPromise p = m.getValue();
					XPromise q = p.cloneRecursively(renaming);
					clone.roots.put(var, q);
				}
			}
			return clone;
		}
		catch (CloneNotSupportedException z) {
			//			  But it is!!
			z.printStackTrace();
			assert false : "Could not clone " + this;
			return null;
		}
	}
	/**
	 * Return the result of copying this into c. Assume that c will be 
	 * the depclause of the same base type as this, hence it is ok to 
	 * copy self-clauses as is. 
	 * @param c
	 * @return
	 */
	private XConstraint copyInto(XConstraint_c c) throws XFailure {
		c.addIn(this);
		// represent varWhoseTypeThisIs via a self==this constraint.
		return c;
	}

	/** Add in a constraint, unifying this.self and c.self */
	public XConstraint addIn(XConstraint c)  throws XFailure {
		if (c != null) {
			List<XTerm> result = c.constraints();
			addConstraints(result);
		}
		return this;
	}
	
	/** Add in a constraint, but don't unify this.self and c.self. */
	public XConstraint andIn(XConstraint c)  throws XFailure {
		if (c != null) {
			List<XTerm> result = c.constraints();
			addConstraints(result);
			assert false : "unimplemented";
		}
		return this;
	}

	public XConstraint addConstraints(List<XTerm> result) throws XFailure {
		if (result==null)
			return this;
		for (XTerm t : result) {
			addTerm(t);
		}
		return this;
	}
	public static XConstraint makeBinding(XVar var, XVar val) throws XFailure {
		XConstraint c = new XConstraint_c();
		return c.addBinding(var,val);
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
				c.addTerm(t);
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
				for (Solver solver : XTerms.externalSolvers()) {
					if (! consistent)
						break;
					consistent &= solver.isConsistent(atoms);
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
			List<XTerm> atoms = constraints();
			if (atoms.size() == 0) {
				valid = true;
			}
			else {
				if (XTerms.externalSolvers().size() == 0)
					valid = false;
				for (Solver solver : XTerms.externalSolvers()) {
					if (! valid)
						break;
					valid &= solver.isValid(atoms);
				}
			}
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
		if (roots == null) roots = new HashMap<XTerm, XPromise>();
		XPromise p = (XPromise) roots.get(baseVar);
		if (p == null) {
			p = (replaceP && last != null) ? last : new XPromise_c(baseVar);
			// Report.report(1, "Constraint_c.intern: adding " + var + " to roots for (:" + this+").");
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
		// it must be the case that term is a C_Var.
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
		// otherwise it must be a C_Var.
		if (roots == null)
			return null;
		if (term instanceof XVar) {
			XVar var = (XVar) term;
			XVar[] vars = var.vars();
			XVar baseVar = vars[0];
			// Report.report(1, "Constraint_c: c=" + this + "
			// looking up |" + term + "| var=" + baseVar);
			XPromise p = (XPromise) roots.get(baseVar);
			// Report.report(1, "Constraint_c: p=" + p+ " : " +
			// p.getClass());
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
	public XConstraint addBinding(XTerm left, XTerm right) throws XFailure {
		assert left != null;
		assert right != null;
		
		try {
			if (!consistent)
				return this;
			if (roots == null)
				roots = new HashMap<XTerm, XPromise>();
			XPromise p1 = intern(left);
			XPromise p2 = intern(right);
			boolean modified = p1.bind(p2);
			valid &= !modified;
		}
		catch (XFailure z) {
			consistent = false;
		}
		return this;
	}
	/**
	 * Add the atomic formula t.
	 * @param t
	 * @return
	 */
	public XConstraint addAtom(XTerm t) throws XFailure {
		if (!consistent)
			return this;
		if (roots == null)
			roots = new HashMap<XTerm, XPromise>();
		XPromise p = lookup(t);
		if (p != null)
			// nothing to do
			return this;
		p = intern(t);
		addDerivedEqualities(t);
		return this;
	}
	
	public void addDerivedEqualities(XTerm t) throws XFailure {
		for (Solver solver : XTerms.externalSolvers()) {
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
			consistent=false;
			//			throw new InternalCompilerError("Adding binding " + t1 + "=" + p + " to " + this 
			//					+ " has made it inconsistent.");
		}
		return this;
	}

	public XConstraint addTerms(List<XTerm> terms) throws XFailure {
		XConstraint c = this;
		for (XTerm t : terms) {
			c = c.addTerm(t);
		}
		return c;
	}

	/**
	 * If other is not inconsistent, and this is consistent,
	 * checks that each binding X=t in other also exists in this.
	 * @param other
	 * @return
	 */
	public boolean entails(XConstraint other) throws XFailure {
		if (! consistent()) return true;
		if (other == null || other.valid()) return true;
		boolean result = other.entailedBy(this);
		return result;
	}

	public List<XTerm> constraints() {
		return constraints(new ArrayList<XTerm>());
	}
	public List<XTerm> constraints(List<XTerm> result) {
		return constraints(result, null);
	}
	public List<XTerm> constraints(List<XTerm> result, XTerm prefix) {
		if (roots == null)
			return result;
		for (XPromise p : roots.values()) {
			p.dump(result, prefix);
		}
		return result;
	}

	public List<XTerm> constraints(XTerm y) throws XFailure {
		XPromise p = lookup(y);
		if (p == null)
			return new ArrayList<XTerm>();
		XTerm rep = p.term();
		return constraints(new ArrayList<XTerm>(), rep);
	}

	public boolean entailedBy(XConstraint other) throws XFailure {
		if (! other.consistent() || valid())
			return true;
		assert (roots !=null);
		List<XTerm> constraints = constraints();
		return other.entails(constraints);
	}

	public boolean entails(List<XTerm> conjuncts) throws XFailure {
		XConstraint me = saturate();
		Set<XTerm> visited = new HashSet<XTerm>();
		for (XTerm term : conjuncts) {
		    term.saturate(me, visited);
		}
		visited = null; // free up for gc
		for (XTerm term : conjuncts) {
			if (! me.entails(term))
				return false;
		}
		return true;
	}
	
	public XConstraint saturate() throws XFailure {
		XConstraint_c c = (XConstraint_c) copy();
		Set<XTerm> visited = new HashSet<XTerm>();
		for (XTerm term : constraints()) {
			term.saturate(c, visited);
		}
		return c;
	}

	public boolean entails(XTerm t) throws XFailure {
		if (t instanceof XEquals) {
			XEquals f = (XEquals) t;
			XTerm left = f.left();
			XTerm right = f.right();
			if (entails(left, right)) {
				return true;
			}
		}
		
		List<XTerm> atoms = constraints();
		
		for (Solver solver : XTerms.externalSolvers()) {
			if (solver.entails(atoms, t))
				return true;
		}

		return false;
	}

	private boolean entails(XTerm t1, XTerm t2) throws XFailure {
		boolean result = entailsImmed(t1, t2);
		
		if (result)
			return result;

                // FIXME: need to handle existentials.
                
		return false;

	}
	
	private boolean entailsImmed(XTerm t1, XTerm t2) throws XFailure {
		//Report.report(1, "Constraint_c: Does (:" + this +  ") entail " 
		//		+ t1 + "=" + t2+ "?");
		if (!consistent)
			return true;
		XPromise p1 = lookupPartialOk(t1);
		if (p1 == null) // No match, the term t1 is not equated to anything by this.
			return false;

		int r1Count=0;
		XVar[] vars1 = null;
		if (p1 instanceof XPromise_c) {
			r1Count = ((XPromise_c) p1).lookupReturnValue();
			vars1 = ((XVar) t1).vars();
		}
		XPromise p2 = lookupPartialOk(t2);
		if (p2==null) // No match, the term t2 is not equated to anything by this.
			return false;

		int r2Count=0;
		XVar[] vars2 = null;
		if (p2 instanceof XPromise_c) {
			r2Count = ((XPromise_c) p2).lookupReturnValue();
			vars2 = ((XVar) t2).vars();
		}

		if ((r1Count==0 || r1Count == vars1.length) && (r2Count==0 || r2Count == vars2.length)) {
			// exact lookups
			return p1.equals(p2);
		} 
		// at least one of them had a suffix left over
		// Now the returned promises must match, and they must have the same suffix.
		if (!p1.equals(p2))
			return false;
		//	Now ensure that they have the same suffix left over. 
		int residual1 = vars1.length-r1Count, residual2=vars2.length-r2Count;
		if (residual1 != residual2) return false;
		for (int i=0; i < residual1; i++) {
			XVar v1 = vars1[r1Count+i];
			XVar v2 = vars2[r2Count+i];
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
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " );
		boolean result = entails(other);
		if (result) result = (other==null)? valid : other.entails(this);
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " + result);
		return result;
	}

	public XTerm find(XName varName) throws XFailure {
		if ((! consistent) || roots ==null) return null;
		// Report.report(1, "Constraint_c.find: roots are " + roots);
		XPromise self = (XPromise) roots.get(XSelf_c.Self);
		if (self == null) return null;
		XPromise result = self.lookup(varName);
		return result==null ? null : result.term();
	}

	public String toString() { 
		String str = constraints().toString();
		str = str.substring(1, str.length()-1);
		return  "{" + str + "}"; // + " roots=" + roots ;
	}

	protected static int eqvCount;
	
	public XEQV genEQV() {
		return genEQV(true);
	}
	
	public XEQV genEQV(boolean hidden) {
		final String name = "_" + eqvCount++;
		XName handle = new XNameWrapper<String>(name);
		XEQV result = new XEQV_c(handle, hidden);
		return result;
	}
	
	public XConstraint substitute(XTerm y, XRoot x) throws XFailure {
		assert (y != null && x !=null);
		if (y.equals(x)) return this;
		XPromise last = lookupPartialOk(x);
		if (last == null) return this; 	// x does not occur in this
		XConstraint_c result = new XConstraint_c();
		for (XTerm term : constraints()) {
		    XTerm t = term.subst(y, x, true);
		    result.addTerm(t);
		}
//		XConstraint_c result = clone();
//		result.valid = true;
//		result.applySubstitution(y,x);
		return result;
	}
	public XConstraint substitute(HashMap<XRoot, XTerm> subs) throws XFailure {
		if (subs==null || subs.isEmpty()) return this;
		boolean notneeded = true;
		for (Iterator<Map.Entry<XRoot, XTerm>> it = subs.entrySet().iterator();
		notneeded && it.hasNext(); ) {
			Map.Entry<XRoot, XTerm> e = it.next();
			XRoot x = e.getKey();
			XTerm y = e.getValue();

			notneeded = (y.equals(x)) || lookupPartialOk(x) == null;
		}
		if (notneeded) return this;
		XConstraint_c result = clone();
		result.valid = true;
		result.applySubstitution(subs);
		return result;
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
		addBinding(XSelf.Self, var);
	}

	// FIXME: need to convert f(g(x)) into \exists y. f(y) && g(x) = y when f and g both atoms
	// This is needed for Nelson-Oppen to work correctly.
	// Each atom should be a root.
	public XConstraint addTerm(XTerm term) throws XFailure {
		XConstraint c = null;
		if (term.isAtomicFormula()) {
			c = addAtom(term);
		}
		else if (term instanceof XVar) {
			c = addBinding(term, XTerms.TRUE);
		}
		else if (term instanceof XNot) {
			XNot t = (XNot) term;
			if (t.unaryArg() instanceof XVar)
				c = addBinding(t.unaryArg(), XTerms.FALSE);
			if (t.unaryArg() instanceof XNot)
				c = addTerm(((XNot) t.unaryArg()).unaryArg());
		}
		else if (term instanceof XAnd) {
			XAnd t = (XAnd) term;
			c = addTerm(t.left()).addTerm(t.right());
		}
		else if (term instanceof XEquals) {
			XEquals eq = (XEquals) term;
			XTerm left = eq.left();
			XTerm right = eq.right();
			c = addBinding(left, right);
		}
		
		if (c != null)
			return c.addIn(term.selfConstraint());

		throw new XFailure("Unexpected term |" + term + "|");
	}

	public void setInconsistent() {
		this.consistent = false;
	}
}
