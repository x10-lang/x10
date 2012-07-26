package x10.constraint.smt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XExpr;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XVar;

/**
 * Implementation of XConstraints to be used by an SMT solver. The constraint
 * represents the conjunction of the XSmtTerms stored in conjuncts, each of which 
 * must have a Boolean type. The constraint also keeps a pointer to an external solver
 * that can answer satisfiability queries. 
 * @author lshadare
 *
 * @param <T>
 */
public class XSmtConstraint<T extends XType> implements XConstraint<T> {
	/**
	 * The name of the SMT solver to be used. 
	 */
	private static final String solverName = "cvc4";
	/**
	 * The conjunction of XSmtTerms representing the constraint
	 */
	List<XSmtTerm<T>> conjuncts;
	/**
	 * Pointer to the SMT solver. 
	 */
	XSmtSolver solver; 

	private static enum Status {
		CONSISTENT,
		INCONSISTENT,
		VALID,
		UNKNOWN
	}
	/**
	 * Caching the status of the constraint to avoid unnecessary costly 
	 * calls to the external solver. The <code>status</code> should be
	 * changed to unknown whenever the constraint is strengthen.  
	 */
	transient Status status; 
	
	protected XSmtConstraint() {
		conjuncts = new ArrayList<XSmtTerm<T>>();
		solver = XSolverFactory.SmtSolver(solverName);
		status = Status.UNKNOWN;
	}
	
	@Override
	public boolean consistent() {
		return status == Status.CONSISTENT;
	}

	@Override
	public boolean valid() {
		return status == Status.VALID;
	}

	@Override
	public void addTerm(XTerm<T> t) throws XFailure {
		status = Status.UNKNOWN;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEquality(XTerm<T> left, XTerm<T> right) {
		status = Status.UNKNOWN;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDisEquality(XTerm<T> left, XTerm<T> right) {
		status = Status.UNKNOWN;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean entails(XConstraint<T> other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean entails(XTerm<T> term) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean entailsDisEquality(XTerm<T> left, XTerm<T> right) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean entailsEquality(XTerm<T> left, XTerm<T> right) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public XConstraint<T> leastUpperBound(XConstraint<T> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XConstraint<T> residue(XConstraint<T> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends XTerm<T>> constraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends XExpr<T>> atoms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends XTerm<T>> getTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XSmtConstraint<T> copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XVar<T> bindingForVar(XVar<T> v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInconsistent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<? extends XVar<T>> vars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends XTerm<T>> extConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends XTerm<T>> extConstraintsHideFake() {
		// TODO Auto-generated method stub
		return null;
	}

}
