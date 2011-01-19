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

package x10.types.constraints;

import java.util.HashMap;

import polyglot.ast.Field;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import polyglot.types.Context;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Field;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;


import x10.constraint.XConstraint;
import x10.constraint.XDisEquals;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10ClassDef;
import polyglot.types.Context;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;
import polyglot.types.TypeSystem;
import x10.types.checker.PlaceChecker;

/**
 * The compiler's notion of a constraint. 
 * 
 * <p> A CConstraint is an XConstraint, together with machinery to track two
 * special variables of interest to the compiler for this constraint, namely the self variable and the this variable.
 * 
 * <p>Further, the XTerms occurring in an XConstraint are created using static methods on 
 * the class XTerms. In particular they carry type information in their internal XName. 
 * This information is used to recursively materialize more constraints from the given 
 * constraint. 
 * 
 * @author vj
 *
 */
public class CConstraint extends XConstraint  implements ThisVar {

	public static final String SELF_VAR_PREFIX="self";
	
	/** Variable to use for self in the constraint. */
	XVar self;

	XVar thisVar;
	/**
	 * 
	 */
	public CConstraint(XVar self) {
		this.self = self;
	}
	public CConstraint() {
		this(XTerms.makeUQV(SELF_VAR_PREFIX));
	}

	/**
	 * Variable to use for self in the constraint.
	 */
	public XVar self() {
		return self;
	}

	/**
	 * Return what, if anything, self is bound to in the current constraint.
	 * @return
	 */
	public XVar selfVarBinding() {
		return  bindingForVar(self());
	}
	public XVar thisVar() {
		return thisVar;
	}
	public boolean hasPlaceTerm() {
		if (roots==null)
			return false;
		for (XTerm t : roots.keySet()) {
			if (PlaceChecker.isGlobalPlace(t))
				return true;
		}
		return false;
	}

	/**
	 * Copy this constraint logically; that is, create a new constraint
	 * that contains the same equalities (if any) as the current one.
	 * vj: 08/12/09
	 * Copying also the consistency, and validity status, and thisVar and self.
	 * It is critical that the selfVar for the constraint's copy is the same
	 * as the selfVar for the original constraint.
	 */
	public CConstraint copy() {
		CConstraint c = new CConstraint();
		c.init(this);
		return c;
	}

	/**
	 * Add constraint c into this, substituting this.self for c.self. Return this.
	 * 
	 * Note: this is possibly side-effected by this operation.
	 * 
	 * No change is made to this if c==null
	 * 
	 * @param c -- the constraint to be added in.
	 * @return the possibly modified constraint
	 */
	public CConstraint addIn(CConstraint c)  throws XFailure {
		return addIn(self(), c);
	}

	/** 
	 * Add constraint c into this, substituting newSelf for c.self. 
	 * Return this.
	 * 
	 * Note: this is possibly side-effected by this operation.
	 * 
	 * No change is made to this if c==null
	 * 
	 * @param c -- the constraint to be added in.
	 * @return the possibly modified constraint
	 * 
	 * */
	public CConstraint addIn(XTerm newSelf, CConstraint c)  throws XFailure {
		if (c != null) {
			List<XTerm> result = c.constraints();
			if (result == null)
				return this;
			for (XTerm t : result) {
				addTerm(t.subst(newSelf, c.self()));
			}
		}
		// vj: What about thisVar for c? Should that be added?
				// thisVar = getThisVar(this, c);
		return this;
	}

	

	/**
	 * Add the binding selfVar == var to this constraint, possibly
	 * modifying it in place.
	 * @param var
	 * @throws XFailure
	 */
	public void addSelfBinding(XTerm var) throws XFailure {
		addBinding(self(), var);
	}
	/**
	 * Add the binding selfVar == var to this constraint, possibly
	 * modifying it in place.
	 * @param var
	 * @throws XFailure
	 */
	public void addSelfBinding(XConstrainedTerm var) throws XFailure {
		addBinding(self(), var);
	}

	/**
	 * Add the binding thisVar == term to this constraint, possibly
	 * modifying it in place.
	 * @param var
	 * @throws XFailure
	 */
	public void addThisBinding(XTerm term) throws XFailure {
		addBinding(thisVar(), term);
	}

	/**
	 * Set thisVar to var (if var is non-null). To be used extremely carefully. Does not change
	 * terms in the constraint. So there should not be terms referring to the old thisVar.
	 * @param var
	 */
	public void setThisVar(XVar var) {
		if (var == null) return;
		thisVar = var;
	}

	/**
	 * Add the binding s=t.term(), and add in the constraints of t into this. This constraint
	 * is possibly modified in place.
	 * @param s
	 * @param t
	 * @throws XFailure
	 */
	public void addBinding(XTerm s, XConstrainedTerm t) throws XFailure {
		addBinding(s, t.term());
		addIn(s, t.constraint());

	}
	/**
	 * Add the binding s=t to this. This constraint is possibly modified in place.
	 * @param s
	 * @param t
	 * @throws XFailure
	 */
	public void addBinding(XConstrainedTerm s, XTerm t) throws XFailure {
		addBinding(t,s);
	}
	/**
	 * Add the binding s.term()=t.term() to this, and add in s.constraint() and t.constraint(). 
	 * This constraint is possibly modified in place.
	 * @param s
	 * @param t
	 * @throws XFailure
	 */
	public void addBinding(XConstrainedTerm s, XConstrainedTerm t) throws XFailure {
		addBinding(s.term(), t.term());
		addIn(s.term(), s.constraint());
		addIn(t.term(), t.constraint());
	}
	
	/**
	 * Substitute y for x in this, returning a new constraint.
	 * // Redeclare with the right return type
	 */
	@Override
	public CConstraint substitute(XTerm y, XVar x) throws XFailure {
		return substitute(new XTerm[] { y }, new XVar[] { x });
	}
	
	/**
	 * Substitute ys for xs in this, returning a new constraint.
	 * // Redeclare with the right return type
	 */
	@Override
	public CConstraint substitute(XTerm[] ys, XVar[] xs, boolean propagate) throws XFailure {
		return substitute(ys, xs);
	}
	/**
	 * Return a new constraint obtained from the current one by substituting
	 * newSelf for this.self(). Note, the resulting constraint may be marked inconsistent.
	 * FIXME: Yoav, this should also take a term to substitute for this, and return an XConstraint
	 * @param newSelf
	 * @return
	 */
	public CConstraint instantiateSelf(XTerm newSelf) {
		try {
			return substitute(newSelf, self());
		} catch (XFailure z) {
			CConstraint False = new CConstraint();
			False.setInconsistent();
			return False;
		}
	}

	public static XVar getThisVar(CConstraint t1, CConstraint t2) throws XFailure {
		XVar thisVar = t1 == null ? null : t1.thisVar();
		if (thisVar == null)
			return t2==null ? null : t2.thisVar();
		if (t2 != null && ! thisVar.equals( t2.thisVar()))
			throw new XFailure("Inconsistent this vars " + thisVar + " and "
					+ t2.thisVar());
		return thisVar;
	}
	public CConstraint substitute(XTerm[] ys, XVar[] xs) throws XFailure {
		assert (ys != null && xs != null);
		assert xs.length == ys.length;

		boolean eq = true;
		for (int i = 0; i < ys.length; i++) {
			XTerm y = ys[i];
			XVar x = xs[i];

			if (! y.equals(x))
				eq = false;
		}
		if (eq)
			return this;

		if (! consistent)
			return this;

		// Don't do the quick occurrence check; x might occur in a self constraint.
		//		XPromise last = lookupPartialOk(x);
		//		if (last == null) return this; 	// x does not occur in this

		CConstraint result = new CConstraint();
		List<XTerm> terms = constraints();
		for (XTerm term : terms) {
			XTerm t = term;

			// if term is y==x.f, the subst will produce y==y.f, which is a cycle--bad!
			//		    if (term instanceof XEquals_c) {
			//		        XEquals_c eq = (XEquals_c) term;
			//		        XTerm l = eq.left();
			//		        XTerm r = eq.right();
			//		        if (y.equals(l) || y.equals(r))
			//		            continue;
			//		    }
			for (int i = 0; i < ys.length; i++) {
				XTerm y = ys[i];
				XVar x = xs[i];
				t = t.subst(y, x, true);
			}

			t = t.subst(result.self(), self(), true);

			try {
				result.addTerm(t);
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

	/** If other is not inconsistent, and this is consistent,
	 * checks that each binding X=t in other also exists in this.
	 * @param other
	 * @return
	 */
	public boolean entails(CConstraint other, CConstraint sigma) throws XFailure {
		if (!consistent())
			return true;
		if (other == null || other.valid())
			return true;
		//       if (other.toString().equals(toString()))
		//       	return true;
		List<XTerm> otherConstraints = other.extConstraints();
		XVar otherSelf = other.self();
		return entails(otherConstraints, otherSelf, sigma);
	}


	public XTerm bindingForSelfField(XName varName)  {
		return bindingForRootField(self(), varName);
	}

	/**
	 * Return the term self.fieldName is bound to in the constraint, and null
	 * if there is no such term.
	 * 
	 * @param fieldName -- Name of field
	 * @return
	 * @throws XFailure
	 */
	public XTerm bindingForSelfField(Field f) {
		assert f != null;
		return bindingForSelfField(XTerms.makeName(f.fieldInstance().def(), f.name().id().toString()));
	}
	public XTerm bindingForSelfField(FieldInstance f) {
		assert f != null;
		return bindingForSelfField(XTerms.makeName(f.def(), f.name().toString()));
	}


	/* Return the least upper bound of this and other. That is, the resulting constraint has precisely
	 * the constraints entailed by both this and other.
	 * @param other
	 * @return
	 */
	public CConstraint leastUpperBound(CConstraint c2) {
		return leastUpperBound1((CConstraint) c2);
	}
	
	/**
	 * If y equals x, or x does not occur in this, return this, else copy
	 * the constraint and return it after performing applySubstitution(y,x).
	 * 
	 * 
	 */
	@Override
	public CConstraint substitute(HashMap<XVar, XTerm> subs) throws XFailure {
		CConstraint c = this;
		for (Map.Entry<XVar,XTerm> e : subs.entrySet()) {
			XVar x = e.getKey();
			XTerm y = e.getValue();
			c = c.substitute(y, x);            
		}
		return c;
	}
	
	/**
	 * Check that the given constraint is entailed, under the given substitution
	 * (ythis for xthis, and y for x).
	 * @param query
	 * @param ythis
	 * @param xthis
	 * @param y
	 * @param x
	 * @param context
	 * @throws SemanticException
	 */

	public void checkQuery(CConstraint query, XVar ythis, XVar xthis, XVar[] y, XVar[] x, 
			Context context) throws SemanticException {
		// Check that the guard is entailed.
		try {
			if (query != null) { 
				if (! ((TypeSystem) context.typeSystem()).consistent(query)) {
					throw new SemanticException("Guard " + query + " cannot be established; inconsistent in calling context.");
				}
				CConstraint query2 = xthis==null ? query : query.substitute(ythis, xthis);
				query2.setThisVar(ythis);
				//	                CConstraint query3 = query2.substitute(Y, X);
				CConstraint query3 = query2;
				CConstraint query4 = query3.substitute(y, x);

				if (! entails(query4, context.constraintProjection(this, query4))) {
					throw new SemanticException("Call invalid; calling environment does not entail the method guard.");
				}
			}
		}
		catch (XFailure f) {
			// Substitution introduces inconsistency.
			throw new SemanticException("Call invalid; calling environment is inconsistent.");
		}
	}

	/**
	 * Return the constraint obtained by existentially quantifying out the variable v.
	 * 
	 * @param v
	 * @return
	 */

	public CConstraint project(XVar v)  {
		if (! consistent)
			return this;
		CConstraint result = null;
		try {
			XVar eqv = XTerms.makeEQV();
			result = substitute(eqv, v); 
		} catch (XFailure c) {
			// should not happen
		}
		return result;
	}

	/**
	 * Add in the constraint c, and all the constraints associated with the
	 * types of the terms referenced in t.
	 * @param c
	 * @param m
	 * @throws XFailure
	 */
	public void addSigma(CConstraint c, Map<XTerm, CConstraint> m) throws XFailure {
		if (c != null && ! c.valid()) {
			addIn(c);
			addIn(c.constraintProjection(m));
		}
	}
	public void addSigma(XConstrainedTerm ct, Map<XTerm, CConstraint> m) throws XFailure {
		if (ct != null) {
			addSigma(ct.xconstraint(), m);
		}
	}

	/**
	 * Return the constraint r generated from this by adding all the constraints specified by
	 * the types of the terms occurring in this. This is done recursively. That is, for each
	 * constraint c added to r, we recursively add the constraints for the terms that occur in
	 * c.
	 * @param m
	 * @param old
	 * @return
	 * @throws XFailure -- if r becomes inconsistent.
	 */
	public CConstraint constraintProjection(Map<XTerm,CConstraint> m) throws XFailure {
		return constraintProjection(m, 0); // CollectionFactory.newHashSet());
	}
	public CConstraint constraintProjection(Map<XTerm,CConstraint> m, int depth /*Set<XTerm> ancestors*/) throws XFailure {
		CConstraint r = new CConstraint();

		for (XTerm t : constraints()) {
			CConstraint tc = constraintProjection(t, m, depth /*ancestors*/);
			if (tc != null)
				r.addIn(tc);
		}
		return r;
	}

	/**
	 * If the XField was created with an X10FieldDef, extract and return it.
	 * @param f
	 * @return
	 */
	public static X10FieldDef getField(XField f) {
		XName n = f.field();
		if (n instanceof XNameWrapper<?>) {
			XNameWrapper<?> w = (XNameWrapper<?>) n;
			if (w.val() instanceof X10FieldDef) {
				return (X10FieldDef) w.val();
			}
		}
		return null;
	}

	/**
	 * If the XLocal was created with an X10LocalDef, extract and return it.
	 * @param f
	 * @return
	 */
	public static X10LocalDef getLocal(XLocal f) {
		XName n = f.name();
		if (n instanceof XNameWrapper<?>) {
			XNameWrapper<?> w = (XNameWrapper<?>) n;
			if (w.val() instanceof X10LocalDef) {
				return (X10LocalDef) w.val();
			}
		}
		return null;
	}
	
	// ***************************************************************** Implementation
	
	protected boolean entails(XTerm  term, XVar self) throws XFailure {
		XTerm subst = term.subst(self(), self);
		return entails(subst);
	}
	
	protected void init(CConstraint from) {
		thisVar = from.thisVar();
		self = from.self();
		super.init(from);
	}
	/**
	 * Return the result of copying this into c. Assume that c will be 
	 * the depclause of the same base type as this, hence it is ok to 
	 * copy self-clauses as is. 
	 * @param c
	 * @return
	 */
	protected CConstraint copyInto(CConstraint c) throws XFailure {
		c.addIn(this);
		return c;
	}
	private static <T> boolean contains(Set<T> s, Set<T> c) {
		for (T t: c) {
			if (s.contains(t))
				return true;
		}
		return false;
	}
	private static int MAX_DEPTH=15;
	private static CConstraint constraintProjection(XTerm t, Map<XTerm,CConstraint> m, int depth /*Set<XTerm> ancestors*/) throws XFailure {
		if (t == null)
			return null;
		if (depth > MAX_DEPTH) {
			//System.err.println("(Warning) Reached threshold when checking constraints. If type-checking fails "
			//		+ "\n please insert a dynamic cast."
			//		+ "\n\t Term: "+ t);
			return new CConstraint();
		}
		CConstraint r = m.get(t);
		if (r != null)
			return r;
		// pre-fill the cache to avoid infinite recursion
		m.put(t, new CConstraint());
		if (t instanceof XLocal) {
			XLocal v = (XLocal) t;
			X10LocalDef ld = getLocal(v);
			if (ld != null) {
				Type ty = Types.get(ld.type());
				ty = PlaceChecker.ReplaceHereByPlaceTerm(ty, ld.placeTerm());
				CConstraint ci = Types.realX(ty);
				ci = ci.substitute(v, ci.self());
				r = new CConstraint();
				r.addIn(ci);
				// Recursively perform a constraintProjection on the new constraint ci
				// only if one of the ancestor terms does not occur in it.
			// if (! contains(ancestors, ci.terms()))
					r.addIn(ci.constraintProjection(m, depth+1));
			}
		} else if (t instanceof XLit) {
		} else if (t instanceof XField) {
			XField f = (XField) t;
			XTerm target = f.receiver();
			//ancestors.add(target);
			//ancestors.add(t);
			CConstraint rt = constraintProjection(target, m, depth+1); //  ancestors);

			X10FieldDef fi = getField(f);
			CConstraint ci = null;

			if (fi != null) {
				Type ty = Types.get(fi.type());
				ci = Types.realX(ty);
				XVar v = ((X10ClassDef) Types.get(fi.container()).toClass().def()).thisVar();
				ci = ci.substitute(target, v); // xts.xtypeTranslator().transThisWithoutTypeConstraint());
				ci = ci.substitute(f, ci.self());
				r = new CConstraint();
				r.addIn(ci);
				
				// Recursively perform a constraintProjection on the new constraint ci
				// only if one of the ancestor terms does not occur in it.
			//	if ( ! contains(ancestors, ci.terms())) {
				  CConstraint ciInferred = ci.constraintProjection(m, depth+1); // ancestors);
				  r.addIn(ciInferred);
			//	}
				if (rt != null) {
					r.addIn(rt);
				}
			} else {
				r = rt;
			}
		} else if (t instanceof XFormula) {
			XFormula f = (XFormula) t;
			for (XTerm a : f.arguments()) {
				CConstraint ca = constraintProjection(a, m, depth+1); //ancestors);
			//	if (m.get(a) == null)
			//		m.put(a, new CConstraint());
			//	ancestors.add(a);
				if (ca != null) {
					if (r == null) {
						r = new CConstraint();
					}
					r.addIn(ca);
				}
			}
		} else {
			assert false : "unexpected " + t;
		}
		if (r != null) // update the entry
			m.put(t, r);
		return r;
	}
	private CConstraint leastUpperBound1(CConstraint c2) {
		CConstraint c1 = this;
		CConstraint result = c1.leastUpperBound0(c2);
		CConstraint c1a = null, c2a = null;
		try {
			XVar x = c1.selfVarBinding();
			if (x instanceof XVar)
				c1a = c1.substitute(XTerms.makeEQV(), (XVar) x);
		} catch (XFailure z) {
			// should not happen.
		}
		try {
			XVar x =  c2.selfVarBinding();
			if (x instanceof XVar)
				c2a = c2.substitute(XTerms.makeEQV(), (XVar) x);
		} catch (XFailure z) {
			// should not happen.
		}
		if (c1a != null) {
			CConstraint d = c1a.leastUpperBound0(c2);
			if (d.entails(result))
				result = d;
			if (c2a != null) {
				d = c1a.leastUpperBound0(c2a);
				if (d.entails(result))
					result = d;
			}
		}
		if (c2a != null) {
			CConstraint d = c1.leastUpperBound0(c2a);
			if (d.entails(result))
				result = d;
		}
		return result;
	}
	private CConstraint leastUpperBound0(CConstraint other) {
		XVar otherSelf = other.self();

		CConstraint result = new CConstraint();
		XVar resultSelf = result.self();
		for (XTerm term : other.constraints()) {
			try {
				if (entails(term, otherSelf)) {
					term = term.subst(resultSelf, otherSelf);
					result.addTerm(term);
				}
			} catch (XFailure z) {

			}
		}
		return result;
	}

	

	private boolean entails(List<XTerm> conjuncts, XVar self, final CConstraint sigma) throws XFailure {

		CConstraint me = copy();
		if (sigma != null) {
			me.addIn(sigma);
		}

		if (! me.consistent()) {
			return true;
		}

		for (XTerm term : conjuncts) {
			if (! me.entails(term, self))
				return false;
		}

		return true;
	}

}

