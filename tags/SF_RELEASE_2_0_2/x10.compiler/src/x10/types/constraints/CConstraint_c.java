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
import java.util.List;
import java.util.Map;

import polyglot.ast.Field;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;


import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XDisEquals;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XFormula;
import x10.constraint.XName;
import x10.constraint.XPromise;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10Context;
import x10.types.X10TypeSystem;

/**
 * The compiler's notion of a constraint keeps track of this and self variables.
 * 
 * @author vj
 *
 */
public class CConstraint_c extends XConstraint_c implements CConstraint {

	  /** Variable to use for self in the constraint. */
    XRoot self;

    XVar thisVar;
	/**
	 * 
	 */
	public CConstraint_c() {
		 self = XTerms.makeUQV(CConstraint.SELF_VAR_PREFIX);
	}
	
	 public XRoot self() {
	        return self;
	    }

	 public XVar selfVarBinding() {
		 return  bindingForVar(self());
	 }
	    public XVar thisVar() {
	    	return thisVar;
	    }
	
	  /**
     * Copy this constraint logically; that is, create a new constraint
     * that contains the same equalities (if any) as the current one.
     * vj: 08/12/09
     * Copying also the consistency, and validity status, and thisVar and self.
     */
    public CConstraint_c copy() {
        CConstraint_c c = new CConstraint_c();
        try {
        	c.thisVar = thisVar();
        	c.self = self();
            return (CConstraint_c) copyInto(c);
        }
        catch (XFailure f) {
            c.setInconsistent();
            return c;
        }
    }
    /** Add in a constraint, substituting this.self for c.self */
    public CConstraint addIn(CConstraint c)  throws XFailure {
    	return addIn(self(), c);
    }
    /** Add in a constraint, with given term for self. */
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
     * Return the result of copying this into c. Assume that c will be 
     * the depclause of the same base type as this, hence it is ok to 
     * copy self-clauses as is. 
     * @param c
     * @return
     */
    protected CConstraint_c copyInto(CConstraint_c c) throws XFailure {
        c.addIn(this);
        return c;
    }
    
    public void addSelfBinding(XTerm var) throws XFailure {
        addBinding(self(), var);
    }
    public void addSelfBinding(XConstrainedTerm var) throws XFailure {
        addBinding(self(), var);
    }

    public void addThisBinding(XTerm term) throws XFailure {
    	addBinding(thisVar(), term);
    }
    
    public void setThisVar(XVar var) {
    	if (var == null) return;
    	thisVar = var;
    }
    
    public void addBinding(XTerm s, XConstrainedTerm t) throws XFailure {
    	addBinding(s, t.term());
    	addIn(s, t.constraint());
    	
    }
    public void addBinding(XConstrainedTerm s, XTerm t) throws XFailure {
    	addBinding(t,s);
    }
    public void addBinding(XConstrainedTerm s, XConstrainedTerm t) throws XFailure {
    	addBinding(s.term(), t.term());
    	addIn(s.term(), s.constraint());
    	addIn(t.term(), t.constraint());
    }
    public CConstraint substitute(XTerm y, XRoot x) throws XFailure {
        return substitute(new XTerm[] { y }, new XRoot[] { x });
    }
    public CConstraint substitute(XTerm[] ys, XRoot[] xs, boolean propagate) throws XFailure {
    	return substitute(ys, xs);
    }
    public CConstraint instantiateSelf(XTerm newSelf) {
    	try {
    		return substitute(newSelf, self());
    	} catch (XFailure z) {
    		CConstraint False = new CConstraint_c();
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
    public CConstraint substitute(XTerm[] ys, XRoot[] xs) throws XFailure {
    	assert (ys != null && xs != null);
    	assert xs.length == ys.length;
    	
    	boolean eq = true;
		for (int i = 0; i < ys.length; i++) {
			XTerm y = ys[i];
			XRoot x = xs[i];

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
    	
    	CConstraint result = new CConstraint_c();
    	
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
    			XRoot x = xs[i];
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
       XRoot otherSelf = other.self();
       return entails(otherConstraints, otherSelf, sigma);
   }
  
   protected boolean entails(XTerm  term, XRoot self, final CConstraint sigma) throws XFailure {
   	XTerm subst = term.subst(self(), self);
   	return entails(subst, (CConstraint) null);
   }
   

   private boolean entails(List<XTerm> conjuncts, XRoot self, final CConstraint sigma) throws XFailure {
       
       CConstraint_c me = copy();
       if (sigma != null) {
           me.addIn(sigma);
       }

       if (! me.consistent()) {
       	return true;
       }
       
   	for (XTerm term : conjuncts) {
   		if (! me.entails(term, self, (CConstraint) null))
   			return false;
   	}
   	
      return true;
   }
   
   public XTerm bindingForSelfField(XName varName)  {
       if (!consistent || roots == null)
           return null;
       XPromise self = (XPromise) roots.get(self());
       if (self == null)
           return null;
       XPromise result = self.lookup(varName);
       return result == null ? null : result.term();
   }
   
   public XTerm bindingForSelfField(Field f) {
		assert f != null;
		return bindingForSelfField(XTerms.makeName(f.fieldInstance().def(), f.name().id().toString()));
	}
   public XTerm bindingForSelfField(FieldInstance f) {
		assert f != null;
		return bindingForSelfField(XTerms.makeName(f.def(), f.name().toString()));
	}

   public CConstraint leastUpperBound(CConstraint other) {
   	XRoot otherSelf = other.self();
   	
   	CConstraint result = new CConstraint_c();
   	XRoot resultSelf = result.self();
   	CConstraint sigma = new CConstraint_c();
   	for (XTerm term : other.constraints()) {
   		try {
   			if (entails(term, otherSelf, sigma)) {
   				term = term.subst(resultSelf, otherSelf);
   				result.addTerm(term);
   			}
   		} catch (XFailure z) {

   		}
   	}
   	return result;
   }
   
   @Override
   public CConstraint substitute(HashMap<XRoot, XTerm> subs) throws XFailure {
       CConstraint c = this;
       for (Map.Entry<XRoot,XTerm> e : subs.entrySet()) {
           XRoot x = e.getKey();
           XTerm y = e.getValue();
           c = c.substitute(y, x);            
       }
       return c;
   }

   public void checkQuery(CConstraint query, XVar ythis, XRoot xthis, XVar[] y, XRoot[] x, 
		   X10Context context) throws SemanticException {
	   // Check that the guard is entailed.
	   try {
		   if (query != null) { 
			   if (! ((X10TypeSystem) context.typeSystem()).consistent(query)) {
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

   public CConstraint project(XRoot v)  {
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
}
