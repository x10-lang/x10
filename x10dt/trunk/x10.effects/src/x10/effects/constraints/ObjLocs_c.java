package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * 
 * @author vj
 *
 */

public class ObjLocs_c extends RigidTerm_c implements ObjLocs {

	public ObjLocs_c(XTerm d) {
		super(d);
	}

	public Locs substitute(XTerm t, XVar s) {
		XTerm old = designator();
		XTerm result = old.subst(t, s);
		return (result.equals(old)) ? this : Effects.makeObjLocs(result);
	}
	
	public boolean disjointFrom(Locs other, XConstraint c) {
		if (other instanceof ObjLocs) {
        	return c.disEntails(designator(), ((ObjLocs) other).designator());
        }
		return true;
	}

	@Override
	public String toString() {
	    return designator.toString();
	}
	@Override
	public int hashCode() {
		return designator().hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (! (other instanceof ObjLocs_c)) return false;
		ObjLocs_c o = (ObjLocs_c) other;
		return designator().equals(o.designator());
	}
}
