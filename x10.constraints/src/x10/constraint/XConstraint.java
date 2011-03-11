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

package x10.constraint;

import x10.util.CollectionFactory;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


/**
 * 
 *  A constraint solver for the following constraint system. Note terms in this 
 *  constraint system are untyped.
 * <verbatim>
 * t ::= x                  -- variable
 *       | t.f              -- field access
 *       | g(t1,..., tn)    -- uninterpreted function symbol
 *       
 * c ::= t == t             -- equality
 *       | t != t           -- dis-equality
 *       | c,c              -- conjunction
 *       | p(t1,..., tn)    -- atomic formula
 * </verbatim>  
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
 * <p>Terms are created using the static API in XTerms. The <code>==</code> 
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
public class XConstraint implements Cloneable {
    protected Map<XTerm, XPromise> roots;
    protected boolean consistent = true;
    protected boolean valid = true;

    public XConstraint() {}
    
    public Map<XTerm, XPromise> roots() {
        return roots;
    }
    
    /**
	 * Return the list of existentially quantified variables in this constraint.
	 * @return
	 */
    public List<XVar> eqvs() {
    	List<XVar> xvars = new LinkedList<XVar>();
    	if (roots==null) return xvars;
    	for (XTerm xt : roots.keySet()) {
    		if (xt.isEQV())
    			xvars.add((XVar) xt);
    	}
    	return xvars;
    }

    /**
     * Return the set of terms occurring in this constraint.
     * @return
     */
    public Set<XTerm> rootTerms() {
    	return roots == null ? Collections.<XTerm> emptySet() : roots.keySet();
    }
   
  
    /*
    private void addTerm(XTerm term, Set<XVar> result) {
        if (term==null)
            return;
        if (term instanceof XFormula) {
            XFormula form = (XFormula) term;
            for (XTerm arg : form.arguments())
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
        List<XTerm> terms = constraints();
        Set <XVar> result = CollectionFactory.newHashSet();
        for (XTerm term : terms) {
           addTerm(term, result);
        }
        return result;   
    }
    */
    /**
     * Copy this constraint. Implemented via a graph traversal.
     */
    public XConstraint copy() {
        return copyInto(new XConstraint());
    }
    
    /**
     * Return the result of copying this into c.  
     * @param c
     * @return
     */
    protected <T extends XConstraint> T copyInto(T c)  {
        c.consistent = consistent;
        c.valid = valid;
        if (roots !=null) {
            c.roots = CollectionFactory.<XTerm, XPromise>newHashMap(roots.size());
            Map<XPromise, XPromise> redirects =  CollectionFactory.<XPromise, XPromise>newHashMap(roots.size());
            for (Map.Entry<XTerm, XPromise> entry : roots.entrySet()) {
                XTerm xt = entry.getKey();
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

    

    
	/**
	 * Return the term v is bound to in this constraint, and null
	 * if there is no such term. This term will be distinct from v.
	 * */
    public XVar bindingForVar(XVar v) {
    	XPromise p = lookup(v);
    	if (p != null && p.term() instanceof XVar && ! p.term().equals(v)) {
    		return (XVar) p.term();
    	}
    	return null;
    }
    
    protected XTerm bindingForRootField(XVar root, Object o) {
    	 if (!consistent || roots == null)
             return null;
         XPromise self = (XPromise) roots.get(root);
         if (self == null)
             return null;
         XPromise result = self.lookup(o);
         return result == null ? null : result.term();
    }

	/**
	 * Return the list of atoms (atomic formulas) in this constraint.
	 * @return
	 */
    public List<XFormula<?>> atoms() {
    	List<XFormula<?>> r = new LinkedList<XFormula<?>>();
    	if (roots == null)
    		return r;
    		
    	for (XTerm t : roots.keySet()) {
    		if (t instanceof XFormula<?>) {
    			r.add((XFormula<?>) t);
    		}
    	}
    	return r;
    }
    /**
     * Return the set of XVars mentioned in this constraint.
     * @return
     */
    public Set<XVar> vars() {
        List<XTerm> terms = constraints();
        Set <XVar> result = CollectionFactory.newHashSet();
        for (XTerm term : terms) {
           addTerm(term, result);
        }
        return result;   
    }
    private void addTerm(XTerm term, Set<XVar> result) {
        if (term==null)
            return;
        if (term instanceof XFormula) {
            XFormula<?> form = (XFormula<?>) term;
            for (XTerm arg : form.arguments())
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
   
    /**
	 * Is the constraint consistent? That is, does it have a solution?
	 * Implementation Note: In the body of XConstraint, we build in that consistent()
	 * returns the field consistent.
	 * @return true iff the constraint is consistent.
     */
    public boolean consistent() {
        return consistent;
    }

    /** Is the constraint valid? i.e. is it satisfied by every solution?
     * 
     */
    public boolean valid() { 	
        return consistent && valid;
    }

    /**
     * Add t1=t2 to the constraint, unless it is inconsistent. 
     * Note: constraint is modified in place.
     * @param var -- t1
     * @param val -- t2
     */
    public void addBinding(XTerm left, XTerm right)  {
    	assert left != null;
        assert right != null;

        if (!consistent)
            return;
        if (roots == null)
            roots = CollectionFactory.<XTerm, XPromise> newHashMap();

        XPromise p1 = intern(left);
        if (p1==null) {
            setInconsistent();
            return;
        }
        XPromise p2 = intern(right);
        if (p2 == null) {
            setInconsistent();
            return;
        }
        try {
            valid &= ! p1.bind(p2);
        } catch (XFailure z) {
            setInconsistent();
        }
       
    }

    /**
	 * Add t1 != t2 to the constraint.
	 * Note: Constraint is updated in place.
	 * @param var
	 * @param t
	 */
    public void addDisBinding(XTerm left, XTerm right)  {
    	assert left != null;
    	assert right !=null;
    	if (! consistent)
    		return;
    	if (roots == null)
    		roots = CollectionFactory.<XTerm,XPromise> newHashMap();
    	XPromise p1 = intern(left);
    	if (p1 == null) {
    	    setInconsistent();
    	    return;
    	}
    	XPromise p2 = intern(right);
    	if (p2 == null) {
    	    setInconsistent();
    	    return;
    	}
    	if (p1.equals(p2)) {
    		setInconsistent();
    		return;
    	}
    	try {
    	    valid &= ! p1.disBind(p2);
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
    public void addAtom(XTerm t) throws XFailure {
        if (!consistent)
            return;
        valid = false;
        if (roots == null)
            roots = CollectionFactory.<XTerm,XPromise> newHashMap();
        XPromise p = lookup(t);
        
        if (p != null)
            // nothing to do
            return;
        
        p = intern(t);
    }
    /**
	 * Does this entail constraint other?
	 * 
	 * @param t
	 * @return
	 */
    public boolean entails(XConstraint other)  {
   //   boolean oldEntails=oldEntails(other);
      boolean newEntails=newEntails(other);
    /*  if (oldEntails != newEntails) {
          System.out.println("Constraint mismatch: a " 
                             + (oldEntails ? "does not now entail " : "now entails ") 
                             + "b."
                             + "\n\t a: " + this
                             + "\n\t b: " + other);
      }*/
      return newEntails;
    }
    static class EntailsVisitor implements XGraphVisitor{
        XConstraint c1;
        boolean result=true;
        EntailsVisitor(XConstraint c1) {
            this.c1=c1;
        }
        public boolean visitAtomicFormula(XTerm t) {
            result &= c1.entails(t);
            return result;
        }
        public boolean visitEquals(XTerm t1, XTerm t2) {
            result &= c1.entails(t1, t2);
            return result;
        }
        public boolean visitDisEquals(XTerm t1, XTerm t2) {
            result &= c1.disEntails(t1, t2);
            return result;
        }
        public boolean result() {
            return result;
        }
    }
    public boolean newEntails(XConstraint other)  {
        if (!consistent)
            return true;
        if (other == null || other.valid())
            return true;
        EntailsVisitor ev = new EntailsVisitor(this);
        other.visit(false,false, ev);
        return ev.result();
    }
    
    public void setInconsistent() {
        this.consistent = false;
    }  
   
    /**
	 * Return the least upper bound of this and other. That is, the resulting 
	 * constraint has precisely the constraints entailed by both this and other.
	 * (Note: An inconsistent constraint entails every constraint, and 
	 * a valid constraint entails only those constraints such as x=x that 
	 * every constraint entails.)
	 * @param other
	 * @return
	 */
    public XConstraint leastUpperBound(XConstraint other) {
        if (! consistent)
            return other;
        if (! other.consistent())
            return this;
        if (valid) 
            return this;
        if (other.valid())
            return other;
       	XConstraint result = new XConstraint();
       	for (XTerm term : other.constraints()) {
       		try {
       			if (entails(term)) {
       				result.addTerm(term);
       			}
       		} catch (XFailure z) {
       		    result.setInconsistent();
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
    public XConstraint residue(XConstraint other) {
        assert other.consistent();
        XConstraint result = new XConstraint();
        if (! consistent)
            return result;

        for (XTerm term : other.constraints()) {
            try {
                if (! entails(term)) {
                    result.addTerm(term);
                }
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
        return constraints(new ArrayList<XTerm>());
    }  
    
    /**
	 * Return a list of bindings t1-> t2 equivalent to the current
	 * constraint, added to result.
	 * 
	 * @return
	 */
    protected List<XTerm> constraints(List<XTerm> result) {
        if (roots == null)
            return new ArrayList<XTerm>(0);
        ConstraintGenerator cg = new ConstraintGenerator();
        visit(true, false, cg);
        return cg.result();
    }
    

	/**
	 * Return a list of bindings t1-> t2 equivalent to the current
	 * constraint except that equalities involving EQV variables are ignored.
	 * 
	 * @return
	 */

    protected void visit(boolean dumpEQV, boolean hideFake, XGraphVisitor xg) {
        if (roots == null)
            return;
        for (XPromise p : roots.values()) {
            if (! p.visit(null, dumpEQV, hideFake, xg))
                return;
        }
    }
    
    public static final class ConstraintGenerator implements XGraphVisitor {
        public List<XTerm> result = new ArrayList<XTerm>(5);
        public boolean visitAtomicFormula(XTerm t) {
            result.add(t);
            return true;
        }
        public boolean visitEquals(XTerm t1, XTerm t2) {
            result.add( XTerms.makeEquals(t1, t2));
            return true;
        }
        public boolean visitDisEquals(XTerm t1, XTerm t2) {
            result.add(XTerms.makeDisEquals(t1, t2));
            return true;
        }
        public List<XTerm> result() {
            return result;
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
        ConstraintGenerator cg = new ConstraintGenerator();
        visit(false, false, cg);
        return cg.result();
    }
    public List<XTerm> extConstraintsHideFake() {
        ConstraintGenerator cg = new ConstraintGenerator();
        visit(false, true, cg);
        return cg.result();
    }

	/**
	 * Does this entail a != b?
	 * @param a
	 * @param b
	 * @return this |- a != b
	 */
    public boolean disEntails(XTerm t1, XTerm t2)  {
    	if (! consistent) return true;
    	XPromise p1 = lookup(t1);
    	if (p1 == null) // this constraint knows nothing about t1.
    		return false;
    	XPromise p2 = lookup(t2);
    	if (p2 == null)
    		return false;
    	return p1.isDisBoundTo(p2);
    	
    }
  
	/**
	 * Does this entail a==b? 
	 * @param a
	 * @param b
	 * @return true iff this |- a==b
	 */
    public boolean entails(XTerm t1, XTerm t2)  {
        if (!consistent)
            return true;
        if (t1.isEQV() || t2.isEQV())
        	return true;
        XPromise p1 = lookupPartialOk(t1);
        if (p1 == null) // No match, the term t1 is not equated to anything by this.
            return false;

        int r1Count = 0;
        XVar[] vars1 = null;
        if (p1 instanceof XPromise_c) {
        	if (t1 instanceof XVar) {
        		r1Count = ((XPromise_c) p1).lookupReturnValue();
        		vars1 = ((XVar) t1).vars();
        	}
        }

        XPromise p2 = lookupPartialOk(t2);
        if (p2 == null) // No match, the term t2 is not equated to anything by this.
        	return false;

        int r2Count = 0;
        XVar[] vars2 = null;
        if (p2 instanceof XPromise_c) {
        	if (t2 instanceof XVar) {
        		r2Count = ((XPromise_c) p2).lookupReturnValue();
        		/* if (! (t2 instanceof XVar)) {
            	assert false: "Internal Error:" + t2 + "expected to be an XVar.";
            }*/
        		vars2 = ((XVar) t2).vars();
        	}
        }

        if ((!(t1 instanceof XVar) || (r1Count == 0 || r1Count == vars1.length))
        		&& (! (t1 instanceof XVar) || (r2Count == 0 || r2Count == vars2.length))) {
        		
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
    
	/**
	 * Does this entail c, and c entail this? 
	 * 
	 * @param other -- null is treated as a valid constraint
	 * @return
	 */
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

    /** Return true if this constraint entails t. */
    public boolean entails(XTerm t) {
        if (t instanceof XEquals) {
            XEquals f = (XEquals) t;
            XTerm left = f.left();
            XTerm right = f.right();
            
            if (entails(left, right)) {
                return true;
            }
            if (right instanceof XEquals) {
            	XEquals r = (XEquals) right;
            	if (entails(r.left(), r.right())) {
            		return entails(left, XTerms.TRUE);
            	}
            	if (disEntails(r.left(), r.right())) {
            		return entails(left, XTerms.FALSE);
            	}
            }
            if (right instanceof XDisEquals) {
            	XDisEquals r = (XDisEquals) right;
            	if (entails(r.left(), r.right())) {
            		return entails(left, XTerms.FALSE);
            	}
            	if (disEntails(r.left(), r.right())) {
            		return entails(left, XTerms.TRUE);
            	}
            }
            
        } else if (t instanceof XDisEquals) {
            XDisEquals f = (XDisEquals) t;
            XTerm left = f.left();
            XTerm right = f.right();
            
            if (disEntails(left, right)) {
                return true;
            }
        }
        else if (t instanceof XFormula) {
        	XFormula f = (XFormula) t;
        	Object op = f.operator();
        	List<XTerm> args = f.arguments();
        	int n = args.size();
        	for (XFormula x : atoms()) {
        		if (x.operator().equals(op)) {
        			List<XTerm> xargs = x.arguments();
        			if (n!= xargs.size())
        				continue;
        			int i=0;
        			while(i < n && entails(args.get(i), xargs.get(i))) i++;
        			if (i==n) return true;
        		}
        	}
        	return false;
        }

        return false;
    }
 
    
   // private static boolean printEQV = true;

    public String toString() {
        XConstraint c = this;
        
        if (! c.consistent) {
            return "{inconsistent}";
        }
        
     /*  try {
           // c = c.substitute(c.genEQV(XTerms.makeName("self"), false), c.self());
        }
        catch (XFailure z) {
            return "{inconsistent}";
        }*/

        String str ="";

        final boolean exists_toString = false;
        if (exists_toString) {
            List<XVar> eqvs = eqvs();
            if (!eqvs.isEmpty()) {
                String temp = eqvs.toString();
                str = "exists " + temp.substring(1, temp.length() - 1) + ".";
            }
            String constr = c.constraints().toString();
            str += constr.substring(1, constr.length() - 1);
        }
        else {
            String constr = c.extConstraintsHideFake().toString();
            str += constr.substring(1, constr.length() - 1);
        }
        
        return "{" + str + "}";
    }

    
    /**
	 * Perform substitute y for x for every binding x -> y in bindings.
	 * 
	 */
    public XConstraint substitute(Map<XVar, XTerm> subs) throws XFailure {
        XConstraint c = this;
        for (Map.Entry<XVar,XTerm> e : subs.entrySet()) {
            XVar x = e.getKey();
            XTerm y = e.getValue();
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
    public XConstraint substitute(XTerm y, XVar x) throws XFailure {
        return substitute(new XTerm[] { y }, new XVar[] { x });
    }
     public XConstraint substitute(XTerm[] ys, XVar[] xs, boolean propagate) throws XFailure {
    	return substitute(ys, xs);
    }
    
	/**
	 * xs and ys must be of the same length. Perform substitute(ys[i],
	 * xs[i]) for each i < xs.length.
	 */

    public XConstraint substitute(XTerm[] ys, XVar[] xs) throws XFailure {
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
    	
    	XConstraint result = new XConstraint();
    	
    	for (XTerm term : constraints()) {
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
    		
    		// t = t.subst(result.self(), self(), true);

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

    /**
	 * Does this constraint contain occurrences of the variable v?
	 * 
	 * @param v
	 * @return true iff v is a root variable of this.
	 */
    public boolean hasVar(XVar v) {
        return roots != null && roots.keySet().contains(v);
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
    public void addTerm(XTerm term) throws XFailure {
        if (term.isAtomicFormula()) {
            addAtom(term);
        }
        else if (term instanceof XVar) {
            addBinding(term, XTerms.TRUE);
        }
        /*else if (term instanceof XNot) {
            XNot t = (XNot) term;
            if (t.unaryArg() instanceof XVar)
                addBinding(t.unaryArg(), XTerms.FALSE);
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
            XTerm left = eq.left();
            XTerm right = eq.right();
            addBinding(left, right);
        } else if (term instanceof XDisEquals) {
        	XDisEquals dq = (XDisEquals) term;
        	   XTerm left = dq.left();
               XTerm right = dq.right();
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

    XPromise intern(XTerm term)  {
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
    XPromise intern(XTerm term, XPromise last)  {
    	assert term != null;
        if (term instanceof XPromise) {
            XPromise q = (XPromise) term;
            
            // this is the case for literals, for here
            if (last != null) {
                try {
                    last.bind(q);
                }
                catch (XFailure f) {
                    return null;
                }
            }
            return q;
        }

        // let the term figure out what to do for itself.
      
        return term.internIntoConstraint(this, last);
    }

    XPromise internBaseVar(XVar baseVar, boolean replaceP, XPromise last)  {
        if (roots == null)
            roots = CollectionFactory.<XTerm,XPromise> newHashMap();
        XPromise p = (XPromise) roots.get(baseVar);
        if (p == null) {
            p = (replaceP && last != null) ? last : new XPromise_c(baseVar);
            roots.put(baseVar, p);
        }
        return p;
    }
    
    void addPromise(XTerm p, XPromise node) {
        if (roots == null)
            roots = CollectionFactory.<XTerm,XPromise> newHashMap();
        roots.put(p, node);
    }

   //  void internRecursively(XVar v) throws XFailure {
   //     intern(v);
   // }
    
     /**
 	 * Look this term up in the constraint graph. Return null if the term
 	 * does not exist. Does not create new nodes in the constraint graph.
 	 * Does not return a forwarded promise (looks it up recursively, instead).
 	 * 
 	 * @param term
 	 * @return the terminal promise this term is associated with (if any), null otherwise
 	 */
    XPromise lookup(XTerm term) {
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
        if (term instanceof XFormula)
        	return result;
        return null;
    }
    
	/**
	 * Look this term up in the constraint graph. If the term is of the form
	 * x.f1...fk and the longest prefix that exists in the graph is
	 * x.f1..fi, return the promise corresponding to x.f1...fi. If the
	 * promise is a Promise_c, the caller must invoke lookupReturnValue() to
	 * determine if the match was partial (value returned is not equal to
	 * the length of term.vars()). If not even a partial match is found, or
	 * the partial match terminates in a literal (which, by definition,
	 * cannot have fields), then return null.
	 * 
	 * @seeAlso lookup(C_term term)
	 * @param term
	 * @return
	 * @throws XFailure
	 */
    XPromise lookupPartialOk(XTerm term) {
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
        
        {
        	XPromise p = roots.get(term);
        	if (p != null)
        		return p;
        }
        
        return null;
    }
    
    /*
    @SuppressWarnings("unchecked") // Casting to a generic type
    private Map<XTerm, XPromise> cloneRoots() {
        return ((Map<XTerm,XPromise>) ((SmallMap<XTerm,XPromise>)roots).clone());
    }
	
    static <T> XTerm makeField(XTerm target, XField<T> field) {
        XTerm t;
        if (target instanceof XVar) {
            t = field.copy((XVar) target); 
        }
        else {
            t = XTerms.makeAtom(field.field(), target);
        }
        return t;
    }
*/
}
