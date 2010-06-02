package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * Represents a mutable local variable.
 * 
 * @author vj
 *
 */
public class LocalLocs_c extends Locs_c implements LocalLocs {

	final XLocal local;
	LocalLocs_c(XLocal x) {
		assert x != null : "Cannot construct LocalLocs_c from null";
		this.local = x;
	}
	public XLocal local() { return local;}
	
	public boolean disjointFrom(Locs other, XConstraint c) {
		return ! equals(other);
	}

	public XTerm term() {
		return local;
	}
	
	public boolean hasSubterm(XTerm t) {
		return local.equals(t);
	}
	
	/**
	 * It should never be the case that 
	 */
	public Locs substitute(XTerm t, XVar s) {
		assert false : "Should never have to replace " + s + " by " + t + " in " + this;
		return this;
	}
	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (! (other instanceof LocalLocs_c)) 
			return false;
		LocalLocs_c o = (LocalLocs_c) other;
		return local.equals(o.local());
	}

	@Override 
	public int hashCode() {
		return local.hashCode();
	}
	@Override
	public String toString() {
	    return local.name().toString();
	}
	
	
}
