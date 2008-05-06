/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sun.tools.tree.AddExpression;


/**
 * A representation of constraints of the form X1=t1 && ... Xk == tk.
 * Note that there is no unification, only checking. So it is possible
 * to represent such a constraint directly as a mapping from Xi to ti.
 * The constraint is implemented as a Map from variables to terms.
 * 
 * @author vj
 *
 */
public class Constraint_c implements Constraint, Cloneable {

	//Maps C_Var's to nodes.
	protected HashMap<C_Term,Promise> roots;
	public HashMap<C_Term,Promise> roots() { return roots; }

	// Map from variables to values for negative bindings.
	// protected Map negBindings;

	// For representation of T(:self == o), selfBinding is o.
	protected C_Var selfVar;

	public int eqvCount() { return eqvCount; }

	boolean consistent = true;
	boolean valid = true;

	public Constraint_c() {
	}

	public Constraint_c(int eqvCount, boolean consistent, Map<C_Term, Promise> roots, boolean valid, C_Var selfVar) {
		this.eqvCount = eqvCount;
		this.consistent = consistent;
		this.roots = new HashMap<C_Term,Promise>(roots);
		this.valid = valid;
		this.selfVar = selfVar;
	}

	/** Copy this constraint logically -- that is create a new constraint which contains
	 * the same equalities (as any) as the current one.
	 * */

	public Constraint copy() {
		Constraint_c c = new Constraint_c();
		try {
			return copyInto(c);
		}
		catch (Failure f) {
			c.setInconsistent();
			return c;
		}
	}

	/** 
	 * Clone this constraint, physically. Note that this is the usual default shallow copy.
	 */
	public Constraint clone() {
		try {
			Constraint_c clone = (Constraint_c) super.clone();
			if (roots != null) {
				clone.roots = new HashMap<C_Term, Promise>();
				HashMap<Promise, Promise> renaming = new HashMap<Promise,Promise>();
				for (Map.Entry<C_Term, Promise> m : roots.entrySet()) {
					C_Term var = m.getKey();
					Promise p = m.getValue();
					Promise q = p.cloneRecursively(renaming);
					clone.roots.put(var, q);
				}
			}
			return clone;
		}
		catch (CloneNotSupportedException z) {
			//			  But it is!!
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
	private Constraint copyInto(Constraint_c c) throws Failure {
		c.addIn(this);
		// represent varWhoseTypeThisIs via a self==this constraint.
		return c;
	}

	public Constraint addIn(Constraint c)  throws Failure {
		if (c != null) {
			List<C_Term> result = c.constraints();
			addConstraints(result);
			C_Var v = c.selfVar();
			if (v != null ) {
				if (selfVar == null) {
					selfVar = v;
				}
				else {
					addSelfBinding(v, this);
				}
			}
		}
		return this;
	}

	public Constraint addConstraints(List<C_Term> result) throws Failure {
		if (result==null)
			return this;
		for (C_Term t : result) {
			if (t.isAtomicFormula()) {
				addAtom(t);
			}
			else if (t instanceof C_Equals) {
				C_Equals f = (C_Equals) t;
				C_Term left = f.left();
				C_Term right = f.right();
				addBinding(left, right);
			}
			else {
				throw new Failure("Unexpected term " + t);
			}
		}
		return this;
	}
	public static Constraint makeBinding(C_Var var, C_Var val) throws Failure {
		Constraint c = new Constraint_c();
		return c.addBinding(var,val);
	}

	public C_Var selfVar() {
		return selfVar;
	}

	/**
	 * Add the constraint self==val.
	 * @param val
	 * @param c
	 * @return
	 */
	public static Constraint addSelfBinding(C_Var val, Constraint c) throws Failure {
		c = (c==null) ? new Constraint_c() : c;
		//		if (c.selfVar() == null) {
		if (val instanceof C_Var && ! (val instanceof C_Lit)) {
			c.setSelfVar((C_Var) val);
			return c;
		}

		c =  c.addBinding(C_Self.Self, val);

		return c;
	}
	public void setSelfVar(C_Var var) {
		selfVar = var;
	}

	//	public static final transient X10TypeSystem typeSystem = X10TypeSystem_c.getTypeSystem();

	/**
	 * Is the constraint consistent? i.e. X=s and X=t have not been added to it,
	 * where s and t are not equal.
	 */
	public boolean consistent() {
		if (! consistent)
			return false;
		List<C_Term> atoms = constraints();
		if (atoms.size() == 0) return consistent=true;
		/// ###
		//		System.err.print("% Is " + atoms + " consistent?");
		//		consistent=xts.ExternalConstraintSolver().isConsistent(atoms);
		//		System.err.println(consistent);
		return consistent;
	}

	/** Is the constraint valid? i.e. vacuous.
	 * 
	 */
	public boolean valid() { 	
		if (! valid)
			return false;
		List<C_Term> atoms = constraints();
		if (atoms.size() == 0) return valid=true;
		/// ###
		//		System.err.print("% Is " + atoms + " valid?");
		//	consistent=xts.ExternalConstraintSolver().isValid(atoms);
		//	System.err.println(valid);
		return valid;
	}


	public Promise intern(C_Term term) throws Failure {
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
	public Promise intern(C_Term term, Promise last) throws Failure {
		if (term instanceof Promise) {
			Promise q = (Promise) term;
			// this is the case for literals, for here
			if (last != null) {
				try {
					last.bind(q);
				}
				catch (Failure f) {
					throw new Failure("A term ( " + term + ") cannot be interned to a promise (" + last + ") whose value is not null.");
				}
			}
			return q;
		}

		// let the term figure out what to do for itself.
		return term.internIntoConstraint(this, last);
	}

	public Promise internBaseVar(C_Var baseVar, boolean replaceP, Promise last) throws Failure {
		if (roots == null) roots = new HashMap<C_Term, Promise>();
		Promise p = (Promise) roots.get(baseVar);
		if (p == null) {
			p = (replaceP && last != null) ? last : new Promise_c(baseVar);
			// Report.report(1, "Constraint_c.intern: adding " + var + " to roots for (:" + this+").");
			roots.put(baseVar, p);
		}
		return p;
	}
	public void addPromise(C_Term p, Promise node) {
		if (roots == null)
			roots = new HashMap<C_Term, Promise>();
		roots.put(p, node);
	}

	public void internRecursively(C_Var v) throws Failure {
		intern(v);
	}
	/**
	 * Return the promise obtained by looking up term in this. Does not create new
	 * nodes in the constraint graph. Does not return a forwarded promise.
	 */
	public Promise lookup(C_Term term) throws Failure {
		Promise result = lookupPartialOk(term);
		if (!(result instanceof Promise_c))
			return result;
		// it must be the case that term is a C_Var.
		if (term instanceof C_Var) {
			C_Var var = (C_Var) term;
			C_Var[] vars = var.vars();
			Promise_c resultC = (Promise_c) result;
			int index = resultC.lookupReturnValue();
			return (index == vars.length) ? result : null;
		}
		return null;
	}
	public Promise lookupPartialOk(C_Term term) throws Failure {
		if (term == null)
			return null;
		if (term instanceof Promise)
			// this is the case for literals, for here
			return (Promise) term;
		// otherwise it must be a C_Var.
		if (roots == null)
			return null;
		if (term instanceof C_Var) {
			C_Var var = (C_Var) term;
			C_Var[] vars = var.vars();
			C_Var baseVar = vars[0];
			// Report.report(1, "Constraint_c: c=" + this + "
			// looking up |" + term + "| var=" + baseVar);
			Promise p = (Promise) roots.get(baseVar);
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
	public Constraint addBinding(C_Term left, C_Term right) throws Failure {
		assert left != null;
		assert right != null;
		
		Constraint result = null;
		try {
			if (!consistent)
				return result = this;
			if (roots == null)
				roots = new HashMap<C_Term, Promise>();
			if (selfVar != null) {
				left = left.substitute(C_Self_c.Self, selfVar);
				right = right.substitute(C_Self_c.Self, selfVar);
			}
			Promise p1 = intern(left);
			Promise p2 = intern(right);
			boolean modified = p1.bind(p2);
			valid &= !modified;
			result = this;
		}
		catch (Failure z) {
			consistent = false;
			return this;
		}
		return result;
	}
	/**
	 * Add the atomic formula t.
	 * @param t
	 * @return
	 */
	public Constraint addAtom(C_Term t) throws Failure {
		if (!consistent)
			return this;
		if (roots == null)
			roots = new HashMap<C_Term, Promise>();
		if (selfVar != null)
			t = t.substitute(C_Self_c.Self, selfVar);
		Promise p = lookup(t);
		if (p != null)
			// nothing to do
			return this;
		p = intern(t);
		return this;
	}
	
	public Constraint addBindingPromise(C_Term t1, Promise p)  {
		try { 
			assert t1 != null;
			if (!consistent)
				return this;
			if (roots == null)
				roots = new HashMap<C_Term, Promise>();
			if (selfVar != null)
				t1 = t1.substitute(C_Self_c.Self, selfVar);
			Promise p1 = intern(t1);
			boolean modified = p1.bind(p);
		}
		catch (Failure z) {
			consistent=false;
			//			throw new InternalCompilerError("Adding binding " + t1 + "=" + p + " to " + this 
			//					+ " has made it inconsistent.");
		}
		return this;
	}

	private C_Term rootBindingForTerm(C_Var t1) throws Failure {
		// Check if t1's type forces t1 to be equal to something (t3). If so, add
		// t2=t3 in there.
		C_Term result = null;
		C_Var t1Root = t1.rootVar();

		if (t1Root.equals(C_Self_c.Self)) {
			Constraint c = this;

			if (c != null) {
				Constraint xd = null;

				if (xd!= null) {
					C_Var cVar = xd.selfVar();
					if (t1Root.equals(cVar)) {
						t1 = (C_Var) t1.substitute(C_Self_c.Self, cVar);
					}
				}

				Promise p = c.lookup(t1);
				if (p != null) {
					// aha there really is a term that t1's roottype binds t1 to!
					result = p.term();
					if (result instanceof C_Var && ((C_Var) result).rootVar().equals(C_Self.Self)) {
						if (xd != null) {
							C_Var sVar = xd.selfVar();
							if (sVar != null) {
								result = result.substitute(sVar, C_Self_c.Self);
							} // else nothing
						} // else nothing.
					} 
				}
			}
		}
		return result;
	}

	/**
	 * Add a boolean term.
	 * @param term
	 */
	public Constraint addTerm(C_Var term) throws Failure {
		return addBinding(term, C_Terms.TRUE);
	}

	/**
	 * If other is not inconsistent, and this is consistent,
	 * checks that each binding X=t in other also exists in this.
	 * @param other
	 * @return
	 */
	public boolean entails(Constraint other) throws Failure {
		if (! consistent()) return true;
		if (other == null || other.valid()) return true;
		boolean result = other.entailedBy(this);
		return  result;
	}

	public List<C_Term> constraints() {
		return constraints(new ArrayList<C_Term>());
	}
	public List<C_Term> constraints(List<C_Term> result) {
		return constraints(result, null, null);
	}
	public List<C_Term> constraints(List<C_Term> result, C_Var newSelf) {
		return constraints(result, null, newSelf);
	}

	public List<C_Term> constraints(List<C_Term> result, C_Term prefix, 
			C_Var newSelf) {
		if (roots == null)
			return result;
		for (Promise p : roots.values()) {
			p.dump(result, prefix, newSelf);
		}
		return result;
	}
	public List<C_Term> constraints(C_Term y) throws Failure {
		Promise p = lookup(y);
		if (p == null)
			return new ArrayList<C_Term>();
			C_Term rep = p.term();
			return constraints(rep, C_Self.Self);
	}
	public List<C_Term> constraints(C_Term y, C_Var newSelf) {
		return constraints(new ArrayList<C_Term>(), newSelf);
	}

	public boolean entailedBy(Constraint other) throws Failure {
		if ((! other.consistent()) || valid() )
			return true;
		assert (roots !=null);
		List<C_Term> result = constraints();
		for (C_Term t : result) {
			if (! other.entails(t))
				return false;
		}
		return true;
	}

	public boolean entails(List<C_Term> list) throws Failure {
		for (C_Term term : list) {
			if (! entails(term))
				return false;
		}
		return true;
	}

	public boolean entails(C_Term t) throws Failure {
		if (t instanceof C_Equals) {
			C_Equals f = (C_Equals) t;
			C_Term left = f.left();
			C_Term right = f.right();
			if (entails(left, right)) {
				return true;
			}
		}
		//		           ExternalConstraintSolver ecs = ts.ExternalConstraintSolver();
		//		           System.err.print("%" + this + " entails " + t + "?");
		//		           boolean result=ecs.entails(constraints(), t);
		//		           System.err.println("" + result);
		//		           return result;
		return false;
	}

	public boolean entails(C_Term t1, C_Term t2) throws Failure {
		boolean result = entailsImmed(t1, t2);
		if (result) return result;

		result = checkSelfEntails(t1, t2);
		if (result) return result;

		if (t1 instanceof C_Var) {
			C_Term t1r = rootBindingForTerm((C_Var) t1);
			if (t1r != null && (! t1r.equals(t1)) && entails(t1r, t2)) return true;
		}

		if (t2 instanceof C_Var) {
			C_Term t2r = rootBindingForTerm((C_Var) t2);
			if (t2r != null && (! t2r.equals(t2)) && entails(t1, t2r)) return true;
		}

		// \exists q. x=q is true since x=x.
		if (t1 instanceof C_EQV) {
			if (roots == null || ! roots.containsKey(t1)) return true;
		}
		if (t2 instanceof C_EQV) {
			if (roots == null || ! roots.containsKey(t2)) return true;
		}

		return false;

	}
	public boolean entailsImmed(C_Term t1, C_Term t2) throws Failure {
		//Report.report(1, "Constraint_c: Does (:" + this +  ") entail " 
		//		+ t1 + "=" + t2+ "?");
		if (!consistent)
			return true;
		if (selfVar !=null) {
			t1 = t1.substitute(C_Self_c.Self, selfVar);
			t2 = t2.substitute(C_Self_c.Self, selfVar);
		}
		Promise p1 = lookupPartialOk(t1);
		if (p1 == null) // No match, the term t1 is not equated to anything by this.
			return false;

		int r1Count=0;
		C_Var[] vars1 = null;
		if (p1 instanceof Promise_c ){
			r1Count = ((Promise_c) p1).lookupReturnValue();
			vars1 = ((C_Var) t1).vars();
		}
		Promise p2 = lookupPartialOk(t2);
		if (p2==null) // No match, the term t2 is not equated to anything by this.
			return false;

		int r2Count=0;
		C_Var[] vars2 = null;
		if (p2 instanceof Promise_c ){
			r2Count = ((Promise_c) p2).lookupReturnValue();
			vars2 = ((C_Var) t2).vars();
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
			C_Var v1 = vars1[r1Count+i];
			C_Var v2 = vars2[r2Count+i];
			if (v1 instanceof C_Field && v2 instanceof C_Field) {
				C_Field f1 = (C_Field) v1;
				C_Field f2 = (C_Field) v2;
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

	protected boolean checkSelfEntails(C_Term t1, C_Term val) {
		if (selfVar == null) return false;
		C_Term var1 = t1.substitute(selfVar, C_Self.Self);
		boolean result = var1.equals(val);
		return result;
	}
	public boolean equiv(Constraint other) throws Failure {
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " );
		boolean result = entails(other);
		if (result) result = (other==null)? valid : other.entails(this);
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " + result);
		return result;
	}

	public C_Term find(C_Name varName) throws Failure {
		if ((! consistent) || roots ==null) return null;
		// Report.report(1, "Constraint_c.find: roots are " + roots);
		Promise self = (Promise) roots.get(C_Self_c.Self);
		if (self == null) return null;
		Promise result = self.lookup(varName);
		return result==null ? null : result.term();
	}

	public String toString() { 
		String str = constraints().toString();
		str = str.substring(1, str.length()-1);
		if (selfVar != null)
			str = "self=" + selfVar + (str.equals("") ? "" : ", " + str);
		return  "{" + str + "}"; // + " roots=" + roots ;
	}

	protected int eqvCount;
	public C_EQV genEQV(boolean isSelfVar) {
		return genEQV(isSelfVar, true);
	}
	public C_EQV genEQV(boolean isSelfVar, boolean hidden) {
		final String name = "_" + eqvCount++;
		C_Name handle = new C_NameWrapper<String>(name);
		C_EQV result = new C_EQV_c(handle, hidden);
		return result;
	}
	public Constraint substitute(C_Term y, C_Root x) throws Failure {
		assert (y != null && x !=null);
		if (y.equals(x)) return this;
		Promise last = lookupPartialOk(x);
		if (last == null) return this; 	// x does not occur in this
		Constraint result = clone();
		result.applySubstitution(y,x);
		return result;
	}
	public Constraint substitute(HashMap<C_Root, C_Term> subs) throws Failure {
		if (subs==null || subs.isEmpty()) return this;
		boolean notneeded = true;
		for (Iterator<Map.Entry<C_Root, C_Term>> it = subs.entrySet().iterator();
		notneeded && it.hasNext(); ) {
			Map.Entry<C_Root, C_Term> e = it.next();
			C_Root x = e.getKey();
			C_Term y = e.getValue();

			notneeded = (y.equals(x)) || lookupPartialOk(x) == null;
		}
		if (notneeded) return this;
		Constraint result = clone();
		result.applySubstitution(subs);
		return result;
	}
	public Constraint substitute(C_Term[] ys, C_Root[] xs) throws Failure {
		return substitute(ys, xs, true);
	}
	public Constraint substitute(C_Term[] ys, C_Root[] xs, boolean propagate) throws Failure {
		assert xs.length == ys.length;
		final int n = xs.length;
		Constraint result = this;
		for (int i=0; i < n; i++) {
			C_Root x = xs[i];
			C_Term y = ys[i];
			if (! (y.equals(x) || lookupPartialOk(x) == null)) {
				if (result==this)
					result = clone();
				result.applySubstitution(y, x);
			}
		}
		return result==null ? this : result;
	}
	public void applySubstitution(HashMap<C_Root, C_Term> subs) throws Failure {
		for (Map.Entry<C_Root, C_Term> e : subs.entrySet()) {
			C_Root x = e.getKey();
			C_Term y = e.getValue();
			applySubstitution(y,x);
		}
	}
	public void applySubstitution(C_Term y, C_Root x) throws Failure {
		if (roots == null)
			// nothing to substitute
			return;
		Promise p = (Promise) roots.get(x);
		if (p == null)
			// nothing to substitute
			return;
		// Remove this now so that you avoid alpha capture issues. y may be the same as x. 
		roots.remove(x);
		Promise q = intern(y); 
		replace(q, p);
		if (q.term().equals(x)) {
			// Cannot replace x with x.  Instead,
			// introduce an EQV and substitute that for x.

			C_EQV v = genEQV(false);

			// Clone the root map, with the renaming map primed
			// with x -> v
			HashMap<Promise, Promise> renaming = new HashMap<Promise,Promise>();
			renaming.put(q, new Promise_c(v));

			for (Map.Entry<C_Term, Promise> m : roots.entrySet()) {
				C_Term var = m.getKey();
				Promise p2 = m.getValue();
				Promise q2 = p2.cloneRecursively(renaming);
				m.setValue(q2);
			}

			return;
		}
		if (p instanceof C_Lit) {
			//			try {
			q.bind(p);
			//			} catch (Failure f) {
			//				throw new InternalCompilerError("Error in replacing " + x 
			//						+ " with " + y + " in " + this + ": binding failure with "  + p);
			//			}
			return;
		}
		Promise xf = p.value();
		if (xf != null) {
			//addBinding(y, xf.term());
			//			try {
			q.bind(xf);
			//			} catch (Failure f) {
			//				throw new InternalCompilerError("Error in replacing " + x 
			//						+ " with " + y + " in " + this + ": binding failure with "  + xf);
			//			}
		} else {

			HashMap<C_Name,Promise> fields = p.fields(); 
			if (fields != null) {
				for (Map.Entry<C_Name, Promise> entry : fields.entrySet()) {
					C_Name s = entry.getKey();
					Promise orphan = entry.getValue();
					//					try {
					q.addIn(s, orphan);
					C_Field oldTerm = (C_Field) orphan.term();
					C_Name oldField = oldTerm.field();
					C_Field newTerm = new C_Field_c((C_Var) q.term(), oldField);
					orphan.setTerm(newTerm);
					//					} catch (Failure f) {
					//						throw new InternalCompilerError("Error in replacing " + x 
					//								+ " with " + y + " in " + this + ": failure in forwarding " + entry);
					//					}
				}
			}
		}
	}
	
	/** Replace all pointers entering x in this constraint with pointers entering y.
	 * 
	 * @param y
	 * @param x
	 */
	public void replace(Promise y, Promise x) {
		Collection<Promise> rootPs = roots.values();
		for (Promise p : rootPs) {
			if (! p.equals(x)) {
				p.replaceDescendant(y, x);
			}
		}
	}

	public boolean hasVar(C_Root v) {
		if (roots == null)
			return false;
		return roots.keySet().contains(v);
	}

	public void addSelfBinding(C_Var var) throws Failure {
		addBinding(selfVar(), var);
	}

	public Constraint addTerm(C_Term term) throws Failure {
		if (term.isAtomicFormula()) {
			return addAtom(term);
		}
		if (term instanceof C_Var) {
			return addBinding(term, C_Terms.TRUE);
		}
		if (term instanceof C_Not) {
			C_Not t = (C_Not) term;
			if (t.unaryArg() instanceof C_Var)
				return addBinding(t.unaryArg(), C_Terms.FALSE);
			if (t.unaryArg() instanceof C_Not)
				return addTerm(((C_Not) t.unaryArg()).unaryArg());
		}
		if (term instanceof C_And) {
			C_And t = (C_And) term;
			return addTerm(t.left()).addTerm(t.right());
		}
		if (term instanceof C_Equals) {
			C_Equals eq = (C_Equals) term;
			C_Term left = eq.left();
			C_Term right = eq.right();
			return addBinding(left, right);
		}
		throw new Failure("Unexpected term " + term);
	}

	public void setInconsistent() {
		this.consistent = false;
	}
}
