package x10.constraint.bapat;

import stp.Expr;
import stp.VC;

/**
 * Note: Do not give this hashCode or equals methods,
 * since we want HashMaps to store multiple IntVars
 * with the same values (see
 * BAPATSolver.propagateIntegerValues).
 */
public class IntConstant extends IntInformationVar {
	
	public final int val;
	
	public IntConstant(int x) {
		super();
		val = x;
	}

	@Override
	public Expr toSTPExpr(VC vc) {
		return vc.bvConstExprFromInt(BAPATSolver.NUM_BITS, val);
	}

	@Override
	public Expr getAssertions(VC vc) {
		return null;  // No assertions
	}
	
	@Override
	public boolean hasIntInformation() {
		return true;
	}

	@Override
	public int getIntInformation() {
		return val;
	}

	@Override
	public void setIntInformation(int n) {
		assert val == n;
	}

	@Override
	public String toLongString() {
		return toString();
	}
	
	@Override
	public String toString() {
		return val + "";
	}

}
