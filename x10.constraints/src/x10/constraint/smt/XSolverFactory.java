package x10.constraint.smt;

public class XSolverFactory {
	static XSmtSolver SmtSolver(String name) {
		if (name.equals("cvc4"))
				return XCvc4Solver.getInstance();
		
		throw new IllegalArgumentException("Uknown solver name.");
	}
}
