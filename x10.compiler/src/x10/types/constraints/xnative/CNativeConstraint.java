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

import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.constraint.XConstraint;
import x10.constraint.XDef;
import x10.constraint.XEQV;
import x10.constraint.XExpr;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeConstraint;
import x10.constraint.xnative.XNativeField;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.xnative.XPromise;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.xnative.visitors.AddInVisitor;
import x10.types.constraints.xnative.visitors.CEntailsVisitor;

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
public class CNativeConstraint extends XNativeConstraint<Type>  implements CConstraint {

    /** Variable to use for self in the constraint. */
	private XVar<Type> self;
    private XVar<Type> thisVar;

    public CNativeConstraint(CNativeConstraintSystem sys, XVar<Type> self, TypeSystem ts) {
    	super(sys, ts);
    	this.self = self; 
    }

    /* Creates an empty constraint that is otherwise a copy of 'other'.
     */
    public CNativeConstraint(CNativeConstraint other) {
        this(other.sys(), other.self == null ? (XVar<Type>)null : other.sys().makeSelf(other.self.type()), other.ts());
    }

    @Override
    public CNativeConstraintSystem sys() { return (CNativeConstraintSystem) super.sys(); }

    @Override
    public TypeSystem ts() { return (TypeSystem) super.ts(); }
        
    /**
     * Variable to use for self in the constraint.
     */
    @Override
    public XVar<Type> self() {return self;}

    /**
     * Return what, if anything, self is bound to in the current constraint.
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
	public XTerm<Type> selfVarBinding() {return self()==null ? null : bindingForVar(self());}
    @Override
    public XVar<Type> thisVar() {return thisVar;}
    @Override
    public boolean hasPlaceTerm() {
        if (roots==null) return false;
        for (XTerm<Type> t : roots.keySet()) if (PlaceChecker.isGlobalPlace(t)) return true;
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
        CNativeConstraint result = new CNativeConstraint(sys(), this.self, ts());
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
    @Override
    public void addIn(CConstraint c) {addIn(ensureSelf(c), c);}

    /** If we do not have a self variable, and c does, give us one based on the type of c.self() */
    private XTerm<Type> ensureSelf(CConstraint c) {
		if (self() == null && c!=null && c.self() != null) {
			XVar<Type> s = sys().makeSelf(c.self().type());
			setSelf(s);
		}
		return self();
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
    @Override
    public void addIn(XTerm<Type> newSelf, CConstraint c) {
        if (c == null) return;
        if (!c.consistent()) {setInconsistent();return;}
        if (c.valid()) return;
        assert newSelf!=null || c.self()==null : this+".addIn("+newSelf+","+c+")   "+c.self();
        AddInVisitor v = new AddInVisitor(sys(), ts(), true, false, this, newSelf, c.self());
        // hideFake==false to permit the "place checking" to work.
        // This ensures that multiple objects created at the same place
        // e.g. GlobalRef's, are treated as being at the same place by the 
        // type-checker.
        ((CNativeConstraint)c).visit(v);
        // vj: What about thisVar for c? Should that be added?
        // thisVar = getThisVar(this, c);
    }



    /**
     * Add the binding selfVar == var to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    @Override
    public void addSelfEquality(XTerm<Type> var) {addEquality(self(), var);}

    /**
     * 
     * Add the binding selfVar != term to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    @Override
    public void addSelfDisEquality(XTerm<Type> term) {addDisEquality(self(), term);}
    /**
     * Add the binding selfVar == var to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    @Override
    public void addSelfEquality(XConstrainedTerm var) {addEquality(self(), var);}

    /**
     * Add the binding thisVar == term to this constraint, possibly
     * modifying it in place.
     * @param var
     */
    @Override
    public void addThisEquality(XTerm<Type> term) {addEquality(thisVar(), term);}

    /**
     * Set thisVar to var (if var is non-null). To be used extremely carefully. Does not change
     * terms in the constraint. So there should not be terms referring to the old thisVar.
     * @param var
     */
    @Override
    public void setThisVar(XVar<Type> var) {
        if (var == null) return;
        thisVar = var;
    }

    /**
     * Add the binding s=t.term(), and add in the constraints of t into this. This constraint
     * is possibly modified in place.
     * @param s
     * @param t
     */
    @Override
    public void addEquality(XTerm<Type> s, XConstrainedTerm t) {
        addEquality(s, t.term());
        addIn(s, t.constraint());

    }
    /**
     * Add the binding s=t to this. This constraint is possibly modified in place.
     * @param s
     * @param t
     */
    @Override
    public void addEquality(XConstrainedTerm s, XTerm<Type> t) {addEquality(t,s);}
    /**
     * Add the binding s.term()=t.term() to this, and add in s.constraint() and t.constraint(). 
     * This constraint is possibly modified in place.
     * @param s
     * @param t
     */
    @Override
    public void addEquality(XConstrainedTerm s, XConstrainedTerm t) {
        addEquality(s.term(), t.term());
        addIn(s.term(), s.constraint());
        addIn(t.term(), t.constraint());
    }

    /**
     * Substitute y for x in this, returning a new constraint.
     * // Redeclare with the right return type
     */
    @SuppressWarnings("unchecked")
	@Override
    public CNativeConstraint substitute(XTerm<Type> y, XTerm<Type> x) throws XFailure {
    	return substitute(new XTerm[] { y }, new XTerm[] { x });
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
    @Override
    public CNativeConstraint instantiateSelf(XTerm<Type> newSelf) {
        CNativeConstraint result = new CNativeConstraint(this);
        List<XNativeTerm<Type>> terms = constraints();
        for (XTerm<Type> term : terms) {
            XTerm<Type> t = term.subst(sys(), newSelf,self);
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

    @Override
    public CNativeConstraint residue(XConstraint<Type> other_) {
    	CNativeConstraint other = (CNativeConstraint) other_;
    	
        other = other.instantiateSelf(self());
        assert other.consistent();

        CNativeConstraint result = new CNativeConstraint(this);
        if (!consistent) return result;

        for (XTerm<Type> term : other.constraints()) {
            try {
                if (! entails(term)) result.addTerm(term);
            } catch (XFailure z) {
                // since other is consistent, result must be.
                result.setInconsistent();
            }
        }
        return result;
    }

    @Override
    public XVar<Type> getThisVar(CConstraint t1, CConstraint t2) throws XFailure {
        XVar<Type> thisVar = t1 == null ? null : t1.thisVar();
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
    @Override
    public CNativeConstraint substitute(XTerm<Type>[] ys, XTerm<Type>[] xs) throws XFailure {
        assert (ys != null && xs != null);
        assert xs.length == ys.length;
        //for (XVar<Type> x : xs)
        //    assert x != self;

        boolean eq = true;
        for (int i = 0; i < ys.length; i++) {
            XTerm<Type> y = ys[i];
            XTerm<Type> x = xs[i];
            if (! y.equals(x)) eq = false;
        }
        if (eq) return this;

        if (! consistent) return this;

        // Don't do the quick occurrence check; x might occur in a self constraint.
        //		XPromise last = lookupPartialOk(x);
        //		if (last == null) return this; 	// x does not occur in this

        CNativeConstraint result = new CNativeConstraint(this);
        //result.self = self(); // the resulting constraint should share the same self.
        List<XNativeTerm<Type>> terms = constraints();
        for (XTerm<Type> term : terms) {
            XTerm<Type> t = term;

            // if term is y==x.f, the subst will produce y==y.f, which is a cycle--bad!
            //		    if (term instanceof XEquals_c) {
            //		        XEquals_c eq = (XEquals_c) term;
            //		        XTerm<Type> l = eq.left();
            //		        XTerm<Type> r = eq.right();
            //		        if (y.equals(l) || y.equals(r))
            //		            continue;
            //		    }
            for (int i = 0; i < ys.length; i++) {
                XTerm<Type> y = ys[i];
                XTerm<Type> x = xs[i];
                t = t.subst(sys(), y, x);
            }

            t = t.subst(sys(), result.self(), self());

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

    @Override
    public boolean entails(CConstraint other, ConstraintMaker sigma)  {
        if (!consistent()) return true;
        if (other == null || other.valid()) return true;
        CEntailsVisitor ev = new CEntailsVisitor(sys, ts, true, true, this, sigma, other.self());
        ((CNativeConstraint)other).visit(ev);
        return ev.result();
    }

    @Override
    public XTerm<Type> bindingForSelfField(XDef<Type> def) {
        XNativeField<Type, XDef<Type>> term = sys().makeField(self(), def);
		XPromise<Type> p = nfp(term);
		if (p == null) return null;
		return p.term();
    }

    /** Return the least upper bound of this and other. That is, the resulting constraint has precisely
     * the constraints entailed by both this and other.
     * @param other
     * @return
     */
    @Override
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

    @Override
    public CNativeConstraint project(XVar<Type> v)  {
        if (! consistent) return this;
        CNativeConstraint result = null;
        try {
            XVar<Type> eqv = ConstraintManager.getConstraintSystem().makeEQV(v.type());
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
    @Override
    public CNativeConstraint exists() { 
    	return instantiateSelf(sys().makeEQV(self.type()));
    }
    /**
     * Add in the constraint c, and all the constraints associated with the
     * types of the terms referenced in t.
     * @param c
     * @param m
     * @throws XFailure
     */
    @Override
    public void addSigma(CConstraint c, Map<XTerm<Type>, CConstraint> m) {
        if (! consistent()) return;
        if (c != null && ! c.valid()) {
            addIn(c);
            addIn(c.constraintProjection(m));
        }
    }
    @Override
    public void addSigma(XConstrainedTerm ct, Type t, Map<XTerm<Type>, CConstraint> m) {
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
    public CNativeConstraint constraintProjection(Map<XTerm<Type>,CConstraint> m) {
        return constraintProjection(m, 0); // CollectionFactory.newHashSet());
    }
    
    private CNativeConstraint constraintProjection(Map<XTerm<Type>,CConstraint> m, int depth) 
    {
        CNativeConstraint r = new CNativeConstraint(this);
        //r.addIn(this);
        for (XTerm<Type> t : constraints()) {
        	// note that the type of the type field of tc will be null
        	// but this does not matter as it is added in r, and self will
        	// be substituted out
            CNativeConstraint tc = constraintProjection(t, m, depth);
            if (tc != null) r.addIn(tc);
        }
        return r;
    }


    // ***************************************************************** Implementation

    private static <T> boolean contains(Set<T> s, Set<T> c) {
        for (T t: c) if (s.contains(t)) return true;
        return false;
    }
    
    private static int MAX_DEPTH=15;
    /**
     * Recursively collects the constraints on the types present in XTerm<Type> t
     * in a new constraint. Note that the returned CNativeConstrained will not
     * have the proper type information.  
     * @param t
     * @param m cache for the constraints already collected
     * @param depth maximum depth of recursion
     * @return
     */
    private CNativeConstraint constraintProjection(XTerm<Type> t, Map<XTerm<Type>,CConstraint> m, int depth)  {
        if (t == null) return null;
        if (depth > MAX_DEPTH) {
            //System.err.println("(Warning) Reached threshold when checking constraints. If type-checking fails "
            //		+ "\n please insert a dynamic cast."
            //		+ "\n\t Term: "+ t);
            return new CNativeConstraint(sys(),(XVar<Type>)null,ts());
        }
        CNativeConstraint r = (CNativeConstraint)m.get(t);
        if (r != null) return r;
        // pre-fill the cache to avoid infinite recursion
        m.put(t, new CNativeConstraint(sys(),(XVar<Type>)null,ts()));
        if (t instanceof CLocal) {
            CLocal v = (CLocal) t;
            X10LocalDef ld = v.def();
            if (ld != null) {
                Type ty = Types.get(ld.type());
                ty = PlaceChecker.ReplaceHereByPlaceTerm(ty, ld.placeTerm());
                CNativeConstraint ci = (CNativeConstraint)Types.realX(ty,ts());
                r = new CNativeConstraint(sys(),(XVar<Type>)null,ts());

                ci = ci.instantiateSelf(v);

                r.addIn(ci);
                if (! r.consistent()) return r;
                // Recursively perform a constraintProjection on the new constraint ci
                // only if one of the ancestor terms does not occur in it.
                // if (! contains(ancestors, ci.terms()))
                r.addIn(ci.constraintProjection(m, depth+1));
            }
        } else if (t instanceof XLit) { // no new info to contribute
        } else if (t instanceof XEQV) { // no new info to contribute
        } else if (t instanceof XUQV) { // no new info to contribute

        } else if (t instanceof XField<?,?>){
            @SuppressWarnings("unchecked")
			XField<Type,XDef<Type>> f = (XField<Type,XDef<Type>>) t;
            XTerm<Type> target = f.receiver();

            CNativeConstraint rt = constraintProjection(target, m, depth+1); 
            
            Type ty = f.type();
            CNativeConstraint ci = null;

            if (ty == null) r = rt;
            else {
            	// collecting the constraints on the type of the field
                ci = (CNativeConstraint)Types.realX(ty,ts());
                XVar<Type> v = thisVar(f);
                r = new CNativeConstraint(sys(),sys().makeSelf(ty),ts());
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
        } else if (t instanceof XExpr<?>) {
        	XExpr<Type> f = (XExpr<Type>) t;
            for (XTerm<Type> ch : f.children()) {
                CNativeConstraint c_ch = constraintProjection(ch, m, depth+1); 
                if (c_ch != null) {
                    if (r == null) r = new CNativeConstraint(sys(), (XVar<Type>)null, ts());
                    r.addIn(c_ch);
                }
            }
        } else if (t instanceof XField<?,?>) {
        }
        else assert false : "unexpected " + t;
        if (r != null) m.put(t, r); // update the entry
        return r;
    }
    
	private static XVar<Type> thisVar(XField<Type,XDef<Type>> f) {
        if (f.field() instanceof X10FieldDef)
            return ((X10ClassDef) Types.get(((X10FieldDef) f.field()).container()).toClass().def()).thisVar();
        return null;
	}    
    
    private CNativeConstraint leastUpperBound1(CNativeConstraint c2, Type t) {
        CNativeConstraint c1 = this;
        CNativeConstraint result = c1.leastUpperBound0(c2, t);
        CNativeConstraint c1a = null, c2a = null;

        // are these steps essentially weakening the constraints?
        // i.e. c1 => c1a
        // because an XVar<Type> can be an XLit, XField etc
        XTerm<Type> x0 = c1.selfVarBinding();
        if (x0 instanceof XVar)
            c1a = c1.project((XVar<Type>) x0);
        
        XTerm<Type> x =  c2.selfVarBinding();
        if (x instanceof XVar) 
        	c2a = c2.project((XVar<Type>) x);

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
        XVar<Type> otherSelf = other.self();
        CNativeConstraint result = new CNativeConstraint(sys(), sys().makeSelf(t), ts());
        XVar<Type> resultSelf = result.self();
        for (XTerm<Type> term : other.constraints()) {
            try {
                if (entailsEquality(term, otherSelf)) {
                    term = term.subst(sys, resultSelf, otherSelf);
                    result.addTerm(term);
                }
            } catch (XFailure z) {
                result.setInconsistent();
            }
        }
        return result;
    }

	@Override
	public void setSelf(XVar<Type> var) {
		this.self = var;
	}

}

