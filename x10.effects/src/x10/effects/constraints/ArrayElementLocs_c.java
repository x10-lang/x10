package x10.effects.constraints;

import x10.constraint.XConstraint;

/**
 * Implements the mutable array element a(t), where a is an Array
 * and t an Index. 
 * @author vj
 *
 */
public class ArrayElementLocs_c extends Locs_c implements ArrayElementLocs {
	
	ArrayLocs a;
	Index t;
	public ArrayElementLocs_c(ArrayLocs a, Index t) {
		this.a = a;
		this.t=t;
	}
	
	public ArrayLocs array() { return a;}
	public Index index() { return t;}
	
	public boolean disjointFrom(Locs other, XConstraint c){
		if (! (other instanceof ArrayElementLocs_c)) return true;
		ArrayElementLocs_c o = (ArrayElementLocs_c) other;
		if (! array().disjointFrom(o.array(), c)) {
			return ! index().equals(o.index());
		}
		return true;
	}

}
