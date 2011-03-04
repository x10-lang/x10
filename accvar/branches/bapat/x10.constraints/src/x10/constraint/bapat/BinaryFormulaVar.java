package x10.constraint.bapat;

import stp.Expr;
import stp.VC;

abstract public class BinaryFormulaVar extends IntInformationVar {
	
	protected Var left, right;
	protected IntOption intValue;
	protected Expr stpExpr;
	
	public BinaryFormulaVar(Var left, Var right) {
		this.left = left;
		this.right = right;
		intValue = IntOption.noIntValue();
		stpExpr = null;
	}
	
	public Var getLeft() {
		return left;
	}
	
	public Var getRight() {
		return right;
	}

	@Override
	public int getIntInformation() {
		assert intValue.hasIntValue();
		return intValue.getValue();
	}

	@Override
	public boolean hasIntInformation() {
		return intValue.hasIntValue();
	}

	@Override
	public void setIntInformation(int n) {
		if (intValue.hasIntValue())
			assert intValue.getValue() == n;
		else
			intValue = IntOption.intValue(n);
	}

	@Override
	public Expr toSTPExpr(VC vc) {
		if (stpExpr == null)
			stpExpr = vc.varExpr(toString(), vc.bvType(BAPATSolver.NUM_BITS));
		return stpExpr;
	}

	@Override
	public Expr getAssertions(VC vc) {
		return vc.sbvGeExpr(toSTPExpr(vc), vc.bvConstExprFromInt(BAPATSolver.NUM_BITS, 0));
	}

}
