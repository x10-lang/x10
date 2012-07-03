package x10.constraint.xsmt;

import java.util.List;

public class SmtSolver {
	public static enum Result {
		SAT,
		UNSAT,
		UNKNOWN
	}
	
	private static SmtSolver instance = null;

	public static final SmtSolver getInstance() {
		if(instance == null) {
			instance = new SmtSolver(); 
		}
		return instance; 
	}
	
	public boolean valid(XSmtTerm formula) {
		return false; 
	}
	
	public boolean checkSat(XSmtTerm formula) {
		return false;
	}
	public boolean checkSat(List<XSmtTerm> formula) {
		return false;
	}

}
