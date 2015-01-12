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

import x10.constraint.xnative.XAnd;
import x10.constraint.XConstraint;
import x10.constraint.xnative.XDisEquals;
import x10.constraint.xnative.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.XVar;
import x10.constraint.visitors.AddInVisitor;
import x10.constraint.visitors.ConstraintGenerator;
import x10.constraint.visitors.EntailsVisitor;
import x10.constraint.visitors.XGraphVisitor;
import x10.util.CollectionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


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
public class XNativeConstraint implements Cloneable, XConstraint {
    protected Map<XNativeTerm, XPromise> roots;
    protected boolean consistent = true;
    protected boolean valid = true;

    public XNativeConstraint() {}
    
    public Map<XNativeTerm, XPromise> roots() { return roots;}
    
    /**
	 * Return the list of existentially quantified variables in this constraint.
	 * @return
	 */
    public List<XNativeVar> eqvs() {
    	List<XNativeVar> xvars = new LinkedList<XNativeVar>();
    	if (roots==null) return xvars;
    	for (XNativeTerm xt : roots.keySet()) {
    		if (xt.isEQV()) xvars.add((XNativeVar) xt);
    	}
    	return xvars;
    }

    /**
     * Return the set of terms occurring in this constraint.
     * @return
     */
    public Set<XNativeTerm> rootTerms() {
    	return roots == null ? Collections.<XNativeTerm> emptySet() : roots.keySet();
    }
   
  
    /*
    private void addTerm(XNativeTerm term, Set<XVar> result) {
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
    private void addVar(XVar var, Set<XVar> result) {
        if (var == null)
            return;
        result.add(var);
        if (var instanceof XField) {
            addVar(((XField)var).receiver(), result);
        }
    }
    public Set<XVar> vars() {
        List<XNativeTerm> terms = constraints();
        Set <XVar> result = CollectionFactory.newHashSet();
        for (XNativeTerm term : terms) {
           addTerm(term, result);
        }
        return result;   
    }
    */
    /**
     * Copy this constraint. Implemented via a graph traversal.
     */
    public XNativeConstraint copy() {
        XNativeConstraint result = new XNativeConstraint();
	    result.addIn(this);
	    return result;
       // return copyInto(new XNativeConstraint());
    }
    public void addIn(XConstraint c) {
        if (c== null) return;
        if (! c.consistent()) {
            setInconsistent();
            return;
        }
        if (c.valid()) return;
        AddInVisitor v  = new AddInVisitor(true, false, this);
        // hideFake==false to permit the "place checking" to work.
        // This ensures that multiple objects created at the same place
        // e.g. GlobalRef's, are treated as being at the same place by the 
        // type-checker.
        c.visit(v);
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
    public XVar bindingForVar(XVar v) {
    	XPromise p = ((XNativeVar)v).nfp(this);
    	if (p != null && p.term() instanceof XVar && ! p.term().equals(v)) {
    		return (XNativeVar)p.term().nf(this);
    	}
    	return null;
    }
    
    protected XNativeTerm bindingForRootField(XVar root, Object field) {
    	XNativeTerm term = new XNativeField<Object>(root, field);
    	XPromise p = term.nfp(this);
    	if (p == null) return null;
    	return p.term();
    }

	/**
	 * Return the list of atoms (atomic formulas) in this constraint.
	 * @return
	 */
    public List<XNativeFormula<?>> atoms() {
    	List<XNativeFormula<?>> r = new ArrayList<XNativeFormula<?>>(5);
    	if (roots == null) return r;
    	for (XNativeTerm t : roots.keySet()) {
    		if (t instanceof XNativeFormula<?>) {
    			r.add((XNativeFormula<?>)t);
    		}
    	}
    	return r;
    }
    /**
     * Return the set of XVars mentioned in this constraint.
     * @return
     */
    public Set<XNativeVar> vars() {
        Set <XNativeVar> result = CollectionFactory.newHashSet();
        for (XTerm term : constraints()) 
        	addTerm((XNativeTerm)term, result);
        return result;
    }
    
    private void addTerm(XNativeTerm term, Set<XNativeVar> result) {
        if (term==null) return;
        if (term instanceof XNativeFormula) {
            XNativeFormula<?> form = (XNativeFormula<?>) term;
            for (XNativeTerm arg : form.arguments()) addTerm(arg, result);
            return;
        } 
        if (term instanceof XVar) addVar((XNativeVar) term, result);
    }
    private void addVar(XNativeVar var, Set<XNativeVar> result) {
        if (var == null) return;
        result.add(var);
        if (var instanceof XField) addVar(((XNativeField<?>)var).receiver, result);
    }
   
    /**
	 * Is the constraint consistent? That is, does it have a solution?
	 * Implementation Note: In the body of XNativeConstraint, we build in that consistent()
	 * returns the field consistent.
	 * @return true iff the constraint is consistent.
     */
    public boolean consistent() { return consistent; }

    /** Is the constraint valid? i.e. is it satisfied by every solution?
     * 
     */
    public boolean valid() { return consistent && valid;}

    private boolean flatten(boolean isEq, XNativeTerm left, XNativeTerm right)  {

        // handle several special cases:
        // (X==Y)==true     ---> X==Y
        // (X!=Y)==true     ---> X!=Y
        // (X==Y)==false    ---> X!=Y
        // (X!=Y)==false    ---> X==Y
        XNativeTerm boolLit = left.isBoolean() ? left : right.isBoolean() ? right : null;
        if (boolLit!=null) {
            XNativeTerm other = boolLit==left ? right : left;
            boolean isEquals = other instanceof XEquals;
            boolean isDisEquals = other instanceof XDisEquals;
            if (isEquals || isDisEquals) {
                XNativeFormula<?> formula = (XNativeFormula<?>) other;
                XNativeTerm[] args = formula.arguments();
                assert args.length==2;
                left = args[0];
                right = args[1];
                boolean isLitTrue = boolLit==XNativeLit.TRUE;
                if (isLitTrue==isEquals) {}           // ok
                else isEq = !isEq;
                if (isEq) addBinding(left, right);
                else  addDisBinding(left, right);
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
    public void addBinding(XTerm l, XTerm r)  {
    	assert l != null;
        assert r != null;
        XNativeTerm left = (XNativeTerm) l;
        XNativeTerm right = (XNativeTerm) r;
        
        if (flatten(true,left, right)) return;
        if (!consistent) return;
        if (roots == null) roots = CollectionFactory.<XNativeTerm, XPromise> newHashMap();

        XPromise p1 = intern(left);
        if (p1==null) { setInconsistent(); return;}
        XPromise p2 = intern(right);
        if (p2 == null) { setInconsistent();return;}
        try {
            valid &= ! p1.bind(p2, this);
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
    public void addDisBinding(XTerm l, XTerm r)  {
    	assert l != null;
        assert r != null;
        XNativeTerm left = (XNativeTerm) l;
        XNativeTerm right = (XNativeTerm) r;

        if (flatten(false,left, right)) return;
    	if (! consistent) return;
    	if (roots == null) roots = CollectionFactory.<XNativeTerm,XPromise> newHashMap();
    	XPromise p1 = intern(left);
    	if (p1 == null)    {setInconsistent();return;}
    	XPromise p2 = intern(right);
    	if (p2 == null)    {setInconsistent();return;}
    	if (p1.equals(p2)) {setInconsistent();return;}
    	try {
    	    valid &= ! p1.disBind(p2);
    	    if (left instanceof XField<?> && right instanceof XField<?>) {
    	    	XField<?> lf = (XField<?>) left, 
    	    			  rf = (XField<?>) right;
    	    	if (lf.field()==rf.field()) addDisBinding(lf.receiver(), rf.receiver()); 	    	
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
    public void addAtom(XTerm term) throws XFailure {
        if (!consistent) return;
        valid = false;
        if (roots == null) roots = CollectionFactory.<XNativeTerm,XPromise> newHashMap();
        XNativeTerm t = (XNativeTerm) term;
        XPromise p = t.nfp(this);
        if (p != null) return;           // nothing to do    
        p = intern(t);
    }
    /**
	 * Does this entail constraint other?
	 * 
	 * @param t
	 * @return
	 */
    public boolean entails(XConstraint other)  {
    	if (!consistent) return true;
        if (other == null || other.valid()) return true;
        EntailsVisitor ev = new EntailsVisitor(true, false, this);
        other.visit(ev);
        return ev.result();
    }
    
    public void setInconsistent() { this.consistent = false; }  
   

    public XConstraint leastUpperBound(XConstraint other) {
        if (! consistent)         return other;
        if (! other.consistent()) return this;
        if (valid)                return this;
        if (other.valid())        return other;
       	XNativeConstraint result = new XNativeConstraint();
       	for (XTerm term : ((XNativeConstraint)other).constraints()) {
       		try {
       			if (entails(term)) result.addTerm(term);
       		} catch (XFailure z) {
       		    result.setInconsistent();
       		}
       	}
       	return result;
       }
    

    public XConstraint residue(XConstraint other) {
        assert other.consistent();
        XNativeConstraint result = new XNativeConstraint();
        if (! consistent) return result;
        for (XTerm term : ((XNativeConstraint)other).constraints()) {
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
    public List<XTerm> constraints() {
        if (roots == null) return new ArrayList<XTerm>(0);
        ConstraintGenerator cg = new ConstraintGenerator(true, false);
        visit(cg);
        return cg.result();
    }
    

    public void visit( XGraphVisitor xg) {
        if (roots == null) return;
        Collection<XPromise> values = roots.values();
        for (XPromise p : values) {
        	boolean result = p.visit(null, xg, this);
            if (! result ) return;
        }
    }
    
   
    /**
     * Return a list of bindings t1-> t2 equivalent to 
     * the current constraint except that equalities involving EQV variables 
     * are ignored.
     * 
     * @return
     */
    public List<XTerm> extConstraints() {
        ConstraintGenerator cg = new ConstraintGenerator(true, false);
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
    public List<XTerm> extConstraintsHideFake() {
        ConstraintGenerator cg = new ConstraintGenerator(true, true);
        visit(cg);
        return cg.result();
    }

	/**
	 * Does this entail a != b?
	 * @param a
	 * @param b
	 * @return this |- a != b
	 */
    public boolean disEntails(XTerm term1, XTerm term2)  {
    	XNativeTerm t1 = (XNativeTerm)term1;
    	XNativeTerm t2 = (XNativeTerm)term2;
    	
    	if (! consistent) return true;
    	XPromise p1 = t1.nfp(this);
    	if (p1 == null) return false; // this constraint knows nothing about t1.
    	XPromise p2 = t2.nfp(this);
    	if (p2 == null) return false;
    	if (p1.isDisBoundTo(p2)) return true;
    	Map<Object, XPromise> p1f = p1.fields();
    	if (p1f !=null) {
    		Map<Object, XPromise> p2f = p2.fields();
    		if (p2f != null) 
    			for (Map.Entry<Object, XPromise> me : p1f.entrySet()) {
    				Object field = me.getKey();
    				XPromise r1 = me.getValue().lookup();
    				XPromise r2 = p2f.get(field).lookup();
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
    public boolean entails(XTerm term1, XTerm term2)  {
    	XNativeTerm t1 = (XNativeTerm)term1;
    	XNativeTerm t2 = (XNativeTerm)term2;

        if (!consistent) return true;
        XPromise p1 = t1.nfp(this);
        XPromise p2 = t2.nfp(this);
        return p1 == p2 || p1.term().equals(p2.term());
    }
    
	/**
	 * Does this entail c, and c entail this? 
	 * 
	 * @param other -- null is treated as a valid constraint
	 * @return
	 */
    public boolean equiv(XNativeConstraint other) throws XFailure {
        boolean result = entails(other);
        if (result) result =  other == null ? valid : other.entails(this);
        return result;
    }

    /** Return true if this constraint entails t. */
    public boolean entails(XTerm t) {
        if (t instanceof XEquals) {
            XEquals f = (XEquals) t;
            XNativeTerm left = f.left();
            XNativeTerm right = f.right();
            
            if (entails(left, right)) return true;
            if (right instanceof XEquals) {
            	XEquals r = (XEquals) right;
            	if (entails(r.left(), r.right())) 
            		return entails(left, XNativeLit.TRUE);
            	if (disEntails(r.left(), r.right())) {
            		return entails(left, XNativeLit.FALSE);
            	}
            }
            if (right instanceof XDisEquals) {
            	XDisEquals r = (XDisEquals) right;
            	if (entails(r.left(), r.right())) 
            		return entails(left, XNativeLit.FALSE);
            	if (disEntails(r.left(), r.right())) 
            		return entails(left, XNativeLit.TRUE);
            }
            
        } else if (t instanceof XDisEquals) {
            XDisEquals f = (XDisEquals) t;
            XNativeTerm left = f.left();
            XNativeTerm right = f.right();
            if (disEntails(left, right)) return true;
        }
        else if (t instanceof XNativeFormula) {
        	XNativeFormula<?> f = (XNativeFormula<?>) t;
        	Object op = f.operator();
        	XNativeTerm[] args = f.arguments();
        	int n = args.length;
        	for (XFormula<?> x : atoms()) {
        		if (x.operator().equals(op)) {
        			XTerm[] xargs = x.arguments();
        			if (n!= xargs.length) continue;
        			int i=0;
        			while(i < n && entails(args[i], xargs[i])) i++;
        			if (i==n) return true;
        		}
        	}
        	return false;
        }
        return false;
    }


    public String toString() {
        XNativeConstraint c = this;
        if (! c.consistent) return "{inconsistent}";
        String str ="{";
        final boolean exists_toString = false;
        if (exists_toString) {
            List<XNativeVar> eqvs = eqvs();
            if (!eqvs.isEmpty()) {
                String temp = eqvs.toString();
                str = "exists " + temp.substring(1, temp.length() - 1) + ".";
            }
            String constr = c.constraints().toString();
            str += constr.substring(1, constr.length() - 1);
        }
        else {
        	
        	List<XTerm> l = c.extConstraintsHideFake();
        	for (XTerm t : l) {
        		str += t.toString() + ", " ;
        	}
       		str = str.length() >= 2? str.substring(0, str.length() - 2) : str;
        }
        return str + "}";
    }

    
    /**
	 * Perform substitute y for x for every binding x -> y in bindings.
	 * 
	 */
    public XNativeConstraint substitute(Map<XNativeVar, XNativeTerm> subs) throws XFailure {
        XNativeConstraint c = this;
        for (Map.Entry<XNativeVar,XNativeTerm> e : subs.entrySet()) {
            XNativeVar x = e.getKey();
            XNativeTerm y = e.getValue();
            c = c.substitute(y, x);            
        }
        return c;
    }
    
    /**
	 * If y equals x, or x does not occur in this, return this, else copy
	 * the constraint and return it after performing applySubstitution(y,x).
	 * 
	 * 
	 */
    public XNativeConstraint substitute(XNativeTerm y, XNativeVar x) throws XFailure {
        return substitute(new XNativeTerm[] { y }, new XNativeVar[] { x });
    }
     public XNativeConstraint substitute(XNativeTerm[] ys, XNativeVar[] xs, boolean propagate) throws XFailure {
    	return substitute(ys, xs);
    }
    
	/**
	 * xs and ys must be of the same length. Perform substitute(ys[i],
	 * xs[i]) for each i < xs.length.
	 */

    public XNativeConstraint substitute(XNativeTerm[] ys, XNativeVar[] xs) throws XFailure {
    	assert (ys != null && xs != null);
    	assert xs.length == ys.length;
    	
    	boolean eq = true;
		for (int i = 0; i < ys.length; i++) {
			XNativeTerm y = ys[i];
			XNativeVar x = xs[i];

			if (! y.equals(x)) eq = false;
		}
		if (eq) return this;
    	if (! consistent) return this;
    	
    	// Don't do the quick occurrence check; x might occur in a self constraint.
    	//		XPromise last = lookupPartialOk(x);
    	//		if (last == null) return this; 	// x does not occur in this
    	
    	XNativeConstraint result = new XNativeConstraint();
    	
    	for (XTerm term : constraints()) {
    		XTerm t = term;
    		
    		// if term is y==x.f, the subst will produce y==y.f, which is a cycle--bad!
    		//		    if (term instanceof XEquals_c) {
    		//		        XEquals_c eq = (XEquals_c) term;
    		//		        XNativeTerm l = eq.left();
    		//		        XNativeTerm r = eq.right();
    		//		        if (y.equals(l) || y.equals(r))
    		//		            continue;
    		//		    }
    		for (int i = 0; i < ys.length; i++) {
    			XTerm y = ys[i];
    			XNativeVar x = xs[i];
    			t = t.subst(y, x);
    		}
    		
    		// t = t.subst(result.self(), self(), true);

    		try {
    			result.addTerm(t);
    		} catch (XFailure z) { throw z;}
    	}
    	//		XNativeConstraint_c result = clone();
    	//		result.valid = true;
    	//		result.applySubstitution(y,x);
    	return result;
    }

    /**
	 * Does this constraint contain occurrences of the variable v?
	 * 
	 * @param v
	 * @return true iff v is a root variable of this.
	 */
    public boolean hasVar(XVar v) { return roots != null && roots.keySet().contains(v);}

  
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
    public void addTerm(XTerm term) throws XFailure {
        if (term.isAtomicFormula())    addAtom(term);
        else if (term instanceof XNativeVar) 
        	addBinding(term, XNativeLit.TRUE);
        /*else if (term instanceof XNot) {
            XNot t = (XNot) term;
            if (t.unaryArg() instanceof XVar)
                addBinding(t.unaryArg(), XNativeTerms.FALSE);
            if (t.unaryArg() instanceof XNot)
                addTerm(((XNot) t.unaryArg()).unaryArg());
        }*/
        else if (term instanceof XAnd) {
            XAnd t = (XAnd) term;
            addTerm(t.left());
            addTerm(t.right());
        }
        else if (term instanceof XEquals) {
            XEquals eq = (XEquals) term;
            XNativeTerm left = eq.left();
            XNativeTerm right = eq.right();
            addBinding(left, right);
        } else if (term instanceof XDisEquals) {
        	XDisEquals dq = (XDisEquals) term;
        	   XNativeTerm left = dq.left();
               XNativeTerm right = dq.right();
               addDisBinding(left, right);
        }
        else {
            throw new XFailure("Unexpected term |" + term + "|");
        }
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

    XPromise intern(XNativeTerm term)  { return intern(term, null);}
    
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
    XPromise intern(XNativeTerm term, XPromise last)  {
    	assert term != null;
        return term.internIntoConstraint(this, last);
    }

    XPromise internBaseVar(XNativeVar baseVar, boolean replaceP, XPromise last)  {
        if (roots == null) roots = CollectionFactory.<XNativeTerm,XPromise> newHashMap();
        XPromise p =  roots.get(baseVar);
        if (p == null) {
            p = (replaceP && last != null) ? last : new XPromise(baseVar);
            roots.put(baseVar, p);
        }
        return p;
    }
    
    void addPromise(XNativeTerm p, XPromise node) {
        if (roots == null) roots = CollectionFactory.<XNativeTerm,XPromise> newHashMap();
        roots.put(p, node);
    }
    public Set<XNativeTerm> getTerms() {
    	if (roots == null)
    		return new HashSet<XNativeTerm>();
    	return roots.keySet();
    }
}
