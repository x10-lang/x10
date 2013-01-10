package x10.constraint.smt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XEQV;
import x10.constraint.XExpr;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;

/**
 * Implementation of XConstraints to be used by an SMT solver. The constraint
 * represents the conjunction of the XSmtTerms stored in conjuncts, each of which 
 * must have a Boolean type. 
 * @author lshadare
 *
 * @param <T>
 */
public class XSmtConstraint<T extends XType> implements XConstraint<T> {
	
	protected XSmtConstraintSystem<T> sys;
	protected XTypeSystem<T> ts;
	
	protected XSmtConstraint(XSmtConstraintSystem<T> sys, XTypeSystem<T> ts) {
		conjuncts = new ArrayList<XSmtTerm<T>>();
		status = Status.UNKNOWN;
		this.sys = sys;
		this.ts = ts;
	}

	@Override
	public XSmtConstraintSystem<T> sys() { return sys; }

	@Override
	public XTypeSystem<T> ts() { return ts; }

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
		
	protected XSmtConstraint(XSmtConstraint<T> other) {
		this.sys = other.sys;
		this.ts = other.ts;
		this.conjuncts = new ArrayList<XSmtTerm<T>>(other.terms().size());
		for (XSmtTerm<T> ch : other.terms()) {
			conjuncts.add(ch.copy());
		}
		this.status = Status.UNKNOWN; 
	}
	
	@Override
	public boolean consistent() {
		// for now to avoid a costly SMT solver call. 
		return true;
	}

	@Override
	public boolean valid() {
		if (terms().isEmpty())
			return true; 
		
		XTerm<T> conjunction = sys.makeAnd(ts(), terms()); 
		try {
			return XSolverEngine.isValid(sys(), ts(), (XSmtTerm<T>)conjunction);
		} catch (XSmtFailure e) {
			throw new IllegalStateException("Validity check failed: " +  e);
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
		XTerm<T> eq = sys.makeEquals(ts(), left, right);
		conjuncts.add((XSmtTerm<T>)eq);
	}

	@Override
	public void addDisEquality(XTerm<T> left, XTerm<T> right) {
		status = Status.UNKNOWN;
		XTerm<T> eq = sys.makeDisEquals(ts(), left, right);
		conjuncts.add((XSmtTerm<T>)eq);
	}

	@Override
	public boolean entails(XConstraint<T> other) {
		// any constraint entails the empty constraint
		if (other == null || other.terms().size() == 0)
			return true;
		XTerm<T> other_and = sys.makeAnd(ts(), other.terms());
		return entails(other_and);
	}

	@Override
	public boolean entails(XTerm<T> term) {
		try {		
			if (terms().size() == 0) 
				return XSolverEngine.isValid(sys(), ts(), (XSmtTerm<T>)term);
			
			XTerm<T> this_and = sys.makeAnd(ts(), terms());
			return XSolverEngine.entails(sys(), ts(), (XSmtTerm<T>)this_and, (XSmtTerm<T>)term);
		} catch (XSmtFailure e) {
			throw new IllegalStateException("Entails check failed " +  e);
		}
	}

	@Override
	public boolean entailsDisEquality(XTerm<T> left, XTerm<T> right) {
		XTerm<T> diseq = sys.makeDisEquals(ts(), left, right);
		return entails(diseq); 
	}

	@Override
	public boolean entailsEquality(XTerm<T> left, XTerm<T> right) {
		XTerm<T> eq = sys.makeEquals(ts(), left, right);
		return entails(eq); 
	}

	@Override
	public XConstraint<T> leastUpperBound(XConstraint<T> other) {
		// TODO: still need to implement this (unclear how to generalize)
		return null;
	}

	@Override
	public XConstraint<T> residue(XConstraint<T> other) {
		// naive implementation that checks which individual conjuncts in other
		// are entailed by the current constraint (could do better by iterating 
		// through atoms?)
		XConstraint<T> result = sys.makeConstraint(ts);
		for (XTerm<T> constraint : other.terms()) {
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
	public List<? extends XSmtTerm<T>> terms() {
		return conjuncts; 
	}

	@Override
	public Set<? extends XSmtTerm<T>> getVarsAndFields() {
		Set<XSmtTerm<T>> res = new HashSet<XSmtTerm<T>>();
		for (XSmtTerm<T> ch :conjuncts) {
			getVarsAndFields(ch, res); 
		}
		return res; 
	}
	
	private void getVarsAndFields(XSmtTerm<T> term, Set<XSmtTerm<T>> visited) {
		if (term instanceof XSmtVar)
			visited.add(term);
		else if (term instanceof XSmtExpr) {
			XSmtExpr<T> exp = (XSmtExpr<T>) term; 
			for (XSmtTerm<T> ch : exp.children()) {
				getVarsAndFields(ch, visited);
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
				"feature only for XNativeConstraints that throw XFailure " +
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
	public List<? extends XTerm<T>> extTermsHideFake() {
		List<XTerm<T>> terms = new ArrayList<XTerm<T>>();
		for (XTerm<T> t :conjuncts) {
			if(!extFake(t, true, true))
				terms.add(t);
		}
		return terms;
	}
	
	@Override
	public List<? extends XTerm<T>> extTerms() {
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
		for (int i = 0; i < terms().size(); ++i) {
			XSmtTerm<T> t = terms().get(i); 
			sb.append((i == 0? "" : ", ") + t.toSmtString());
		}
		sb.append("}");
		
		return sb.toString();
	}

}
