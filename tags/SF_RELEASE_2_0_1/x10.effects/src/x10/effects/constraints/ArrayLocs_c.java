package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

/**
 * Represents an array -- and hence the set of locations specified 
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
	public Locs substitute(XTerm t, XRoot s) {
		XTerm old = designator();
		XTerm result = old.subst(t, s);
		return (result.equals(old)) ? this : Effects.makeArrayLocs(result);
	}
	
	public boolean disjointFrom(Locs other, XConstraint c) {
		try {
			if (other instanceof ArrayLocs_c) {
				ArrayLocs_c o = (ArrayLocs_c) other;
				return c.disEntails(designator(), o.designator());
			}
		} catch (XFailure z) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return designator().toString();
	}
	@Override
	public int hashCode() {
		return designator().hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (! (other instanceof ArrayLocs_c)) return false;
		ArrayLocs_c o = (ArrayLocs_c) other;
		return designator().equals(o.designator());
		
	}
}
