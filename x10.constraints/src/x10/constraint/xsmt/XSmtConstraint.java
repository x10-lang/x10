package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintManager;
import x10.constraint.XFailure;
import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.constraint.XVar;

public class XSmtConstraint implements XConstraint {
	/**
	 * The formula representing the constraint we are checking as the conjunction of XSmtTerms. 
	 * This can be incrementally built but has the invariant that none of the XSmtTerms in the formula
	 * is a top level conjunction.  
	 */
	public List<XSmtTerm> formula; 
	/**
	 * The external SMT solver we are using to check the formula. 
	 */
	private static final SmtSolver solver = SmtSolver.getInstance(); 
	/**
	 * Cache the result if the constraint becomes inconsistent
	 */
	private boolean inconsistent; 
	/**
	 * Storing a pointer to the constraint system for convenience. 
	 */
	private final static XSmtConstraintSystem cm = (XSmtConstraintSystem)XConstraintManager.getConstraintSystem();  
	
	/**
	 * Constructors
	 */
	public XSmtConstraint() {
		this.formula = new ArrayList<XSmtTerm>(); 
		inconsistent = false; 
	}

	public XSmtConstraint(XSmtTerm formula) {
		this.formula = new ArrayList<XSmtTerm>();
		flattenAnd(formula, this.formula);
		inconsistent = false; 
	}
	
	
	public XSmtConstraint(List<XSmtTerm> formula) {
		this.formula = new ArrayList<XSmtTerm>();
		for (XSmtTerm term : formula) {
			flattenAnd(term, this.formula);
		}
		inconsistent = false; 
	}	

	private static void flattenAnd(XSmtTerm term, List<XSmtTerm> res) {
		assert term != null; 
		if(term instanceof XSmtAnd) {
			XSmtAnd eq = (XSmtAnd) term; 
			for (XSmtTerm t : eq.arguments()) {
				flattenAnd(t, res);
			}
		} else {
			res.add(term);
		}
	}

	@Override
	public boolean consistent() {
		if (inconsistent)
			return false; 
		if (formula.isEmpty())
			return true;

		return solver.checkSat(cm.makeAnd(formula));
	}

	@Override
	public boolean valid() {
		if (formula.isEmpty())
			return true;
		
		XSmtTerm f = cm.makeAnd(formula); 
		return solver.valid(f);
	}

	@Override
	public void addBinding(XTerm left, XTerm right) {
		XSmtTerm eq = cm.makeEquals(left, right);
		formula.add(eq); 
	}

	@Override
	public void addDisBinding(XTerm left, XTerm right) {
		XSmtTerm diseq = cm.makeNot(cm.makeEquals(left, right));
		formula.add(diseq); 
	}

	@Override
	public void addAtom(XTerm t) throws XFailure {
		flattenAnd((XSmtTerm)t, this.formula);
	}

	@Override
	public void addTerm(XTerm t) throws XFailure {
		flattenAnd((XSmtTerm)t, this.formula);
	}

	@Override
	public boolean entails(XConstraint other) {
		if (other == null)
			return true; 
		
		XSmtTerm a = cm.makeAnd(formula); 
		XSmtTerm b = cm.makeAnd(((XSmtConstraint)other).constraints());
		XSmtTerm entailment = cm.makeImpl(a, b);
		return solver.valid(entailment);
	}

	@Override
	public boolean entails(XTerm term) {
		XSmtTerm a = cm.makeAnd(formula); 
		XSmtTerm entailment = cm.makeImpl(a, term);
		return solver.valid(entailment);
	}

	@Override
	public boolean disEntails(XTerm left, XTerm right) {
		XSmtTerm a = cm.makeAnd(formula); 
		XSmtTerm b = cm.makeNot(cm.makeEquals(left, right));
		XSmtTerm entailment = cm.makeImpl(a, b);
		return solver.valid(entailment);
	}

	@Override
	public boolean entails(XTerm left, XTerm right) {
		XSmtTerm a = cm.makeAnd(formula); 
		XSmtTerm b = cm.makeEquals(left, right);
		XSmtTerm entailment = cm.makeImpl(a, b);
		return solver.valid(entailment);
	}

	@Override
	public XConstraint leastUpperBound(XConstraint other) {
		//FIXME this is not general enough 
        if (! consistent())       return other;
        if (! other.consistent()) return this;
        if (valid())              return this;
        if (other.valid())        return other;
       	XSmtConstraint result = new XSmtConstraint();
       	for (XTerm term : ((XSmtConstraint)other).constraints()) {
       		try {
       			if (entails(term)) result.addTerm(term);
       		} catch (XFailure z) {
       		    result.setInconsistent();
       		}
       	}
       	return result;		
	}

	@Override
	public XConstraint residue(XConstraint other) {
		// FIXME if this is called only when compilation fails can do something
		// potentially expensive such as check each conjunct of other and see if it is implied
		
		List<XSmtTerm> residue = new ArrayList<XSmtTerm>();
		XSmtTerm assump = cm.makeAnd(formula); 
		for (XSmtTerm c : ((XSmtConstraint)other).constraints()) {
			XSmtTerm impl = cm.makeImpl(assump, c);
			if(!solver.valid(impl))
				residue.add(c); 
		}
		return new XSmtConstraint(residue);
	}

	@Override
	public List<XSmtTerm> constraints() {
		return formula;
	}

	@Override
	public List<? extends XFormula<?>> atoms() {
		List<XFormula<?>> res = new ArrayList<XFormula<?>>();
		for (XSmtTerm form : formula) {
			if (form instanceof XSmtFormula<?>) {
				XSmtFormula<?> f = (XSmtFormula<?>) form; 
				if (f.isAtomicFormula())
					res.add(f); 
			}
		}
		return res;
	}


	@Override
	public XConstraint copy() {
		List<XSmtTerm> newformula = new ArrayList<XSmtTerm>(formula);
		Collections.copy(newformula, formula);
		return new XSmtConstraint(newformula);
	}

	@Override
	public XVar bindingForVar(XVar v) {
		// FIXME: naive check if v is equal to anything
		for (XSmtTerm t : formula) {
			if (t instanceof XSmtEquals) {
				XSmtEquals eq = (XSmtEquals) t;
				if (eq.get(0).equals(v)) {
					return (XVar)eq.get(1); 
				}
				if (eq.get(1).equals(v)) {
					return (XVar)eq.get(0); 
				}
			}
		}
		return null;
	}

	@Override
	public Set<? extends XTerm> getTerms() {
		Set<XTerm> res = new HashSet<XTerm>(); 
		for (XSmtTerm t : formula) {
			res.add(t); 
		}
		return res; 
	}

	@Override
	public void setInconsistent() {
		inconsistent = true; 
	}

	@Override
	public Set<? extends XVar> vars() {
		Set<XVar> res = new HashSet<XVar>(); 
		for (XSmtTerm f : formula)
			collectXVar(f, res); 
		return res; 
	}

	private void collectXVar(XSmtTerm term, Set<XVar> result) {
		if (term instanceof XSmtFormula<?>) {
			XSmtFormula<?> f = (XSmtFormula<?>)term;
			for (XSmtTerm t : f.arguments()) {
				collectXVar(t, result);
			}
		}
		if (term instanceof XVar)
			result.add((XVar)term);
		
		if (term instanceof XSmtField<?>) {
			XSmtField<?> field = (XSmtField<?>) term; 
			collectXVar((XSmtTerm)field.receiver(), result);
		}
	}
	
	@Override
	public List<? extends XTerm> extConstraints() {
		List<XTerm> res = new ArrayList<XTerm>(); 
		for (XSmtTerm f : formula) {
			if (!f.hasEQV())
				res.add(f); 
		}
		return res; 
	}

	@Override
	public List<? extends XTerm> extConstraintsHideFake() {
		// FIXME: hide fake fields
		List<XTerm> res = new ArrayList<XTerm>(); 
		for (XSmtTerm f : formula) {
			if (!f.hasEQV() && !hasFakeField(f)) {
				res.add(f); 
			}
		}
		return res; 
	}
	
	public boolean hasFakeField(XSmtTerm term) {
		// TODO: implement this
		return false;
	}

}
