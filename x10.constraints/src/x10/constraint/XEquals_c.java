/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

/**
   Class representing left == right constraints.
   @author njnystrom
 */
public class XEquals_c extends XFormula_c implements XEquals {
	
	public XEquals_c(XTerm left, XTerm right) {
		super(XTerms.equalsName, left, right);
	}
	
	public XPromise internIntoConstraint(XConstraint c, XPromise last) throws XFailure {
//	    XTerm left = left();
//	    XTerm right = right();
//	    if (left instanceof XLit && right instanceof XLit) {
//	        if (left.equals(right))
//	            return XTerms.TRUE.internIntoConstraint(c, last);
//	        else
//	            return XTerms.FALSE.internIntoConstraint(c, last);
//	    }

	    XPromise p = c.intern(left());
	    XPromise q = c.intern(right());

	    if (p instanceof XLit && q instanceof XLit) {
	        if (p.equals(q))
	            return c.intern(XTerms.TRUE);
	        else
	            return c.intern(XTerms.FALSE);
	    }
	    else {
	        if (p == q || p.term().equals(q.term()))
	            // Handle x==x also
	            return c.intern(XTerms.TRUE);
	        else
	            return super.internIntoConstraint(c, last);
	    }
	}
	
	@Override
	public String toString() {
		return left() + "==" + right();
	}

}
