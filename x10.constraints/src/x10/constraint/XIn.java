package x10.constraint;

import x10.constraint.bapat.BAPATSolver;

public class XIn extends XFormula_c {
	
	private enum LeftType { REGION, POINT, INT, UNKNOWN };
	
	private LeftType leftType; 
	
	private XIn(XTerm left, XTerm right, LeftType leftType) {
		super(XTerms.makeName("in"), left, right);
		this.leftType = leftType;
		markAsAtomicFormula();
	}
	
	public static XIn makeRegionIn(XTerm left, XTerm right) {
		return new XIn(left, right, LeftType.REGION);
	}
	
	public static XIn makePointIn(XTerm left, XTerm right) {
		return new XIn(left, right, LeftType.POINT);
	}
	
	public static XIn makeIntIn(XTerm left, XTerm right) {
		return new XIn(left, right, LeftType.INT);
	}
	
	public static XIn makeUnknownIn(XTerm left, XTerm right) {
		return new XIn(left, right, LeftType.UNKNOWN);
	}
	
	@Override
	public Solver solver() {
		return BAPATSolver.solver;
	}
	
	public boolean isSubset() {
		return leftIsRegion();
	}
	
	public boolean leftIsRegion() {
		return leftType == LeftType.REGION;
	}
	
	public boolean leftIsPoint() {
		return leftType == LeftType.POINT;
	}
	
	public boolean leftIsInt() {
		return leftType == LeftType.INT;
	}
	
	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(left());
        sb.append(" in ");
        sb.append(right());
        return sb.toString();
	}
	
}
