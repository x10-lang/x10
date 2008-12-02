/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

import java.util.Set;

public abstract class XTerm_c implements XTerm, Cloneable {
	public XTerm_c() {
		super();
	}
	
	public final XTerm subst(XTerm y, XRoot x) {
	    return subst(y, x, true);
	}
	
	public XTerm subst(final XTerm y, final XRoot x, boolean propagate) {
	    XTerm t = this;
	    if (propagate && ! x.equals(XSelf.Self)) {
		if (selfConstraint != null) {
		    t = clone();
		    // Wrap the self constraint in a substitution.
		    t.setSelfConstraint(new XRef_c<XConstraint>() {
			@Override
			public XConstraint compute() {
			    XConstraint c = selfConstraint != null ? selfConstraint.get() : null;
			    if (c != null) {
				try {
				    c = c.substitute(y, x);
				}
				catch (XFailure e) {
				    // fatal error
				    throw new RuntimeException("Cannot perform substitution on self constraint: " + e.getMessage()); 
				}
			    }
			    return c;
			}
		    });
		}
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
	
	public XConstraint selfConstraint() {
		return selfConstraint != null ? selfConstraint.get() : null;
	}
	
	public void setSelfConstraint(XRef_c<XConstraint> c) {
		this.selfConstraint = c;
	}

	public boolean saturate(XConstraint c, Set<XTerm> visited) throws XFailure {
		if (visited.contains(this))
			return false;
		visited.add(this);
		XConstraint self = this.selfConstraint();
		if (self != null) {
//		    self = self.saturate();
			if (this instanceof XVar) {
				self = self.substitute(this, XSelf.Self);
			}
			else {
				XEQV v = self.genEQV(true);
				self = self.substitute(v, XSelf.Self);
				self = self.addBinding(v, this);
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

	public boolean isEQV() {
		return false;
	}

	public boolean prefixes(XTerm term) {
		return false;
	}

	public boolean prefersBeingBound() {
		return isEQV() || this instanceof XSelf;
	}

	protected boolean isAtomicFormula = false;

	public boolean isAtomicFormula() {
		return isAtomicFormula;
	}

	public void markAsAtomicFormula() {
		isAtomicFormula = true;
	}
}
