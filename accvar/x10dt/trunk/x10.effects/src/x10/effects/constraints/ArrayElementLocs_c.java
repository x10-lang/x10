package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * Implements the mutable array element a(t), where a is an Array
 * and t an Index. 
 * @author vj
 *
 */
public class ArrayElementLocs_c extends RigidTerm_c implements ArrayElementLocs {
	
	XTerm index;
	public ArrayElementLocs_c(XTerm array, XTerm index) {
		super(array);
		this.index=index;
	}
	
	public XTerm array() { return designator();}
	public XTerm index() { return index;}
	
	public Locs substitute(XTerm t, XVar s) {
		XTerm old = designator();
		XTerm result = old.subst(t, s);
		XTerm newIndex = index.subst(t, s);
		if (result.equals(old) && newIndex.equals(index))
			return this;
		else 
			return Effects.makeArrayElementLocs(result, newIndex);
		
	}
	public ArrayLocs generalize(XVar x) {
		return (index.hasVar(x)) ? Effects.makeArrayLocs(array()) : null;
	}
	public boolean disjointFrom(Locs other, XConstraint c){
		if (other instanceof ArrayLocs) {
        	ArrayLocs o = (ArrayLocs) other;
        	return (c.disEntails(array(), o.designator()));
        }
        if (other instanceof ArrayElementLocs) {
        	ArrayElementLocs o = (ArrayElementLocs) other;
        	if (c.disEntails(array(), o.array()))
        		return true;
        	return c.disEntails(index(), o.index());
        }

		return true;
	}

	@Override
	public String toString() {
	    return array().toString() + "(" + index() + ")";
	}
	
	@Override
	public int hashCode() {
		return array().hashCode() + index().hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (! (other instanceof ArrayElementLocs_c)) return false;
		ArrayElementLocs_c o = (ArrayElementLocs_c) other;
		return array().equals(o.array()) 
		&& index().equals(o.index());
	}
}
