package x10.constraint.smt;

import x10.constraint.XType;

/**
 * Interface to be implemented by all external SMT solvers. 
 * @author lshadare
 *
 */
public interface XSmtSolver<T extends XType> {
	
	enum Result {
		SAT,
		UNSAT,
		UKNOWN
	}
	/**
	 * Returns true if the formula is valid i.e. if it is true under
	 * all variable assignments and false otherwise
	 * @param formula
	 * @return
	 * @throws XSmtFailure if there was a problem calling the external solver.
	 */
	boolean isValid(XSmtTerm<T> formula) throws XSmtFailure;
	/**
	 * Returns true if the formula is satisfiable i.e. iif there is an
	 * assignment to the free variables that renders the formula true, and 
	 * false otherwise
	 * @param formula
	 * @return
	 * @throws XSmtFailure if there was a problem calling the external solver.
	 */
	boolean isSatisfiable(XSmtTerm<T> formula) throws XSmtFailure;
	
	boolean entails(XSmtTerm<T> t1, XSmtTerm<T> t2) throws XSmtFailure; 
}
