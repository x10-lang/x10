package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XTerm;

/**
 * The base class for rigid terms. A rigid term designates an
 * immutable value.
 * 
 * @author vj
 *
 */
public class RigidTerm_c implements RigidTerm {

	XTerm designator;
	
	public RigidTerm_c(XTerm d) {
		this.designator = d;
	}

	public XTerm designator() {
		return designator;
	}
	
	public boolean equals(RigidTerm other, XConstraint c) {
		if (! (other instanceof RigidTerm_c)) return false;
		RigidTerm_c o = (RigidTerm_c) other;
		return c.entails(designator(), o.designator());
	}

}
