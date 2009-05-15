package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;

/**
 * Represents an object -- and hence the set of locations specified 
 * by its mutable fields. An object is designated by an XTerm. 
 * The XTerm must be rigid -- that is all variables occurring in it 
 * must be final.
 * @author vj
 *
 */

public class ArrayLocs_c extends RigidTerm_c implements ArrayLocs {

	public ArrayLocs_c(XTerm d) {
		super(d);
	}
	// TODO: Should really be return c.disEntails(designator(), o.designator())
	public boolean disjointFrom(Locs other, XConstraint c) {
		if (! (other instanceof ArrayLocs_c)) return true;
		ArrayLocs_c o = (ArrayLocs_c) other;
		try {
			return ! c.entails(designator(), o.designator());
		} catch (XFailure z) {
			return false;
		}
	}

}
