package x10.types.constraints;

import x10.constraint.XFailure;
import x10.constraint.XTerm;

public class DepthBoundReached extends Exception {
	XTerm t;
	CConstraint c = new CConstraint();
	public void addIn(CConstraint c) {
		try {
			CConstraint d = this.c.addIn(c);
		} catch (XFailure z) {
			c.setInconsistent();
		}
	}
	public CConstraint constraint() { return c;}
	public DepthBoundReached(XTerm t) { this.t=t;}
}