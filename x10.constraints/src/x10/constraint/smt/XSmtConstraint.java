package x10.constraint.smt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintManager;
import x10.constraint.XConstraintSystem;
import x10.constraint.XExpr;
import x10.constraint.XFailure;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;

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
	 * Helper method to get the constraint System. 
	 */
	protected XConstraintSystem<T> cs() {
		return XConstraintManager.<T>getConstraintSystem();
	}
	/**
	 * The name of the SMT solver to be used. 
	 */
	private static final String solverName = "cvc4";
	/**
	 * The conjunction of XSmtTerms representing the constraint. Maintains the
	 * invariant that none of the terms in conjuncts are a top level AND 
	 * i.e. the AND is flatten for convenience 
	 */
	protected List<XSmtTerm<T>> conjuncts;
	/**
	 * Pointer to the SMT solver. 
	 */
	protected XSmtSolver<T> solver; 

	protected static enum Status {
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
	
	protected XSmtConstraint(XSmtConstraint<T> other) {
		this.conjuncts = new ArrayList<XSmtTerm<T>>(other.constraints().size());
		for (XSmtTerm<T> ch : other.constraints()) {
			conjuncts.add(XConstraintManager.<T>getConstraintSystem().copy(ch));
		}
		this.solver = other.solver; 
		this.status = Status.UNKNOWN; 
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
		assert t!= null; 
		status = Status.UNKNOWN;
		// if it is an AND we flatten it by recursively adding all conjuncts
		if (t.isAnd()) {
			XExpr<T> exp = (XExpr<T>)t;
			for (XTerm<T> ch : exp.children())
				addTerm(ch);
			return;
		}
		// otherwise just add it to the constraints
		assert t.type().isBoolean(); 
		conjuncts.add((XSmtTerm<T>)t); 
	}
	

	@Override
	public void addEquality(XTerm<T> left, XTerm<T> right) {
		status = Status.UNKNOWN;
		XTerm<T> eq = cs().makeEquals(left, right);
		conjuncts.add((XSmtTerm<T>)eq);
	}

	@Override
	public void addDisEquality(XTerm<T> left, XTerm<T> right) {
		status = Status.UNKNOWN;
		XTerm<T> eq = cs().makeDisEquals(left, right);
		conjuncts.add((XSmtTerm<T>)eq);
	}

	@Override
	public boolean entails(XConstraint<T> other) {
		XTerm<T> other_and = cs().makeExpr(XOp.<T>AND(), other.constraints());
		return entails(other_and);
	}

	@Override
	public boolean entails(XTerm<T> term) {
		XTerm<T> this_and = cs().makeExpr(XOp.<T>AND(), constraints());
		@SuppressWarnings("unchecked")
		XSmtTerm<T> impl = (XSmtTerm<T>) cs().makeExpr(XOp.<T>IMPL(), this_and, term);
		try {
			return solver.isValid(impl);
		} catch (XSmtFailure e) {
			throw new IllegalStateException("Smt Solver Failure " +  e);
		}
	}

	@Override
	public boolean entailsDisEquality(XTerm<T> left, XTerm<T> right) {
		XTerm<T> diseq = cs().makeDisEquals(left, right);
		return entails(diseq); 
	}

	@Override
	public boolean entailsEquality(XTerm<T> left, XTerm<T> right) {
		XTerm<T> eq = cs().makeEquals(left, right);
		return entails(eq); 
	}

	@Override
	public XConstraint<T> leastUpperBound(XConstraint<T> other) {
		// TODO Auto-generated method stub
		return null;
	}

	/**oh
	 * 
	 */
	@Override
	public XConstraint<T> residue(XConstraint<T> other) {
		XConstraint<T> result = cs().makeConstraint();
		for (XTerm<T> constraint : other.constraints()) {
			if (!entails(constraint)) {
				try {
					result.addTerm(constraint);
				} catch (XFailure e) {
					throw new AssertionError("This should never happen.");
				}
			}
		}
		return result; 
	}

	@Override
	public List<? extends XSmtTerm<T>> constraints() {
		return conjuncts; 
	}

	@Override
	public Set<? extends XSmtTerm<T>> getVarsAndProjections() {
		Set<XSmtTerm<T>> res = new HashSet<XSmtTerm<T>>();
		for (XSmtTerm<T> ch :conjuncts) {
			getVarsAndProjections(ch, res); 
		}
		return res; 
	}
	
	private void getVarsAndProjections(XSmtTerm<T> term, Set<XSmtTerm<T>> visited) {
		if (term instanceof XSmtVar)
			visited.add(term);
		else if (term instanceof XSmtExpr) {
			XSmtExpr<T> exp = (XSmtExpr<T>) term; 
			for (XSmtTerm<T> ch : exp.children()) {
				getVarsAndProjections(ch, visited);
			}
		}
	}

	@Override
	public XSmtConstraint<T> copy() {
		return new XSmtConstraint<T>(this);
	}
	/**
	 * Very naive approximation just checks if there is a top level
	 * equality setting v equal to something. 
	 */
	@Override
	public XSmtTerm<T> bindingForVar(XTerm<T> v) {
		for (XSmtTerm<T> ch : conjuncts) {
			if (ch.isEquals()) {
				XSmtExpr<T> exp =  (XSmtExpr<T>) ch;
				if (exp.get(0).equals(v))
					return exp.get(1); 
				if (exp.get(1).equals(v))
					return exp.get(0); 
			}
		}
		return null; 
	}

	@Override
	public void setInconsistent() {
		throw new UnsupportedOperationException("XSmtConstraints should " +
				"never be set inconsistent from the outside. This is a " +
				"feature only for XNativeConstraints tha throw XFailure " +
				"when they become inconsistent.");
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
