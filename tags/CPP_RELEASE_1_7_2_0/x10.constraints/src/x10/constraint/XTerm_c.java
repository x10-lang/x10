/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class XTerm_c implements XTerm, Cloneable {
	public XTerm_c() {
		super();
	}

	public Solver solver() {
		return null;
	}
	
	public final XTerm subst(XTerm y, XRoot x) {
	    return subst(y, x, true);
	}

	int nextId = 0;
	
	public XTerm subst(XTerm y, final XRoot x, boolean propagate) {
	    XTerm_c t = this;
	    
	    if (propagate && selfConstraint != null) {
	        /*
	        // t : c
	        // ->
	        // t[y/x] : c[y/x]
	        if (x.equals(XSelf.Self)) {
	            // t[y/self] : c[y/self] -- self is rebound by c, do don't subst
	            return t;
	        }
	        
	        if (y.equals(XSelf.Self)) {
	            // t[self/x] : c[self/x] -- will capture another self in c, do subst in a fresh var instead
	            // NOTE: we should give each term its own self variable rather than using just XSelf.Self.
	            if (false) {
	                t = clone();
	                t.setSelfConstraint(null);
	                return t;
	            }
	            XRoot newSelf = new XEQV_c(XTerms.makeName("__self" + (nextId++) + "__"), true);
	            y = newSelf;
	        }
*/
	        t = clone();
	        
	        final XTerm yy = y;
	        
	        // Wrap the self constraint in a substitution.
	        t.setSelfConstraint(new XRef_c<XConstraint>() {
	            @Override
	            public XConstraint compute() {
	                XConstraint c = selfConstraint != null ? selfConstraint.get() : null;
	                if (c != null) {
	                    try {
	                        c = c.substitute(yy, x);
	                    }
	                    catch (XFailure e) {
	                        // fatal error
	                        XConstraint c2 = new XConstraint_c();
	                        c2.setInconsistent();
	                        return c2;
	                        //				    throw new RuntimeException("Cannot perform substitution on self constraint: " + e.getMessage()); 
	                    }
	                }
	                return c;
	            }
	        });
	    }
	    return t;
	}

	@Override
	public XTerm_c clone() {
		try {
			XTerm_c n = (XTerm_c) super.clone();
			return n;
		}
		catch (CloneNotSupportedException e) {
			return this;
		}
	}

	protected XRef_c<XConstraint> selfConstraint;
	
	public XConstraint selfConstraint() throws XFailure {
	    XConstraint c = selfConstraint != null ? selfConstraint.get() : null;
	    if (c != null && ! c.consistent())
	        throw new XFailure("self constraint of " + this + " is inconsistent.");
	    return c;
	}
	
	public XTerm setSelfConstraint(XRef_c<XConstraint> c) {
		this.selfConstraint = c;
		return this;
	}

	public boolean saturate(XConstraint c, Set<XTerm> visited) throws XFailure {
		if (visited.contains(this))
			return false;
		visited.add(this);
		XConstraint self = this.selfConstraint();
		if (self != null) {
		    //		    self = self.saturate();
		    self = self.copy();
		    if (this instanceof XVar) {
		        self = self.substitute(this, self.self());
		    }
		    else {
		        XEQV v = self.genEQV(true);
		        self = self.substitute(v, self.self());
		        self.addBinding(v, this);
		    }
		    for (XTerm term : self.constraints()) {
		        term.saturate(c, visited);
		    }
		    c.addIn(self);
		}
		return true;
	}

	public boolean rootVarIsSelf() {
		return false;
	}

	public boolean hasEQV() {
		return false;
	}
	public boolean isEQV() {
		return false;
	}

	public boolean prefixes(XTerm term) {
		return false;
	}

	public boolean prefersBeingBound() {
		return hasEQV();
	}

	protected boolean isAtomicFormula = false;

	public boolean isAtomicFormula() {
		return isAtomicFormula;
	}

	public void markAsAtomicFormula() {
		isAtomicFormula = true;
	}
}
