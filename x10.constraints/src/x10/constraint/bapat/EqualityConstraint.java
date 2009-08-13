package x10.constraint.bapat;

import stp.Expr;
import stp.VC;

public class EqualityConstraint extends Constraint {
	
	public EqualityConstraint(Var l, Var r) {
		super(l, r);
	}

	@Override
	public Expr toSTPExpr(VC vc) {
		return vc.eqExpr(left.toSTPExpr(vc), right.toSTPExpr(vc));
	}
	
	@Override
	public String toLongString() {
		return left.toLongString() + "  ==  " + right.toLongString(); 
	}
	
	@Override
	public String toString() {
		return left.toString() + "  ==  " + right.toString(); 
	}

}
