package x10.constraint;

import x10.constraint.bapat.BAPATSolver;

public class XTranslate extends XFormula_c implements XCall {
	
	public XTranslate(XTerm... args) {
		super(XTerms.makeName("translate"), args);
	}
	
	@Override
	public Solver solver() {
		return BAPATSolver.solver;
	}

}
