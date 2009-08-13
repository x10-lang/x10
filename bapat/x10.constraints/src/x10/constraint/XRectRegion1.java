package x10.constraint;

import java.util.List;

/**
 * Note that this should never be in an XConstraint.  It should
 * only be created as a temporary and later translated into
 * something else.
 */
public class XRectRegion1 extends XTerm_c implements XCall {
	
	private Object min, max;
	private boolean minIsConstant, maxIsConstant;
	
	public XRectRegion1(Object min, boolean minIsConstant, Object max, boolean maxIsConstant) {
		this.min = min;
		this.minIsConstant = minIsConstant;
		this.max = max;
		this.maxIsConstant = maxIsConstant;
	}
	
	public Object getMin() {
		return min;
	}
	
	public boolean minIsConstant() {
		return minIsConstant;
	}
	
	public Object getMax() {
		return max;
	}
	
	public boolean maxIsConstant() {
		return maxIsConstant;
	}

	public List<XEQV> eqvs() {
		assert false;
		return null;
	}

	public boolean hasVar(XVar v) {
		assert false;
		return false;
	}

	public XPromise internIntoConstraint(XConstraint constraint, XPromise last)
			throws XFailure {
		assert(false);
		return null;
	}

}
