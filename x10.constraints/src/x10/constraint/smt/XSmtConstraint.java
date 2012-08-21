package x10.constraint.smt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintManager;
import x10.constraint.XConstraintSystem;
import x10.constraint.XEQV;
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
	 * The conjunction of XSmtTerms representing the constraint. Maintains the
	 * invariant that none of the terms in conjuncts are a top level AND 
	 * i.e. the AND is flatten for convenience 
	 */
	protected List<XSmtTerm<T>> conjuncts;
 
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
		status = Status.UNKNOWN;
	}
	
	protected XSmtConstraint(XSmtConstraint<T> other) {
		this.conjuncts = new ArrayList<XSmtTerm<T>>(other.constraints().size());
		for (XSmtTerm<T> ch : other.constraints()) {
			conjuncts.add(XConstraintManager.<T>getConstraintSystem().copy(ch));
		}
		this.status = Status.UNKNOWN; 
	}
	
	@Override
	public boolean consistent() {
		// FIXME: 
		return true;
		//return status == Status.CONSISTENT;
	}

	@Override
	public boolean valid() {
		if (constraints().isEmpty())
			return true; 
//		return false; 
		
		XTerm<T> conjunction = cs().makeAnd(constraints()); 
		try {
			boolean res = XSolverEngine.isValid((XSmtTerm<T>)conjunction);
			return res; 
		} catch (XSmtFailure e) {
			throw new IllegalStateException("Smt Solver Failure " +  e);
		}
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
		// any constraint entails the empty constraint
		if (other == null || other.constraints().size() == 0)
			return true;

		XTerm<T> other_and = cs().makeAnd(other.constraints());
		return entails(other_and);
	}

	@Override
	public boolean entails(XTerm<T> term) {
		try {		
			if (constraints().size() == 0) 
				return XSolverEngine.isValid((XSmtTerm<T>)term);
			
			XTerm<T> this_and = cs().makeAnd(constraints());
			return XSolverEngine.entails((XSmtTerm<T>)this_and, (XSmtTerm<T>)term);
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

	/**
	 * Helper method that returns true if the XTerm contains existentially quantified 
	 * variables and/or fake fields (depending on the arguments)
	 * @param t
	 * @param ext return true if the term contains EQV
	 * @param fake return true if the term contains hidden expressions 
	 * @return
	 */
	private boolean extFake(XTerm<T> t, boolean ext, boolean fake) {
		if (ext && t instanceof XEQV)
			return true; 
		
		if (t instanceof XExpr) {
			XExpr<T> exp = (XExpr<T>) t; 
			
			if (fake && exp.isHidden())
				return true;
			for (XTerm<T> ch : exp.children()) {
				if (extFake(ch, ext, fake))
					return true;
			}
		}
		return false;
	}

	@Override
	public List<? extends XTerm<T>> extConstraintsHideFake() {
		List<XTerm<T>> terms = new ArrayList<XTerm<T>>();
		for (XTerm<T> t :conjuncts) {
			if(!extFake(t, true, true))
				terms.add(t);
		}
		return terms;
	}
	
	@Override
	public List<? extends XTerm<T>> extConstraints() {
		List<XTerm<T>> terms = new ArrayList<XTerm<T>>();
		for (XTerm<T> t :conjuncts) {
			if(!extFake(t, true, false))
				terms.add(t);
		}
		return terms;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("{");
		for (int i = 0; i < constraints().size(); ++i) {
			XTerm<T> t = constraints().get(i); 
			sb.append((i == 0? "" : ", ") + t.prettyPrint());
		}
		sb.append("}");
		
		return sb.toString();
	}
}
