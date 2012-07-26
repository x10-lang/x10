package x10.constraint.smt;

import x10.constraint.XType;

/**
 * Wrapper around an external process calling to the SMT solver CVC4. Currently
 * constraints are dumped to a file and read in by the solver. 
 * TODO: use more efficient means of communication (pipe, library call etc.)
 * @author lshadare
 *
 */
public class XCvc4Solver implements XSmtSolver {
	/**
	 * Some useful String constants for now. 
	 */
	private static final String path = "/home/lshadare/temp/constraints-dump/constraint.smt2";
	private static final String solverPath = "/home/lshadare/solvers/cvc4";
	private static ProcessBuilder pb = null;
	private static XCvc4Solver instance = null;
	
	private XCvc4Solver() {
		pb = new ProcessBuilder(solverPath, path);
		pb.redirectErrorStream(true);
	}
	
	public static XCvc4Solver getInstance() {
		if (instance == null)
			instance = new XCvc4Solver(); 
		return instance;
	}
	
	@Override
	public boolean isValid(XSmtTerm<? extends XType> formula) throws XSmtFailure {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSatsifiable(XSmtTerm<? extends XType> formula) throws XSmtFailure {
		// TODO Auto-generated method stub
		return false;
	}

}
