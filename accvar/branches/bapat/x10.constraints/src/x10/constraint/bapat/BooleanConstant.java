package x10.constraint.bapat;

import stp.Expr;
import stp.VC;

/**
 * Note that we have to treat boolean constants as
 * STP bitvectors of width 1 because for some reason
 * STP (or the Java interface) dies when given a
 * constraint like "a__rect = true."
 */
public class BooleanConstant extends Var {
	
	public final boolean val;
	
	public BooleanConstant(boolean b) {
		super();
		val = b;
	}
	
	@Override
	public Expr toSTPExpr(VC vc) {
		if (val)
			return vc.bvConstExprFromInt(1, 1);
		else
			return vc.bvConstExprFromInt(1, 1);
	}

	@Override
	public Expr getAssertions(VC vc) {
		return null;  // No assertions
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
