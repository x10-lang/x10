package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
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

	public Locs substitute(XTerm t, XRoot s) {
		XTerm old = designator();
		XTerm result = old.subst(t, s);
		return (result.equals(old)) ? this : Effects.makeObjLocs(result);
	}
	
	// TODO: Should really be return c.disEntails(designator(), o.designator())
	public boolean disjointFrom(Locs other, XConstraint c) {
		try {
			if (other instanceof ObjLocs) {
				return ! c.entails(designator(), ((ObjLocs) other).designator());
			}
		} catch (XFailure z) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
	    return designator.toString();
	}
}
