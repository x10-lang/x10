package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XLocal;

/**
 * Represents a mutable local variable.
 * 
 * @author vj
 *
 */
public class LocalLocs_c extends Locs_c implements LocalLocs {

	final XLocal local;
	public LocalLocs_c(XLocal x) {
		this.local = x;
	}
	public XLocal local() { return local;}
	
	public boolean disjointFrom(Locs other, XConstraint c) {
		return equals(other);
	}

	public boolean equals(Object other) {
		if (! (other instanceof LocalLocs_c)) return false;
		LocalLocs_c o = (LocalLocs_c) other;
		return local.equals(o.local());
	}

	@Override
	public String toString() {
	    return local.name().toString();
	}
}
