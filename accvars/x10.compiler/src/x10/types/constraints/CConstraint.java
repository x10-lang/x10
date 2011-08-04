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
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
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
import x10.constraint.XEQV;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XGraphVisitor;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XUQV;
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
 * special variables of interest to the compiler for this constraint, namely 
 * the self variable and the this variable.
 * 
 * <p>Further, the XTerms occurring in an XConstraint are created using static 
 * methods on the class XTerms. In particular they carry type information in 
 * their internal XName. This information is used to recursively materialize 
 * more constraints from the given constraint. 
 * 
 * @author vj
 *
 */
public class CConstraint extends XConstraint  implements ThisVar {

	
	
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
		this(CTerms.makeSelf());
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
	 * <p> Always returns a non-null constraint.
	 */
	@Override
	  public CConstraint copy() {
	    CConstraint result = new CConstraint();
	    result.self = this.self();
	    result.thisVar = this.thisVar();
	    return copyInto(result);
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
	public void addIn(CConstraint c) {
		addIn(self(), c);
	}

	static class AddInVisitor implements XGraphVisitor {
        CConstraint c2;
        XTerm newSelf;
        XVar cSelf;
        AddInVisitor(CConstraint c2, XTerm newSelf, XVar cSelf) {
            this.c2=c2;
            this.newSelf = newSelf;
            this.cSelf = cSelf;
        }
        public boolean visitAtomicFormula(XTerm t) {
            try {
                t = t.subst(newSelf, cSelf);
                c2.addTerm(t);
                return true;
            } catch (XFailure z) {
                c2.setInconsistent();
                return false;
            }
        }
        public boolean visitEquals(XTerm t1, XTerm t2) {
            t1 = t1.subst(newSelf, cSelf);
            t2 = t2.subst(newSelf, cSelf);
            c2.addBinding(t1, t2);
            return c2.consistent();     
        }
        public boolean visitDisEquals(XTerm t1, XTerm t2) {
            t1 = t1.subst(newSelf, cSelf);
            t2 = t2.subst(newSelf, cSelf);
            c2.addDisBinding(t1, t2);
            return c2.consistent();
        }
    }
	/** 
	 * Add constraint c into this, substituting newSelf for c.self. 
	 * Return this.
	 * 
	 * Note: this is possibly side-effected by this operation.
	 * 
	 * No change is made to this if c==null or c is valid.
	 * 
	 * @param c -- the constraint to be added in.
	 * @return the possibly modified constraint
	 * 
	 * */
	
	public void addIn(XTerm newSelf, CConstraint c) {
	    if (c== null)
	        return;
	    if (! c.consistent()) {
	        setInconsistent();
            return;
	    }
	    if (c.valid()) {
	        return;
	    }
	    AddInVisitor v = new AddInVisitor(this, newSelf, c.self());
	    // hideFake==false to permit the "place checking" to work.
	    // This ensures that multiple objects created at the same place
	    // e.g. GlobalRef's, are treated as being at the same place by the 
	    // type-checker.
	    c.visit(true, false, v);
	    // vj: What about thisVar for c? Should that be added?
	    // thisVar = getThisVar(this, c);
	    return;
	}

	

	/**
	 * Add the binding selfVar == var to this constraint, possibly
	 * modifying it in place.
	 * @param var
	 */
	public void addSelfBinding(XTerm var) {
		addBinding(self(), var);
	}
	
	 /**
	  * 
	 * Add the binding selfVar != term to this constraint, possibly
	 * modifying it in place.
	  * @param var
	  */
		public void addSelfDisBinding(XTerm term) {
			addDisBinding(self(), term);
		}
	/**
	 * Add the binding selfVar == var to this constraint, possibly
	 * modifying it in place.
	 * @param var
	 */
	public void addSelfBinding(XConstrainedTerm var) {
		addBinding(self(), var);
	}

	/**
	 * Add the binding thisVar == term to this constraint, possibly
	 * modifying it in place.
	 * @param var
	 */
	public void addThisBinding(XTerm term) {
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
	 */
	public void addBinding(XTerm s, XConstrainedTerm t) {
		addBinding(s, t.term());
		addIn(s, t.constraint());

	}
	/**
	 * Add the binding s=t to this. This constraint is possibly modified in place.
	 * @param s
	 * @param t
	 */
	public void addBinding(XConstrainedTerm s, XTerm t) {
		addBinding(t,s);
	}
	/**
	 * Add the binding s.term()=t.term() to this, and add in s.constraint() and t.constraint(). 
	 * This constraint is possibly modified in place.
	 * @param s
	 * @param t
	 */
	public void addBinding(XConstrainedTerm s, XConstrainedTerm t) {
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
	/**
	 * TODO: Use an XGraphVisitor instead of constraints().
	 * Note: The only vars that need to be changed are in roots!
	 * So doing constraints() and iterating over its terms is really bad.
	 */
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
	
	  static class CEntailsVisitor implements XGraphVisitor{
	        CConstraint c1;
	        ConstraintMaker c2m;
	        XVar otherSelf;
	        boolean result=true;
	        CEntailsVisitor(CConstraint c1, ConstraintMaker c2m, XVar otherSelf) {
	            this.c1=c1;
	            this.c2m = c2m;
	            this.otherSelf=otherSelf;
	        }
	        public boolean visitAtomicFormula(XTerm t) {
	            try {
	                t = t.subst(c1.self(), otherSelf);
	                boolean myResult = c1.entails(t);
	                if (! myResult && c2m!=null) {
	                    c1 = c1.copy();
	                    c1.addIn(c2m.make());
	                    c2m=null;
	                    if (! c1.consistent())
	                        return false;
	                    
	                    myResult = c1.entails(t);
	                }
	                result &=myResult;   
	                    
	            } catch (XFailure z) {
	                return false;
	            }
	            return result;
	        }
	        public boolean visitEquals(XTerm t1, XTerm t2) {
	            t1 = t1.subst(c1.self(), otherSelf);
	            t2 = t2.subst(c1.self(), otherSelf);
	            boolean myResult = c1.entails(t1, t2);
	            if (! myResult && c2m!=null) {
	                try {
	                    c1 = c1.copy();
	                    c1.addIn(c2m.make());
                        c2m=null;
                        if (! c1.consistent())
                            return false;
	                    myResult = c1.entails(t1, t2);
	                } catch (XFailure z) {
	                    myResult=false;
	                }
	            }
                result &=myResult;   
	            return result;
	        }
	        public boolean visitDisEquals(XTerm t1, XTerm t2) {
	            t1 = t1.subst(c1.self(), otherSelf);
                t2 = t2.subst(c1.self(), otherSelf);
	            boolean myResult = c1.disEntails(t1, t2);
	            if (! myResult && c2m!=null) {
                    try {
                        c1 = c1.copy();
                        c1.addIn(c2m.make());
                        c2m=null;
                        if (! c1.consistent())
                            return false;
                        myResult = c1.disEntails(t1, t2);
                    } catch (XFailure z) {
                        myResult=false;
                    }
                }
                result &=myResult;   
	            return result;
	        }
	        public boolean result() {
	            return result;
	        }
	    }
	/** If other is not inconsistent, and this is consistent,
	 * checks that each binding X=t in other also exists in this.
	 * TODO: Improve performance by doing entailment in place
	 * without getting other term's extConstraints.
	 * @param other
	 * @return
	 */

	public boolean entails(CConstraint other, ConstraintMaker sigma)  {
        if (!consistent())
            return true;
        if (other == null || other.valid())
            return true;
        CEntailsVisitor ev = new CEntailsVisitor(this, sigma, other.self());
        other.visit(false,true, ev);
        return ev.result();
	}

	/*
    // not being used currently. remove eventually if it remains unused.
      static class EntailsVisitor implements XGraphVisitor{
            CConstraint c1;
            XVar otherSelf;
            boolean result=true;
            EntailsVisitor(CConstraint c1, XVar otherSelf) {
                this.c1=c1;
            }
            public boolean visitAtomicFormula(XTerm t) {
                try {
                    t = t.subst(c1.self(), otherSelf);
                    result = c1.entails(t, otherSelf);
                } catch (XFailure z) {
                    return false;
                }
                return result;
            }
            public boolean visitEquals(XTerm t1, XTerm t2) {
                t1 = t1.subst(c1.self(), otherSelf);
                t2 = t2.subst(c1.self(), otherSelf);
                result = c1.entails(t1, t2);
                return result;
            }
            public boolean visitDisEquals(XTerm t1, XTerm t2) {
                t1 = t1.subst(c1.self(), otherSelf);
                t2 = t2.subst(c1.self(), otherSelf);
                result = c1.entails(t1, t2);
                return result;
            }
            public boolean result() {
                return result;
            }
        }
        */
	/*
	public boolean entails(CConstraint other, CConstraint sigma)  {
	    if (!consistent())
	        return true;
	    if (other == null || other.valid())
			return true;
		//       if (other.toString().equals(toString()))
		//       	return true;
        CConstraint me = this;
        if (sigma != null) {
            me = me.copy();
            try {
            me.addIn(sigma);
            } catch (XFailure z) {
                // Logically this should return true;
                return false;
            }
            
        }

        if (! me.consistent()) {
            return true;
        }
        CConstraint.EntailsVisitor ev = new CConstraint.EntailsVisitor(me, other.self());
        other.visit(false,false, ev);
        return ev.result();
        }
        */
	public XTerm bindingForSelfField(FieldDef fd)  {
		return bindingForRootField(self(), fd);
	}
	public XTerm bindingForSelfField(MethodDef fd)  {
        return bindingForRootField(self(), fd);
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
		return bindingForSelfField(f.fieldInstance().def());
	}
	public XTerm bindingForSelfField(FieldInstance f) {
		assert f != null;
		return bindingForSelfField(f.def());
	}


	/* Return the least upper bound of this and other. That is, the resulting constraint has precisely
	 * the constraints entailed by both this and other.
	 * @param other
	 * @return
	 */
	public CConstraint leastUpperBound(CConstraint c2) {
		return leastUpperBound1(c2);
	}
	
	/**
	 * If y equals x, or x does not occur in this, return this, else copy
	 * the constraint and return it after performing applySubstitution(y,x).
	 * 
	 * 
	 */
	/*@Override
	public CConstraint substitute(Map<XVar, XTerm> subs) throws XFailure {
		CConstraint c = this;
		for (Map.Entry<XVar,XTerm> e : subs.entrySet()) {
			XVar x = e.getKey();
			XTerm y = e.getValue();
			c = c.substitute(y, x);            
		}
		return c;
	}*/
	
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
/*
	public void checkQuery(CConstraint query, XVar ythis, XVar xthis, XVar[] y, XVar[] x, 
			Context context) throws SemanticException {
		// Check that the guard is entailed.
		try {
			if (query != null) { 
				if (! ((TypeSystem) context.typeSystem()).consistent(query)) {
					throw new SemanticException("Guard " + query 
					                            + " cannot be established; inconsistent in calling context.");
				}
				CConstraint query2 = xthis==null ? query : query.substitute(ythis, xthis);
				query2.setThisVar(ythis);
				//	                CConstraint query3 = query2.substitute(Y, X);
				CConstraint query3 = query2;
				CConstraint query4 = query3.substitute(y, x);

				if (! entails(query4, context.constraintProjection(this, query4))) {
					throw new SemanticException("Call invalid; calling environment is inconsistent or does not entail the method guard.");
				}
			}
		}
		catch (XFailure f) {
			// Substitution introduces inconsistency.
			throw new SemanticException("Call invalid; calling environment is inconsistent.");
		}
	}
*/
	/**
	 * Return the constraint obtained by existentially quantifying out the 
	 * Svariable v.
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
	public void addSigma(CConstraint c, Map<XTerm, CConstraint> m) 
	 {
		if (! consistent())
		    return;
	    if (c != null && ! c.valid()) {
			addIn(c);
			addIn(c.constraintProjection(m));
		}
	}
	public void addSigma(XConstrainedTerm ct, Map<XTerm, CConstraint> m) 
	 {
		if (! consistent())
		    return;
	    if (ct != null) {
			addSigma(ct.xconstraint(), m);
		}
	}

	/**
	 * Return the constraint r generated from this by adding all the constraints
	 * specified by the types of the terms occurring in this. This is done 
	 * recursively. That is, for each constraint c added to r, we recursively 
	 * add the constraints for the terms that occur in c.
	 * @param m
	 * @param old
	 * @return
	 * @throws XFailure -- if r becomes inconsistent.
	 */
	public CConstraint constraintProjection(Map<XTerm,CConstraint> m) 
	 {
		return constraintProjection(m, 0); // CollectionFactory.newHashSet());
	}
	public CConstraint constraintProjection(Map<XTerm,CConstraint> m, 
	                                        int depth /*Set<XTerm> ancestors*/) 
	 {
		CConstraint r = new CConstraint();

		for (XTerm t : constraints()) {
			CConstraint tc = constraintProjection(t, m, depth /*ancestors*/);
			if (tc != null)
				r.addIn(tc);
		}
		return r;
	}

	
	// ***************************************************************** Implementation
	
	protected boolean entails(XTerm  term, XVar self) throws XFailure {
		XTerm subst = term.subst(self(), self);
		return entails(subst);
	}

	private static <T> boolean contains(Set<T> s, Set<T> c) {
		for (T t: c) {
			if (s.contains(t))
				return true;
		}
		return false;
	}
	private static int MAX_DEPTH=15;
	private static CConstraint constraintProjection(XTerm t, Map<XTerm,CConstraint> m, int depth /*Set<XTerm> ancestors*/)  {
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
		if (t instanceof CLocal) {
			CLocal v = (CLocal) t;
			X10LocalDef ld = v.localDef();
			if (ld != null) {
				Type ty = Types.get(ld.type());
				ty = PlaceChecker.ReplaceHereByPlaceTerm(ty, ld.placeTerm());
				CConstraint ci = Types.realX(ty);
			    r = new CConstraint();
				try {
				ci = ci.substitute(v, ci.self());
				} catch (XFailure z) {
				    r.setInconsistent();
				    return r;
				}
				r.addIn(ci);
				// Recursively perform a constraintProjection on the new constraint ci
				// only if one of the ancestor terms does not occur in it.
			// if (! contains(ancestors, ci.terms()))
					r.addIn(ci.constraintProjection(m, depth+1));
			}
		} else if (t instanceof XLit) { // no new info to contribute
		} else if (t instanceof CSelf){ // no new info to contribute
		} else if (t instanceof CThis){ // no new info to contribute
		} else if (t instanceof XEQV) { // no new info to contribute
		} else if (t instanceof XUQV) { // no new info to contribute
	
		} else if (t instanceof CField){
			CField f = (CField) t;
			XTerm target = f.receiver();
			//ancestors.add(target);
			//ancestors.add(t);
			CConstraint rt = constraintProjection(target, m, depth+1); //  ancestors);
			Type ty = f.type();
			
			CConstraint ci = null;

			if (ty != null) {
				ci = Types.realX(ty);
				XVar v = f.thisVar();
				r = new CConstraint();
				if (v != null) {
				    try {
				        ci = ci.substitute(target, v); // xts.xtypeTranslator().transThisWithoutTypeConstraint());
				    } catch (XFailure z) {
				        r.setInconsistent();
				        return r;
				    }
				}
				try {
				    ci = ci.substitute(f, ci.self());
				} catch (XFailure z) {
				    r.setInconsistent();
				    return r;
				}
				
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
			XFormula<?> f = (XFormula<?>) t;
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
		} else if (t instanceof XField) {
		    
		}
		else {
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
			    result.setInconsistent();
			}
		}
		return result;
	}

	

	
}
