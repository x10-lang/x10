package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;

/**
 * 
 * @author vj
 *
 */

public class ObjLocs_c extends RigidTerm_c implements ObjLocs {

	public ObjLocs_c(XTerm d) {
		super(d);
	}

	// TODO: Should really be return c.disEntails(designator(), o.designator())
	public boolean disjointFrom(Locs other, XConstraint c) {
		if (! (other instanceof ObjLocs_c)) return true;
		ObjLocs_c o = (ObjLocs_c) other;
		try {
			return ! c.entails(designator(), o.designator());
		} catch (XFailure z) {
			return false;
		}
	}

	@Override
	public String toString() {
	    return designator.toString();
	}
}
