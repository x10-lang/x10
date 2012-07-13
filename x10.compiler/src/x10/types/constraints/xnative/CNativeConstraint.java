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

package x10.types.constraints.xnative;

import java.util.HashMap;

import polyglot.ast.Field;
import polyglot.ast.Typed;
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
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.visitors.XGraphVisitor;
import x10.constraint.xnative.XNativeConstraint;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.xnative.XPromise;
import x10.types.X10ClassDef;
import polyglot.types.Context;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;
import polyglot.types.TypeSystem;
import x10.types.checker.PlaceChecker;

import x10.types.constraints.CConstraint;
import x10.types.constraints.CField;
import x10.types.constraints.CLocal;
import x10.types.constraints.CSelf;
import x10.types.constraints.CThis;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.visitors.AddInVisitor;
import x10.types.constraints.visitors.CEntailsVisitor;

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
public class CNativeConstraint extends XNativeConstraint  implements CConstraint {

    /** Variable to use for self in the constraint. */
    XVar self;
    XVar thisVar;
    Type baseType; 
    
    public CNativeConstraint(XVar self, Type t) {
    	this.self = self; 
    	this.baseType = t; 
    }

    public CNativeConstraint(Type t) {
        this(ConstraintManager.getConstraintSystem().makeSelf(t), t);
    }

    /**
     * Variable to use for self in the constraint.
     */
    public XVar self() {return self;}

    /**
     * Return what, if anything, self is bound to in the current constraint.
     * @return
     */
    public XVar selfVarBinding() {return  bindingForVar(self());}
    public XVar thisVar() {return thisVar;}
    public boolean hasPlaceTerm() {
        if (roots==null) return false;
        for (XTerm t : roots.keySet()) if (PlaceChecker.isGlobalPlace(t)) return true;
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
    public CNativeConstraint copy() {
        CNativeConstraint result = new CNativeConstraint(this.self, this.baseType);
        result.thisVar = this.thisVar();
        result.addIn(this);
        return result;
        //return copyInto(result);
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
    public void addIn(CConstraint c) {addIn(self(), c);}


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
        if (c== null) return;
        if (! c.consistent()) {setInconsistent();return;}
        if (c.valid()) return;
        AddInVisitor v = new AddInVisitor(true, false, this, newSelf, c.self());
        // hideFake==false to permit the "place checking" to work.
        // This ensures that multiple objects created at the same place
        // e.g. GlobalRef's, are treated as being at the same place by the 
        // type-checker.
        ((CNativeConstraint)c).visit(v);
        // vj: What about thisVar for c? Should that be added?
        // thisVar = getThisVar(this, c);
        return;
    }



    /**
     * Add the binding selfVar == var to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    public void addSelfBinding(XTerm var) {addBinding(self(), var);}

    /**
     * 
     * Add the binding selfVar != term to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    public void addSelfDisBinding(XTerm term) {addDisBinding(self(), term);}
    /**
     * Add the binding selfVar == var to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    public void addSelfBinding(XConstrainedTerm var) {addBinding(self(), var);}

    /**
     * Add the binding thisVar == term to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    public void addThisBinding(XTerm term) {addBinding(thisVar(), term);}

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
    public void addBinding(XConstrainedTerm s, XTerm t) {addBinding(t,s);}
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
    public CNativeConstraint substitute(XTerm y, XVar x) throws XFailure {
    	return substitute(new XTerm[] { y }, new XVar[] { x });
    }

    /**
     * Return a new constraint obtained from the current one by substituting
     * newSelf for this.self(). 
     * 
     * Consequently, the self variable of the returned constraint does not
     * occur in it.
     * 
     * Note, the resulting constraint may be marked inconsistent.
     * 
     * FIXME: Yoav, this should also take a term to substitute for this, and return an XConstraint
     * @param newSelf
     * @return
     */
    public CNativeConstraint instantiateSelf(XTerm newSelf) {
        CNativeConstraint result = new CNativeConstraint(baseType());
        List<XTerm> terms = constraints();
        for (XTerm term : terms) {
            XTerm t = term.subst(newSelf,self);
            try {
                result.addTerm(t);
            } catch (XFailure z) {
                result.setInconsistent();
                return result;
            }
        }
        return result;
    }

    /**
     * Return those subset of constraints in the base set of other that are 
     * <em>not</em> implied by this. That is, return the residue
     * r such that (r and this) implies other.
     * @param other -- must be checked for consistency before call is made
     * @return
     */

    public CNativeConstraint residue(CConstraint other) {
        other = other.instantiateSelf(self());
        assert other.consistent();

        CNativeConstraint result = new CNativeConstraint(baseType());
        if (! consistent) return result;

        for (XTerm term : other.constraints()) {
            try {
                if (! entails(term)) result.addTerm(term);
            } catch (XFailure z) {
                // since other is consistent, result must be.
                result.setInconsistent();
            }
        }
        return result;
    }

    public XVar getThisVar(CConstraint t1, CConstraint t2) throws XFailure {
        XVar thisVar = t1 == null ? null : t1.thisVar();
        if (thisVar == null) return t2==null ? null : t2.thisVar();
        if (t2 != null && ! thisVar.equals( t2.thisVar()))
            throw new XFailure("Inconsistent this vars " + thisVar + " and "
                               + t2.thisVar());
        return thisVar;
    }
    /**
     * Return the result of substituting each yi for xi in this.
     * 
     * The self var of the resulting constraint is guaranteed different from
     * the self var of this.
     * 
     * TODO: Use an XGraphVisitor instead of constraints().
     * Note: The only vars that need to be changed are in roots!
     * So doing constraints() and iterating over its terms is really bad.
     */
    public CNativeConstraint substitute(XTerm[] ys, XVar[] xs) throws XFailure {
        assert (ys != null && xs != null);
        assert xs.length == ys.length;
        //for (XVar x : xs)
        //    assert x != self;

        boolean eq = true;
        for (int i = 0; i < ys.length; i++) {
            XTerm y = ys[i];
            XVar x = xs[i];
            if (! y.equals(x)) eq = false;
        }
        if (eq) return this;

        if (! consistent) return this;

        // Don't do the quick occurrence check; x might occur in a self constraint.
        //		XPromise last = lookupPartialOk(x);
        //		if (last == null) return this; 	// x does not occur in this

        CNativeConstraint result = new CNativeConstraint(baseType());
        //result.self = self(); // the resulting constraint should share the same self.
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
                t = t.subst(y, x);
            }

            t = t.subst(result.self(), self());

            try {
                result.addTerm(t);
            }
            catch (XFailure z) {
                throw z;
            }
        }
        return result;
    }


    /** If other is not inconsistent, and this is consistent,
     * checks that each binding X=t in other also exists in this.
     * TODO: Improve performance by doing entailment in place
     * without getting other term's extConstraints.
     * @param other
     * @return
     */

    public boolean entails(CConstraint other, ConstraintMaker sigma)  {
        if (!consistent()) return true;
        if (other == null || other.valid()) return true;
        CEntailsVisitor ev = new CEntailsVisitor(true, true, this, sigma, other.self());
        ((CNativeConstraint)other).visit(ev);
        return ev.result();
    }

    // Why not just use superclass method? 
    @Override
    protected XNativeTerm bindingForRootField(XVar root, Object field) {
        XNativeTerm term;
        if (field instanceof FieldDef) 
        	term = (XNativeTerm)ConstraintManager.getConstraintSystem().makeField(root, (FieldDef) field);
        else if (field instanceof MethodDef) 
        	term = (XNativeTerm)ConstraintManager.getConstraintSystem().makeField(root, (MethodDef) field);
        else { assert false; term = null;}
        XPromise p = term.nfp(this);
        if (p == null) return null;
        return p.term();
    }
    
    public XTerm bindingForSelfField(FieldDef fd) {
        return bindingForRootField(self(), fd);
    }
    public XTerm bindingForSelfField(MethodDef fd) {
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
    public CNativeConstraint leastUpperBound(CConstraint c2, Type t) {
        return leastUpperBound1((CNativeConstraint)c2, t);
    }

    /**
     * Return the constraint obtained by existentially quantifying out the 
     * variable v.
     * 
     * The self var of the resulting constraint is guaranteed different from
     * the self var of this.
     * @param v
     * @return
     */

    public CNativeConstraint project(XVar v)  {
        if (! consistent) return this;
        CNativeConstraint result = null;
        try {
            XVar eqv = ConstraintManager.getConstraintSystem().makeEQV();
            result = substitute(eqv, v); 
        } catch (XFailure c) {
            // should not happen
        }
        return result;
    }

    /**
     * Return exists self.this. Guaranteed that the self var of the
     *  returned constrained does not occur in the constraint.
     *  
     * The self var of the resulting constraint is guaranteed different from
     * the self var of this.
     * @return
     */
    public CNativeConstraint exists() { 
    	return instantiateSelf(ConstraintManager.getConstraintSystem().makeEQV());
    }
    /**
     * Add in the constraint c, and all the constraints associated with the
     * types of the terms referenced in t.
     * @param c
     * @param m
     * @throws XFailure
     */
    public void addSigma(CConstraint c, Map<XTerm, CConstraint> m) {
        if (! consistent()) return;
        if (c != null && ! c.valid()) {
            addIn(c);
            addIn(c.constraintProjection(m));
        }
    }
    public void addSigma(XConstrainedTerm ct, Type t, Map<XTerm, CConstraint> m) {
        if (! consistent()) return;
        if (ct != null) addSigma(ct.xconstraint(t), m);
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
    @Override
    public CNativeConstraint constraintProjection(Map<XTerm,CConstraint> m) {
        return constraintProjection(m, 0); // CollectionFactory.newHashSet());
    }
    
    CNativeConstraint constraintProjection(Map<XTerm,CConstraint> m, 
                                            int depth) 
    {
        CNativeConstraint r = new CNativeConstraint(baseType());
        for (XTerm t : constraints()) {
        	// note that the type of the type field of tc will be null
        	// but this does not matter as it is added in r, and self will
        	// be substituted out
            CNativeConstraint tc = constraintProjection(t, m, depth);
            if (tc != null) r.addIn(tc);
        }
        return r;
    }


    // ***************************************************************** Implementation

    protected boolean entails(XTerm  term, XVar self) throws XFailure {
        XTerm subst = term.subst(self(), self);
        return entails(subst);
    }

    private static <T> boolean contains(Set<T> s, Set<T> c) {
        for (T t: c) if (s.contains(t)) return true;
        return false;
    }
    
    private static int MAX_DEPTH=15;
    /**
     * Recursively collects the constraints on the types present in XTerm t
     * in a new constraint. Note that the returned CNativeConstrained will not
     * have the proper type information.  
     * @param t
     * @param m cache for the constraints already collected
     * @param depth maximum depth of recursion
     * @return
     */
    private static CNativeConstraint constraintProjection(XTerm t, Map<XTerm,CConstraint> m, int depth)  {
        if (t == null) return null;
        if (depth > MAX_DEPTH) {
            //System.err.println("(Warning) Reached threshold when checking constraints. If type-checking fails "
            //		+ "\n please insert a dynamic cast."
            //		+ "\n\t Term: "+ t);
            return new CNativeConstraint(null);
        }
        CNativeConstraint r = (CNativeConstraint)m.get(t);
        if (r != null) return r;
        // pre-fill the cache to avoid infinite recursion
        m.put(t, new CNativeConstraint(null));
        if (t instanceof CLocal) {
            CLocal v = (CLocal) t;
            X10LocalDef ld = v.localDef();
            if (ld != null) {
                Type ty = Types.get(ld.type());
                ty = PlaceChecker.ReplaceHereByPlaceTerm(ty, ld.placeTerm());
                CNativeConstraint ci = (CNativeConstraint)Types.realX(ty);
                r = new CNativeConstraint(null);

                ci = ci.instantiateSelf(v);

                r.addIn(ci);
                if (! r.consistent()) return r;
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

            CNativeConstraint rt = constraintProjection(target, m, depth+1); 
            
            Type ty = f.type();
            CNativeConstraint ci = null;

            if (ty == null) r = rt;
            else {
            	// collecting the constraints on the type of the field
                ci = (CNativeConstraint)Types.realX(ty);
                XVar v = f.thisVar();
                r = new CNativeConstraint(ty);
                if (v != null) {
                    try {
                        ci = ci.substitute(target, v); 
                    } catch (XFailure z) {
                        r.setInconsistent();
                        return r;
                    }
                }

                ci = ci.instantiateSelf(f);
                r.addIn(ci);
                if (! r.consistent()) return r;

                // Recursively perform a constraintProjection on the new constraint ci
                // only if one of the ancestor terms does not occur in it.
                //	if ( ! contains(ancestors, ci.terms())) {
                CNativeConstraint ciInferred = ci.constraintProjection(m, depth+1); // ancestors);
                r.addIn(ciInferred);
                //	}
                if (rt != null) r.addIn(rt);
            } 
        } else if (t instanceof XFormula<?>) {
            XFormula<?> f = (XFormula<?>) t;
            for (XTerm a : f.arguments()) {
                CNativeConstraint ca = constraintProjection(a, m, depth+1); 
                if (ca != null) {
                    if (r == null) r = new CNativeConstraint(null);
                    r.addIn(ca);
                }
            }
        } else if (t instanceof XField<?>) {
        }
        else assert false : "unexpected " + t;
        if (r != null) m.put(t, r); // update the entry
        return r;
    }
    
    private CNativeConstraint leastUpperBound1(CNativeConstraint c2, Type t) {
        CNativeConstraint c1 = this;
        CNativeConstraint result = c1.leastUpperBound0(c2, t);
        CNativeConstraint c1a = null, c2a = null;

        // are these steps essentially weakening the constraints?
        // i.e. c1 => c1a
        // because an XVar can be an XLit, XField etc
        XVar x0 = c1.selfVarBinding();
        if (x0 instanceof XVar)
            c1a = c1.project((XVar) x0);
        
        XVar x =  c2.selfVarBinding();
        if (x instanceof XVar) 
        	c2a = c2.project((XVar) x);

        // this seems to try refine the result
        // Say in c1 you have self = A.f, and A = B. then c1a becomes self = eqv#0, A = B
        // if c2 has B.f = self. Then c1 => B.f = self so it would be in the lub(c1, c2),
        // while it is not in the lub(c1a, c2)
        if (c1a != null) {
            CNativeConstraint d = c1a.leastUpperBound0(c2, t);
            if (d.entails(result)) result = d;
            if (c2a != null) {
                d = c1a.leastUpperBound0(c2a, t);
                if (d.entails(result)) result = d;
            }
        }
        if (c2a != null) {
            CNativeConstraint d = c1.leastUpperBound0(c2a, t);
            if (d.entails(result)) result = d;
        }
        return result;
    }
    
    // also, shoudn't this be symmetric? 
    private CNativeConstraint leastUpperBound0(CNativeConstraint other, Type t) {
        XVar otherSelf = other.self();
        CNativeConstraint result = new CNativeConstraint(t);
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

	@Override
	public Type baseType() {
		return baseType; 
	}

	@Override
	public void setBaseType(Type t) {
		// comparing references because equals for Type is not implemented
		if (baseType != null && baseType != t)
			throw new UnsupportedOperationException("The type is already set.");
		baseType = t; 
	}
    
}

