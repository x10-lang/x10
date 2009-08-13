package x10.constraint;

import x10.constraint.bapat.BAPATSolver;

public class XPlus extends XFormula_c {
	
	public XPlus(XTerm... args) {
		super(XTerms.makeName("+"), args);
	}
	
	@Override
	public Solver solver() {
		return BAPATSolver.solver;
	}

}
