package x10.constraint.bapat;

import stp.Expr;
import stp.VC;

public abstract class Constraint {
	
	public final Var left, right;
	
	public Constraint(Var l, Var r) {
		left = l;
		right = r;
	}
	
	public void addConstraintToVars() {
		left.addConstraint(this);
		right.addConstraint(this);
	}
	
	public Var getOtherVar(Var one) {
		assert one == left || one == right;
		if (one == left)
			return right;
		else
			return left;
	}
	
	public abstract Expr toSTPExpr(VC vc);
	
	public abstract String toLongString();

}
