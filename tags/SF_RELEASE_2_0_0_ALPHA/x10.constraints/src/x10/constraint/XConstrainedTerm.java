package x10.constraint;

/**
 * A representation of a constrained term: an XTerm t with a constraint c
 * on the XTerm (and possibly other variables). c is required to have a bound self
 * variable, and t cannot contain that variable.
 * 
 * @author vj 09/02/09
 *
 */
public class XConstrainedTerm  {
	
	XTerm term;
	XConstraint constraint;
	
	/**
	 * Return a (non-null) XConstrainedTerm built from t and c. 
	 * c must have a bound self variable, and t cannot contain that variable.
	 * c is modified in place.
	 * @param t
	 * @param c
	 * @return
	 * @throws XFailure
	 */
	public static XConstrainedTerm make(XTerm t, XConstraint c)  {
		return new XConstrainedTerm(t, c);
	}
	
	/**
	 * Return a (non-null) XConstrainedTerm built from t and c. The constraint
	 * recorded with the returned value is c{self==t}.
	 * c is modified in place.
	 * @param t
	 * @param c
	 * @return
	 * @throws XFailure
	 */
	public static XConstrainedTerm instantiate(XConstraint c, XTerm t) throws XFailure {
		c.addSelfBinding(t);
		// the self variable in c is now bound.
		return new XConstrainedTerm(t, c);
	}
	
	/**
	 * Return a (non-null) XConstrainedTerm built from t and {}.
	 * @param t
	 * @return
	 */
	public static XConstrainedTerm make(XTerm t) {
		try {
			return instantiate(new XConstraint_c(), t);
		} catch (XFailure r) {
			throw new InternalError("Cannot constrain " + t);
		}
	}
	/**
	 * Returns a constrained term. 
	 * @param t
	 * @param c
	 * @throws XFailure
	 */
	private XConstrainedTerm(XTerm t, XConstraint c)  {
		assert t!= null;
		assert c!= null;
		assert c.consistent();
		
		this.term=t;
		this.constraint=c;
	}
	
	public XTerm term(){return term;}
	public XConstraint constraint() { return constraint;}
	
	/**
	 * Returns true iff every value of term() (satisfying constraint()) is also a value of t.term(),
	 * and satisfies t.constraint().
	 * @param t
	 * @return
	 */
	public boolean entails(XConstrainedTerm t) {
		assert t!= null;
		try {
		return constraint.entails(term(), t.term()) && constraint.entails(t.constraint());
		} catch (XFailure f) {
			return false;
		}
	}
	/**
	 * Add t1==t2 to the underlying constraint.
	 * @param t1
	 * @param t2
	 */
	public void addBinding(XTerm t1, XTerm t2) {
		try {
		constraint.addBinding(t1, t2);
		} catch (XFailure f) {
			constraint.setInconsistent();
		}
	}
	public XConstrainedTerm copy() {
		return new XConstrainedTerm(term(), constraint().copy());
	}
	/**
	 * Return the constraint, instantiated with the term.
	 * @return
	 */
	public XConstraint xconstraint() {
		XConstraint s = constraint();
		s = s == null ? new XConstraint_c() : s.copy();
		try {
			s = s.substitute(term(), s.self());
		} catch (XFailure z) {
			s.setInconsistent();
		}
		return s;
		
	}
	
	public String toString() { return term.toString() + constraint.toString();}
}
