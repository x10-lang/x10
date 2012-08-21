package x10.constraint.smt;

import x10.constraint.XType;

public class XSolverFactory {
	static<T extends XType> XSmtSolver SmtSolver(String name) {
		if (name.equals("cvc4"))
				return Cvc4Solver.<T>getInstance();
		if(name.equals("z3"))
			return Z3Solver.<T>getInstance(); 
		if(name.equals("cvc3"))
			return Cvc3Solver.<T>getInstance(); 
		
		throw new IllegalArgumentException("Uknown solver name.");
	}
}
