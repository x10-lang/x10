package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintManager;
import x10.constraint.XConstraintSystem;
import x10.constraint.XFailure;
import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.visitors.XGraphVisitor;

public class XSmtConstraint implements XConstraint {
	/**
	 * The formula representing the constraint we are checking as the conjunction of XSmtTerms. 
	 * This can be incrementally built. 
	 */
	private List<XSmtTerm> formula; 
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
		this.formula.add(formula); 
		inconsistent = false; 
	}
	public XSmtConstraint(List<XSmtTerm> formula) {
		this.formula = formula;
		inconsistent = false; 
	}	

	@Override
	public boolean consistent() {
		if (inconsistent)
			return false; 
		if (formula.isEmpty())
			return true;

		return solver.checkSat(formula);
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
		formula.add((XSmtTerm)t); 
	}

	@Override
	public void addTerm(XTerm t) throws XFailure {
		formula.add((XSmtTerm)t);
	}

	@Override
	public boolean entails(XConstraint other) {
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
		// TODO just return the disjunction of the two constraints? 
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visit(XGraphVisitor xg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public XConstraint copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XVar bindingForVar(XVar v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends XTerm> getTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInconsistent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<? extends XVar> vars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends XTerm> extConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends XTerm> extConstraintsHideFake() {
		// TODO Auto-generated method stub
		return null;
	}

}
