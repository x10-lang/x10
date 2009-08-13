package x10.constraint;

import x10.constraint.bapat.BAPATSolver;

public class XMul extends XFormula_c {
	
	public XMul(XTerm... args) {
		super(XTerms.makeName("*"), args);
	}
	
	@Override
	public Solver solver() {
		return BAPATSolver.solver;
	}

}
