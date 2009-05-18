package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
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
	
	public Locs substitute(XTerm t, XRoot s) {
		XTerm old = designator();
		XTerm result = old.subst(t, s);
		XTerm newIndex = index.subst(t, s);
		if (result.equals(old) && newIndex.equals(index))
			return this;
		else 
			return Effects.makeArrayElementLocs(result, newIndex);
		
	}
	public ArrayLocs generalize(XVar x) {
		return (index.hasVar(x)) ? Effects.makeArrayLocs(array()) 
				: null;
	}
	public boolean disjointFrom(Locs other, XConstraint c){
		try {
			if (other instanceof ArrayLocs) {
				ArrayLocs o = (ArrayLocs) other;
				return (! c.entails(array(), o.designator()));
			}
			if (other instanceof ArrayElementLocs) {
				ArrayElementLocs o = (ArrayElementLocs) other;
				if (c.entails(array(), o.array())) 
					return ! index().equals(o.index());
			}
		} catch (XFailure z) {
			// hmm should not happen
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
	    return array().toString() + "(" + index() + ")";
	}
}
