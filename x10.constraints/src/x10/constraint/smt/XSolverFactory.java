package x10.constraint.smt;

import x10.constraint.XType;

public class XSolverFactory {
	static<T extends XType> XSmtSolver<T> SmtSolver(String name) {
		if (name.equals("cvc4"))
				return XCvc4Solver.<T>getInstance();
		
		throw new IllegalArgumentException("Uknown solver name.");
	}
}
