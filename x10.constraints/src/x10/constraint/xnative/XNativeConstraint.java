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

package x10.constraint.xnative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XExpr;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XLabeledOp;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;
import x10.constraint.XVar;
import x10.constraint.xnative.visitors.AddInVisitor;
import x10.constraint.xnative.visitors.ConstraintGenerator;
import x10.constraint.xnative.visitors.EntailsVisitor;
import x10.constraint.xnative.visitors.XGraphVisitor;
import x10.util.CollectionFactory;


/**
 * 
 *  A constraint solver for the following constraint system. Note terms in this 
 *  constraint system are untyped.
 * <pre>
 * t ::= x                  -- variable
 *       | t.f              -- field access
 *       | g(t1,..., tn)    -- uninterpreted function symbol
 *       
 * c ::= t == t             -- equality
 *       | t != t           -- dis-equality
 *       | c,c              -- conjunction
 *       | p(t1,..., tn)    -- atomic formula
 * </pre>  
 * 
 * The constraint system implements the usual congruence rules for equality. 
 * That is, if <code>s1,...,sn</code> and 
 * <code>t1,...,tn</code> are terms, and <code> s1 == t1,..., sn == tn</code>, 
 * then 
 * <code>g(s1,..., sn) == g(s1,...,sn)</code>, and 
 * <code>p(t1,..., tn) == p(t1,...,tn)</code>. Further, 
 * <uline>
 *   <li> <code>s equals t</code> implies <code>t equals s</code>
 *   <li> <code>s equals t</code> and <code>t equals u</code> implies <code> s 
 *   equals u</code>
 *   <li> it is always the case that <code>s equals s</code>
 * </uline>
 * <p>Terms are created using the static API in XNativeTerms. The <code>==</code> 
 * relation on terms at the level of the constraint system is translated into 
 * the <code>equals</code> relation on the Java representation of the terms.
 * 
 * <p>A constraint is implemented as a graph whose nodes are XPromises. Two 
 * different constraints will not share @link{XPromise}. See the description of 
 * @link{XPromise} for more information about the structure of a promise.
 * 
 * <p>This representation is a bit different from the Nelson-Oppen and 
 * Shostak congruence closure algorithms described, e.g. in Cyrluk, Lincoln and 
 * Shankar "On Shostak's Decision Procedure for Combination of Theories", 
 * CADE 96.
 * 
 * <p>
 * <bf>TODO:</bf>
 * Use Shostak's congruence procedure. Treat <tt>t.f</tt> as the term 
 * <tt>f(t)</tt>, i.e. each field is regarded as a unary function symbol. 
 * This will be helpful in implementing Nelson-Oppen integration of 
 * decision procedures.
 * 
 * <p> Additional Notes.
 * This constraint system and its implementation knows nothing about X10 or 
 * internal compiler structures. Specifically it knows nothing about X10 types.
 * The package x10.types.constraints contains an extension of this type system 
 * that is aware of a this variable, a self variable and other compiler-related 
 * data-structures.
 * 
 * @author vj
 *
 */
public class XNativeConstraint<T extends XType> implements Cloneable, XConstraint<T> {
	
    protected Map<XNativeTerm<T>, XPromise<T>> roots;
    protected boolean consistent = true;
    protected boolean valid = true;
    protected XNativeConstraintSystem<T> sys;
    protected XTypeSystem<T> ts;

    public XNativeConstraint(XNativeConstraintSystem<T> sys, XTypeSystem<T> ts) {
		this.sys = sys;
		this.ts = ts;
	}

    @Override
    public XNativeConstraintSystem<T> sys() { return sys; }
    
    @Override
    public XTypeSystem<T> ts() { return ts; }

    public Map<XNativeTerm<T>, XPromise<T>> roots() { return roots;}
    
    /**
	 * Return the list of existentially quantified variables in this constraint.
	 * @return
	 */
    private List<XNativeEQV<T>> eqvs() {
    	List<XNativeEQV<T>> xvars = new LinkedList<XNativeEQV<T>>();
    	if (roots==null) return xvars;
    	for (XNativeTerm<T> xt : roots.keySet()) {
    		if (xt instanceof XNativeEQV<?>) xvars.add((XNativeEQV<T>) xt);
    	}
    	return xvars;
    }

    /**
     * Return the set of terms occurring in this constraint.
     * @return
     */
    /*
    @Override
    public Set<XNativeTerm<T>> rootTerms() {
    	return roots == null ? Collections.<XNativeTerm<T>> emptySet() : roots.keySet();
    }
    */
   
  
    /*
    private void addTerm(XNativeTerm term, Set<XVar<Type>> result) {
        if (term==null)
            return;
        if (term instanceof XFormula) {
            XFormula form = (XFormula) term;
            for (XNativeTerm arg : form.arguments())
                addTerm(arg, result);
            return;
        } 
        if (term instanceof XVar)
            addVar((XVar) term, result);
    }
    private void addVar(XVar var, Set<XVar<Type>> result) {
        if (var == null)
            return;
        result.add(var);
        if (var instanceof XField) {
            addVar(((XField)var).receiver(), result);
        }
    }
    public Set<XVar<Type>> vars() {
        List<XNativeTerm> terms = constraints();
        Set <XVar<Type>> result = CollectionFactory.newHashSet();
        for (XNativeTerm term : terms) {
           addTerm(term, result);
        }
        return result;   
    }
    */
    /**
     * Copy this constraint. Implemented via a graph traversal.
     */
    @Override
    public XNativeConstraint<T> copy() {
        XNativeConstraint<T> result = new XNativeConstraint<T>(sys, ts);
	    result.addIn(this);
	    return result;
       // return copyInto(new XNativeConstraint());
    }
    
    private void addIn(XConstraint<T> c) {
        if (c== null) return;
        if (! c.consistent()) {
            setInconsistent();
            return;
        }
        if (c.valid()) return;
        AddInVisitor<T> v  = new AddInVisitor<T>(sys(), ts(), true, false, this);
        // hideFake==false to permit the "place checking" to work.
        // This ensures that multiple objects created at the same place
        // e.g. GlobalRef's, are treated as being at the same place by the 
        // type-checker.
        ((XNativeConstraint<T>)c).visit(v);
        // vj: What about thisVar for c? Should that be added?
        // thisVar = getThisVar(this, c);
        return;
    }

    /**
     * Return the result of copying this into c.  
     * TODO: Do this with an XGraphVisitor.
     * @param c
     * @return
     */
 /*   protected <T extends XNativeConstraint> T copyInto(T c)  {
        c.consistent = consistent;
        c.valid = valid;
        if (roots !=null) {
            c.roots = CollectionFactory.<XNativeTerm, XPromise>newHashMap(roots.size());
            Map<XPromise, XPromise> redirects =  CollectionFactory.<XPromise, XPromise>newHashMap(roots.size());
            for (Map.Entry<XNativeTerm, XPromise> entry : roots.entrySet()) {
                XNativeTerm xt = entry.getKey();
                XPromise p = entry.getValue();
                XPromise np = p.cloneShallow();
                c.roots.put(xt, np);
                redirects.put(p, np);
            }
            for (XPromise p : roots.values()) {
                p.transfer(redirects);
            }
        }
        return c;
    }
*/
    

    
	/**
	 * Return the term v is bound to in this constraint, and null
	 * if there is no such term. This term will be distinct from v.
	 * */
    @Override
    public XNativeTerm<T> bindingForVar(XTerm<T> v) {
    	assert v != null;
		XPromise<T> p = nfp((XNativeTerm<T>)v);
    	if (p != null && ! p.term().equals(v)) {
    		// [DC] why not just return p.term() at this point?
    		// [DC] CURRENT BUG: NEED TO FIND WHY THIS RETURNS NULL!
    		return nfp(p.term()).term();
    	}
    	return null;
    }
    
//    private XNativeTerm<T> bindingForRootField(XVar<T> root, Object field) {
//    	XNativeTerm<T> term = new XNativeField<T,Object>(root, field);
//    	XPromise<T> p = term.nfp(this);
//    	if (p == null) return null;
//    	return p.term();
//    }

	/**
	 * Return the list of atoms (atomic formulas) in this constraint.
	 * @return
	 */
    private List<XNativeExpr<T>> atoms() {
    	List<XNativeExpr<T>> r = new ArrayList<XNativeExpr<T>>(5);
    	if (roots == null) return r;
    	for (XNativeTerm<T> t : roots.keySet()) {
    		if ((t instanceof XNativeExpr<?>) && !(t instanceof XNativeField<?,?>)) {
    			r.add((XNativeExpr<T>)t);
    		}
    	}
    	return r;
    }
    
    /**
     * Return the set of XVars mentioned in this constraint.
     * @return
     */
    @Override
    public Set<XNativeTerm<T>> getVarsAndFields() {
        Set<XNativeTerm<T>> result = CollectionFactory.newHashSet();
        for (XNativeTerm<T> term : constraints()) 
        	addTerm(term, result);
        return result;
    }
    
    private void addTerm(XNativeTerm<T> term, Set<XNativeTerm<T>> result) {
        if (term instanceof XVar) {
        	result.add(term);
        } else if (term instanceof XNativeExpr) {
            if (term instanceof XField) result.add(term);
            
        	// recurse over children
            XNativeExpr<T> expr = (XNativeExpr<T>) term;
            for (XNativeTerm<T> child : expr.children()) addTerm(child, result);
        } 
    }
   
    /**
	 * Is the constraint consistent? That is, does it have a solution?
	 * Implementation Note: In the body of XNativeConstraint, we build in that consistent()
	 * returns the field consistent.
	 * @return true iff the constraint is consistent.
     */
    @Override
    public boolean consistent() { return consistent; }

    /** Is the constraint valid? i.e. is it satisfied by every solution?
     * 
     */
    @Override
    public boolean valid() { return consistent && valid;}

    private boolean addFlattenedEquality(XNativeTerm<T> left, XNativeTerm<T> right)  {
    	return addFlattenedTerm(false, left, right);
    }

    private boolean addFlattenedDisEquality(XNativeTerm<T> left, XNativeTerm<T> right)  {
    	return addFlattenedTerm(true, left, right);
    }

	private boolean addFlattenedTerm(boolean ctxDisEq, XNativeTerm<T> left, XNativeTerm<T> right)  {

        // handle several special cases:
        // (X==Y)==true     ---> X==Y
        // !(X==Y)==true     ---> X!=Y
        // (X==Y)==false    ---> X!=Y
        // !(X==Y)==false    ---> X==Y
    	
        @SuppressWarnings("unchecked")
		XNativeLit<T,Boolean> boolLit = left.isBooleanLit() ? (XNativeLit<T,Boolean>)left : right.isBooleanLit() ? (XNativeLit<T,Boolean>)right : null;
        
        if (boolLit!=null) {
            XNativeTerm<T> other = boolLit==left ? right : left;
            boolean isEquals = other.isEquals();
            boolean isDisEquals = other.isDisEquals();
            if (isEquals || isDisEquals) {
				XNativeExpr<T> otherExpr = (XNativeExpr<T>) other;
        		left = otherExpr.get(0);
        		right = otherExpr.get(1);
        		if (boolLit.isLiteralValue(false) ^ isDisEquals ^ ctxDisEq) {
        			addDisEquality(left, right);
        		} else {
        			addEquality(left,right);
        		}
                return true;
            }
        }
        return false;
    }
    /**
     * Add t1=t2 to the constraint, unless it is inconsistent. 
     * Note: constraint is modified in place.
     * @param nodeLabel -- t1
     * @param val -- t2
     */
    @Override
    public void addEquality(XTerm<T> l, XTerm<T> r)  {
    	assert l != null;
        assert r != null;
        XNativeTerm<T> left = (XNativeTerm<T>) l;
        XNativeTerm<T> right = (XNativeTerm<T>) r;
        
        if (addFlattenedEquality(left, right)) return;
        if (!consistent) return;
        if (roots == null) roots = CollectionFactory.<XNativeTerm<T>, XPromise<T>> newHashMap();

        XPromise<T> p1 = intern(left);
        if (p1==null) { setInconsistent(); return;}
        XPromise<T> p2 = intern(right);
        if (p2 == null) { setInconsistent();return;}
        try {
            valid &= ! p1.bind(sys, p2, this);
        } catch (XFailure z) {
            setInconsistent();
        }
    }

    /**
	 * Add t1 != t2 to the constraint.
	 * Note: Constraint is updated in place.
	 * @param nodeLabel
	 * @param t
	 */
    @SuppressWarnings("unchecked")
	@Override
    public void addDisEquality(XTerm<T> l, XTerm<T> r)  {
    	assert l != null;
        assert r != null;
        XNativeTerm<T> left = (XNativeTerm<T>) l;
        XNativeTerm<T> right = (XNativeTerm<T>) r;

        if (addFlattenedDisEquality(left, right)) return;
    	if (! consistent) return;
    	if (roots == null) roots = CollectionFactory.<XNativeTerm<T>,XPromise<T>> newHashMap();
    	XPromise<T> p1 = intern(left);
    	if (p1 == null)    {setInconsistent();return;}
    	XPromise<T> p2 = intern(right);
    	if (p2 == null)    {setInconsistent();return;}
    	if (p1.equals(p2)) {setInconsistent();return;}
    	try {
    	    valid &= ! p1.disBind(p2);
    	    if (left instanceof XField<?,?> && right instanceof XField<?,?>) {
    	    	XField<?,?> lf = (XField<?,?>) left, 
    	    			  rf = (XField<?,?>) right;
    	    	if (lf.field()==rf.field()) addDisEquality((XTerm<T>)lf.receiver(), (XTerm<T>)rf.receiver()); 	    	
    	    }
    	} catch (XFailure z) {
    	    setInconsistent();
    	}
        
    }


	/**
	 * Add an atomic formula to the constraint.
	 * Note: Constraint is modified in place.
	 * @param term
	 * @return
	 * @throws XFailure
	 */
    private void addAtom(XTerm<T> term) throws XFailure {
        if (!consistent) return;
        valid = false;
        if (roots == null) roots = CollectionFactory.<XNativeTerm<T>,XPromise<T>> newHashMap();
        XNativeTerm<T> t = (XNativeTerm<T>) term;
        XPromise<T> p = nfp(t);
        if (p != null) return;           // nothing to do    
        p = intern(t);
    }
    /**
	 * Does this entail constraint other?
	 * 
	 * @param t
	 * @return
	 */
    @Override
    public boolean entails(XConstraint<T> other)  {
    	if (!consistent) return true;
        if (other == null || other.valid()) return true;
        EntailsVisitor<T> ev = new EntailsVisitor<T>(sys(), ts(), true, false, this);
        ((XNativeConstraint<T>)other).visit(ev);
        return ev.result();
    }
    
    @Override
    public void setInconsistent() { this.consistent = false; }  
   

    @Override
    public XConstraint<T> leastUpperBound(XConstraint<T> other) {
        if (! consistent)         return other;
        if (! other.consistent()) return this;
        if (valid)                return this;
        if (other.valid())        return other;
       	XNativeConstraint<T> result = new XNativeConstraint<T>(sys, ts);
       	for (XTerm<T> term : ((XNativeConstraint<T>)other).constraints()) {
       		try {
       			if (entails(term)) result.addTerm(term);
       		} catch (XFailure z) {
       		    result.setInconsistent();
       		}
       	}
       	return result;
       }
    

    @Override
    public XConstraint<T> residue(XConstraint<T> other) {
        assert other.consistent();
        XNativeConstraint<T> result = new XNativeConstraint<T>(sys, ts);
        if (! consistent) return result;
        for (XNativeTerm<T> term : ((XNativeConstraint<T>)other).constraints()) {
            try {
                if (! entails(term)) result.addTerm(term);
            } catch (XFailure z) {
                // since other is consistent, result must be.
                result.setInconsistent();
            }
        }
        return result;
    }

    
    /**
	 * Return a list of bindings t1-> t2 equivalent to the current
	 * constraint. Equivalent to constraints(new ArrayList()).
	 * 
	 * @return
	 */
    protected List<XNativeTerm<T>> constraints() {
        if (roots == null) return new ArrayList<XNativeTerm<T>>(0);
        ConstraintGenerator<T> cg = new ConstraintGenerator<T>(sys(), ts(), true, false);
        visit(cg);
        return cg.result();
    }
    

    protected void visit( XGraphVisitor<T> xg) {
        if (roots == null) return;
        Collection<XPromise<T>> values = roots.values();
        for (XPromise<T> p : values) {
        	boolean result = p.visit(xg, this);
            if (! result) return;
        }
    }
    
   
    /**
     * Return a list of bindings t1-> t2 equivalent to 
     * the current constraint except that equalities involving EQV variables 
     * are ignored.
     * 
     * @return
     */
    @Override
    public List<XNativeTerm<T>> extTerms() {
        ConstraintGenerator<T> cg = new ConstraintGenerator<T>(sys(), ts(), true, false);
        visit(cg);
        return cg.result();
    }
	/**
	 * Return a list of bindings t1-> t2 equivalent to the current
	 * constraint except that equalities involving only EQV variables are 
	 * ignored if dumpEQV is false, and equalities involving only fake fields
	 * are ignored if hideFake is true.
	 * 
	 * @return
	 */
    @Override
    public List<XNativeTerm<T>> extTermsHideFake() {
        ConstraintGenerator<T> cg = new ConstraintGenerator<T>(sys(), ts(), true, true);
        visit(cg);
        return cg.result();
    }

	/**
	 * Does this entail a != b?
	 * @param a
	 * @param b
	 * @return this |- a != b
	 */
    @Override
    public boolean entailsDisEquality(XTerm<T> term1, XTerm<T> term2)  {
    	XNativeTerm<T> t1 = (XNativeTerm<T>)term1;
    	XNativeTerm<T> t2 = (XNativeTerm<T>)term2;
    	
    	if (! consistent) return true;
    	XPromise<T> p1 = nfp(t1);
    	if (p1 == null) return false; // this constraint knows nothing about t1.
    	XPromise<T> p2 = nfp(t2);
    	if (p2 == null) return false;
    	if (p1.isDisBoundTo(p2)) return true;
    	Map<Object, XPromise<T>> p1f = p1.fields();
    	if (p1f !=null) {
    		Map<Object, XPromise<T>> p2f = p2.fields();
    		if (p2f != null) 
    			for (Map.Entry<Object, XPromise<T>> me : p1f.entrySet()) {
    				Object field = me.getKey();
    				XPromise<T> v1 = me.getValue();
    				XPromise<T> v2 = p2f.get(field);
    				if (v2 == null) continue;
    				XPromise<T> r1 = v1.lookup();
    				XPromise<T> r2 = v2.lookup();
    				if (r1.isDisBoundTo(r2)) return true;
    			}
    	}
    	return false;
    	
    	
    }
  
	/**
	 * Does this entail a==b? 
	 * @param a
	 * @param b
	 * @return true iff this |- a==b
	 */
    @Override
    public boolean entailsEquality(XTerm<T> term1, XTerm<T> term2)  {
    	XNativeTerm<T> t1 = (XNativeTerm<T>)term1;
    	XNativeTerm<T> t2 = (XNativeTerm<T>)term2;

        if (!consistent) return true;
        XPromise<T> p1 = nfp(t1);
        XPromise<T> p2 = nfp(t2);
        return p1 == p2 || p1.term().equals(p2.term());
    }
    
    /** Return true if this constraint entails t. */
    @Override
    public boolean entails(XTerm<T> t) {
    	if (!(t instanceof XExpr<?>)) return false;
        XNativeExpr<T> e = (XNativeExpr<T>)t;
        
        // [DC] all of this code is very unappealing
        
        if (e.isEquals()) {
            XNativeTerm<T> left = e.get(0);
            XNativeTerm<T> right = e.get(1);
            if (entailsEquality(left, right)) return true;
            /*
            if (right.isEquals()) {
            	XNativeExpr<T> r = (XNativeExpr<T>) right;
                XNativeTerm<T> rleft = r.get(0);
                XNativeTerm<T> rright = r.get(1);
            	if (entailsEquality(rleft, rright)) 
            		return entails(left, XNativeLit.TRUE);
            	if (entailsDisEquality(rleft, rright)) {
            		return entails(left, XNativeLit.FALSE);
            	}
            }
            if (right.isDisEquals()) {
            	XNativeExpr<T> r = (XNativeExpr<T>) right;
                XNativeTerm<T> rleft = r.get(0);
                XNativeTerm<T> rright = r.get(1);
            	if (entailsEquality(rleft, rright)) 
            		return entails(left, XNativeLit.FALSE);
            	if (entailsDisEquality(rleft, rright)) 
            		return entails(left, XNativeLit.TRUE);
            }
            */
            
        } else if (e.isDisEquals()) {
            XNativeTerm<T> left = e.get(0);
            XNativeTerm<T> right = e.get(1);
            if (entailsDisEquality(left, right)) return true;
        } else {
        	XOp<T> op = e.op();
        	List<XNativeTerm<T>> children = e.children();
        	int n = children.size();
        	for (XNativeExpr<T> x : atoms()) {
        		if (x.op().equals(op)) {
        			List<XNativeTerm<T>> xchildren = x.children();
        			if (n!= xchildren.size()) continue;
        			int i=0;
        			while(i < n && entailsEquality(children.get(i), xchildren.get(i))) i++;
        			if (i==n) return true;
        		}
        	}
        	return false;
        }
        return false;
    }


    @Override
    public String toString() {
        XNativeConstraint<T> c = this;
        if (! c.consistent) return "{inconsistent}";
        String str ="{";
        final boolean exists_toString = false;
        if (exists_toString) {
            List<XNativeEQV<T>> eqvs = eqvs();
            if (!eqvs.isEmpty()) {
                String temp = eqvs.toString();
                str = "exists " + temp.substring(1, temp.length() - 1) + ".";
            }
            String constr = c.constraints().toString();
            str += constr.substring(1, constr.length() - 1);
        }
        else {
        	
        	List<XNativeTerm<T>> l = c.extTermsHideFake();
        	for (XNativeTerm<T> t : l) {
        		str += t.toString() + ", " ;
        	}
       		str = str.length() >= 2? str.substring(0, str.length() - 2) : str;
        }
        return str + "}";
    }

    
	/**
	 * Add the binding term=true to the constraint.
	 * 
	 * @param term -- must be of type Boolean.
	 * @return new constraint with term=true added.
	 * @throws SemanticException
	 */
    // FIXME: need to convert f(g(x)) into \exists y. f(y) && g(x) = y when f and g both atoms
    // This is needed for Nelson-Oppen to work correctly.
    // Each atom should be a root.
    @Override
    public void addTerm(XTerm<T> term_) throws XFailure {
    	assert term_ != null;
    	XNativeTerm<T> term = (XNativeTerm<T>) term_;
        if (isAtomic(term)) {
        	addAtom(term);
        } else if (term instanceof XVar<?>) {
        	// addTerm(x) causes x==true to be added (i.e. x->true in the promise graph)
            addEquality(term, sys.xtrue(ts));
        } else if (term instanceof XNativeLit<?,?>) {
        	// addTerm(x) causes x==true to be added (i.e. x->true in the promise graph)
            addEquality(term, sys.xtrue(ts));
        } else if (term instanceof XNativeField<?,?>) {
        	// addTerm(x) causes x==true to be added (i.e. x->true in the promise graph)
            addEquality(term, sys.xtrue(ts));
        } else if (term instanceof XExpr<?>) {
        	XNativeExpr<T> term2 = (XNativeExpr<T>)term;
        	if (term2.children().size() == 2) {
            	XNativeTerm<T> left = term2.get(0);
            	XNativeTerm<T> right = term2.get(1);
            	if (term.isAnd()) {
	                addTerm(left);
	                addTerm(right);
            	} else if (term.isEquals()) {
                    addEquality(left, right);
            	} else if (term.isDisEquals()) {
            		addDisEquality(left, right);
            	} else {
                    throw new XFailure("Unexpected term |" + term + "|");
            	}
        	} else {
        		throw new XFailure("Unexpected term |" + term + "|");
        	}
        } else {
            throw new XFailure("Unexpected term |" + term + "|");
        }
    }
    
    static boolean isAtomic(XNativeTerm<?> term) {
    	if (term instanceof XNativeField<?,?>) return false;
    	if (term instanceof XNativeExpr<?>) {
    		XNativeExpr<?> e = (XNativeExpr<?>) term;
    		XOp<?> o = e.op();
    		if (o instanceof XLabeledOp<?,?>) {
    			return true;
    		}
    	}
		return false;
	}

	// *****************************************************************INTERNAL ROUTINES

	/**
	 * Return the promise obtained by interning this term in the constraint.
	 * This may result in new promises being added to the graph maintained
	 * by the constraint. 
	 * <p>term: Literal -- return the literal. 
	 * <p> term:LocalVariable, Special, Here Check if term is already in the roots
	 * maintained by the constraint. If so, return the root, if not add a
	 * promise to the roots and return it. 
	 * <p> term: XField. Start with the rootVar x and follow the path f1...fk, 
	 * if term=x.f1...fk. If the graph contains no nodes after fi, 
	 * for some i < k, add promises into the graph from fi+1...fk. 
	 * Return the last promise.
	 * 
	 * <p> Package protected -- should only be used in the implementation of the constraint
	 * system.
	 * @param term
	 * @return
	 * @throws XFailure
	 */

    XPromise<T> intern(XNativeTerm<T> term)  { return intern(term, null);}
    
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
    XPromise<T> intern(XNativeTerm<T> term, XPromise<T> last)  {
    	assert term != null;
        return internIntoConstraint(term, last);
    }

    XPromise<T> internBaseVar(XNativeTerm<T> baseVar, boolean replaceP, XPromise<T> last)  {
        if (roots == null) roots = CollectionFactory.<XNativeTerm<T>,XPromise<T>> newHashMap();
        XPromise<T> p =  roots.get(baseVar);
        if (p == null) {
            p = (replaceP && last != null) ? last : new XPromise<T>(baseVar);
            roots.put(baseVar, p);
        }
        return p;
    }
    
    void addPromise(XNativeTerm<T> p, XPromise<T> node) {
        if (roots == null) roots = CollectionFactory.<XNativeTerm<T>,XPromise<T>> newHashMap();
        roots.put(p, node);
    }

	@Override
	public List<? extends XTerm<T>> terms() {
		ArrayList<XNativeTerm<T>> r = new ArrayList<XNativeTerm<T>>();
		if (roots == null) return r;
		for (XNativeTerm<T> root : roots.keySet()) r.add(root);
		return r;
	}

	
    /**
     * Return the promise corresponding to the normal form of the term, 
     * interning the term if it is not interned already. 
     * If p is the return value, then guaranteed p!= null and p=p.lookup().
     * @param c
     * @return
     */
	public final XPromise<T> nfp(XNativeTerm<T> term) {
    	XPromise<T> p = null;
    	if (roots == null) {
			roots = CollectionFactory.<XNativeTerm<T>, XPromise<T>> newHashMap();
    	} else {
    		p = roots.get(term);
    	}
    	// [DC] p==null => the term does not exist in the promise graph (yet) 
    	
		if (term instanceof XNativeField<?,?>) {
			// [DC] special case for fields...
			@SuppressWarnings("unchecked")
			XNativeField<T,?> field = (XNativeField<T,?>)term;
			XPromise<T> root =  nfp(field.receiver());
			root.ensureFields();
			Map<Object, XPromise<T>>  map = root.fields(); 
			assert map != null;
			p = map.get(field.field());
			if (p == null) {
				p = new XPromise<T>(term);
				map.put(field.field(), p);
				return p;
			}
		} else if (term instanceof XNativeExpr<?>) {
			if (p == null) {
				p = new XPromise<T>(term);
				roots.put(term, p);
				return p;
			}
		} else if (term instanceof XNativeVar<?> || term instanceof XNativeLit<?,?>) {
			if (p == null) {
				p = intern(term);
			}
		} else {
			throw new Error("Unrecognised term: " + term);
		}
        return p.lookup();			
	}

	
	/**
       Intern this term into constraint and return the promise
       representing the term. 
       
       <p> Throw an XFailure if the resulting constraint is inconsistent.
	 */
	XPromise<T> internIntoConstraint(XNativeTerm<T> term, XPromise<T> last) {
		if (term instanceof XExpr<?>) {
			XNativeExpr<T> expr = (XNativeExpr<T>) term;
	        assert last == null;
	        // Evaluate left == right, if both are literals.
	        XPromise<T> result = nfp(term);
	        if (result != null) return result; // this term has already been interned.
	        Map<Object, XPromise<T>> fields = CollectionFactory.newHashMap();
	        for (int i = 0; i < expr.children().size(); i++) {
	            XNativeTerm<T> arg = expr.children().get(i);
	            XPromise<T> child = intern(arg);
	            fields.put(new Integer(i), child);
	        }
	        // create a new promise and return it.
	        XPromise<T> p = new XPromise<T>(fields, term);
	        addPromise(term, p);
	        result = p;
	        return result;
        } else {
			@SuppressWarnings("unchecked")
			XNativeTerm<T>[] vars = new XNativeTerm[] { term };
			XNativeTerm<T> baseVar = vars[0];
			XPromise<T> p = internBaseVar(baseVar, vars.length == 1, last);
			if (p == null) return null;
			return p.intern(sys(), vars, 1, last);
		}
	}
}
