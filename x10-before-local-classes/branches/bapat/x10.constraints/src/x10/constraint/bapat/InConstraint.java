package x10.constraint.bapat;

import stp.Expr;
import stp.VC;
import x10.constraint.XIn;

public class InConstraint extends Constraint {
	
	private XIn xin;
	
	public InConstraint(Var l, Var r, XIn xin) {
		super(l, r);
		this.xin = xin;
	}

	@Override
	public Expr toSTPExpr(VC vc) {
		IntInformationVar left = (IntInformationVar) this.left;
		NamedVar r = (NamedVar) right;
		assert left.hasIntInformation() == r.hasIntInformation() || xin.leftIsInt();
		if (left.hasIntInformation()) {  // ranked
			assert left.getIntInformation() == r.getIntInformation() || xin.leftIsInt();
			int rank = r.getIntInformation();
			// TODO: Handle ranks > 1 (requires changes to XRX as well).
			// TODO: Handle strides, empty regions, etc (requires changes to XRX as well).
			if (left instanceof NamedVar) {
				NamedVar l = (NamedVar) left;
				if (xin.leftIsRegion() && rank == 1 && l.hasField("intervalMin") && l.hasField("intervalMax") && r.hasField("intervalMin") && r.hasField("intervalMax")) {
					Expr isContained = vc.andExpr(vc.sbvLeExpr(r.getField("intervalMin").toSTPExpr(vc), l.getField("intervalMin").toSTPExpr(vc)), vc.sbvLeExpr(l.getField("intervalMax").toSTPExpr(vc), r.getField("intervalMax").toSTPExpr(vc)));
					return isContained;
				} else if (xin.leftIsPoint() && rank == 1 && l.hasField("x1") && r.hasField("intervalMin") && r.hasField("intervalMax")) {
					Expr isContained = vc.andExpr(vc.sbvLeExpr(r.getField("intervalMin").toSTPExpr(vc), l.getField("x1").toSTPExpr(vc)), vc.sbvLeExpr(l.getField("x1").toSTPExpr(vc), r.getField("intervalMax").toSTPExpr(vc)));
					return isContained;
				}
			} else if (xin.leftIsInt() && r.hasField("intervalMin") && r.hasField("intervalMax")) {
				Expr isContained = vc.andExpr(vc.sbvLeExpr(r.getField("intervalMin").toSTPExpr(vc), vc.bvConstExprFromInt(BAPATSolver.NUM_BITS, left.getIntInformation())), vc.sbvLeExpr(vc.bvConstExprFromInt(BAPATSolver.NUM_BITS, left.getIntInformation()), r.getField("intervalMax").toSTPExpr(vc)));
				return isContained;
			}
		}
		// unranked
		// TODO: Depends on our exact representation.  Right now we're doing the simple thing.
		return vc.sbvLeExpr(left.toSTPExpr(vc), r.toSTPExpr(vc));
	}
	
	public boolean leftIsInt() {
		return xin.leftIsInt();
	}
	
	@Override
	public String toLongString() {
		return left.toLongString() + "  in  " + right.toLongString(); 
	}
	
	@Override
	public String toString() {
		return left.toString() + "  in  " + right.toString(); 
	}

}
